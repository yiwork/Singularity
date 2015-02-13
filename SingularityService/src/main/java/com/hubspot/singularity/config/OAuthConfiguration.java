package com.hubspot.singularity.config;

import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.URL;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotNull;

import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.hubspot.singularity.auth.DiscoveryDocument;

public class OAuthConfiguration {
  private String discoveryDocumentUrl;
  private DiscoveryDocument discoveryDocument;

  @NotEmpty
  private String clientId;

  @NotEmpty
  private String clientSecret;

  @URL
  private String redirectUri;

  @NotNull
  private Map<String, String> extraAuthorizationQueryParams = Collections.emptyMap();

  @NotNull
  private Map<String, String> extraTokenParams = Collections.emptyMap();

  @NotNull
  private List<String> scopes = Collections.emptyList();

  public Map<String, String> getExtraTokenParams() {
    return extraTokenParams;
  }

  public void setExtraTokenParams(Map<String, String> extraTokenParams) {
    this.extraTokenParams = extraTokenParams;
  }

  public Map<String, String> getExtraAuthorizationQueryParams() {
    return extraAuthorizationQueryParams;
  }

  public void setExtraAuthorizationQueryParams(Map<String, String> extraAuthorizationQueryParams) {
    this.extraAuthorizationQueryParams = extraAuthorizationQueryParams;
  }

  public Optional<DiscoveryDocument> getDiscoveryDocument() {
    return Optional.fromNullable(discoveryDocument);
  }

  public void setDiscoveryDocument(DiscoveryDocument discoveryDocument) {
    this.discoveryDocument = discoveryDocument;
  }

  public Optional<String> getDiscoveryDocumentUrl() {
    return Optional.fromNullable(Strings.emptyToNull(discoveryDocumentUrl));
  }

  public void setDiscoveryDocumentUrl(String discoveryDocumentUrl) {
    this.discoveryDocumentUrl = discoveryDocumentUrl;
  }

  public String getClientId() {
    return clientId;
  }

  public void setClientId(String clientId) {
    this.clientId = clientId;
  }

  public String getClientSecret() {
    return clientSecret;
  }

  public void setClientSecret(String clientSecret) {
    this.clientSecret = clientSecret;
  }

  public String getRedirectUri() {
    return redirectUri;
  }

  public void setRedirectUri(String redirectUri) {
    this.redirectUri = redirectUri;
  }

  public List<String> getScopes() {
    return scopes;
  }

  public void setScopes(List<String> scopes) {
    this.scopes = scopes;
  }
}
