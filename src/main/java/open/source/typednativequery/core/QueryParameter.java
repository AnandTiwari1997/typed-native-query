package open.source.typednativequery.core;

/** The interface Query parameter. */
public interface QueryParameter extends IQuery {
  /**
   * Register parameter string.
   *
   * @param nativeQueryGeneratorContext the native query generator context
   * @return the string
   */
  String registerParameter(NativeQueryGeneratorContext nativeQueryGeneratorContext);

  /**
   * Gets parameter key.
   *
   * @return the parameter key
   */
  String getParameterKey();

  /**
   * Gets parameter value.
   *
   * @return the parameter value
   */
  Object getParameterValue();
}
