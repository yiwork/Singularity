package com.hubspot.singularity.auth;

import java.net.URI;

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

  @Inject
  public OAuth2Manager(SingularityConfiguration config, AsyncHttpClient httpClient, ObjectMapper objectMapper) {
    this.config = config;
    this.httpClient = httpClient;
    this.objectMapper = objectMapper;
  }

  public Optional<URI> generateAuthUrl() {
    if (!config.getOAuthConfiguration().isPresent()) {
      return Optional.absent();
    }

    final UriBuilder builder = UriBuilder.fromUri(config.getOAuthConfiguration().get().getAuthUrl());

    builder.queryParam("response_type", "code");
    builder.queryParam("client_id", config.getOAuthConfiguration().get().getClientId());
    builder.queryParam("redirect_uri", config.getOAuthConfiguration().get().getRedirectUri());
    builder.queryParam("scope", SPACE_JOINER.join(config.getOAuthConfiguration().get().getScopes()));
    builder.queryParam("access_type", "online");
    builder.queryParam("approval_prompt", "auto");

    return Optional.of(builder.build());
  }

  public OAuthTokenResponse getAccessToken(String authorizationCode) {
    final Request request = httpClient.preparePost(config.getOAuthConfiguration().get().getTokenUrl())
            .addParameter("code", authorizationCode)
            .addParameter("client_id", config.getOAuthConfiguration().get().getClientId())
            .addParameter("client_secret", config.getOAuthConfiguration().get().getClientSecret())
            .addParameter("redirect_uri", config.getOAuthConfiguration().get().getRedirectUri())
            .addParameter("grant_type", "authorization_code")
            .build();

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
    final Request request = httpClient.prepareGet(config.getOAuthConfiguration().get().getTokenInfoUrl())
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
