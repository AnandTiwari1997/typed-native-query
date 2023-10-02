package open.source.typednativequery.metaentity;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import javax.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Type;

/** The type Benchmark metadata. */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(
    fieldVisibility = JsonAutoDetect.Visibility.ANY,
    getterVisibility = JsonAutoDetect.Visibility.NONE,
    setterVisibility = JsonAutoDetect.Visibility.NONE)
@Getter
@Setter
@Entity
@Data
@NoArgsConstructor
@Accessors(fluent = true)
public class BenchmarkMetadata implements Serializable {

  @Id @NonNull private String id;

  @Column
  @NonNull
  @Enumerated(EnumType.STRING)
  @JsonProperty(value = "os_family")
  private OsFamily osFamily;

  @Column(updatable = false)
  @NonNull
  private Instant createdAt;

  @Column(insertable = false)
  @NonNull
  private Instant updatedAt;

  @Column
  @NonNull
  @JsonProperty(value = "revision")
  private Integer versionBuild;

  @Column
  @NonNull
  @JsonProperty(value = "major_version")
  private Integer versionMajor;

  @Column
  @NonNull
  @JsonProperty(value = "minor_version")
  private Integer versionMinor;

  @Column
  @NonNull
  @JsonProperty(value = "patch")
  private Integer versionPatch;

  @Column @NonNull private Long cmsSegmentId;

  @Column @NonNull private String architecture;

  @Column
  @NonNull
  @JsonProperty(value = "guid")
  private String policyId;

  @Column
  @NonNull
  @JsonProperty(value = "segment_type")
  private String cmsSegmentType;

  @Column(name = "segment_urn")
  @NonNull
  @JsonProperty(value = "segment_urn")
  private String cmsSegmentUrn;

  @Column @NonNull private String distribution;

  @Column @NonNull private String name;

  @JsonProperty(value = "platform_type")
  @Column
  @NonNull
  private String platform;

  @Column(columnDefinition = "jsonb")
  @NonNull
  @Type(type = "jsonb")
  private Map<Long, String> supportedOs;

  @JsonProperty(value = "cis_version_info")
  @Transient
  private List<OsMetadata> cisVersionInfos;

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "id.benchmarkMetadata", cascade = CascadeType.ALL)
  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  @Setter
  private List<BenchmarkSection> sections;

  @Column(name = "manifest_uuid")
  @JsonProperty(value = "manifest_uuid")
  @NonNull
  private String manifestUUID;

  @Column(name = "min_supported_sensor", columnDefinition = "jsonb")
  @NonNull
  @Type(type = "jsonb")
  private Map<DeploymentType, String> minSupportedSensorVersion;

  @JsonProperty(value = "supported_minimum_sensor_version")
  @Transient
  private List<SupportedDeploymentSensorVersion> minSupportedSensor;

  public String formattedVersion() {
    return String.format(
        "%s.%s.%s.%s",
        this.versionMajor(), this.versionMinor(), this.versionPatch(), this.versionBuild());
  }

  @PrePersist
  public void prePersist() {
    this.createdAt = Instant.now();
  }

  @PreUpdate
  public void preUpdate() {
    this.updatedAt = Instant.now();
  }
}
