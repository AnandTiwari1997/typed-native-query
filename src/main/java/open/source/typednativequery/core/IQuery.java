package open.source.typednativequery.core;

/** The interface Query. */
public interface IQuery extends NativeQueryRenderer {
  /**
   * Render string.
   *
   * @param nativeQueryGeneratorContext the native query generator context
   * @return the string
   */
  String render(NativeQueryGeneratorContext nativeQueryGeneratorContext);
}
