package com.hubspot.singularity.auth;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class DiscoveryDocument {
  private final String issuer;
  private final String authorizationEndpoint;
  private final String tokenEndpoint;
  private final String userinfoEndpoint;
  private final String jwksUri;
  private final List<String> responseTypesSupported;
  private final List<String> subjectTypesSupported;
  private final List<String> idTokenAlgValuesSupported;

  @JsonCreator
  public DiscoveryDocument(@JsonProperty("issuer") String issuer,
                           @JsonProperty("authorization_endpoint") String authorizationEndpoint,
                           @JsonProperty("token_endpoint") String tokenEndpoint,
                           @JsonProperty("userinfo_endpoint") String userinfoEndpoint,
                           @JsonProperty("jwks_uri") String jwksUri,
                           @JsonProperty("response_types_supported") List<String> responseTypesSupported,
                           @JsonProperty("subject_types_supported") List<String> subjectTypesSupported,
                           @JsonProperty("id_token_alg_values_supported") List<String> idTokenAlgValuesSupported) {
    this.issuer = issuer;
    this.authorizationEndpoint = authorizationEndpoint;
    this.tokenEndpoint = tokenEndpoint;
    this.userinfoEndpoint = userinfoEndpoint;
    this.jwksUri = jwksUri;
    this.responseTypesSupported = responseTypesSupported;
    this.subjectTypesSupported = subjectTypesSupported;
    this.idTokenAlgValuesSupported = idTokenAlgValuesSupported;
  }

  public String getIssuer() {
    return issuer;
  }

  public String getAuthorizationEndpoint() {
    return authorizationEndpoint;
  }

  public String getTokenEndpoint() {
    return tokenEndpoint;
  }

  public String getUserinfoEndpoint() {
    return userinfoEndpoint;
  }

  public String getJwksUri() {
    return jwksUri;
  }

  public List<String> getResponseTypesSupported() {
    return responseTypesSupported;
  }

  public List<String> getSubjectTypesSupported() {
    return subjectTypesSupported;
  }

  public List<String> getIdTokenAlgValuesSupported() {
    return idTokenAlgValuesSupported;
  }
}
