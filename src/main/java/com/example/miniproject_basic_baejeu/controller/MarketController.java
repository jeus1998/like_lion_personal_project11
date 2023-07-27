package com.example.miniproject_basic_baejeu.controller;

import com.example.miniproject_basic_baejeu.dto.MarketDto;
import com.example.miniproject_basic_baejeu.dto.ResponseDto;
import com.example.miniproject_basic_baejeu.service.MarketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class MarketController {

    private final MarketService service;

    // POST create Market
    // 1. 누구든지 중고 거래 목적으로 물품에 대한 정보를 등록할 수 있다.
    // 필수 포함 목록 : 제목, 설명, 최소 가격, 작성자, 비밀번호 유효성 검사 항목에서 제약조건 넣기
    // 최초로 물품이 등록될 때 중고 물품의 상태 STATUS 는 판매중 상태가 된다.
    @PostMapping
    public ResponseDto create(@Valid @RequestBody MarketDto dto){
        ResponseDto responseDto = new ResponseDto();
        responseDto.setMessage("등록이 완료되었습니다.");
        service.createMarket(dto);
        return responseDto;
    }
    // Get ReadALL Market 전체조회  (페이지 단위 전체 조회)  2. 등록된 물품 정보는 누구든지 열람 가능하다.
    @GetMapping
    public Page<MarketDto> readPage(
            @RequestParam(value = "page", defaultValue = "0") Long page,
            @RequestParam(value = "limit", defaultValue = "1") Long limit
    ){
        return service.readMarketAll(page, limit);
    }
    // Get Read Market 단일 조회
    @GetMapping( "/{itemId}")
    public MarketDto read (@PathVariable("itemId") Long id){
        return service.readMarket(id);
    }

    // PUT update
    // 3. 등록된 물품 정보는 수정이 가능하다. 이때 물품이 등록될 때 추가한 비밀번호를 첨부해야 한다.
    // 경로에 password 넣는 방법.. not restful 하다.
    @PutMapping("/{itemId}")
    public ResponseDto update (@PathVariable("itemId") Long id,
                               @RequestBody  MarketDto dto
                               ){
        service.updateMarket(dto , id);
        ResponseDto responseDto = new ResponseDto();
        responseDto.setMessage("물품이 수정되었습니다.");
        return responseDto;
    }
    // DELETE
    // 5. 등록된 물품 정보는 삭제가 가능하다. // 이때, 물품이 등록될 때 추가한 비밀번호를 첨부해야한다.
    @DeleteMapping("/{itemId}")
    public ResponseDto delete (@RequestBody MarketDto dto,    // 1. Writer랑 password를 받는 전용 dto를 만든다. 즉 클레스 생성 2.기존의 dto를 사용한다. 22
                               @PathVariable("itemId") Long id){
        service.deleteMarket(dto, id);
        ResponseDto response = new ResponseDto();
        response.setMessage("물품을 삭제했습니다.");
        return response;
    }
    // 4. 등록된 물품 정보에 이미지를 첨부할 수 있다. imgae_url
    // 이때  물품이 등록될 때 추가한 비밀번호를 첨부해야 한다. 이미지 관리 방법은 자율이다.
    @PutMapping(
            value= "/image/{itemId}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseDto updateImage(@RequestParam("password") String password,
                                 @PathVariable("itemId") Long id,
                                 @RequestParam("image") MultipartFile Image
    ){
        ResponseDto responseDto = new ResponseDto();
        responseDto.setMessage("이미지가 등록되었습니다.");
        service.updateMarketImage(password, Image , id);
        return responseDto;
    }
}
