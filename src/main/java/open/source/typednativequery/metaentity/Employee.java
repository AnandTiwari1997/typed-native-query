package open.source.typednativequery.metaentity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import lombok.*;
import lombok.experimental.Accessors;

/** The type Employee. */
@Getter
@Setter
@RequiredArgsConstructor
// @TableBuilder(tableName = "employee", compositeColumnName = "id")
@Entity
@NoArgsConstructor
@Accessors(fluent = true)
public class Employee implements Serializable {

  //  @TableColumn(isCompositeId = true)
  @EmbeddedId private EmployeeId employeeId;

  //  @TableColumn(columnName = "first_name")
  @Column @NonNull private String firstName;

  //  @TableColumn(columnName = "last_name")
  @Column @NonNull private String lastName;

  //  @TableColumn(columnName = "salary")
  @Column @NonNull private double salary;

  //  @TableColumn(columnName = "is_manager")
  @Column @NonNull private boolean isManager;
}
