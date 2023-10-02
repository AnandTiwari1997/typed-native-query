package open.source.typednativequery.function.arithmatic;

import java.util.ArrayList;
import java.util.List;
import open.source.typednativequery.core.*;
import open.source.typednativequery.function.window.WindowFunction;
import open.source.typednativequery.metaentity.Table;

/**
 * The type Avg function.
 *
 * @param <T> the type parameter
 * @param <R> the type parameter
 */
public class AvgFunction<T extends Table, R extends Number>
    extends WindowFunction<T, AvgFunction<T, R>, R> implements IQuery {

  private final NativeQuerySelect<T, ?, R> select;
  private String alias;

  /**
   * Instantiates a new Avg function.
   *
   * @param select the select
   */
  public AvgFunction(NativeQuerySelect<T, ?, R> select) {
    this.select = select;
  }

  @Override
  public String render(NativeQueryGeneratorContext nativeQueryGeneratorContext) {
    List<String> tokens = new ArrayList<>();
    tokens.add("AVG");
    tokens.add(wrap(this.select.render(nativeQueryGeneratorContext)));
    if (nativeQueryGeneratorContext.isShouldRenderAlias(this.getAs())) {
      tokens.add("AS");
      tokens.add(this.getAs());
    }
    return renderQuery(tokens);
  }

  @Override
  public void prepareAlias(NativeQueryGeneratorContext nativeQueryGeneratorContext) {
    if (!nativeQueryGeneratorContext.isRenderAble(this.getAs())) {
      this.as(String.format(nativeQueryGeneratorContext.generateAlias("average")));
    }
  }

  @Override
  public AvgFunction<T, R> as(String alias) {
    this.alias = alias;
    return this;
  }

  @Override
  public AvgFunction<T, R> over() {
    return super.over(this);
  }

  @Override
  public String getAs() {
    return this.alias;
  }

  @Override
  public String getName() {
    return this.alias;
  }

  @Override
  public AvgFunction<T, R> get() {
    return this;
  }
}
