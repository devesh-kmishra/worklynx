package com.worklynx.backend.auth.dto;

import java.util.List;

public record AccountConnectionsResponse(
    boolean passwordLoginEnabled,
    List<ConnectedProviderResponse> providers) {
}
