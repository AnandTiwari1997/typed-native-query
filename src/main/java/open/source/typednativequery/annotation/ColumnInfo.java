/*
 * This Source Code is generated as part of Open Source Project.
 */

package open.source.typednativequery.annotation;

import java.util.ArrayList;
import java.util.List;
import open.source.typednativequery.columns.*;

/** The type Column info. */
class ColumnInfo {
  /** The Property name. */
  String propertyName;
  /** The Type name. */
  List<String> typeName = new ArrayList<>();
  /** The Column name. */
  String columnName;
  /** The Type parameter. */
  List<String> typeParameter = new ArrayList<>();

  /** The enum Supported columns. */
  enum SupportedColumns {
    /** String supported columns. */
    STRING(StringNativeColumn.class, false, true, false),
    /** Boolean supported columns. */
    BOOLEAN(BooleanNativeColumn.class, false, true, false),
    /** Number supported columns. */
    NUMBER(NumberNativeColumn.class, true, true, false),
    /** List supported columns. */
    LIST(ListNativeColumn.class, true, false, true),
    /** Map supported columns. */
    MAP(MapNativeColumn.class, true, false, true),
    /** Entity supported columns. */
    ENTITY(NativeQueryColumn.class, true, true, false);
    private final Class<?> aClass;
    /** The Support type parameter. */
    final boolean supportTypeParameter;
    /** The Is primitive. */
    final boolean isPrimitive;
    /** The Is list. */
    final boolean isList;

    SupportedColumns(
        Class<?> stringNativeColumnClass,
        boolean supportTypeParameter,
        boolean isPrimitive,
        boolean isList) {
      this.aClass = stringNativeColumnClass;
      this.supportTypeParameter = supportTypeParameter;
      this.isPrimitive = isPrimitive;
      this.isList = isList;
    }

    /**
     * Gets type.
     *
     * @return the type
     */
    public Class<?> getType() {
      return this.aClass;
    }

    /**
     * Support type parameter boolean.
     *
     * @return the boolean
     */
    public boolean supportTypeParameter() {
      return this.supportTypeParameter;
    }

    /**
     * Is primitive boolean.
     *
     * @return the boolean
     */
    public boolean isPrimitive() {
      return this.isPrimitive;
    }

    /**
     * Is list boolean.
     *
     * @return the boolean
     */
    public boolean isList() {
      return this.isList;
    }
  }

  /** The Supported type. */
  SupportedColumns supportedType;

  /**
   * Gets supported column.
   *
   * @param typeKind the type kind
   * @return the supported column
   */
  public SupportedColumns getSupportedColumn(String typeKind) {
    switch (typeKind) {
      case "SHORT":
      case "INT":
      case "LONG":
      case "FLOAT":
      case "DOUBLE":
      case "INTEGER":
        return SupportedColumns.NUMBER;
      case "BOOLEAN":
      case "BOOL":
        return SupportedColumns.BOOLEAN;
      case "STRING":
        return SupportedColumns.STRING;
      case "LIST":
        return SupportedColumns.LIST;
      case "MAP":
        return SupportedColumns.MAP;
      default:
        return SupportedColumns.ENTITY;
    }
  }

  @Override
  public String toString() {
    return "ColumnInfo{ "
        + "propertyName='"
        + propertyName
        + '\''
        + ", typeName='"
        + typeName
        + '\''
        + ", columnName='"
        + columnName
        + '\''
        + ", typeParameter="
        + typeParameter
        + ", supportedType="
        + supportedType
        + '}';
  }
}
