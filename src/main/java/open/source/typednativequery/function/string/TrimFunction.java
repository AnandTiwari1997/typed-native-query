package open.source.typednativequery.function.string;

import java.util.ArrayList;
import java.util.List;
import open.source.typednativequery.core.IQuery;
import open.source.typednativequery.core.NativeQueryGeneratorContext;
import open.source.typednativequery.expressions.StringFunctionExpression;
import open.source.typednativequery.function.Function;
import open.source.typednativequery.metaentity.Table;

/**
 * The type Trim function.
 *
 * @param <T> the type parameter
 */
public class TrimFunction<T extends Table>
    implements IQuery, Function<T, TrimFunction<T>, String>, StringFunctionExpression<T> {

  private final IQuery select;
  private IQuery from;
  private StringFunctionExpression.TrimType trimType;
  private String alias;

  /**
   * Instantiates a new Trim function.
   *
   * @param select the select
   */
  public TrimFunction(IQuery select) {
    this.select = select;
  }

  /**
   * Instantiates a new Trim function.
   *
   * @param select the select
   * @param from the from
   */
  public TrimFunction(IQuery select, IQuery from) {
    this.select = select;
    this.from = from;
  }

  /**
   * Instantiates a new Trim function.
   *
   * @param select the select
   * @param trimType the trim type
   */
  public TrimFunction(IQuery select, StringFunctionExpression.TrimType trimType) {
    this.select = select;
    this.trimType = trimType;
  }

  /**
   * Instantiates a new Trim function.
   *
   * @param select the select
   * @param from the from
   * @param trimType the trim type
   */
  public TrimFunction(IQuery select, IQuery from, StringFunctionExpression.TrimType trimType) {
    this.select = select;
    this.from = from;
    this.trimType = trimType;
  }

  @Override
  public String render(NativeQueryGeneratorContext nativeQueryGeneratorContext) {
    List<String> tokens = new ArrayList<>();
    tokens.add("TRIM");
    List<String> newTokens = new ArrayList<>();
    if (shouldRender(this.trimType)) {
      newTokens.add(this.trimType.name());
    }
    if (shouldRender(this.from)) {
      newTokens.add(this.from.render(nativeQueryGeneratorContext));
      newTokens.add("FROM");
    }
    newTokens.add(this.select.render(nativeQueryGeneratorContext));
    tokens.add(wrap(renderQuery(newTokens)));
    if (nativeQueryGeneratorContext.isShouldRenderAlias(this.getAs())) {
      tokens.add("AS");
      tokens.add(this.getAs());
    }
    return renderQuery(tokens);
  }

  @Override
  public void prepareAlias(NativeQueryGeneratorContext nativeQueryGeneratorContext) {
    if (!nativeQueryGeneratorContext.isRenderAble(this.getAs())) {
      this.as(String.format(nativeQueryGeneratorContext.generateAlias("trim")));
    }
  }

  @Override
  public TrimFunction<T> as(String alias) {
    this.alias = alias;
    return this;
  }

  @Override
  public String getAs() {
    return this.alias;
  }
}
