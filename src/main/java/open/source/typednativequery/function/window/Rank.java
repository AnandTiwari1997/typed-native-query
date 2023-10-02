package open.source.typednativequery.function.window;

import java.util.ArrayList;
import java.util.List;
import open.source.typednativequery.core.NativeQueryGeneratorContext;
import open.source.typednativequery.metaentity.Table;

/**
 * The type Rank.
 *
 * @param <T> the type parameter
 * @param <O> the type parameter
 */
public class Rank<T extends Table, O extends Number> extends WindowFunction<T, Rank<T, O>, O> {

  private String alias;

  @Override
  public String render(NativeQueryGeneratorContext nativeQueryGeneratorContext) {
    if (orderBy.isEmpty()) {
      throw new RuntimeException("ORDER BY is mandatory for RANK function");
    }
    List<String> tokens = new ArrayList<>();
    tokens.add("RANK()");
    tokens.add(super.render(nativeQueryGeneratorContext));
    if (nativeQueryGeneratorContext.isShouldRenderAlias(this.getAs())) {
      tokens.add("AS");
      tokens.add(this.getAs());
    }
    return renderQuery(tokens);
  }

  @Override
  public void prepareAlias(NativeQueryGeneratorContext nativeQueryGeneratorContext) {
    if (!nativeQueryGeneratorContext.isRenderAble(this.getAs())) {
      this.as(String.format(nativeQueryGeneratorContext.generateAlias("rank")));
    }
  }

  @Override
  public Rank<T, O> as(String alias) {
    this.alias = alias;
    return this;
  }

  @Override
  public String getAs() {
    return this.alias;
  }

  @Override
  public Rank<T, O> over() {
    return super.over(this);
  }

  @Override
  public String getName() {
    return this.alias;
  }
}
