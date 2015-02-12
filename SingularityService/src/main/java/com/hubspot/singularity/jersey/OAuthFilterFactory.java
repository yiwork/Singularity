package com.hubspot.singularity.jersey;

import java.net.URI;
import java.util.Collections;
import java.util.List;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import com.google.common.base.Optional;
import com.google.inject.Inject;
import com.hubspot.singularity.auth.NoAuth;
import com.hubspot.singularity.auth.OAuth2Manager;
import com.hubspot.singularity.config.OAuthConfiguration;
import com.hubspot.singularity.config.SingularityConfiguration;
import com.sun.jersey.api.model.AbstractMethod;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;
import com.sun.jersey.spi.container.ContainerResponseFilter;
import com.sun.jersey.spi.container.ResourceFilter;
import com.sun.jersey.spi.container.ResourceFilterFactory;

public class OAuthFilterFactory implements ResourceFilterFactory {
  public static final String COOKIE_NAME = "singularity_jwt";

  private final Optional<OAuthConfiguration> maybeOAuthConfiguration;
  private final OAuth2Manager manager;

  @Inject
  public OAuthFilterFactory(SingularityConfiguration configuration, OAuth2Manager manager) {
    this.maybeOAuthConfiguration = configuration.getOAuthConfiguration();
    this.manager = manager;
  }

  @Override
  public List<ResourceFilter> create(AbstractMethod am) {
    if (maybeOAuthConfiguration.isPresent()) {
      if ((am.getAnnotation(NoAuth.class) == null) && (am.getResource().getAnnotation(NoAuth.class) == null)) {
        return Collections.<ResourceFilter>singletonList(new ResourceFilter() {
          @Override
          public ContainerRequestFilter getRequestFilter() {
            return new ContainerRequestFilter() {
              @Override
              public ContainerRequest filter(ContainerRequest request) {
                if (!request.getCookies().containsKey(COOKIE_NAME) || !manager.validateIdToken(request.getCookies().get(COOKIE_NAME).getValue()).isPresent()) {
                  throw new WebApplicationException(Response.seeOther(URI.create("/auth/login")).build());
                }

                return request;
              }
            };
          }

          @Override
          public ContainerResponseFilter getResponseFilter() {
            return null;
          }
        });
      }
    }

    return null;
  }
}
