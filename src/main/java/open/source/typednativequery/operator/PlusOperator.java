package open.source.typednativequery.operator;

import java.util.ArrayList;
import java.util.List;
import open.source.typednativequery.core.IQuery;
import open.source.typednativequery.core.NativeQueryGeneratorContext;
import open.source.typednativequery.core.NativeQuerySelect;
import open.source.typednativequery.core.QueryAlias;
import open.source.typednativequery.expressions.MathFunctionExpression;
import open.source.typednativequery.metaentity.Table;

/**
 * The type Plus operator.
 *
 * @param <T> the type parameter
 * @param <C> the type parameter
 */
public class PlusOperator<T extends Table, C extends Number>
    implements IQuery,
        QueryAlias<PlusOperator<T, C>>,
        NativeQuerySelect<T, PlusOperator<T, C>, C>,
        MathFunctionExpression<T, C> {

  private final IQuery leftEndSide;
  private final IQuery rightEndSide;
  private String alias;

  /**
   * Instantiates a new Plus operator.
   *
   * @param leftEndSide the left end side
   * @param rightEndSide the right end side
   */
  public PlusOperator(IQuery leftEndSide, IQuery rightEndSide) {
    this.leftEndSide = leftEndSide;
    this.rightEndSide = rightEndSide;
  }

  @Override
  public String render(NativeQueryGeneratorContext nativeQueryGeneratorContext) {
    List<String> tokens = new ArrayList<>();
    tokens.add(this.leftEndSide.render(nativeQueryGeneratorContext));
    tokens.add("+");
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
      this.as(nativeQueryGeneratorContext.generateAlias("plus"));
    }
  }

  @Override
  public PlusOperator<T, C> as(String alias) {
    this.alias = alias;
    return this;
  }

  @Override
  public String getAs() {
    return this.alias;
  }

  @Override
  public Class<C> getResultType() {
    return (Class<C>) Number.class;
  }

  @Override
  public String getName() {
    return this.getAs();
  }
}
