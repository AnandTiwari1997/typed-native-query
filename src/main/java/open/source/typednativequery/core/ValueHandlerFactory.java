package open.source.typednativequery.core;

/** The type Value handler factory. */
class ValueHandlerFactory {
  /**
   * Is numeric boolean.
   *
   * @param object the object
   * @return the boolean
   */
  public static boolean isNumeric(Object object) {
    return object instanceof Number;
  }
}
