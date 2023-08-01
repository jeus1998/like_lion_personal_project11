package com.example.miniproject_basic_baejeu.service;

import com.example.miniproject_basic_baejeu.dto.CommentDto;
import com.example.miniproject_basic_baejeu.entity.CommentEntity;
import com.example.miniproject_basic_baejeu.entity.MarketEntity;
import com.example.miniproject_basic_baejeu.entity.UserEntity;
import com.example.miniproject_basic_baejeu.repository.CommentRepository;
import com.example.miniproject_basic_baejeu.repository.MarketRepository;
import com.example.miniproject_basic_baejeu.repository.UserRepository;
import com.example.miniproject_basic_baejeu.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
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
    public final UserRepository userRepository;
    public CommentDto createComment(Long itemId, CommentDto dto, Authentication authentication) {
        Optional<MarketEntity> optionalMarket = marketRepository.findById(itemId);
        if (optionalMarket.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        Long id = customUserDetails.getId();
        Optional<UserEntity> optionalUser1 = userRepository.findById(id);
        UserEntity user1 = optionalUser1.get();

        String currentUser = authentication.getName();
        Optional<UserEntity> optionalUser = userRepository.findByUsername(currentUser);
        UserEntity user = optionalUser.get();

        MarketEntity marketEntity = optionalMarket.get();
        CommentEntity entity = new CommentEntity();
        entity.setContent(dto.getContent());
        entity.setSalesItem(marketEntity); // 연관된 MarketEntity 설정
        entity.setUser(user1); // 현재 사용자 등록
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
    public CommentDto updateComment(Long itemId, Long commentsId, Authentication authentication, CommentDto dto) {
            Optional<CommentEntity> optionalComment = commentRepository.findById(commentsId);
            if (optionalComment.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            }
            CommentEntity commentEntity = optionalComment.get();
            if (authentication.getName().equals(commentEntity.getUser().getUsername()) && itemId == commentEntity.getSalesItem().getId()) {
                commentEntity.setContent(dto.getContent());
                commentEntity.setReply(dto.getReply());
                return CommentDto.fromEntity(commentRepository.save(commentEntity));
            }
            else throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    public void deleteComment(Long itemId, Long commentsId, Authentication authentication) {
        Optional<CommentEntity> optionalComment = commentRepository.findById(commentsId);
            if (optionalComment.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            }
            CommentEntity commentEntity = optionalComment.get();
            if (authentication.getName().equals(commentEntity.getUser().getUsername()) && itemId == commentEntity.getSalesItem().getId()) {
                commentRepository.deleteById(commentsId);
            }
            else throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }
    public CommentDto updateReply(Long itemId, Long commentsId, Authentication authentication, CommentDto dto){
        Optional<CommentEntity> optionalComment = commentRepository.findById(commentsId); // 댓글 id
        if (optionalComment.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        CommentEntity commentEntity = optionalComment.get();
        // 물품의 주인이 맞아야 답글 다는게 가능하다.
        if (!authentication.getName().equals(commentEntity.getSalesItem().getUser().getUsername()) && itemId == commentEntity.getSalesItem().getId()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        commentEntity.setReply(dto.getReply());
        commentEntity.setContent(commentEntity.getContent());
        return CommentDto.fromEntity(commentRepository.save(commentEntity));
    }
}
