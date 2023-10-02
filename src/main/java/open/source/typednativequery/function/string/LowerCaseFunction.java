package open.source.typednativequery.function.string;

import java.util.ArrayList;
import java.util.List;
import open.source.typednativequery.core.IQuery;
import open.source.typednativequery.core.NativeQueryGeneratorContext;
import open.source.typednativequery.expressions.StringFunctionExpression;
import open.source.typednativequery.function.Function;
import open.source.typednativequery.metaentity.Table;

/**
 * The type Lower case function.
 *
 * @param <T> the type parameter
 */
public class LowerCaseFunction<T extends Table>
    implements IQuery, Function<T, LowerCaseFunction<T>, String>, StringFunctionExpression<T> {

  private final IQuery select;
  private String alias;

  /**
   * Instantiates a new Lower case function.
   *
   * @param select the select
   */
  public LowerCaseFunction(IQuery select) {
    this.select = select;
  }

  @Override
  public String render(NativeQueryGeneratorContext nativeQueryGeneratorContext) {
    List<String> tokens = new ArrayList<>();
    tokens.add("LOWER");
    tokens.add(wrap(select));
    if (nativeQueryGeneratorContext.isShouldRenderAlias(this.alias)) {
      tokens.add("AS");
      tokens.add(this.alias);
    }
    return renderQuery(tokens);
  }

  @Override
  public void prepareAlias(NativeQueryGeneratorContext nativeQueryGeneratorContext) {
    if (!nativeQueryGeneratorContext.isRenderAble(this.getAs())) {
      this.as(String.format(nativeQueryGeneratorContext.generateAlias("lower")));
    }
  }

  @Override
  public LowerCaseFunction<T> as(String alias) {
    this.alias = alias;
    return this;
  }

  @Override
  public String getAs() {
    return this.alias;
  }
}
