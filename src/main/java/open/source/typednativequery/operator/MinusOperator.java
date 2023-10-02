package open.source.typednativequery.operator;

import java.util.ArrayList;
import java.util.List;
import open.source.typednativequery.core.IQuery;
import open.source.typednativequery.core.NativeQueryGeneratorContext;
import open.source.typednativequery.core.QueryAlias;
import open.source.typednativequery.expressions.MathFunctionExpression;
import open.source.typednativequery.metaentity.Table;

/**
 * The type Minus operator.
 *
 * @param <T> the type parameter
 * @param <C> the type parameter
 */
public class MinusOperator<T extends Table, C extends Number>
    implements IQuery, QueryAlias<MinusOperator<T, C>>, MathFunctionExpression<T, C> {

  private final IQuery leftEndSide;
  private final IQuery rightEndSide;
  private String alias;

  /**
   * Instantiates a new Minus operator.
   *
   * @param leftEndSide the left end side
   * @param rightEndSide the right end side
   */
  public MinusOperator(IQuery leftEndSide, IQuery rightEndSide) {
    this.leftEndSide = leftEndSide;
    this.rightEndSide = rightEndSide;
  }

  @Override
  public String render(NativeQueryGeneratorContext nativeQueryGeneratorContext) {
    List<String> tokens = new ArrayList<>();
    tokens.add(this.leftEndSide.render(nativeQueryGeneratorContext));
    tokens.add("-");
    tokens.add(this.rightEndSide.render(nativeQueryGeneratorContext));
    if (nativeQueryGeneratorContext.isShouldRenderAlias(this.getAs())) {
      tokens.add("AS");
      tokens.add(this.getAs());
    }
    return renderQuery(tokens);
  }

  @Override
  public void prepareAlias(NativeQueryGeneratorContext nativeQueryGeneratorContext) {
    if (!nativeQueryGeneratorContext.isRenderAble(this.getAs())) {
      this.as(nativeQueryGeneratorContext.generateAlias("minus"));
    }
  }

  @Override
  public MinusOperator as(String alias) {
    this.alias = alias;
    return this;
  }

  @Override
  public String getAs() {
    return this.alias;
  }
}
