package open.source.typednativequery.function.arithmatic;

import java.util.ArrayList;
import java.util.List;
import open.source.typednativequery.core.IQuery;
import open.source.typednativequery.core.NativeQueryGeneratorContext;
import open.source.typednativequery.core.NativeQuerySelect;
import open.source.typednativequery.metaentity.Table;

/**
 * The type Count function.
 *
 * @param <T> the type parameter
 * @param <R> the type parameter
 */
public class CountFunction<T extends Table, R extends Number>
    implements IQuery, AggregateFunction<T, CountFunction<T, R>, R> {

  private final NativeQuerySelect<T, ?, R> select;
  private String alias;

  /**
   * Instantiates a new Count function.
   *
   * @param select the select
   */
  public <C> CountFunction(NativeQuerySelect<T, ?, R> select) {
    this.select = select;
  }

  @Override
  public CountFunction<T, R> as(String alias) {
    this.alias = alias;
    return this;
  }

  @Override
  public String render(NativeQueryGeneratorContext nativeQueryGeneratorContext) {
    List<String> tokens = new ArrayList<>();
    tokens.add("COUNT");
    tokens.add(wrap(this.select));
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
      this.as(String.format(nativeQueryGeneratorContext.generateAlias("count")));
    }
  }
}
