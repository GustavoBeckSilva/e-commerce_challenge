package com.compass.e_commerce_challenge.dto.report;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class PeriodRequest {
    private LocalDateTime start;
    private LocalDateTime end;
}