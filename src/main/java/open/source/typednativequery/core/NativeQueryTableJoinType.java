package open.source.typednativequery.core;

/** The enum Native query table join type. */
public enum NativeQueryTableJoinType {
  /** Join native query table join type. */
  JOIN("JOIN"),
  /** The Inner join. */
  INNER_JOIN("INNER JOIN"),
  /** The Left join. */
  LEFT_JOIN("LEFT JOIN"),
  /** The Right join. */
  RIGHT_JOIN("RIGHT JOIN"),
  /** The Full join. */
  FULL_JOIN("FULL JOIN");

  private final String joinName;

  NativeQueryTableJoinType(String joinName) {
    this.joinName = joinName;
  }

  /**
   * Gets join name.
   *
   * @return the join name
   */
  public String getJoinName() {
    return this.joinName;
  }
}
