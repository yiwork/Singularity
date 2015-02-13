package com.hubspot.singularity.auth;

import java.net.URI;
import java.util.Map;

import javax.ws.rs.core.UriBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.base.Throwables;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.hubspot.singularity.config.SingularityConfiguration;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.Request;
import com.ning.http.client.Response;

@Singleton
public class OAuth2Manager {
  private static final Joiner SPACE_JOINER = Joiner.on(" ");

  private final SingularityConfiguration config;
  private final AsyncHttpClient httpClient;
  private final ObjectMapper objectMapper;
  private final Optional<DiscoveryDocument> maybeDiscoveryDocument;

  @Inject
  public OAuth2Manager(SingularityConfiguration config, Optional<DiscoveryDocument> maybeDiscoveryDocument, AsyncHttpClient httpClient, ObjectMapper objectMapper) {
    this.config = config;
    this.httpClient = httpClient;
    this.objectMapper = objectMapper;
    this.maybeDiscoveryDocument = maybeDiscoveryDocument;
  }

  public Optional<URI> generateAuthUrl() {
    if (!maybeDiscoveryDocument.isPresent() || !config.getOAuthConfiguration().isPresent()) {
      return Optional.absent();
    }

    final UriBuilder builder = UriBuilder.fromUri(maybeDiscoveryDocument.get().getAuthorizationEndpoint());

    builder.queryParam("client_id", config.getOAuthConfiguration().get().getClientId());
    builder.queryParam("redirect_uri", config.getOAuthConfiguration().get().getRedirectUri());
    builder.queryParam("scope", SPACE_JOINER.join(config.getOAuthConfiguration().get().getScopes()));

    for (Map.Entry<String, String> entry : config.getOAuthConfiguration().get().getExtraAuthorizationQueryParams().entrySet()) {
      builder.queryParam(entry.getKey(), entry.getValue());
    }

    return Optional.of(builder.build());
  }

  public OAuthTokenResponse getAccessToken(String authorizationCode) {
    final AsyncHttpClient.BoundRequestBuilder requestBuilder = httpClient.preparePost(maybeDiscoveryDocument.get().getTokenEndpoint())
            .addParameter("code", authorizationCode)
            .addParameter("client_id", config.getOAuthConfiguration().get().getClientId())
            .addParameter("client_secret", config.getOAuthConfiguration().get().getClientSecret())
            .addParameter("redirect_uri", config.getOAuthConfiguration().get().getRedirectUri());

    for (Map.Entry<String, String> entry : config.getOAuthConfiguration().get().getExtraTokenParams().entrySet()) {
      requestBuilder.addParameter(entry.getKey(), entry.getValue());
    }

    final Request request = requestBuilder.build();

    try {
      final Response response = httpClient.executeRequest(request).get();

      return objectMapper.readValue(response.getResponseBodyAsBytes(), OAuthTokenResponse.class);
    } catch (Exception e) {
      throw Throwables.propagate(e);
    }
  }

  // TODO: decode / verify keys locally
  // TODO: check HTTP status codes!!!
  public Optional<IdTokenValue> validateIdToken(String idToken) {
    final Request request = httpClient.prepareGet(maybeDiscoveryDocument.get().getUserinfoEndpoint())
            .addQueryParameter("id_token", idToken)
            .build();

    try {
      final Response response = httpClient.executeRequest(request).get();

      return Optional.of(objectMapper.readValue(response.getResponseBodyAsBytes(), IdTokenValue.class));
    } catch (Exception e) {
      throw Throwables.propagate(e);
    }
  }
}
