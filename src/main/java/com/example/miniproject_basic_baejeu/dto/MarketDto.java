package com.example.miniproject_basic_baejeu.dto;


import com.example.miniproject_basic_baejeu.entity.MarketEntity;
import lombok.Data;

@Data
public class MarketDto {

    private Long id;
    private String title;
    private String description;
    private String image_url;
    private Long min_price_wanted;
    private String status;

    public static MarketDto fromEntity(MarketEntity entity){
        MarketDto dto = new MarketDto();
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setDescription(entity.getDescription());
        dto.setImage_url(entity.getImage_url());
        dto.setMin_price_wanted(entity.getMin_price_wanted());
        dto.setStatus(entity.getStatus());
        return dto;
    }
}
