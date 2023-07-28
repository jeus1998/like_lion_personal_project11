package com.example.miniproject_basic_baejeu.service;

import com.example.miniproject_basic_baejeu.dto.CommentDto;
import com.example.miniproject_basic_baejeu.entity.CommentEntity;
import com.example.miniproject_basic_baejeu.entity.MarketEntity;
import com.example.miniproject_basic_baejeu.repository.CommentRepository;
import com.example.miniproject_basic_baejeu.repository.MarketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentService {
    public final MarketRepository marketRepository;
    public final CommentRepository commentRepository;
    public CommentDto createComment(Long itemId, CommentDto dto) {
        Optional<MarketEntity> optionalMarket = marketRepository.findById(itemId);
        if (optionalMarket.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        MarketEntity marketEntity = optionalMarket.get();
        CommentEntity entity = new CommentEntity();
        entity.setWriter(dto.getWriter());
        entity.setPassword(dto.getPassword());
        entity.setContent(dto.getContent());
        entity.setSalesItem(marketEntity); // 연관된 MarketEntity 설정
        return CommentDto.fromEntity(commentRepository.save(entity));
    }
    public Page<CommentDto> readCommentAll(Long itemId, Long page, Long limit) {
        Pageable pageable = PageRequest.of(Math.toIntExact(page), Math.toIntExact(limit));

        // itemId에 해당하는 CommentEntity 리스트 가져오기
        List<CommentEntity> commentEntityList = commentRepository.findAll();
        List<CommentEntity> filteredCommentEntityList = new ArrayList<>();
        for (CommentEntity comment : commentEntityList) {
            if (comment.getSalesItem().getId() == itemId) {
                filteredCommentEntityList.add(comment);
            }
        }
        // itemId에 맞는 값만 페이징 처리하기
        int startIndex = Math.toIntExact(pageable.getOffset());
        int endIndex = Math.min(startIndex + Math.toIntExact(pageable.getPageSize()), filteredCommentEntityList.size());
        List<CommentEntity> pagedCommentEntityList = filteredCommentEntityList.subList(startIndex, endIndex);

        List<CommentDto> commentDtoList = new ArrayList<>();
        for (CommentEntity comment : pagedCommentEntityList) {
            commentDtoList.add(CommentDto.fromEntity(comment));
        }
        return new PageImpl<>(commentDtoList, pageable, filteredCommentEntityList.size());
    }
    // update itemid , password , commentid 모두 정확하게 요청해야 업데이트 가능
    public CommentDto updateComment(Long itemId, Long commentsId, String password, CommentDto dto) {
            Optional<CommentEntity> optionalComment = commentRepository.findById(commentsId);
            if (optionalComment.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            }
            CommentEntity commentEntity = optionalComment.get();
            if (password.equals(commentEntity.getPassword()) && itemId == commentEntity.getSalesItem().getId()) {
                commentEntity.setWriter(dto.getWriter());
                commentEntity.setPassword(dto.getPassword());
                commentEntity.setContent(dto.getContent());
                commentEntity.setReply(dto.getReply());
                return CommentDto.fromEntity(commentRepository.save(commentEntity));
            }
            else throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    public void deleteComment(Long itemId, Long commentsId, String password) {
        Optional<CommentEntity> optionalComment = commentRepository.findById(commentsId);
            if (optionalComment.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            }
            CommentEntity commentEntity = optionalComment.get();
            if (password.equals(commentEntity.getPassword()) && itemId == commentEntity.getSalesItem().getId()) {
                commentRepository.deleteById(commentsId);
            }
            else throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }
    public CommentDto updateReply(Long itemId, Long commentsId, String password, CommentDto dto){
        Optional<CommentEntity> optionalComment = commentRepository.findById(commentsId); // 댓글 id
        if (optionalComment.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        CommentEntity commentEntity = optionalComment.get();
        Optional<MarketEntity> optionalMarket = marketRepository.findById(commentEntity.getSalesItem().getId()); // item_id
        MarketEntity marketEntity = optionalMarket.get();
        if (!password.equals(marketEntity.getPassword())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        commentEntity.setReply(dto.getReply());
        return CommentDto.fromEntity(commentRepository.save(commentEntity));
    }
}

/*
 */