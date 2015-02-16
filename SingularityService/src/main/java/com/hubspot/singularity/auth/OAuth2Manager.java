package com.hubspot.singularity.auth;

import java.net.URI;
import java.util.Map;

import javax.ws.rs.core.MediaType;
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

import static com.google.common.base.Preconditions.checkNotNull;

@Singleton
public class OAuth2Manager {
  private static final Joiner SPACE_JOINER = Joiner.on(" ").skipNulls();

  private static final String ACCEPT_HEADER = "Accept";
  private static final String CLIENT_ID = "client_id";
  private static final String CLIENT_SECRET = "client_secret";
  private static final String SCOPE = "scope";
  private static final String REDIRECT_URI = "redirect_uri";
  private static final String CODE = "code";

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

    builder.queryParam(CLIENT_ID, config.getOAuthConfiguration().get().getClientId());
    builder.queryParam(REDIRECT_URI, config.getOAuthConfiguration().get().getRedirectUri());
    builder.queryParam(SCOPE, SPACE_JOINER.join(config.getOAuthConfiguration().get().getScopes()));

    for (Map.Entry<String, String> entry : config.getOAuthConfiguration().get().getExtraAuthorizationQueryParams().entrySet()) {
      builder.queryParam(entry.getKey(), entry.getValue());
    }

    return Optional.of(builder.build());
  }

  public OAuthTokenResponse getAccessToken(String authorizationCode) {
    final AsyncHttpClient.BoundRequestBuilder requestBuilder = httpClient.preparePost(maybeDiscoveryDocument.get().getTokenEndpoint())
            .addHeader(ACCEPT_HEADER, MediaType.APPLICATION_JSON)
            .addParameter(CODE, checkNotNull(authorizationCode))
            .addParameter(CLIENT_ID, config.getOAuthConfiguration().get().getClientId())
            .addParameter(CLIENT_SECRET, config.getOAuthConfiguration().get().getClientSecret())
            .addParameter(REDIRECT_URI, config.getOAuthConfiguration().get().getRedirectUri());

    for (Map.Entry<String, String> entry : config.getOAuthConfiguration().get().getExtraTokenParams().entrySet()) {
      requestBuilder.addParameter(entry.getKey(), entry.getValue());
    }

    final Request request = requestBuilder.build();

    try {
      final Response response = httpClient.executeRequest(request).get();

      if (response.getStatusCode() != 200) {
        throw new RuntimeException(response.getResponseBody());
      }

      return objectMapper.readValue(response.getResponseBodyAsBytes(), OAuthTokenResponse.class);
    } catch (Exception e) {
      throw Throwables.propagate(e);
    }
  }

  // TODO: decode / verify keys locally where possible
  public Optional<IdTokenValue> validateIdToken(String idToken) {
    final Request request = httpClient.prepareGet(maybeDiscoveryDocument.get().getUserinfoEndpoint())
            .addQueryParameter(config.getOAuthConfiguration().get().getAccessTokenQueryParamName(), idToken)
            .build();

    try {
      final Response response = httpClient.executeRequest(request).get();

      if (response.getStatusCode() != 200) {
        throw new RuntimeException(response.getResponseBody());
      }

      return Optional.of(objectMapper.readValue(response.getResponseBodyAsBytes(), IdTokenValue.class));
    } catch (Exception e) {
      throw Throwables.propagate(e);
    }
  }
}
