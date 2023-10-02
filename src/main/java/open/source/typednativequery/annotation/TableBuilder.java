package open.source.typednativequery.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** The interface Meta entity builder. */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.SOURCE)
public @interface TableBuilder {
  /**
   * Table name string.
   *
   * @return the string
   */
  String tableName();

  /**
   * Composite column name string.
   *
   * @return the string
   */
  String compositeColumnName() default "";
}
