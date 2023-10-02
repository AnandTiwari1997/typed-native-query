package open.source.typednativequery.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** The interface Meta entity composite id. */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.SOURCE)
public @interface TableCompositeId {}
