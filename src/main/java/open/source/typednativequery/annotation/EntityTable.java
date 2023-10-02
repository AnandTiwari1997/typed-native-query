/*
 * This Source Code is generated as part of Open Source Project.
 */

package open.source.typednativequery.annotation;

import java.util.*;
import javax.lang.model.element.Element;

public class EntityTable {
  String entityName;
  String className;
  String packageName;
  String tableName;
  Element element;
  Map<String, ColumnInfo> columnInfoMap = new HashMap<>();
  boolean containsEmbeddedId = false;
  List<String> idColumns = new ArrayList<>();
}
