package com.worklynx.backend.auth.dto;

import com.worklynx.backend.auth.oauth.OAuthProvider;

public record ConnectedProviderResponse(
        OAuthProvider provider,
        boolean canDisconnect) {
}
