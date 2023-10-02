package open.source.typednativequery.metaentity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Type;
import org.hibernate.proxy.HibernateProxy;

/** The type Rule. */
@Setter
@Getter
@Accessors(fluent = true)
@NoArgsConstructor
@Entity
@ToString
@RequiredArgsConstructor
// @TableBuilder(tableName = "rule")
public class Rule implements Serializable {

  @EmbeddedId @JsonUnwrapped private RuleId ruleId;

  @Column private String additionalInformation;

  @Column private String auditProcedure;

  @Column private String cisControls;

  @Column private String description;

  @JsonProperty("impact_statement")
  @Column
  private String impact;

  @Type(type = "jsonb")
  @Column(columnDefinition = "jsonb")
  @NonNull
  private List<String> profiles;

  @JsonProperty("rationale_statement")
  @Column
  private String rationale;

  @Column private String reference;

  @JsonProperty("remediation")
  @Column
  private String remediationProcedure;

  @JsonProperty("title")
  @Column
  @NonNull
  private String ruleName;

  // Keys in Map would be the id ( primary Key ) for supported os. Value - Cis_Version as default.
  @Type(type = "jsonb")
  @Column(columnDefinition = "jsonb")
  @NonNull
  // This contains the Os_metadata_id to Cis_version mapping.
  private Map<Long, String> supportedOs;

  @JsonProperty("order")
  @Column
  @NonNull
  private Integer orderId;

  @JsonProperty(value = "cis_version_info")
  @Transient
  private List<OsMetadata> cisVersionInfos;

  @Column(name = "min_supported_sensor", columnDefinition = "jsonb")
  @NonNull
  @Type(type = "jsonb")
  private Map<DeploymentType, String> minSupportedSensorVersion;

  @JsonProperty(value = "supported_minimum_sensor_version")
  @Transient
  private List<SupportedDeploymentSensorVersion> minSupportedSensor;

  @Column(updatable = false)
  Instant createdAt;

  @Column(insertable = false)
  Instant updatedAt;

  @PrePersist
  public void prePersist() {
    this.createdAt = Instant.now();
  }

  @PreUpdate
  public void preUpdate() {
    this.updatedAt = Instant.now();
  }

  @Override
  public final boolean equals(Object o) {
    if (this == o) return true;
    if (o == null) return false;
    Class<?> oEffectiveClass =
        o instanceof HibernateProxy
            ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass()
            : o.getClass();
    Class<?> thisEffectiveClass =
        this instanceof HibernateProxy
            ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass()
            : this.getClass();
    if (thisEffectiveClass != oEffectiveClass) return false;
    Rule rule = (Rule) o;
    return ruleId != null && Objects.equals(ruleId, rule.ruleId);
  }

  @Override
  public final int hashCode() {
    return Objects.hash(ruleId);
  }
}
