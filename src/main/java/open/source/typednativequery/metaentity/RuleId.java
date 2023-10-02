package open.source.typednativequery.metaentity;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import javax.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

/** The type Rule id. */
@Accessors(fluent = true)
@EqualsAndHashCode
@Getter
@NoArgsConstructor
@Setter
@Embeddable
// @TableCompositeId
public class RuleId implements Serializable {

  @Column(name = "id")
  @JsonProperty("guid")
  private String id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumns({
    @JoinColumn(name = "section_id", referencedColumnName = "id"),
    @JoinColumn(name = "benchmark_metadata_id", referencedColumnName = "benchmark_metadata_id")
  })
  private BenchmarkSection benchmarkSection;
}
