package com.worklynx.backend.security.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.worklynx.backend.common.exception.ForbiddenException;
import com.worklynx.backend.organization.OrganizationAccessService;
import com.worklynx.backend.organization.OrganizationMember;
import com.worklynx.backend.security.UserPrincipal;
import com.worklynx.backend.security.annotation.RequireRole;

@Aspect
@Component
public class RoleAuthorizationAspect {

  private final OrganizationAccessService accessService;

  public RoleAuthorizationAspect(OrganizationAccessService accessService) {
    this.accessService = accessService;
  }

  @Before("@annotation(requireRole)")
  public void checkRole(JoinPoint joinPoint, RequireRole requireRole) {

    Object[] args = joinPoint.getArgs();

    Long orgId = extractOrgId(args);

    if (orgId == null) {
      return; // no org -> skip
    }

    UserPrincipal principal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

    OrganizationMember member = accessService.getMembership(principal.getUserId(), orgId);

    boolean allowed = false;

    for (OrganizationMember.Role role : requireRole.value()) {
      if (member.getRole() == role) {
        allowed = true;
        break;
      }
    }

    if (!allowed) {
      throw new ForbiddenException("Insufficient permissions");
    }
  }

  private Long extractOrgId(Object[] args) {

    for (Object arg : args) {
      if (arg instanceof Long aLong) {
        return aLong;
      }
    }

    return null;
  }
}
