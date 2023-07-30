package com.example.miniproject_basic_baejeu.dto;

import com.example.miniproject_basic_baejeu.entity.CommentEntity;
import lombok.Data;

@Data
public class CommentDto {
    private Long id;
    private String content;
    private String reply;
    private Long item_id;

    public static CommentDto fromEntity(CommentEntity entity){
        CommentDto dto = new CommentDto();
        dto.setId(entity.getId());
        dto.setContent(entity.getContent());
        dto.setReply(entity.getReply());
        dto.setItem_id(entity.getSalesItem().getId());
        return dto;
    }
}
