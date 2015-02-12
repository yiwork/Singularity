package com.hubspot.singularity.auth;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties( ignoreUnknown = true )
public class IdTokenValue {
  private final String email;

  @JsonCreator
  public IdTokenValue(@JsonProperty("email") String email) {
    this.email = email;
  }

  public String getEmail() {
    return email;
  }
}
