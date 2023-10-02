package open.source.typednativequery.metaentity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import javax.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;

/** The type Benchmark section. */
@Getter
@Setter
@NoArgsConstructor
@Accessors(fluent = true)
@Entity
public class BenchmarkSection implements Serializable {

  @EmbeddedId @JsonUnwrapped private BenchmarkSectionId id;

  @JsonProperty("name")
  @Column
  @NonNull
  private String name;

  @Column private String description;

  @JsonProperty("order")
  @Column
  private Integer orderId;

  @Column(updatable = false)
  @NonNull
  Instant createdAt;

  @Column(insertable = false)
  Instant updatedAt;

  @OneToMany(
      fetch = FetchType.LAZY,
      mappedBy = "ruleId.benchmarkSection",
      cascade = {CascadeType.ALL})
  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  @Setter
  private List<Rule> rules;

  @PrePersist
  public void prePersist() {
    this.createdAt = Instant.now();
  }

  @PreUpdate
  public void preUpdate() {
    this.updatedAt = Instant.now();
  }

  @Column(name = "parent_id")
  private String parentId;

  @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  @JoinColumns({
    @JoinColumn(
        name = "benchmark_metadata_id",
        referencedColumnName = "benchmark_metadata_id",
        updatable = false,
        insertable = false),
    @JoinColumn(
        name = "parent_id",
        referencedColumnName = "id",
        updatable = false,
        insertable = false)
  })
  @JsonBackReference
  private BenchmarkSection parent;

  @OneToMany(fetch = FetchType.EAGER, mappedBy = "parent", cascade = CascadeType.ALL)
  private List<BenchmarkSection> sections;

  public BenchmarkSection parent(final BenchmarkSection parent) {
    this.parent = parent;
    parentId = parent != null ? parent.id().sectionId() : null;
    return this;
  }

  public String parentId() {
    return parent != null ? parent.id.sectionId() : null;
  }
}
