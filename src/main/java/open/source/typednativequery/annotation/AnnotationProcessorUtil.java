/*
 * This Source Code is generated as part of Open Source Project.
 */

package open.source.typednativequery.annotation;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;
import javax.annotation.processing.FilerException;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.PrimitiveType;
import lombok.SneakyThrows;
import open.source.typednativequery.columns.NativeQueryColumn;
import open.source.typednativequery.metaentity.Table;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

public class AnnotationProcessorUtil {

  public static EntityTable getEntityTable(Element element)
      throws ClassNotFoundException, IOException {
    EntityTable table = new EntityTable();
    try {
      Class<?> entityClass = Class.forName(element.toString());
      table.packageName = entityClass.getPackageName();
      table.className = entityClass.getSimpleName() + "Table";
    } catch (ClassNotFoundException classNotFoundException) {
      String[] tokens = element.toString().split("\\.");
      StringJoiner joiner = new StringJoiner(".");
      for (int i = 0; i < tokens.length - 1; i++) joiner.add(tokens[i]);
      table.packageName = joiner.toString();
      table.className = tokens[tokens.length - 1] + "Table";
    }
    table.element = element;
    table.entityName = element.getSimpleName().toString();
    table.tableName = AnnotationProcessorUtil.toCamelCase(table.entityName);
    return table;
  }

  public static ColumnInfo cloneColumnInfo(String columnName, ColumnInfo columnInfo) {
    ColumnInfo newColumnInfo = new ColumnInfo();
    newColumnInfo.columnName = columnName;
    newColumnInfo.propertyName = toCamelCase(newColumnInfo.columnName);
    newColumnInfo.supportedType = columnInfo.supportedType;
    newColumnInfo.typeName = new ArrayList<>(columnInfo.typeName);
    newColumnInfo.typeParameter = new ArrayList<>(columnInfo.typeParameter);
    return newColumnInfo;
  }

  public static String toCamelCase(String text) {
    StringBuilder builder = new StringBuilder();
    boolean shouldConvertNextCharToLower = true;
    for (int i = 0; i < text.length(); i++) {
      char currentChar = text.charAt(i);
      if (currentChar == '_') {
        shouldConvertNextCharToLower = false;
      } else if (shouldConvertNextCharToLower) {
        builder.append(Character.toLowerCase(currentChar));
      } else {
        builder.append(Character.toUpperCase(currentChar));
        shouldConvertNextCharToLower = true;
      }
    }
    return builder.toString();
  }

  public static String buildColumnName(String name, String defaultName) {
    if (StringUtils.isNotBlank(name)) defaultName = name;
    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < defaultName.length(); i++) {
      char currentChar = defaultName.charAt(i);
      if (Character.isUpperCase(currentChar)) builder.append("_");
      builder.append(Character.toLowerCase(currentChar));
    }
    return builder.toString();
  }

  @SneakyThrows
  public static ColumnInfo createColumn(Element enclosedElement) {
    ColumnInfo columnInfo = new ColumnInfo();
    boolean isPrimitiveType = enclosedElement.asType().getKind().isPrimitive();
    if (isPrimitiveType) {
      PrimitiveType primitiveType = (PrimitiveType) enclosedElement.asType();
      columnInfo.typeName.add(StringUtils.capitalize(primitiveType.getKind().name().toLowerCase()));
      columnInfo.supportedType =
          columnInfo.getSupportedColumn(columnInfo.typeName.get(0).toUpperCase());
    } else {
      DeclaredType declaredType = (DeclaredType) enclosedElement.asType();
      Element element = declaredType.asElement();
      Class<?> tClass = Class.forName(element.toString());
      if (tClass.isAssignableFrom(List.class)) {
        columnInfo.typeName.add(declaredType.getTypeArguments().get(0).toString());
        columnInfo.supportedType =
            columnInfo.getSupportedColumn(List.class.getSimpleName().toUpperCase());
      } else if (tClass.isAssignableFrom(Map.class)) {
        declaredType
            .getTypeArguments()
            .forEach(typeMirror -> columnInfo.typeName.add(typeMirror.toString()));
        columnInfo.supportedType =
            columnInfo.getSupportedColumn(Map.class.getSimpleName().toUpperCase());
      } else {
        columnInfo.typeName.add(element.toString());
        columnInfo.supportedType =
            columnInfo.getSupportedColumn(tClass.getSimpleName().toUpperCase());
      }
    }
    if (columnInfo.supportedType.supportTypeParameter()) {
      columnInfo.typeParameter.addAll(columnInfo.typeName);
    }
    return columnInfo;
  }

  public static void buildFile(
      EntityTable entityTable, Logger log, ProcessingEnvironment processingEnv) throws IOException {

    List<ColumnInfo> elementInfos = new ArrayList<>(entityTable.columnInfoMap.values());

    log.debug("Writing Element " + entityTable.element);
    log.debug("Following columns ");
    log.debug("{}", elementInfos);

    String classFileName = entityTable.packageName + "." + entityTable.className;

    try (PrintWriter out =
        new PrintWriter(processingEnv.getFiler().createSourceFile(classFileName).openWriter())) {
      ClassBuilder classBuilder = new ClassBuilder();

      // package
      classBuilder.addPackage(entityTable.packageName, out);
      out.println();

      // imports
      classBuilder.addImport(Table.class.getPackageName(), Table.class.getSimpleName(), out);
      classBuilder.addImport(List.class.getPackageName(), List.class.getSimpleName(), out);
      classBuilder.addImport(
          NativeQueryColumn.class.getPackageName(), NativeQueryColumn.class.getSimpleName(), out);
      Arrays.stream(ColumnInfo.SupportedColumns.values())
          .forEach(
              supportedType -> {
                Class<?> supportedClass = supportedType.getType();
                classBuilder.addImport(
                    supportedClass.getPackageName(), supportedClass.getSimpleName(), out);
              });
      out.println();

      List<String> implement = new ArrayList<>();
      implement.add(
          classBuilder.typeDeclaration(
              Table.class.getSimpleName(), null, false, Collections.emptyList()));
      classBuilder.addClass(
          List.of(Modifier.toString(Modifier.PUBLIC), Modifier.toString(Modifier.FINAL)),
          entityTable.className,
          Collections.emptyList(),
          implement,
          out,
          0);
      out.println(" {");
      out.println();

      String methodDeclaration;

      // fields
      String finalBuilderSimpleClassName = entityTable.className;
      elementInfos.forEach(
          elementInfo -> {
            LinkedList<String> modifierDeclaration = new LinkedList<>();
            modifierDeclaration.add(Modifier.toString(Modifier.PUBLIC));

            LinkedList<String> typeParameters = new LinkedList<>();
            typeParameters.add(finalBuilderSimpleClassName);
            typeParameters.addAll(elementInfo.typeParameter);

            String declaredType =
                classBuilder.typeDeclaration(
                    elementInfo.supportedType.getType().getSimpleName(),
                    elementInfo.propertyName,
                    true,
                    typeParameters);
            String leftEndSide = classBuilder.addModifiers(modifierDeclaration, declaredType);

            LinkedList<String> parameterDeclaration = new LinkedList<>();
            parameterDeclaration.add(String.format("\"%s\"", elementInfo.columnName));
            elementInfo.typeName.forEach(
                s -> parameterDeclaration.add(String.format("%s.class", s)));
            String rightEndSide = "";
            if (ColumnInfo.SupportedColumns.ENTITY.equals(elementInfo.supportedType)) {
              parameterDeclaration.add(String.valueOf(elementInfo.supportedType.isPrimitive()));
              parameterDeclaration.add(String.valueOf(elementInfo.supportedType.isList()));
              rightEndSide =
                  classBuilder
                      .typeInitialization(
                          String.format(
                              "%s.build", elementInfo.supportedType.getType().getSimpleName()),
                          false,
                          parameterDeclaration)
                      .replace("new ", "");
            } else {
              rightEndSide =
                  classBuilder.typeInitialization(
                      elementInfo.supportedType.getType().getSimpleName(),
                      true,
                      parameterDeclaration);
            }
            classBuilder.addField(leftEndSide, rightEndSide, out, 1);
          });
      out.println();

      // private constructor
      methodDeclaration =
          classBuilder.methodDeclaration(
              List.of(Modifier.toString(Modifier.PRIVATE)),
              entityTable.className,
              null,
              Collections.emptyMap());
      classBuilder.addMethod(methodDeclaration, Collections.emptyList(), out, 1);
      out.println();

      // implement methods
      Method method = Table.class.getMethod("getTableName");
      LinkedList<String> modifierDeclaration = new LinkedList<>();
      if (Modifier.isPublic(method.getModifiers())) {
        modifierDeclaration.add(Modifier.toString(Modifier.PUBLIC));
      }
      methodDeclaration =
          classBuilder.methodDeclaration(
              modifierDeclaration,
              method.getReturnType().getSimpleName(),
              method.getName(),
              Collections.emptyMap());
      List<String> methodDefinition = new ArrayList<>();
      methodDefinition.add(String.format("return \"%s\";", entityTable.tableName));
      classBuilder.addMethod(methodDeclaration, methodDefinition, out, 1);
      out.println();

      List<String> typeParameters = new ArrayList<>();
      typeParameters.add("?");
      typeParameters.add("?");
      String innerTypeDeclaration =
          classBuilder.typeDeclaration("NativeQueryColumn", null, true, typeParameters);
      String typeDeclaration =
          classBuilder.typeDeclaration("List", null, true, List.of(innerTypeDeclaration));
      methodDeclaration =
          classBuilder.methodDeclaration(
              modifierDeclaration, typeDeclaration, "getColumns", Collections.emptyMap());

      methodDefinition = new ArrayList<>();
      methodDefinition.add(
          String.format(
              "return List.of(%s);",
              elementInfos.stream()
                  .map(columnInfo -> columnInfo.propertyName)
                  .collect(Collectors.joining(", "))));
      classBuilder.addMethod(methodDeclaration, methodDefinition, out, 1);

      out.println();
      out.println("}");
    } catch (NoSuchMethodException | FilerException e) {
      //      throw new RuntimeException(e);
    }
  }
}
