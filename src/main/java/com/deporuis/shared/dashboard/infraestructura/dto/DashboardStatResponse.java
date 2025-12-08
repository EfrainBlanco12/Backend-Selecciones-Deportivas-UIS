package com.deporuis.shared.dashboard.infraestructura.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatResponse {
    private String title;
    private Long value;
    private TrendData trend;
    private Boolean year;
}
