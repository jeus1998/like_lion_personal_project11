package com.example.miniproject_basic_baejeu.controller;

import com.example.miniproject_basic_baejeu.dto.*;
import com.example.miniproject_basic_baejeu.service.NegotiationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/items/{itemId}/proposal")
public class NegotiationController {

    public final NegotiationService negotiationService;

    // 등록된 물품에 대하여 구매 제안을 등록할 수 있다.  POST
    // 이때 반드시 포함되어야 하는 내용은 대상 물품, 제안 가격, 작성자이다. 구매 제안을 등록할 때, 비밀번호 항목을 추가해서 등록한다.
    // 즉 반드시 포함되어야 하는 내용은 대상 물품, 제안 가격, 작성자, 비밀 번호이다.
    // 구매 제안이 등록될 때, 제안의 상태는 제안 상태가 된다

    @PostMapping
    public ResponseDto create(@PathVariable("itemId")Long itemId,
                                 @Valid @RequestBody NegotiationDto dto){
     //   return negotiationService.createNegotiation(dto , itemId);
        negotiationService.createNegotiation(dto , itemId);
        ResponseDto response = new ResponseDto();
        response.setMessage("구매 제안이 등록되었습니다.");
        return response;
    }

    // 구매 제안은 대상 물품의 주인과 등록한 사용자만 조회할 수 있다.
    // 대상 물품의 주인은, 대상 물품을 등록할 때 사용한 작성자와 비밀번호를 첨부해야 한다. 이때 물품에 등록된 모든 구매 제안이 확인 가능하다. 페이지 기능을 지원한다.
    // 등록한 사용자는, 조회를 위해서 자신이 사용한 작성자와 비밀번호를 첨부해야 한다. 이때 자신이 등록한 구매 제안만 확인이 가능하다. 페이지 기능을 지원한다.
    // 지금까지는 비밀번호 첨부를 url을 통해서 했는데 이제는 requestBody에 실어서 첨부해보겠당
    // 조회시 sales_item writer and password 첨부해야한다. 자신이 등록한 아이템 모든 제안 페이지 지원
    @GetMapping
    public Page<NegotiationDto> masterSearch (
            @PathVariable("itemId") Long itemId,
            @RequestBody NegotiationDto dto, // writer and password check 하기
            @RequestParam(value = "page", defaultValue = "0") Long page, // 조회 페이지 시작 페이지 0부터 시작 쿼리파람
            @RequestParam(value = "limit", defaultValue = "5") Long limit // 한페이지당 리미트

    ){
        return negotiationService.masterService(itemId, dto, page, limit);
    }
    // negotiation writer and password 자신이 등록한 구매 제안 다른 아이템에도 등록 제안이 가능하다 페이지 지원
    // RequestParam 이용하기
    @GetMapping ("/user") // 사용자
    public Page<NegotiationDto> search(@RequestParam("writer")String writer,
                                               @RequestParam("password") String password,
                                               @RequestParam(value = "page", defaultValue = "0") Long page,
                                               @RequestParam(value = "limit", defaultValue = "5") Long limit
    ){
       return negotiationService.search(writer, password, page, limit);
    }
    // 3. 등록된 제안은 수정이 가능하다.
    // 이때, 제안이 등록될때 추가한 작성자와 비밀번호를 첨부해야 한다.

    // 5. 대상 물품의 주인은 구매 제안을 수락할 수 있다.
    // 이를 위해서 제안의 대상 물품을 등록할 때 사용한 작성자와 비밀번호를 첨부해야 한다.
    // 이때 구매 제안의 상태는 수락이 된다.
    // 6. 대상 물품의 주인은 구매 제안을 거절할 수 있다.
    // 이를 위해서 제안의 대상 물품을 등록할 때 사용한 작성자와 비밀번호를 첨부해야 한다.
    // 이때 구매 제안의 상태는 거절이 된다.
    // 7. 구매 제안을 등록한 사용자는, 자신이 등록한 제안이 수락 상태일 경우, 구매 확정을 할 수 있다.
    // 이를 위해서 제안을 등록할 때 사용한 작성자와 비밀번호를 첨부해야 한다.
    // 이때 구매 제안의 상태는 확정 상태가 된다.
    // 구매 제안이 확정될 경우, 대상 물품의 상태는 판매 완료가 된다.
    // 구매 제안이 확정될 경우, 확정되지 않은 다른 구매 제안의 상태는 모두 거절이 된다.
    @PutMapping("/{proposalId}")
    public ResponseDto update(@PathVariable("proposalId") Long proposalId, @RequestBody NegotiationDto dto){
        ResponseDto response = new ResponseDto();
        if (dto.getStatus() == null){
            response.setMessage("제안이 수정되었습니다.");
        }
        else if ((dto.getStatus().equals("수락")) || (dto.getStatus().equals("거절")) )
            response.setMessage("제안의 상태가 변경되었습니다.");
        else if (dto.getStatus().equals("확정"))
            response.setMessage("구매가 확정되었습니다.");

        negotiationService.update(proposalId, dto);
        return response;
    }

    // 4. 등록된 제안은 삭제가 가능하다.
    // 이때, 제안이 등록될때 추가한 작성자와 비밀번호를 첨부해야 한다.
    @DeleteMapping("/{proposalId}")
    public ResponseDto delete(@PathVariable("proposalId") Long proposalId,
                              @RequestBody NegotiationCheckDto dto){
        ResponseDto response = new ResponseDto();
        response.setMessage("제안을 삭제했습니다.");
        negotiationService.delete(proposalId, dto);
        return response;
    }
    // 7. 구매 제안을 등록한 사용자는, 자신이 등록한 제안이 수락 상태일 경우, 구매 확정을 할 수 있다.
    // 이를 위해서 제안을 등록할 때 사용한 작성자와 비밀번호를 첨부해야 한다.
    // 이때 구매 제안의 상태는 확정 상태가 된다.
    // 구매 제안이 확정될 경우, 대상 물품의 상태는 판매 완료가 된다.
    // 구매 제안이 확정될 경우, 확정되지 않은 다른 구매 제안의 상태는 모두 거절이 된다.
}
