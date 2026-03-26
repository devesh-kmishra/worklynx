package com.worklynx.backend.common.exception;

import java.time.Instant;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ErrorResponse {

  public final Instant timestamp;
  public final int status;
  public final String error;
  public final String message;
  public final String path;
}
