package open.source.typednativequery.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** The interface Meta entity column. */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.SOURCE)
public @interface TableColumn {
  /**
   * Column name string [ ].
   *
   * @return the string [ ]
   */
  String columnName() default "";

  /**
   * Is id boolean.
   *
   * @return the boolean
   */
  boolean isId() default false;

  /**
   * Is composite id boolean.
   *
   * @return the boolean
   */
  boolean isCompositeId() default false;
}
