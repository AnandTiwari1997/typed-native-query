package open.source.typednativequery.metaentity;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.experimental.Accessors;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(
    fieldVisibility = JsonAutoDetect.Visibility.ANY,
    getterVisibility = JsonAutoDetect.Visibility.NONE,
    setterVisibility = JsonAutoDetect.Visibility.NONE)
@Data
@NoArgsConstructor
@Accessors(fluent = true)
public class SupportedDeploymentSensorVersion {

  @JsonProperty(value = "deployment_type")
  @NonNull
  @Enumerated(EnumType.STRING)
  private DeploymentType deploymentType;

  @JsonProperty(value = "sensor_version")
  @NonNull
  private String sensorVersion;
}
