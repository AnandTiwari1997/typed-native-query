package open.source.typednativequery.metaentity;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.time.Instant;
import javax.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.experimental.Accessors;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(
    fieldVisibility = JsonAutoDetect.Visibility.ANY,
    getterVisibility = JsonAutoDetect.Visibility.NONE,
    setterVisibility = JsonAutoDetect.Visibility.NONE)
@Entity
@Data
@NoArgsConstructor
@Accessors(fluent = true)
public class OsMetadata implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @NonNull
  private Long id;

  @JsonProperty(value = "os_type")
  @NonNull
  @Enumerated(EnumType.STRING)
  //    @EqualsAndHashCode.Include
  private OSTypeDto osType;

  @JsonProperty(value = "os_name")
  @NonNull
  @Column
  private String osName;

  @JsonProperty(value = "cis_version")
  @Transient
  private String cisVersion;

  @Column(updatable = false)
  private Instant createdAt;

  @Column(insertable = false)
  private Instant updatedAt;

  @PrePersist
  public void prePersist() {
    this.createdAt = Instant.now();
  }

  @PreUpdate
  public void preUpdate() {
    this.updatedAt = Instant.now();
  }

  public String getUniqueKeyForOsMetadata() {
    return OsMetadata.getUniqueKeyForOsMetadata(this.osName, this.osType);
  }

  public static String getUniqueKeyForOsMetadata(String osName, OSTypeDto osType) {
    return osName + "__" + osType;
  }
}
