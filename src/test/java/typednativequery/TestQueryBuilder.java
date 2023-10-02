package typednativequery;

import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import open.source.typednativequery.columns.NativeQueryColumn;
import open.source.typednativequery.operator.InOperator;
import open.source.typednativequery.core.*;
import open.source.typednativequery.function.CaseOperator;
import open.source.typednativequery.function.arithmatic.SumFunction;
import open.source.typednativequery.function.string.*;
import open.source.typednativequery.function.window.Rank;
import open.source.typednativequery.metaentity.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestQueryBuilder {

  @Test
  public void testEmployeeQuery() {
    NativeQuery<EmployeeTable, NativeQueryColumn<EmployeeTable, ?>> nativeQuery =
        NativeQuery.createNativeQuery(EmployeeTable.class);
    NativeQueryTable<EmployeeTable> employeeTable = nativeQuery.from(EmployeeTable.class);
    nativeQuery
        .selectMultiple(employeeTable.entity.salary)
        .selectMultiple(employeeTable.entity.firstName)
        .selectMultiple(employeeTable.entity.lastName)
        .selectMultiple(employeeTable.entity.salary.sum().as("total_salary"));
    NativeSubQuery<EmployeeTable, NativeQueryColumn<EmployeeTable, Double>> customSubQuery =
        nativeQuery.subQuery(EmployeeTable.class);
    NativeQueryTable<EmployeeTable> subQuery =
        customSubQuery.from(EmployeeTable.class).as("sub_query");
    customSubQuery.selectMultiple(subQuery.entity.salary.avg());
    nativeQuery
        .where(employeeTable.entity.salary.equal(customSubQuery))
        .where(employeeTable.entity.isManager.isTrue())
        .where(employeeTable.entity.salary.in(Set.of(1.0, 2.0, 3.0)));
    System.out.println(nativeQuery.getQuery());
  }

  /**
   * ORIGINAL
   *
   * <p>WITH employee_ranking AS ( SELECT employee_id, last_name, first_name, salary, SUM
   * (employee_ranking_0.salary) AS total_salary FROM employee ) SELECT employee_id, last_name,
   * first_name, salary FROM employee_ranking
   *
   * <p>WITH employee_ranking AS ( SELECT employee_ranking_0.employee_id, employee_ranking_0.salary,
   * employee_ranking_0.first_name, employee_ranking_0.last_name, SUM (employee_ranking_0.salary) AS
   * total_salary FROM employee employee_0 ) SELECT employee_ranking_0.employee_id,
   * employee_ranking_0.salary, employee_ranking_0.first_name, employee_ranking_0.last_name,
   * employee_ranking_0.total_salary FROM employee_ranking employee_ranking_0
   */
  @Test
  public void testFindUsingCTE() {

    NativeQuery<EmployeeTable, NativeQueryColumn<EmployeeTable, ?>> nativeQuery =
        NativeQuery.createNativeQuery(EmployeeTable.class);

    //
    NativeSubQuery<EmployeeTable, NativeQueryColumn<EmployeeTable, ?>> subQuery =
        nativeQuery.subQuery(EmployeeTable.class);
    NativeQueryTable<EmployeeTable> employeeTable = subQuery.from(EmployeeTable.class);
    SumFunction<EmployeeTable, Double> sum = employeeTable.entity.salary.sum().as("total_salary");
    subQuery
        .selectMultiple(employeeTable.entity.employeeId.as("empId"))
        .selectMultiple(employeeTable.entity.salary)
        .selectMultiple(employeeTable.entity.firstName)
        .selectMultiple(employeeTable.entity.lastName)
        .selectMultiple(sum);

    //
    NativeQueryTable<EmployeeTable> withCTE = new NativeQueryTable<>(EmployeeTable.class);
    withCTE.setTableName("employee_ranking");
    nativeQuery.with(withCTE, subQuery);
    NativeQueryTable<EmployeeTable> outerTable = nativeQuery.from(withCTE);
    nativeQuery
        .selectMultiple(outerTable.entity.employeeId)
        .selectMultiple(outerTable.entity.salary)
        .selectMultiple(outerTable.entity.firstName)
        .selectMultiple(outerTable.entity.lastName)
        .selectMultiple(outerTable.getNumber(sum));
    System.out.println(nativeQuery.getQuery());
  }

  @Test
  public void testFindUsingJoiningSubQuery() {
    NativeQuery<EmployeeTable, NativeQueryColumn<EmployeeTable, ?>> nativeQuery =
        NativeQuery.createNativeQuery(EmployeeTable.class);

    NativeSubQuery<EmployeeTable, NativeQueryColumn<EmployeeTable, ?>> subQueryOne =
        nativeQuery.subQuery(EmployeeTable.class);
    NativeQueryTable<EmployeeTable> subQueryOneTable = subQueryOne.from(EmployeeTable.class);
    subQueryOne
        .selectMultiple(subQueryOneTable.entity.employeeId)
        .selectMultiple(subQueryOneTable.entity.lastName)
        .selectMultiple(subQueryOneTable.entity.salary.sum().as("total_salary"));

    NativeSubQuery<EmployeeTable, NativeQueryColumn<EmployeeTable, ?>> subQueryTwo =
        nativeQuery.subQuery(EmployeeTable.class);
    NativeQueryTable<EmployeeTable> subQueryTwoTable = subQueryTwo.from(EmployeeTable.class);
    subQueryTwo
        .selectMultiple(subQueryTwoTable.entity.firstName)
        .selectMultiple(subQueryTwoTable.entity.salary)
        .selectMultiple(subQueryTwoTable.entity.salary.avg().as("average_salary"));
    subQueryTwo.where(subQueryTwoTable.entity.firstName.like("%andy%"));

    NativeQueryTable<EmployeeTable> table = nativeQuery.from(subQueryOne);
    NativeQueryTableJoin<EmployeeTable> join =
        table.join(subQueryTwo, NativeQueryTableJoinType.INNER_JOIN);

    join.on(join.getString("first_name").equal(table.getString("last_name")))
        .on(join.getNumber("average_salary").equal(table.getNumber("total_salary")))
        .on(join.getNumber("average_salary").equal(10));

    nativeQuery.selectMultiple(join.entity.salary);
    System.out.println(nativeQuery.getQuery());
  }

  /**
   * WITH employee_ranking AS ( SELECT employee_id, last_name, first_name, salary, dept_id RANK()
   * OVER (PARTITION BY dept_id ORDER BY salary DESC) as ranking FROM employee ) SELECT dept_id,
   * employee_id, last_name, first_name, salary FROM employee_ranking WHERE ranking = 2 ORDER BY
   * dept_id, last_name
   *
   * <p>WITH EMPLOYEE_RANKING AS (SELECT EMPLOYEE_0.EMPLOYEE_ID, EMPLOYEE_0.LAST_NAME,
   * EMPLOYEE_0.FIRST_NAME, EMPLOYEE_0.SALARY, EMPLOYEE_0.DEPARTMENT_ID, RANK() OVER (PARTITION BY
   * EMPLOYEE_0.DEPARTMENT_ID ORDER BY EMPLOYEE_0.SALARY DESC) AS RANKING FROM EMPLOYEE EMPLOYEE_0)
   * SELECT EMPLOYEE_RANKING_0.DEPARTMENT_ID, EMPLOYEE_RANKING_0.EMPLOYEE_ID,
   * EMPLOYEE_RANKING_0.LAST_NAME, EMPLOYEE_RANKING_0.FIRST_NAME, EMPLOYEE_RANKING_0.SALARY FROM
   * EMPLOYEE_RANKING EMPLOYEE_RANKING_0 WHERE EMPLOYEE_RANKING_0.RANKING = 2 ORDER BY
   * EMPLOYEE_RANKING_0.DEPARTMENT_ID ASC, EMPLOYEE_RANKING_0.LAST_NAME ASC
   */
  @Test
  public void testSecondHighestSalaryByDepartment() {

    NativeQuery<EmployeeTable, NativeQueryColumn<EmployeeTable, ?>> nativeQuery =
        NativeQuery.createNativeQuery(EmployeeTable.class);

    NativeSubQuery<EmployeeTable, NativeQueryColumn<EmployeeTable, ?>> subQuery =
        nativeQuery.subQuery(EmployeeTable.class);

    NativeQueryTable<EmployeeTable> subQueryTable = subQuery.from(EmployeeTable.class);
    subQuery
        .selectMultiple(subQueryTable.entity.employeeId)
        .selectMultiple(subQueryTable.entity.lastName)
        .selectMultiple(subQueryTable.entity.firstName)
        .selectMultiple(subQueryTable.entity.salary)
        .selectMultiple(subQueryTable.entity.deptId)
        .selectMultiple(
            new Rank<EmployeeTable, Long>()
                .over()
                .partitionBy(subQueryTable.entity.deptId)
                .orderBy(subQueryTable.entity.salary.desc())
                .as("ranking"));

    NativeQueryTable<EmployeeTable> table = new NativeQueryTable<>(EmployeeTable.class);
    table.setTableName("employee_ranking");

    nativeQuery.from(table);

    nativeQuery
        .with(table, subQuery)
        .selectMultiple(table.getNumber("dept_id"))
        .selectMultiple(table.getNumber("emp_id"))
        .selectMultiple(table.getString("last_name"))
        .selectMultiple(table.getString("first_name"))
        .selectMultiple(table.getNumber("salary"));

    nativeQuery.where(table.getNumber("ranking").equal(2));
    nativeQuery
        .orderBy(table.getNumber("dept_id"), true)
        .orderBy(table.getString("last_name"), true);

    System.out.println(nativeQuery.getQuery());
  }

  /**
   * SELECT CASE WHEN salary <= 750000 THEN ‘low’ WHEN salary > 750000 AND salary <= 100000 THEN
   * ‘medium’ WHEN salary > 100000 THEN ‘high’ END AS salary_category, COUNT(*) AS
   * number_of_employees FROM employee GROUP BY CASE WHEN salary <= 750000 THEN ‘low’ WHEN salary >
   * 750000 AND salary <= 100000 THEN ‘medium’ WHEN salary > 100000 THEN ‘high’ END
   */
  @Test
  public void testGroupRowsByRange() {
    NativeQuery<EmployeeTable, NativeQueryColumn<EmployeeTable, ?>> nativeQuery =
        NativeQuery.createNativeQuery(EmployeeTable.class);
    NativeQueryTable<EmployeeTable> table = nativeQuery.from(EmployeeTable.class);

    CaseOperator<EmployeeTable, CustomQueryParameter<String>> caseOperator =
        new CaseOperator<EmployeeTable, CustomQueryParameter<String>>()
            .when(table.entity.salary.equal(75000D))
            .thenValue(new CustomQueryParameter<>("High"))
            .when(table.entity.salary.equal(50000D))
            .thenValue(new CustomQueryParameter<>("Low"));

    nativeQuery.selectMultiple(caseOperator.as("salary_category"));
    nativeQuery.groupBy(caseOperator);
    System.out.println(nativeQuery.getQuery());
    System.out.println(nativeQuery.getParameters());
  }

  @Test
  public void testLengthFunctions() {
    NativeQuery<EmployeeTable, NativeQueryColumn<EmployeeTable, ?>> nativeQuery =
        NativeQuery.createNativeQuery(EmployeeTable.class);
    NativeQueryTable<EmployeeTable> table = nativeQuery.from(EmployeeTable.class);
    nativeQuery
        .selectMultiple(new LengthFunction<>(new CustomQueryParameter<>("Fire me")))
        .selectMultiple(table.entity.firstName.length());
    System.out.println(nativeQuery.getQuery());
  }

  @Test
  public void testLowerCaseFunction() {
    NativeQuery<EmployeeTable, NativeQueryColumn<EmployeeTable, ?>> nativeQuery =
        NativeQuery.createNativeQuery(EmployeeTable.class);
    NativeQueryTable<EmployeeTable> table = nativeQuery.from(EmployeeTable.class);
    nativeQuery
        .selectMultiple(new LowerCaseFunction<>(new CustomQueryParameter<>("FIRE ME")))
        .selectMultiple(table.entity.firstName.lower());
    System.out.println(nativeQuery.getQuery());
  }

  @Test
  public void testUpperCaseFunction() {
    NativeQuery<EmployeeTable, NativeQueryColumn<EmployeeTable, ?>> nativeQuery =
        NativeQuery.createNativeQuery(EmployeeTable.class);
    NativeQueryTable<EmployeeTable> table = nativeQuery.from(EmployeeTable.class);
    nativeQuery
        .selectMultiple(new UpperCaseFunction<>(new CustomQueryParameter<>("fire me")))
        .selectMultiple(table.entity.firstName.upper());
    System.out.println(nativeQuery.getQuery());
  }

  @Test
  public void testTrimFunction() {
    NativeQuery<EmployeeTable, NativeQueryColumn<EmployeeTable, ?>> nativeQuery =
        NativeQuery.createNativeQuery(EmployeeTable.class);
    NativeQueryTable<EmployeeTable> table = nativeQuery.from(EmployeeTable.class);
    nativeQuery
        .selectMultiple(new TrimFunction<>(new CustomQueryParameter<>("   Fire me         ")))
        .selectMultiple(table.entity.firstName.trim());
    System.out.println(nativeQuery.getQuery());
  }

  @Test
  public void testConcatFunction() {
    NativeQuery<EmployeeTable, NativeQueryColumn<EmployeeTable, ?>> nativeQuery =
        NativeQuery.createNativeQuery(EmployeeTable.class);
    NativeQueryTable<EmployeeTable> table = nativeQuery.from(EmployeeTable.class);
    nativeQuery
        .selectMultiple(
            new ConcatFunction<>(
                new CustomQueryParameter<>("Fire"), new CustomQueryParameter<>(" me")))
        .selectMultiple(
            table.entity.firstName.concat(", How are you?").concat("").substring(3).concat(""));
    System.out.println(nativeQuery.getQuery());
  }

  @Test
  public void testSubStrFunction() {
    NativeQuery<EmployeeTable, NativeQueryColumn<EmployeeTable, ?>> nativeQuery =
        NativeQuery.createNativeQuery(EmployeeTable.class);
    NativeQueryTable<EmployeeTable> table = nativeQuery.from(EmployeeTable.class);
    nativeQuery
        .selectMultiple(new SubStrFunction<>(new CustomQueryParameter<>("Fire"), 0, 2))
        .selectMultiple(table.entity.firstName.substring(2));
    System.out.println(nativeQuery.getQuery());
  }

  @Test
  public void testAverageFunction() {
    NativeQuery<EmployeeTable, NativeQueryColumn<EmployeeTable, ?>> nativeQuery =
        NativeQuery.createNativeQuery(EmployeeTable.class);
    NativeQueryTable<EmployeeTable> table = nativeQuery.from(EmployeeTable.class);
    nativeQuery.selectMultiple(table.entity.salary.avg());
    System.out.println(nativeQuery.getQuery());
  }

  @Test
  public void testAverageFunctionWithWindow() {
    NativeQuery<EmployeeTable, NativeQueryColumn<EmployeeTable, ?>> nativeQuery =
        NativeQuery.createNativeQuery(EmployeeTable.class);
    NativeQueryTable<EmployeeTable> table = nativeQuery.from(EmployeeTable.class);
    nativeQuery.selectMultiple(
        table.entity.salary.avg().over().partitionBy(table.entity.firstName));
    System.out.println(nativeQuery.getQuery());
  }

  @Test
  public void testRankFunctionWithWindow() {
    NativeQuery<EmployeeTable, NativeQueryColumn<EmployeeTable, ?>> nativeQuery =
        NativeQuery.createNativeQuery(EmployeeTable.class);
    NativeQueryTable<EmployeeTable> table = nativeQuery.from(EmployeeTable.class);
    nativeQuery.selectMultiple(
        new Rank<EmployeeTable, Long>()
            .over()
            .partitionBy(table.entity.firstName, table.entity.lastName)
            .orderBy(table.entity.firstName.asc()));
    System.out.println(nativeQuery.getQuery());
  }

  @Test
  public void testWithDepartmentTable() {

    NativeQuery<DepartmentTable, NativeQueryColumn<DepartmentTable, ?>> nativeQuery =
        NativeQuery.createNativeQuery(DepartmentTable.class);

    NativeSubQuery<DepartmentTable, NativeQueryColumn<DepartmentTable, ?>> subQuery =
        nativeQuery.subQuery(DepartmentTable.class);

    NativeQueryTable<DepartmentTable> subqueryTable = subQuery.from(DepartmentTable.class);
    DepartmentTable departmentMetaEntity = subqueryTable.entity;
    subQuery.selectMultiple(departmentMetaEntity.departmentId.as("department"));

    NativeQueryTable<DepartmentTable> table = nativeQuery.from(subQuery).as("dept");
    DepartmentTable departmentMetaEntity2 = table.entity;
    nativeQuery.selectMultiple(departmentMetaEntity2.departmentId);
    System.out.println(nativeQuery.getQuery());
  }

  @Test
  public void testIncreaseInSalary() {
    NativeQuery<EmployeeTable, NativeQueryColumn<EmployeeTable, ?>> nativeQuery =
        NativeQuery.createNativeQuery(EmployeeTable.class);
    NativeSubQuery<EmployeeTable, NativeQueryColumn<EmployeeTable, ?>> subQuery =
        nativeQuery.subQuery(EmployeeTable.class);

    NativeQueryTable<EmployeeTable> table = subQuery.from(EmployeeTable.class);
    EmployeeTable employeeMetaEntity = table.entity;
    subQuery.selectMultiple(employeeMetaEntity.employeeId.as("emp_id"));
    subQuery.selectMultiple(
        employeeMetaEntity
            .firstName
            .concat(" ")
            .concat(employeeMetaEntity.lastName)
            .as("full_name"));
    subQuery.selectMultiple(employeeMetaEntity.salary.plus(1.0).plus(2.0).as("plus"));

    NativeQueryTable<EmployeeTable> table1 = nativeQuery.from(subQuery).as("emp");
    EmployeeTable employeeMetaEntity1 = table1.entity;
    nativeQuery.selectMultiple(employeeMetaEntity1.employeeId);
    nativeQuery.selectMultiple(table1.getNumber("plus").abs());
    nativeQuery.selectMultiple(table1.getString("full_name"));

    System.out.println(nativeQuery.getQuery());
    System.out.println(nativeQuery.getParameters());
  }

  @Test
  public void testJoin() {
    NativeQuery<EmployeeTable, NativeQueryColumn<EmployeeTable, ?>> nativeQuery =
        NativeQuery.createNativeQuery(EmployeeTable.class);
    NativeQueryTable<EmployeeTable> employeeMetaTable = nativeQuery.from(EmployeeTable.class);
    NativeQueryTableJoin<DepartmentTable> nativeQueryTableJoin =
        employeeMetaTable.join(DepartmentTable.class, NativeQueryTableJoinType.INNER_JOIN);
    nativeQueryTableJoin.on(
        nativeQueryTableJoin.entity.departmentId.equal(employeeMetaTable.entity.deptId));
    nativeQuery
        //        .selectMultiple(nativeJoin.entity.departmentId)
        .selectMultiple(employeeMetaTable.entity.deptId);
    nativeQuery.select(employeeMetaTable);
    System.out.println(nativeQuery.getQuery());
  }

  @Test
  public void testMultipleFrom() {
    NativeQuery<Table, NativeQueryColumn<Table, ?>> nativeQuery =
        NativeQuery.createNativeQuery(Table.class);
    NativeQueryTable<EmployeeTable> employeeNativeQueryTable =
        nativeQuery.from(EmployeeTable.class);
    NativeQueryTable<DepartmentTable> departmentNativeQueryTable =
        nativeQuery.from(DepartmentTable.class);

    nativeQuery.selectMultiple(employeeNativeQueryTable).selectMultiple(departmentNativeQueryTable);
    nativeQuery
        .where(
            employeeNativeQueryTable.entity.deptId.equal(
                departmentNativeQueryTable.entity.departmentId))
        .where(employeeNativeQueryTable.entity.salary.in(List.of(75000D, 50000D, 1800000D)));
    System.out.println(nativeQuery.getQuery());
  }

  @Test
  public void testMultipleSubQuery() {
    NativeQuery<Table, NativeQueryColumn<Table, ?>> nativeQuery =
        NativeQuery.createNativeQuery(Table.class);
    NativeSubQuery<EmployeeTable, NativeQueryColumn<EmployeeTable, ?>> employeeSubQuery =
        nativeQuery.subQuery(EmployeeTable.class);
    NativeQueryTable<EmployeeTable> employeeNativeQueryTable =
        employeeSubQuery.from(EmployeeTable.class);
    employeeSubQuery
        .select(employeeNativeQueryTable.entity.employeeId)
        .select(employeeNativeQueryTable.entity.deptId);

    NativeSubQuery<DepartmentTable, NativeQueryColumn<DepartmentTable, ?>> departmentSubQuery =
        nativeQuery.subQuery(DepartmentTable.class);
    NativeQueryTable<DepartmentTable> departmentNativeQueryTable =
        departmentSubQuery.from(DepartmentTable.class);
    departmentSubQuery
        .select(departmentNativeQueryTable.entity.departmentId)
        .select(departmentNativeQueryTable.entity.departmentName);

    NativeQueryTable<EmployeeTable> employeeTable = nativeQuery.from(employeeSubQuery);
    NativeQueryTable<DepartmentTable> departmentTable = nativeQuery.from(departmentSubQuery);

    nativeQuery.selectMultiple(employeeTable).selectMultiple(departmentTable);

    nativeQuery.where(employeeTable.entity.deptId.equal(departmentTable.entity.departmentId));
    nativeQuery.distinct();
    System.out.println(nativeQuery.getQuery());
  }

  @Test
  public void testEqualsQueryWithEntity() {

    NativeQuery<EmployeeTable, NativeQueryColumn<EmployeeTable, ?>> nativeQuery =
        NativeQuery.createNativeQuery(EmployeeTable.class);

    NativeQueryTable<EmployeeTable> employeeTable = nativeQuery.from(EmployeeTable.class);

    nativeQuery.select(employeeTable);
    nativeQuery.where(employeeTable.entity.deptId.equal(101L));

    nativeQuery.distinct();
    System.out.println(nativeQuery.getQuery());
    System.out.println(nativeQuery.getParameters());

    NativeQueryTable<RuleTable> ruleTable = nativeQuery.from(RuleTable.class);
    String cl = "Domain";
    nativeQuery.where(ruleTable.entity.profiles.contains(cl));
    nativeQuery.where(ruleTable.entity.profiles.contains(new CustomQueryParameter<>(cl)));
    nativeQuery.where(ruleTable.entity.profiles.contains(new NamedQueryParameter("param_domain")));
    System.out.println(nativeQuery.getQuery());
    System.out.println(nativeQuery.getParameters());

    Pattern pattern =
        Pattern.compile(
            "SELECT DISTINCT (.)+.last_name, (.)+.dept_id, (.)+.salary, (.)+.first_name, (.)+.is_manager, (.)+.emp_id FROM employee (.)+, rule (.)+ WHERE (.)+.dept_id = 101 AND (.)+.profiles \\? :(.)+ AND (.)+.profiles \\? :(.)+ AND (.)+.profiles \\? :param_domain");
    Assertions.assertTrue(pattern.matcher(nativeQuery.getQuery().replaceAll("\\s+", " ")).find());
  }

  @Test
  public void testUnionAll() {

    NativeQuery<Table, NativeQueryColumn<Table, ?>> bsOuterNativeQuery =
        NativeQuery.createNativeQuery(Table.class);

    NativeQueryTable<BenchmarkSectionTable> bsHierarchyTable =
        new NativeQueryTable<>(BenchmarkSectionTable.class).as("BSH");
    bsHierarchyTable.setTableName("BENCHMARK_SECTION_HIERARCHY");

    NativeQuery<Table, NativeQueryColumn<Table, ?>> bsFirstQuery =
        NativeQuery.createNativeQuery(Table.class);

    NativeQueryTable<BenchmarkSectionTable> table = bsFirstQuery.from(BenchmarkSectionTable.class);
    bsFirstQuery
        .selectMultiple(table.entity.sectionId)
        .selectMultiple(table.entity.name)
        .selectMultiple(table.entity.parentId)
        .selectMultiple(table.entity.benchmarkMetadataId);
    bsFirstQuery.where(table.entity.benchmarkMetadataId.equal(table.entity.benchmarkMetadataId));
    bsFirstQuery.where(table.entity.sectionId.equal(new NamedQueryParameter("section_id")));

    NativeQuery<BenchmarkSectionTable, NativeQueryColumn<BenchmarkSectionTable, ?>> bsSecondQuery =
        NativeQuery.createNativeQuery(BenchmarkSectionTable.class);
    NativeQueryTable<BenchmarkSectionTable> bsTable =
        bsSecondQuery.from(BenchmarkSectionTable.class);
    NativeQueryTableJoin<BenchmarkSectionTable> join = bsTable.join(bsHierarchyTable);
    join.on(bsTable.entity.parentId.equal(join.entity.sectionId))
        .on(bsTable.entity.benchmarkMetadataId.equal(join.entity.benchmarkMetadataId));
    bsSecondQuery
        .selectMultiple(bsTable.entity.sectionId)
        .selectMultiple(bsTable.entity.name)
        .selectMultiple(bsTable.entity.parentId)
        .selectMultiple(bsTable.entity.benchmarkMetadataId);

    NativeQueryBuilder nativeQueryBuilder = new NativeQueryBuilder();
    INativeSelection iQuery = nativeQueryBuilder.union(bsFirstQuery, bsSecondQuery);

    bsOuterNativeQuery.withRecursive(bsHierarchyTable, iQuery);
    bsOuterNativeQuery.from(bsHierarchyTable);
    bsOuterNativeQuery.selectMultiple(bsHierarchyTable.entity.sectionId);

    System.out.println(bsOuterNativeQuery.getQuery());
  }

  @Test
  public void testFromSubQuery() {
    NativeQuery<Table, NativeQueryColumn<Table, ?>> nativeQuery =
        NativeQuery.createNativeQuery(Table.class);

    NativeSubQuery<Table, NativeQueryColumn<Table, ?>> subQueryOne =
        nativeQuery.subQuery(Table.class);
    NativeQueryTable<EmployeeTable> subQueryOneTable = subQueryOne.from(EmployeeTable.class);
    subQueryOne
        .selectMultiple(subQueryOneTable.entity.employeeId)
        .selectMultiple(subQueryOneTable.entity.lastName)
        .selectMultiple(subQueryOneTable.entity.salary.sum().as("total_salary"));

    NativeSubQuery<Table, NativeQueryColumn<Table, ?>> subQueryTwo =
        nativeQuery.subQuery(Table.class);
    NativeQueryTable<EmployeeTable> subQueryTwoTable = subQueryTwo.from(EmployeeTable.class);
    subQueryTwo
        .selectMultiple(subQueryTwoTable.entity.firstName)
        .selectMultiple(subQueryTwoTable.entity.salary)
        .selectMultiple(subQueryTwoTable.entity.salary.avg().as("average_salary"));
    subQueryTwo.where(subQueryTwoTable.entity.firstName.like("%andy%"));

    NativeQueryTable<Table> table = nativeQuery.from(subQueryOne);
    NativeQueryTableJoin<Table> join = table.join(subQueryTwo, NativeQueryTableJoinType.INNER_JOIN);

    join.on(join.getString("first_name").equal(table.getString("last_name")))
        .on(join.getNumber("average_salary").equal(table.getNumber("total_salary")))
        .on(join.getNumber("average_salary").equal(10));

    nativeQuery.selectMultiple(join.getNumber("salary"));
    System.out.println(nativeQuery.getQuery());
  }

  @Test
  public void testBuildQueryForRuleComplianceUsingNativeQuery() {
    NativeQuery<Table, NativeQueryColumn<Table, ?>> outerNativeQuery =
        NativeQuery.createNativeQuery(Table.class);

    NativeSubQuery<BenchmarkSectionTable, NativeQueryColumn<BenchmarkSectionTable, ?>>
        bsOuterNativeQuery = outerNativeQuery.subQuery(BenchmarkSectionTable.class);

    NativeQueryTable<BenchmarkSectionTable> table =
        bsOuterNativeQuery.from(BenchmarkSectionTable.class);
    bsOuterNativeQuery
        .selectMultiple(table.entity.sectionId)
        .selectMultiple(table.entity.name)
        .selectMultiple(table.entity.parentId)
        .selectMultiple(table.entity.benchmarkMetadataId);
    bsOuterNativeQuery
        .where(table.entity.benchmarkMetadataId.equal("table.entity.benchmarkMetadataId"))
        .where(table.entity.benchmarkMetadataId.equal("ruleTable.entity.benchmarkMetadataId"));

    // Build Rule Details
    NativeSubQuery<RuleTable, NativeQueryColumn<RuleTable, ?>> ruleDetailsQuery =
        outerNativeQuery.subQuery(RuleTable.class);
    NativeQueryTable<RuleTable> ruleTable = ruleDetailsQuery.from(RuleTable.class);
    ruleDetailsQuery
        .selectMultiple(ruleTable.entity.id)
        .selectMultiple(ruleTable.entity.ruleName)
        .selectMultiple(ruleTable.entity.sectionId)
        .selectMultiple(ruleTable.entity.profiles)
        .selectMultiple(ruleTable.entity.benchmarkMetadataId);

    NativeSubQuery<BenchmarkMetadataTable, NativeQueryColumn<BenchmarkMetadataTable, ?>> bmQuery =
        ruleDetailsQuery.subQuery(BenchmarkMetadataTable.class);

    NativeQueryTable<BenchmarkMetadataTable> bmTable = bmQuery.from(BenchmarkMetadataTable.class);
    bmQuery.selectMultiple(bmTable.entity.name);
    bmQuery.where(bmTable.entity.id.equal("bmTable.entity.id"));

    ruleDetailsQuery
        .where(ruleTable.entity.benchmarkMetadataId.equal("ruleTable.entity.benchmarkMetadataId"))
        .where(bmQuery.exist());

    NativeQueryTable<BenchmarkSectionTable> bsTableO =
        outerNativeQuery.from(bsOuterNativeQuery.as("BS"));
    NativeQueryTableJoin<RuleTable> ruleBsJoinTable =
        bsTableO.join(ruleDetailsQuery, NativeQueryTableJoinType.INNER_JOIN).as("R");
    ruleBsJoinTable
        .on(ruleBsJoinTable.getString("id").equal(bsTableO.getString("id")))
        .on(
            ruleBsJoinTable
                .getString("benchmark_metadata_id")
                .equal(bsTableO.getString("benchmark_metadata_id")));

    System.out.println(outerNativeQuery.getQuery());
  }

  // Operators

  @Test
  public void testInOperator() {
    NativeQuery<EmployeeTable, NativeQueryColumn<EmployeeTable, ?>> nativeQuery =
        NativeQuery.createNativeQuery(EmployeeTable.class);
    NativeQueryTable<EmployeeTable> employeeTable =
        nativeQuery.from(EmployeeTable.class).as("my_employee");
    nativeQuery.where(
        employeeTable.entity.deptId.in(List.of(1L, 2L, 3L)).value(List.of(4L, 5L, 6L)));
    System.out.println(nativeQuery.getQuery());

    nativeQuery = NativeQuery.createNativeQuery(EmployeeTable.class);
    employeeTable = nativeQuery.from(EmployeeTable.class).as("my_employee");
    nativeQuery.where(new InOperator<>(employeeTable.entity.deptId).value(List.of(7L, 8L, 9L)));
    System.out.println(nativeQuery.getQuery());

    nativeQuery = NativeQuery.createNativeQuery(EmployeeTable.class);
    employeeTable = nativeQuery.from(EmployeeTable.class).as("my_employee");
    nativeQuery.where(employeeTable.entity.deptId.in(List.of(1L, 2L, 3L)).not());
    System.out.println(nativeQuery.getQuery());

    nativeQuery = NativeQuery.createNativeQuery(EmployeeTable.class);
    employeeTable = nativeQuery.from(EmployeeTable.class).as("my_employee");
    nativeQuery.where(
        new InOperator<>(employeeTable.entity.deptId).not().value(List.of(7L, 8L, 9L)));
    System.out.println(nativeQuery.getQuery());
  }

  // Aggregate Function

  @Test
  public void testAggregateFunctions() {
    
  }
}
