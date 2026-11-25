package com.worklynx.backend.billing;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.worklynx.backend.common.exception.ForbiddenException;
import com.worklynx.backend.common.exception.ResourceNotFoundException;
import com.worklynx.backend.organization.Organization;
import com.worklynx.backend.organization.OrganizationRepository;
import com.worklynx.backend.user.User;

@ExtendWith(MockitoExtension.class)
class BillingAuthorizationServiceTest {

  @Mock
  private OrganizationRepository organizationRepository;

  @InjectMocks
  private BillingAuthorizationService billingAuthorizationService;

  private Organization organization;

  private void mockOrganizationWithOwner(Long ownerId) {

    User owner = mock(User.class);
    organization = mock(Organization.class);

    when(owner.getId()).thenReturn(ownerId);
    when(organization.getOwner()).thenReturn(owner);
  }

  @BeforeEach
  void setUp() {
  }

  @Test
  void shouldAllowOrganizationOwner() {

    mockOrganizationWithOwner(10L);

    when(organizationRepository.findById(1L))
        .thenReturn(Optional.of(organization));

    Organization result = billingAuthorizationService.validateOwnership(1L, 10L);

    assertSame(organization, result);
  }

  @Test
  void shouldRejectAdmin() {

    mockOrganizationWithOwner(10L);

    when(organizationRepository.findById(1L))
        .thenReturn(Optional.of(organization));

    assertThrows(
        ForbiddenException.class,
        () -> billingAuthorizationService.validateOwnership(1L, 20L));
  }

  @Test
  void shouldRejectMember() {

    mockOrganizationWithOwner(10L);

    when(organizationRepository.findById(1L))
        .thenReturn(Optional.of(organization));

    assertThrows(
        ForbiddenException.class,
        () -> billingAuthorizationService.validateOwnership(1L, 30L));
  }

  @Test
  void shouldThrowWhenOrganizationDoesNotExist() {

    when(organizationRepository.findById(1L))
        .thenReturn(Optional.empty());

    assertThrows(
        ResourceNotFoundException.class,
        () -> billingAuthorizationService.validateOwnership(1L, 10L));
  }
}
