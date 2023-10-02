package open.source.typednativequery.core;

import java.util.Set;

/** The interface Native selection. */
public interface INativeSelection extends IQuery {

  /**
   * Gets selection.
   *
   * @return the selection
   */
  Set<NativeQuerySelect<?, ?, ?>> getSelection();
}
