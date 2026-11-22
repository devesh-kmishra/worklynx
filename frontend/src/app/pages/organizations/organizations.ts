import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { OrganizationService } from '../../core/services/organization';
import {
  Organization,
  OrganizationRole,
  SubscriptionPlan,
} from '../../core/models/organization.model';

@Component({
  selector: 'app-organizations',
  imports: [],
  templateUrl: './organizations.html',
  styleUrl: './organizations.css',
})
export class Organizations implements OnInit {
  private organizationService = inject(OrganizationService);

  organizations = signal<Organization[]>([]);

  loading = signal(true);
  error = signal('');
  success = signal('');

  organizationName = signal('');
  creating = signal(false);

  ownedOrganization = computed(
    () => this.organizations().find((org) => org.role === 'OWNER') ?? null,
  );

  joinedOrganizations = computed(() => this.organizations().filter((org) => org.role !== 'OWNER'));

  canCreateOrganization = computed(() => !this.ownedOrganization());

  ngOnInit(): void {
    this.loadOrganizations();
  }

  loadOrganizations(): void {
    this.loading.set(true);
    this.error.set('');

    this.organizationService.getOrganizations().subscribe({
      next: (organizations) => {
        this.organizations.set(organizations);
        this.loading.set(false);
      },
      error: (error) => {
        this.error.set(error?.error?.message ?? 'Unable to load your organizations.');
        this.loading.set(false);
      },
    });
  }

  createOrganization(event: Event): void {
    event.preventDefault();

    const name = this.organizationName().trim();

    if (!name || this.creating() || !this.canCreateOrganization()) {
      return;
    }

    this.creating.set(true);
    this.error.set('');
    this.success.set('');

    this.organizationService.createOrganization({ name }).subscribe({
      next: (organization) => {
        this.organizations.update((current) => [...current, organization]);

        this.organizationName.set('');
        this.creating.set(false);
        this.success.set('Your organization has been created successfully');
      },
      error: (error) => {
        this.creating.set(false);
        this.error.set(error?.error?.message ?? 'Unable to create your organization.');
      },
    });
  }

  getRoleLabel(role: OrganizationRole): string {
    switch (role) {
      case 'OWNER':
        return 'Owner';
      case 'ADMIN':
        return 'Admin';
      case 'MEMBER':
        return 'Member';
    }
  }

  getPlanLabel(plan: SubscriptionPlan): string {
    switch (plan) {
      case 'FREE':
        return 'Free';
      case 'PRO':
        return 'Pro';
      case 'ENTERPRISE':
        return 'Enterprise';
    }
  }

  getUsagePercentage(count: number, limit: number): number {
    if (limit === Number.MAX_SAFE_INTEGER || limit === 2147483647) {
      return 0;
    }

    if (limit <= 0) {
      return 0;
    }

    return Math.min((count / limit) * 100, 100);
  }

  getUsageLabel(count: number, limit: number): string {
    if (limit === 2147483647) {
      return `${count} / Unlimited`;
    }

    return `${count} / ${limit}`;
  }

  isUnlimited(limit: number): boolean {
    return limit === 2147483647;
  }
}
