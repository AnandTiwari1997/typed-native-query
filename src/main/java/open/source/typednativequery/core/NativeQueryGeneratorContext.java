package open.source.typednativequery.core;

import java.util.*;
import java.util.stream.Collectors;
import open.source.typednativequery.condition.Predicate;
import open.source.typednativequery.function.Function;
import org.apache.commons.lang3.StringUtils;

/** The type Native query generator context. */
public class NativeQueryGeneratorContext implements NativeQueryRenderer {

  /** The Native query. */
  NativeQuery<?, ?> nativeQuery;

  /** The Select. */
  String SELECT = "SELECT",
      /** The Distinct. */
      DISTINCT = "DISTINCT",
      /** The From. */
      FROM = "FROM",
      /** The Where. */
      WHERE = "WHERE",
      /** The Group by. */
      GROUP_BY = "GROUP BY",
      /** The Order by. */
      ORDER_BY = "ORDER BY",
      /** The Offset. */
      OFFSET = "OFFSET",
      /** The Limit. */
      LIMIT = "LIMIT",
      /** The Having. */
      HAVING = "HAVING",
      /** The With. */
      WITH = "WITH",
      /** The On. */
      ON = "ON";

  /**
   * Instantiates a new Native query generator context.
   *
   * @param nativeQuery the native query
   */
  public NativeQueryGeneratorContext(NativeQuery<?, ?> nativeQuery) {
    this.nativeQuery = nativeQuery;
  }

  private final Stack<String> clauseStack = new Stack<>();

  /**
   * Render select string.
   *
   * @param <R> the type parameter
   * @param selection the selection
   * @param distinct the distinct
   * @return the string
   */
  public <R> String renderSelect(Set<R> selection, boolean distinct) {
    this.clauseStack.push(this.SELECT);
    List<String> tokens = new ArrayList<>();
    tokens.add(this.SELECT);
    if (distinct) tokens.add(this.DISTINCT);
    List<String> selectQuery =
        selection.stream()
            .map(r -> (NativeQuerySelect<?, ?, ?>) r)
            .map(q -> q.render(this))
            .collect(Collectors.toList());
    tokens.add(renderQuery(selectQuery, ", "));
    this.clauseStack.pop();
    return renderQuery(tokens);
  }

  /**
   * Render from string.
   *
   * @param from the from
   * @return the string
   */
  public String renderFrom(Set<NativeQueryTable<?>> from) {
    this.clauseStack.push(this.FROM);
    List<String> tokens = new ArrayList<>();
    tokens.add(this.FROM);
    List<String> fromQuery =
        from.stream()
            .map(nativeQueryFrom -> nativeQueryFrom.render(this))
            .collect(Collectors.toList());
    tokens.add(renderQuery(fromQuery, ", "));
    this.clauseStack.pop();
    return renderQuery(tokens);
  }

  /**
   * Render where string.
   *
   * @param conditions the conditions
   * @return the string
   */
  public String renderWhere(Set<Predicate> conditions) {
    if (conditions.isEmpty()) return "";
    this.clauseStack.push(this.WHERE);
    List<String> tokens = new ArrayList<>();
    tokens.add(this.WHERE);
    List<String> whereQuery =
        conditions.stream()
            .map(predicateOperator -> predicateOperator.render(this))
            .collect(Collectors.toList());
    tokens.add(renderQuery(whereQuery, addSpaces("AND")));
    this.clauseStack.pop();
    return renderQuery(tokens);
  }

  /**
   * Render on string.
   *
   * @param conditions the conditions
   * @return the string
   */
  public String renderOn(Set<Predicate> conditions) {
    if (conditions.isEmpty()) return "";
    this.clauseStack.push(this.ON);
    List<String> tokens = new ArrayList<>();
    tokens.add(this.ON);
    List<String> whereQuery =
        conditions.stream()
            .map(predicateOperator -> predicateOperator.render(this))
            .collect(Collectors.toList());
    tokens.add(renderQuery(whereQuery, addSpaces("AND")));
    this.clauseStack.pop();
    return renderQuery(tokens);
  }

  /**
   * Render group by string.
   *
   * @param columns the columns
   * @return the string
   */
  public String renderGroupBy(Set<NativeQueryGroupBy> columns) {
    if (columns.isEmpty()) return "";
    this.clauseStack.push(this.GROUP_BY);
    List<String> tokens = new ArrayList<>();
    tokens.add(this.GROUP_BY);
    List<String> whereQuery =
        columns.stream()
            .map(nativeQueryGroupBy -> nativeQueryGroupBy.render(this))
            .collect(Collectors.toList());
    tokens.add(renderQuery(whereQuery, ", "));
    this.clauseStack.pop();
    return renderQuery(tokens);
  }

  /**
   * Render order by string.
   *
   * @param orderBy the order by
   * @return the string
   */
  public String renderOrderBy(Set<NativeQueryOrderBy> orderBy) {
    if (orderBy.isEmpty()) return "";
    this.clauseStack.push(this.ORDER_BY);
    List<String> tokens = new ArrayList<>();
    tokens.add(this.ORDER_BY);
    List<String> whereQuery =
        orderBy.stream()
            .map(nativeQueryOrderBy -> nativeQueryOrderBy.render(this))
            .collect(Collectors.toList());
    tokens.add(renderQuery(whereQuery, ", "));
    this.clauseStack.pop();
    return renderQuery(tokens);
  }

  /**
   * Render limit string.
   *
   * @param limit the limit
   * @return the string
   */
  public String renderLimit(Integer limit) {
    if (limit == null || limit == 0) return "";
    List<String> tokens = new ArrayList<>();
    tokens.add(this.LIMIT);
    tokens.add(String.valueOf(limit));
    return renderQuery(tokens);
  }

  /**
   * Render offset string.
   *
   * @param offset the offset
   * @return the string
   */
  public String renderOffset(Integer offset) {
    if (offset == null) return "";
    List<String> tokens = new ArrayList<>();
    tokens.add(this.OFFSET);
    tokens.add(String.valueOf(offset));
    return renderQuery(tokens);
  }

  /**
   * Render with string.
   *
   * @param with the with
   * @return the string
   */
  public String renderWith(Set<NativeQueryWith<?>> with) {
    if (with.isEmpty()) return "";
    this.clauseStack.push(this.WITH);
    List<String> tokens =
        with.stream()
            .map(nativeQueryWith -> nativeQueryWith.render(this))
            .collect(Collectors.toList());
    this.clauseStack.pop();
    return renderQuery(tokens);
  }

  private void prepareAliases() {
    for (NativeQueryFrom<?, ?> table : this.nativeQuery.getTables()) {
      table.prepareAlias(this);
    }
    for (NativeQuerySelect<?, ?, ?> select : this.nativeQuery.getSelection()) {
      if (select instanceof Function) {
        select.prepareAlias(this);
      }
    }
  }

  /**
   * Compile string.
   *
   * @return the string
   */
  public String compile() {
    if (this.nativeQuery.getTables().isEmpty()) return null;
    if (this.nativeQuery.getSelection().isEmpty()) {
      this.nativeQuery
          .getTables()
          .forEach(
              nativeQueryFrom ->
                  this.nativeQuery
                      .getSelection()
                      .addAll(nativeQueryFrom.getPossibleColumns().values()));
    }
    prepareAliases();
    List<String> tokens = new ArrayList<>();
    if (this.nativeQuery.isUseWith()) tokens.add(renderWith(this.nativeQuery.getWith()));
    tokens.add(renderSelect(this.nativeQuery.getSelection(), this.nativeQuery.isDistinct()));
    tokens.add(renderFrom(this.nativeQuery.getTables()));
    tokens.add(renderWhere(this.nativeQuery.getExpressions()));
    tokens.add(renderGroupBy(this.nativeQuery.getGroupBy()));
    tokens.add(renderOrderBy(this.nativeQuery.getOrderBy()));
    tokens.add(renderOffset(this.nativeQuery.getOffset()));
    tokens.add(renderLimit(this.nativeQuery.getLimit()));
    return renderQuery(tokens).trim();
  }

  // For maintaining aliases
  private int aliasCount = 0;
  private int parameterCount = 0;

  /**
   * Generate alias string.
   *
   * @param name the name
   * @return the string
   */
  public String generateAlias(String name) {
    return String.format("%s_%s", name, aliasCount++);
  }

  // For maintaining implicit parameters
  private final Map<String, QueryParameter> parameterMappings = new HashMap<>();

  /**
   * Parameter string.
   *
   * @param queryParameter the query parameter
   * @return the string
   */
  public String parameter(QueryParameter queryParameter) {
    if (this.parameterMappings.containsKey(queryParameter.getParameterKey())) {
      return queryParameter.getParameterKey();
    } else if (!Objects.isNull(queryParameter.getParameterKey())) {
      this.parameterCount++;
      this.parameterMappings.put(queryParameter.getParameterKey(), queryParameter);
      return queryParameter.getParameterKey();
    }
    String key = String.format("param_%s", this.parameterCount++);
    this.parameterMappings.put(key, queryParameter);
    return key;
  }

  /**
   * Is should render alias boolean.
   *
   * @param aliasObject the alias object
   * @return the boolean
   */
  // alias
  public boolean isShouldRenderAlias(String aliasObject) {
    if (!this.clauseStack.peek().equals(this.WHERE) && !this.clauseStack.peek().equals(this.ON)) {
      return isRenderAble(aliasObject);
    }
    return false;
  }

  /**
   * Is render able boolean.
   *
   * @param token the token
   * @return the boolean
   */
  public boolean isRenderAble(String token) {
    return StringUtils.isNotBlank(token);
  }

  /**
   * Gets parameters.
   *
   * @return the parameters
   */
  public Map<String, QueryParameter> getParameters() {
    return this.parameterMappings;
  }
}
