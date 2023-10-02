package open.source.typednativequery.core;

import open.source.typednativequery.expressions.StringFunctionExpression;
import open.source.typednativequery.metaentity.Table;

/**
 * The type Custom query parameter.
 *
 * @param <C> the type parameter
 */
public class CustomQueryParameter<C> implements QueryParameter, StringFunctionExpression<Table> {

  private String key;
  private final C value;

  /**
   * Instantiates a new Custom query parameter.
   *
   * @param param the param
   */
  public CustomQueryParameter(C param) {
    this.value = param;
  }

  @Override
  public String render(NativeQueryGeneratorContext nativeQueryGeneratorContext) {
    if (ValueHandlerFactory.isNumeric(this.value)) {
      return String.format("%s", this.value);
    }
    this.key = registerParameter(nativeQueryGeneratorContext);
    return String.format(":%s", this.key);
  }

  @Override
  public String registerParameter(NativeQueryGeneratorContext nativeQueryGeneratorContext) {
    return nativeQueryGeneratorContext.parameter(this);
  }

  @Override
  public String getParameterKey() {
    return this.key;
  }

  @Override
  public C getParameterValue() {
    return this.value;
  }

  @Override
  public String toString() {
    return "CustomQueryParameter{" + "name='" + key + '\'' + ", param=" + value + '}';
  }
}
