package com.example.miniproject_basic_baejeu.dto;


import com.example.miniproject_basic_baejeu.entity.NegotiationEntity;
import lombok.Data;

@Data
public class NegotiationDto {
    private Long id;
    private Long item_id;
    private Long suggested_price;
    private String status;


    public static NegotiationDto fromEntity(NegotiationEntity entity){
        NegotiationDto dto = new NegotiationDto();
        dto.setId(entity.getId());
        dto.setSuggested_price(entity.getSuggested_price());
        dto.setStatus(entity.getStatus());
        return dto;
    }
}
