package com.hubspot.singularity.resources;

import java.net.URI;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.common.base.Optional;
import com.google.inject.Inject;
import com.hubspot.singularity.auth.OAuth2Manager;
import com.hubspot.singularity.auth.OAuthTokenResponse;

@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
public class AuthResource {
  private final OAuth2Manager oauth;

  @Inject
  public AuthResource(OAuth2Manager oauth) {
    this.oauth = oauth;
  }

  @GET
  @Path("/login")
  public Response getAuth() {
    final Optional<URI> maybeAuthUri = oauth.generateAuthUrl();

    return Response.seeOther(maybeAuthUri.get()).build();
  }

  @GET
  @Path("/callback")
  public OAuthTokenResponse callback(@QueryParam("code") String authorizationCode) {
    return oauth.getAccessToken(authorizationCode);
  }
}
