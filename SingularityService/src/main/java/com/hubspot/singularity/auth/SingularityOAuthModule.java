package com.hubspot.singularity.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Optional;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.hubspot.singularity.config.SingularityConfiguration;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.Request;
import com.ning.http.client.Response;

public class SingularityOAuthModule extends AbstractModule {
  private static final Logger LOG = LoggerFactory.getLogger(SingularityOAuthModule.class);

  @Override
  protected void configure() {

  }

  @Singleton
  @Provides
  public Optional<DiscoveryDocument> providesDiscoveryDocument(SingularityConfiguration config, AsyncHttpClient httpClient, ObjectMapper objectMapper) throws Exception {
    if (!config.getOAuthConfiguration().isPresent()) {
      return Optional.absent();
    }

    if (config.getOAuthConfiguration().get().getDiscoveryDocument().isPresent()) {
      LOG.info("Loaded hardcoded OpenID discovery document");
      return config.getOAuthConfiguration().get().getDiscoveryDocument();
    }

    final Request request = httpClient.prepareGet(config.getOAuthConfiguration().get().getDiscoveryDocumentUrl().get()).build();

    final Response response = httpClient.executeRequest(request).get();

    if (response.getStatusCode() != 200) {
      LOG.error("Failed to load OpenID discovery document from {} -- HTTP {}: {}", config.getOAuthConfiguration().get().getDiscoveryDocumentUrl().get(), response.getStatusCode(), response.getResponseBody());
      throw new Exception("Failed to load OpenID discovery document");
    } else {
      LOG.info("Loaded OpenID discovery document from {}", config.getOAuthConfiguration().get().getDiscoveryDocumentUrl().get());
    }

    return Optional.of(objectMapper.readValue(response.getResponseBodyAsBytes(), DiscoveryDocument.class));
  }
}
