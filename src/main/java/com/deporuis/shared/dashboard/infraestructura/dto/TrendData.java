package com.deporuis.shared.dashboard.infraestructura.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrendData {
    private Double value;
    private Boolean isPositive;
}
