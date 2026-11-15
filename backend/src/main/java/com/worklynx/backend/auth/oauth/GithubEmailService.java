package com.worklynx.backend.auth.oauth;

import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class GithubEmailService {

    private final RestClient restClient = RestClient.builder()
            .baseUrl("https://api.github.com")
            .defaultHeader(HttpHeaders.ACCEPT, "application/vnd.github+json")
            .defaultHeader(HttpHeaders.USER_AGENT, "Worklynx")
            .build();

    public String getVerifiedEmail(OAuth2UserRequest request) {
        List<GithubEmail> emails = restClient.get()
                .uri("/user/emails")
                .headers(headers -> headers.setBearerAuth(
                        request.getAccessToken().getTokenValue()))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(new ParameterizedTypeReference<List<GithubEmail>>() {
                });

        if (emails == null) {
            return null;
        }

        return emails.stream()
                .filter(githubEmail -> githubEmail.verified())
                .filter(githubEmail -> githubEmail.primary())
                .map(githubEmail -> githubEmail.email())
                .filter(email -> email != null && !email.isBlank())
                .findFirst()
                .orElseGet(() -> emails.stream()
                        .filter(githubEmail -> githubEmail.verified())
                        .map(githubEmail -> githubEmail.email())
                        .filter(email -> email != null && !email.isBlank())
                        .findFirst()
                        .orElse(null));
    }

    private record GithubEmail(String email, boolean primary, boolean verified) {
    }
}
