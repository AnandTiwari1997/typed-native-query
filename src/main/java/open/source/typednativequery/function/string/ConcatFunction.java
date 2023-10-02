package open.source.typednativequery.function.string;

import java.util.ArrayList;
import java.util.List;
import open.source.typednativequery.core.IQuery;
import open.source.typednativequery.core.NativeQueryGeneratorContext;
import open.source.typednativequery.expressions.StringFunctionExpression;
import open.source.typednativequery.function.Function;
import open.source.typednativequery.metaentity.Table;

/**
 * The type Concat function.
 *
 * @param <T> the type parameter
 */
public class ConcatFunction<T extends Table>
    implements IQuery, Function<T, ConcatFunction<T>, String>, StringFunctionExpression<T> {

  private final IQuery leftParam;
  private final IQuery rightParam;
  private String alias;

  /**
   * Instantiates a new Concat function.
   *
   * @param leftParam the left param
   * @param rightParam the right param
   */
  public ConcatFunction(IQuery leftParam, IQuery rightParam) {
    this.leftParam = leftParam;
    this.rightParam = rightParam;
  }

  @Override
  public String render(NativeQueryGeneratorContext nativeQueryGeneratorContext) {
    List<String> tokens = new ArrayList<>();
    tokens.add("CONCAT");
    tokens.add(
        wrap(
            renderQuery(
                List.of(
                    this.leftParam.render(nativeQueryGeneratorContext),
                    this.rightParam.render(nativeQueryGeneratorContext)),
                ", ")));
    if (nativeQueryGeneratorContext.isShouldRenderAlias(this.getAs())) {
      tokens.add("AS");
      tokens.add(this.getAs());
    }
    return renderQuery(tokens);
  }

  @Override
  public void prepareAlias(NativeQueryGeneratorContext nativeQueryGeneratorContext) {
    if (!nativeQueryGeneratorContext.isRenderAble(this.getAs())) {
      this.as(String.format(nativeQueryGeneratorContext.generateAlias("concat")));
    }
  }

  @Override
  public ConcatFunction<T> as(String alias) {
    this.alias = alias;
    return this;
  }

  @Override
  public String getAs() {
    return this.alias;
  }

  @Override
  public String getName() {
    return this.getAs();
  }

  @Override
  public Class<String> getResultType() {
    return String.class;
  }
}
