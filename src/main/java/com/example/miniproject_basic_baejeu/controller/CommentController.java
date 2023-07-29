package com.example.miniproject_basic_baejeu.controller;

import com.example.miniproject_basic_baejeu.dto.CommentDto;
import com.example.miniproject_basic_baejeu.dto.ResponseDto;
import com.example.miniproject_basic_baejeu.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/items/{itemId}/comments") // 댓글 아이템 댓글 url
public class CommentController {
    public final CommentService commentService;

    // 1. 등록된 물품에 대한 질문을 위하여 댓글을 등록할 수 있다. POST
    // 이때 반드시 포함되어야 하는 내용은 대상 물품, 댓글 내용, 작성자이다.  댓글을 등록할 때, 비밀번호 항목을 추가해서 등록한다.
    // 즉 반드시 호함되어야 하는 내용은 대상 물품, 댓글 내용, 작성자, 비밀번호이다.
    @PostMapping
    public ResponseDto create(@PathVariable("itemId")Long itemId,
                              @Valid @RequestBody CommentDto dto,
                              Authentication authentication){
      commentService.createComment(itemId, dto, authentication);
      ResponseDto response = new ResponseDto();
      response.setMessage("댓글이 등록되었습니다.");
      return response;
    }

    // 2. 등록된 댓글은 누구든지 열람할 수 있다. GET
    // 페이지 단위 조회가 가능하다.
    @GetMapping
    public Page<CommentDto> readPage(
            @PathVariable("itemId") Long itemId,
            @RequestParam(value = "page", defaultValue = "0") Long page,
            @RequestParam(value = "limit", defaultValue = "10") Long limit
    ){
        return commentService.readCommentAll(itemId, page, limit);
    }

    // 3. 등록된 댓글은 수정이 가능하다. PUT
    // 이때, 댓글이 등록될 때 추가한 비밀번호를 첨부해야 한다.
    // 아이템 아이디는 의미없다. -> itemId와 commentsId에 해당하는 entity.get 을해서 itemId와 비교를 하여 throw처리 가능하다.
    @PutMapping("/{commentsId}")
    public ResponseDto update(
                             @PathVariable("itemId")Long itemId,
                             @PathVariable("commentsId") Long commentsId,
                             Authentication authentication,
                             @RequestBody CommentDto dto){
         commentService.updateComment(itemId, commentsId, authentication , dto);
         ResponseDto responseDto = new ResponseDto();
         responseDto.setMessage("댓글이 수정되었습니다.");
         return responseDto;
    }
    // 4. 등록된 댓글은 삭제가 가능하다.
    // 이때, 댓글이 등록될 때 추가한 비밀번호를 첨부해야 한다.
    @DeleteMapping("/{commentsId}")
    public ResponseDto delete(
                        @PathVariable("itemId")Long itemId,
                        @PathVariable("commentsId") Long commentsId,
                        Authentication authentication){
        commentService.deleteComment(itemId, commentsId, authentication);
        ResponseDto response = new ResponseDto();
        response.setMessage("댓글을 삭제했습니다.");
        return response;
    }
    // 5. 댓글에는 초기에 비워져 있는 답글 항목이 존재한다
    // 만약 댓글이 등록된 대상 물품을 등록한 사람일 경우, 물품을 등록할 때 사용한 비밀번호를 첨부할 경우 답글 항목을 수정할 수 있다.
    // 답글은 댓글에 포함된 공개 정보이다
    @PutMapping("/{commentsId}/reply")
    public ResponseDto updateReply( @PathVariable("itemId")Long itemId,
                                    @PathVariable("commentsId") Long commentsId,
                                    Authentication authentication,
                                    @RequestBody CommentDto dto){
      commentService.updateReply(itemId, commentsId, authentication , dto);
      ResponseDto responseDto = new ResponseDto();
      responseDto.setMessage("댓글에 답변이 추가되었습니다.");
      return responseDto;
    }
}
