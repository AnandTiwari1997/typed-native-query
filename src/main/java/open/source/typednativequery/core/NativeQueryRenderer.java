package open.source.typednativequery.core;

import java.util.List;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;

/** The interface Native query renderer. */
public interface NativeQueryRenderer {

  /**
   * Render query string.
   *
   * @param query the query
   * @return the string
   */
  default String renderQuery(List<String> query) {
    return String.join(addSpaces(""), query);
  }

  /**
   * Render query string.
   *
   * @param query the query
   * @param token the token
   * @return the string
   */
  default String renderQuery(List<String> query, String token) {
    String queryString = String.join(token, query);
    return addSpaces(queryString);
  }

  /**
   * Add spaces string.
   *
   * @param query the query
   * @return the string
   */
  default String addSpaces(String query) {
    return StringUtils.SPACE + query + StringUtils.SPACE;
  }

  /**
   * Wrap string.
   *
   * @param query the query
   * @return the string
   */
  default String wrap(Object query) {
    return "( " + query + " )";
  }

  /**
   * Transform string.
   *
   * @param query the query
   * @param nativeQueryGeneratorContext the native query generator context
   * @return the string
   */
  default String transform(Object query, NativeQueryGeneratorContext nativeQueryGeneratorContext) {
    if (query instanceof IQuery) return ((IQuery) query).render(nativeQueryGeneratorContext);
    return String.valueOf(query);
  }

  /**
   * Should render boolean.
   *
   * @param object the object
   * @return the boolean
   */
  default boolean shouldRender(Object object) {
    return !Objects.isNull(object);
  }
}
