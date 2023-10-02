package open.source.typednativequery.metaentity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;

/** The type Department. */
@Getter
@Setter
// @TableBuilder(tableName = "department", compositeColumnName = "id")
@Entity
@NoArgsConstructor
@Accessors(fluent = true)
public class Department implements Serializable {

  //  @TableColumn(columnName = "dept_id", isId = true)
  @Id
  @Column(name = "dept_id")
  private Long departmentId;

  //  @TableColumn(columnName = "dept_name")
  @Column @NonNull private String departmentName;

  //  @TableColumn(columnName = "no_of_people")
  @Column @NonNull private long noOfPeople;

  //  @TableColumn(columnName = "dept_revenue")
  @Column @NonNull private double revenue;
}
