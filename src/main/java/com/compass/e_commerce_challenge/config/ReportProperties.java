package com.compass.e_commerce_challenge.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import lombok.Data;

@Data
@Component
@ConfigurationProperties(prefix = "app.report")
public class ReportProperties {
    private Integer lowStockThreshold;
}
