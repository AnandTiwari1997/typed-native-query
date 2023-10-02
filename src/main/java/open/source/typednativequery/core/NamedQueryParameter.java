package open.source.typednativequery.core;

/** The type Named query parameter. */
public class NamedQueryParameter implements QueryParameter {

  private final String key;

  /**
   * Instantiates a new Named query parameter.
   *
   * @param parameterKey the parameter key
   */
  public NamedQueryParameter(String parameterKey) {
    this.key = parameterKey;
  }

  @Override
  public String render(NativeQueryGeneratorContext nativeQueryGeneratorContext) {
    return String.format(":%s", this.key);
  }

  @Override
  public String registerParameter(NativeQueryGeneratorContext nativeQueryGeneratorContext) {
    // Do not register named parameter because value will be set by the query object.
    return null;
  }

  @Override
  public String getParameterKey() {
    return this.key;
  }

  @Override
  public Object getParameterValue() {
    return null;
  }

  @Override
  public String toString() {
    return "NamedQueryParameter{" + "key='" + this.key + '\'' + '}';
  }
}
