/*
 * This Source Code is generated as part of Open Source Project.
 */

package open.source.typednativequery.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.SOURCE)
public @interface TableJoinColumn {
  /**
   * Column name string [ ].
   *
   * @return the string [ ]
   */
  String[] columnName();

  /**
   * Reference string [ ].
   *
   * @return the string [ ]
   */
  String[] reference();
}
