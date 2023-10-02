package open.source.typednativequery.metaentity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/** Gets or Sets OSType */
public enum OSTypeDto {
  WINDOWS("WINDOWS"),
  SLES("SLES"),
  CENTOS("CENTOS"),
  UBUNTU("UBUNTU"),
  RHEL("RHEL"),
  SUSE("SUSE"),
  AMAZON_LINUX("AMAZON_LINUX"),
  ORACLE("ORACLE"),
  INVALID_OS_TYPE("INVALID_OS_TYPE"),
  OTHER("OTHER"),
  LINUX("LINUX"),
  SDDC("SDDC");

  private String value;

  OSTypeDto(String value) {
    this.value = value;
  }

  @Override
  @JsonValue
  public String toString() {
    return String.valueOf(value);
  }

  @JsonCreator
  public static OSTypeDto fromValue(String text) {
    for (OSTypeDto b : OSTypeDto.values()) {
      if (String.valueOf(b.value).equals(text)) {
        return b;
      }
    }
    return null;
  }
}
