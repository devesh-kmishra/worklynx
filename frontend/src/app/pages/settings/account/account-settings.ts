import { Component, OnInit, signal } from '@angular/core';
import { AccountConnectionsResponse, AuthService } from '../../../core/services/auth';
import { ActivatedRoute } from '@angular/router';
import { environment } from '../../../../environments/environment';

type Provider = 'google' | 'github';

@Component({
  selector: 'app-account-settings',
  imports: [],
  templateUrl: './account-settings.html',
  styleUrl: './account-settings.css',
})
export class AccountSettings implements OnInit {
  connections = signal<AccountConnectionsResponse | null>(null);

  loading = signal(true);
  connectingProvider = signal<Provider | null>(null);
  disconnectingProvider = signal<Provider | null>(null);

  errorMessage = signal('');
  successMessage = signal('');

  constructor(
    private authService: AuthService,
    private route: ActivatedRoute,
  ) {}

  ngOnInit(): void {
    const queryParams = this.route.snapshot.queryParamMap;

    if (queryParams.get('linked') === 'true') {
      this.successMessage.set('Account connected successfully.');
    }

    const linkError = queryParams.get('linkError');

    if (linkError) {
      const messages: Record<string, string> = {
        identity_in_use:
          'This Google or GitHub account is already connected to another Worklynx account.',
        provider_already_connected: 'You already have an account connected with this provider.',
        email_in_use: 'This email belongs to another Worklynx account.',
      };

      this.errorMessage.set(
        messages[linkError] ??
          'Unable to connect your account. The operation may have been cancelled.',
      );
    }

    this.loadConnections();
  }

  loadConnections(): void {
    this.loading.set(true);

    this.authService.getConnections().subscribe({
      next: (connections) => {
        this.connections.set(connections);
        this.loading.set(false);
      },
      error: () => {
        this.errorMessage.set('Unable to load connected accounts.');
        this.loading.set(false);
      },
    });
  }

  isConnected(provider: Provider): boolean {
    return (
      this.connections()?.providers.some(
        (account) => account.provider === provider.toUpperCase(),
      ) ?? false
    );
  }

  canDisconnect(provider: Provider): boolean {
    return (
      this.connections()?.providers.find((account) => account.provider === provider.toUpperCase())
        ?.canDisconnect ?? false
    );
  }

  connectProvider(provider: Provider): void {
    this.errorMessage.set('');
    this.successMessage.set('');
    this.connectingProvider.set(provider);

    this.authService.startOAuthLink(provider).subscribe({
      next: ({ authorizationPath }) => {
        const authorizationUrl = new URL(authorizationPath, environment.apiUrl);

        window.location.assign(authorizationUrl.toString());
      },
      error: (error) => {
        this.connectingProvider.set(null);

        this.errorMessage.set(
          error.error?.message ?? 'Unable to start the account connection process.',
        );
      },
    });
  }

  disconnectProvider(provider: Provider): void {
    const confirmed = window.confirm(
      `Are you sure you want to disconnect ${this.getProviderName(provider)}?`,
    );

    if (!confirmed) return;

    this.errorMessage.set('');
    this.successMessage.set('');
    this.disconnectingProvider.set(provider);

    this.authService.disconnectProvider(provider).subscribe({
      next: () => {
        this.successMessage.set(`${this.getProviderName(provider)} disconnected successfully.`);

        this.disconnectingProvider.set(null);
        this.loadConnections();
      },
      error: (error) => {
        this.errorMessage.set(error.error?.message ?? 'Unable to disconnect this account.');

        this.disconnectingProvider.set(null);
      },
    });
  }

  getProviderName(provider: Provider): string {
    return provider === 'google' ? 'Google' : 'GitHub';
  }
}
