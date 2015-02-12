package com.hubspot.singularity.auth;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Optional;

public class OAuthTokenResponse {
  private final String accessToken;
  private final String idToken;
  private final int expiresIn;
  private final String tokenType;
  private final Optional<String> refreshToken;

  @JsonCreator
  public OAuthTokenResponse(@JsonProperty("access_token") String accessToken,
                            @JsonProperty("id_token") String idToken,
                            @JsonProperty("expires_in") int expiresIn,
                            @JsonProperty("token_type") String tokenType,
                            @JsonProperty("refresh_token") Optional<String> refreshToken) {
    this.accessToken = accessToken;
    this.idToken = idToken;
    this.expiresIn = expiresIn;
    this.tokenType = tokenType;
    this.refreshToken = refreshToken;
  }

  public String getAccessToken() {
    return accessToken;
  }

  public String getIdToken() {
    return idToken;
  }

  public int getExpiresIn() {
    return expiresIn;
  }

  public String getTokenType() {
    return tokenType;
  }

  public Optional<String> getRefreshToken() {
    return refreshToken;
  }
}
