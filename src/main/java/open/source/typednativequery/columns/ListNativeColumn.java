package open.source.typednativequery.columns;

import java.util.ArrayList;
import java.util.List;
import open.source.typednativequery.condition.Condition;
import open.source.typednativequery.core.CustomQueryParameter;
import open.source.typednativequery.core.IQuery;
import open.source.typednativequery.core.NamedQueryParameter;
import open.source.typednativequery.core.NativeQueryGeneratorContext;
import open.source.typednativequery.metaentity.Table;

/**
 * The type List native column.
 *
 * @param <T> the type parameter
 * @param <C> the type parameter
 */
public class ListNativeColumn<T extends Table, C> extends NativeQueryColumn<T, List<C>> {

  /**
   * Instantiates a new List native column.
   *
   * @param name the name
   * @param javaType the java type
   */
  @SuppressWarnings("unchecked")
  public ListNativeColumn(String name, Class<C> javaType) {
    super(name, (Class<List<C>>) javaType, true);
  }

  /**
   * Contains condition.
   *
   * @param value the value
   * @return the condition
   */
  public Condition<C> contains(C value) {
    return new Condition<>() {
      @Override
      public C not() {
        return null;
      }

      @Override
      public String render(NativeQueryGeneratorContext nativeQueryGeneratorContext) {
        List<String> tokens = new ArrayList<>();
        tokens.add(ListNativeColumn.super.render(nativeQueryGeneratorContext));
        tokens.add("?");
        tokens.add((new CustomQueryParameter<C>(value)).render(nativeQueryGeneratorContext));
        return renderQuery(tokens);
      }
    };
  }

  /**
   * Contains condition.
   *
   * @param value the value
   * @return the condition
   */
  public Condition<C> contains(CustomQueryParameter<C> value) {
    return getCondition(value);
  }

  /**
   * Contains condition.
   *
   * @param value the value
   * @return the condition
   */
  public Condition<C> contains(NamedQueryParameter value) {
    return getCondition(value);
  }

  private Condition<C> getCondition(IQuery value) {
    return new Condition<>() {
      @Override
      public C not() {
        return null;
      }

      @Override
      public String render(NativeQueryGeneratorContext nativeQueryGeneratorContext) {
        List<String> tokens = new ArrayList<>();
        tokens.add(ListNativeColumn.super.render(nativeQueryGeneratorContext));
        tokens.add("?");
        tokens.add(value.render(nativeQueryGeneratorContext));
        return renderQuery(tokens);
      }
    };
  }
}
