package open.source.typednativequery.columns;

import java.util.Map;
import open.source.typednativequery.metaentity.Table;

/**
 * The type Map native column.
 *
 * @param <T> the type parameter
 * @param <K> the type parameter
 * @param <V> the type parameter
 */
public class MapNativeColumn<T extends Table, K, V> extends NativeQueryColumn<T, Map<K, V>> {

  /**
   * Instantiates a new Map native column.
   *
   * @param name the name
   * @param keyType the key type
   * @param valueType the value type
   */
  @SuppressWarnings("unchecked")
  public MapNativeColumn(String name, Class<K> keyType, Class<V> valueType) {
    super(name, (Class<Map<K, V>>) keyType);
  }
}
