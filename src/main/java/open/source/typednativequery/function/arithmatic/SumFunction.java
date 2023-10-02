package open.source.typednativequery.function.arithmatic;

import java.util.ArrayList;
import java.util.List;
import open.source.typednativequery.core.IQuery;
import open.source.typednativequery.core.NativeQueryGeneratorContext;
import open.source.typednativequery.core.NativeQuerySelect;
import open.source.typednativequery.function.window.WindowFunction;
import open.source.typednativequery.metaentity.Table;

/**
 * The type Sum function.
 *
 * @param <T> the type parameter
 * @param <R> the type parameter
 */
public class SumFunction<T extends Table, R extends Number>
    extends WindowFunction<T, SumFunction<T, R>, R> implements IQuery {

  private final NativeQuerySelect<T, ?, R> iQuery;
  private String alias;

  /**
   * Instantiates a new Sum function.
   *
   * @param iQuery the query
   */
  public SumFunction(NativeQuerySelect<T, ?, R> iQuery) {
    this.iQuery = iQuery;
  }

  @Override
  public SumFunction<T, R> as(String alias) {
    this.alias = alias;
    return this;
  }

  @Override
  public String render(NativeQueryGeneratorContext nativeQueryGeneratorContext) {
    List<String> tokens = new ArrayList<>();
    tokens.add("SUM");
    tokens.add(wrap(this.iQuery.render(nativeQueryGeneratorContext)));
    if (nativeQueryGeneratorContext.isShouldRenderAlias(this.getAs())) {
      tokens.add("AS");
      tokens.add(this.getAs());
    }
    return renderQuery(tokens);
  }

  @Override
  public String getAs() {
    return this.alias;
  }

  @Override
  public void prepareAlias(NativeQueryGeneratorContext nativeQueryGeneratorContext) {
    if (!nativeQueryGeneratorContext.isRenderAble(this.getAs())) {
      this.as(String.format(nativeQueryGeneratorContext.generateAlias("sum")));
    }
  }

  @Override
  public String getName() {
    return this.alias;
  }

  @Override
  public SumFunction<T, R> get() {
    return this;
  }

  @Override
  public SumFunction<T, R> over() {
    return super.over(this);
  }
}
