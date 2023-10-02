package open.source.typednativequery.core;

/**
 * The interface Query alias.
 *
 * @param <T> the type parameter
 */
public interface QueryAlias<T> {
  /**
   * Prepare alias.
   *
   * @param nativeQueryGeneratorContext the native query generator context
   */
  void prepareAlias(NativeQueryGeneratorContext nativeQueryGeneratorContext);

  /**
   * As t.
   *
   * @param alias the alias
   * @return the t
   */
  T as(String alias);

  /**
   * Gets as.
   *
   * @return the as
   */
  String getAs();
}
