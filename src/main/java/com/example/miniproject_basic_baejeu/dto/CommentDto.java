package com.example.miniproject_basic_baejeu.dto;

import com.example.miniproject_basic_baejeu.entity.CommentEntity;
import lombok.Data;

@Data
public class CommentDto {
    private Long id;
    private String content;
    private String reply;

    public static CommentDto fromEntity(CommentEntity entity){
        CommentDto dto = new CommentDto();
        dto.setId(entity.getId());
        dto.setContent(entity.getContent());
        dto.setReply(entity.getReply());
        return dto;
    }
}
