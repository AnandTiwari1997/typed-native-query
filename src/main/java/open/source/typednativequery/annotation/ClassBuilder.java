/*
 * This Source Code is generated as part of Open Source Project.
 */

package open.source.typednativequery.annotation;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
import org.apache.commons.lang3.StringUtils;

public class ClassBuilder {
  public void addPackage(String packageName, PrintWriter out) {
    out.printf("package %s;", packageName);
    out.println();
  }

  public void addImport(String packageName, String simpleName, PrintWriter out) {
    out.printf("import %s.%s;", packageName, simpleName);
    out.println();
  }

  public void addField(String leftEndSide, String rightEndSide, PrintWriter out, int tabCount) {
    IntStream.of(tabCount).forEach(value -> out.print("\t"));
    out.printf("%s = %s;", leftEndSide, rightEndSide);
    out.println();
  }

  public String typeDeclaration(
      String type, String typeName, boolean isTypeParameterized, List<String> typeParameters) {
    StringBuilder declaration = new StringBuilder(type);
    if (isTypeParameterized) {
      declaration.append("<");
      declaration.append(String.join(", ", typeParameters));
      declaration.append(">");
    }
    List<String> statements = new ArrayList<>();
    statements.add(declaration.toString());
    if (!StringUtils.isBlank(typeName)) statements.add(typeName);
    return String.join(" ", statements);
  }

  public String typeInitialization(
      String type, boolean isTypeParameterized, List<String> parameters) {
    StringBuilder declaration = new StringBuilder(type);
    if (isTypeParameterized) {
      declaration.append("<>");
    }
    declaration.append("(");
    declaration.append(String.join(", ", parameters));
    declaration.append(")");
    return String.join(" ", List.of("new", declaration));
  }

  public String addModifiers(List<String> modifiers, String declaredType) {
    List<String> statement = new ArrayList<>(modifiers);
    statement.add(declaredType);
    return String.join(" ", statement);
  }

  public String methodDeclaration(
      List<String> modifiers,
      String returnType,
      String methodName,
      Map<String, String> acceptedParameters) {
    String declaration = typeDeclaration(returnType, methodName, false, Collections.emptyList());
    String withModifiers = addModifiers(modifiers, declaration);
    StringBuilder stringBuilder = new StringBuilder(withModifiers);
    stringBuilder.append("(");
    List<String> parameters = new ArrayList<>();
    acceptedParameters.forEach(
        (type, name) -> parameters.add(String.join(" ", List.of(type, name))));
    stringBuilder.append(String.join(", ", parameters));
    stringBuilder.append(")");
    return stringBuilder.toString();
  }

  public void addMethod(
      String declaration, List<String> definition, PrintWriter out, int tabCount) {
    IntStream.of(tabCount).forEach(value -> out.print("\t"));
    out.printf("%s {", declaration);
    out.println();
    definition.forEach(
        s -> {
          IntStream.of(tabCount + 1).forEach(value -> out.print("\t"));
          out.printf("%s", s);
          out.println();
        });
    IntStream.of(tabCount + 1).forEach(value -> out.print("\t"));
    out.println("}");
  }

  public void addClass(
      List<String> modifiers,
      String className,
      List<String> extendClassName,
      List<String> implementClassName,
      PrintWriter out,
      int tabCount) {
    IntStream.of(tabCount).forEach(value -> out.print("\t"));
    String declaredType = typeDeclaration("class", className, false, Collections.emptyList());
    String withModifiers = addModifiers(modifiers, declaredType);
    List<String> stringBuilder = new ArrayList<>();
    stringBuilder.add(withModifiers);
    if (!extendClassName.isEmpty()) {
      stringBuilder.add("extends");
      stringBuilder.add(String.join(", ", extendClassName));
    }
    if (!implementClassName.isEmpty()) {
      stringBuilder.add("implements");
      stringBuilder.add(String.join(", ", implementClassName));
    }
    out.print(String.join(" ", stringBuilder));
  }
}
