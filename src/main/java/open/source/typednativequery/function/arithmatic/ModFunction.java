package open.source.typednativequery.function.arithmatic;

import java.util.ArrayList;
import java.util.List;
import open.source.typednativequery.columns.NativeQueryColumn;
import open.source.typednativequery.core.IQuery;
import open.source.typednativequery.core.NativeQueryGeneratorContext;
import open.source.typednativequery.function.Function;
import open.source.typednativequery.metaentity.Table;

/**
 * The type Mod function.
 *
 * @param <T> the type parameter
 * @param <R> the type parameter
 */
public class ModFunction<T extends Table, R extends Number>
    implements IQuery, Function<T, ModFunction<T, R>, R> {

  private final NativeQueryColumn<T, R> select;
  private String alias;

  /**
   * Instantiates a new Mod function.
   *
   * @param select the select
   */
  public ModFunction(NativeQueryColumn<T, R> select) {
    this.select = select;
  }

  @Override
  public String render(NativeQueryGeneratorContext nativeQueryGeneratorContext) {
    List<String> tokens = new ArrayList<>();
    tokens.add("MOD");
    tokens.add(wrap(this.select));
    if (nativeQueryGeneratorContext.isShouldRenderAlias(this.getAs())) {
      tokens.add("AS");
      tokens.add(this.getAs());
    }
    return renderQuery(tokens);
  }

  @Override
  public void prepareAlias(NativeQueryGeneratorContext nativeQueryGeneratorContext) {
    if (!nativeQueryGeneratorContext.isRenderAble(this.getAs())) {
      this.as(String.format(nativeQueryGeneratorContext.generateAlias("mod")));
    }
  }

  @Override
  public ModFunction<T, R> as(String alias) {
    this.alias = alias;
    return this;
  }

  @Override
  public String getAs() {
    return this.alias;
  }
}
