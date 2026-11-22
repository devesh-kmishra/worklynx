export type OrganizationRole = 'OWNER' | 'ADMIN' | 'MEMBER';

export type SubscriptionPlan = 'FREE' | 'PRO' | 'ENTERPRISE';

export type SubscriptionStatus = 'ACTIVE' | 'TRIALING' | 'PAST_DUE' | 'CANCELLED';

export interface OrganizationProject {
  id: number;
  name: string;
}

export interface Organization {
  id: number;
  name: string;
  role: OrganizationRole;

  plan: SubscriptionPlan;
  subscriptionStatus: SubscriptionStatus;

  projectCount: number;
  projectLimit: number;

  memberCount: number;
  memberLimit: number;

  taskCount: number;
  taskLimit: number;

  projects: OrganizationProject[];
}

export interface CreateOrganizationRequest {
  name: string;
}
