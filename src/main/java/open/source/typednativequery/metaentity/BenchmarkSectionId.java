package open.source.typednativequery.metaentity;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import javax.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;

/** The type Benchmark section id. */
@Accessors(fluent = true)
@Embeddable
@EqualsAndHashCode
@Getter
@NoArgsConstructor
@Setter
// @TableCompositeId
public class BenchmarkSectionId implements Serializable {

  @Column(name = "id")
  @JsonProperty("guid")
  @NonNull
  private String sectionId;

  @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, optional = false)
  @JoinColumn(name = "benchmark_metadata_id", updatable = false, insertable = false)
  @NonNull
  private BenchmarkMetadata benchmarkMetadata;
}
