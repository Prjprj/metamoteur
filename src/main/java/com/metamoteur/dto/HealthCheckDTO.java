package com.metamoteur.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class HealthCheckDTO {
    private String status;
    private Instant timestamp;
    private String error;
}