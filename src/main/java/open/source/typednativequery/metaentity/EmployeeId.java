package open.source.typednativequery.metaentity;

import java.io.Serializable;
import javax.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;

/** The type Employee id. */
@Accessors(fluent = true)
@Embeddable
@EqualsAndHashCode
@Getter
@NoArgsConstructor
@Setter
// @TableCompositeId
public class EmployeeId implements Serializable {

  private static final long serialVersionUID = -2688019116421998134L;
  //  @TableColumn(columnName = "emp_id", isId = true)
  @Column(name = "emp_id")
  @NonNull
  private long employeeId;

  //  @TableColumn(columnName = "dept_id", reference = "departmentId")
  @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, optional = false)
  @JoinColumn(name = "dept_id", updatable = false, insertable = false)
  @NonNull
  private Department departmentId;
}
