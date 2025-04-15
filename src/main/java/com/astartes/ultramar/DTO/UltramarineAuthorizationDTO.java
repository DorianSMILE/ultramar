package com.astartes.ultramar.DTO;

import lombok.Data;

import java.util.Map;

@Data
public class UltramarineAuthorizationDTO {
    private int ultramarineId;
    private Map<String, String> supplyAuthorizations;
    private Map<String, String> weightAuthorizations;

    public UltramarineAuthorizationDTO(int ultramarineId, Map<String, String> supplyMap, Map<String, String> weightMap) {
        this.ultramarineId = ultramarineId;
        this.supplyAuthorizations = supplyMap;
        this.weightAuthorizations = weightMap;
    }
}
