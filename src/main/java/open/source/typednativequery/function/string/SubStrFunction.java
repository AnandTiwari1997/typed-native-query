package open.source.typednativequery.function.string;

import java.util.ArrayList;
import java.util.List;
import open.source.typednativequery.core.IQuery;
import open.source.typednativequery.core.NativeQueryGeneratorContext;
import open.source.typednativequery.expressions.StringFunctionExpression;
import open.source.typednativequery.function.Function;
import open.source.typednativequery.metaentity.Table;

/**
 * The type Sub str function.
 *
 * @param <T> the type parameter
 */
public class SubStrFunction<T extends Table>
    implements IQuery, Function<T, SubStrFunction<T>, String>, StringFunctionExpression<T> {

  private final IQuery select;
  private final int from;
  private final int length;
  private String alias;

  /**
   * Instantiates a new Sub str function.
   *
   * @param select the select
   * @param from the from
   * @param length the length
   */
  public SubStrFunction(IQuery select, int from, int length) {
    this.select = select;
    this.from = from;
    this.length = length;
  }

  @Override
  public String render(NativeQueryGeneratorContext nativeQueryGeneratorContext) {
    List<String> tokens = new ArrayList<>();
    tokens.add("SUBSTR");
    List<String> newTokens = new ArrayList<>();
    newTokens.add(this.select.render(nativeQueryGeneratorContext));
    newTokens.add(String.valueOf(this.from));
    newTokens.add(String.valueOf(this.length));
    tokens.add(wrap(renderQuery(newTokens, ", ")));
    if (nativeQueryGeneratorContext.isShouldRenderAlias(this.getAs())) {
      tokens.add("AS");
      tokens.add(this.getAs());
    }
    return renderQuery(tokens);
  }

  @Override
  public void prepareAlias(NativeQueryGeneratorContext nativeQueryGeneratorContext) {
    if (!nativeQueryGeneratorContext.isRenderAble(this.getAs())) {
      this.as(String.format(nativeQueryGeneratorContext.generateAlias("substr")));
    }
  }

  @Override
  public SubStrFunction<T> as(String alias) {
    this.alias = alias;
    return this;
  }

  @Override
  public String getAs() {
    return this.alias;
  }
}
