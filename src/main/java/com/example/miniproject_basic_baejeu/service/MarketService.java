package com.example.miniproject_basic_baejeu.service;

import com.example.miniproject_basic_baejeu.dto.MarketDto;
import com.example.miniproject_basic_baejeu.entity.MarketEntity;
import com.example.miniproject_basic_baejeu.repository.MarketRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;


@Slf4j
@Service
@RequiredArgsConstructor
public class MarketService {

    private final MarketRepository marketRepository;
    public MarketDto createMarket(MarketDto dto){
        MarketEntity marketEntity = new MarketEntity();
        marketEntity.setStatus("판매중");
        marketEntity.setTitle(dto.getTitle());
        marketEntity.setDescription(dto.getDescription());
        marketEntity.setMin_price_wanted(dto.getMin_price_wanted());
        marketEntity.setWriter(dto.getWriter());
        marketEntity.setPassword(dto.getPassword());
       return MarketDto.fromEntity(marketRepository.save(marketEntity));
    }
    // 전체 페이징 조회
    public Page<MarketDto> readMarketAll(Long page, Long limit) {
        Pageable pageable = PageRequest.of(Math.toIntExact(page), Math.toIntExact(limit));
        Page<MarketEntity> MarketEntityPage = marketRepository.findAll(pageable);
        Page<MarketDto> MarketDtoPage = MarketEntityPage.map(MarketDto :: fromEntity);
        return MarketDtoPage;
    }
    // 단일 조회
    public MarketDto readMarket(Long id){
        Optional<MarketEntity> optionalMarket = marketRepository.findById(id);
        if (optionalMarket.isPresent())
            return MarketDto.fromEntity(optionalMarket.get());
        // 아니면 404 에러
        throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }
    // 업데이트
    public MarketDto updateMarket(MarketDto dto, Long id) {
        Optional<MarketEntity> optionalMarket = marketRepository.findById(id);
        if (optionalMarket.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        MarketEntity marketEntity = optionalMarket.get(); // 어떤 내용을 업데이트 하고싶은지 모른다.
        if (!dto.getPassword().equals(marketEntity.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        marketEntity.setPassword(dto.getPassword() != null ? dto.getPassword() : marketEntity.getPassword());
        marketEntity.setTitle(dto.getTitle() != null ? dto.getTitle() : marketEntity.getTitle());
        marketEntity.setDescription(dto.getDescription() != null ? dto.getDescription() : marketEntity.getDescription());
        marketEntity.setWriter(dto.getWriter() != null ? dto.getWriter() : marketEntity.getWriter());
        marketEntity.setMin_price_wanted(dto.getMin_price_wanted() != null ? dto.getMin_price_wanted() : marketEntity.getMin_price_wanted());
        marketEntity.setImage_url(dto.getImage_url() != null ? dto.getImage_url() : marketEntity.getImage_url());
        marketEntity.setStatus(dto.getStatus() != null ? "판매중" : marketEntity.getStatus());

        return MarketDto.fromEntity(marketRepository.save(marketEntity));
    }

    // 삭제
    public void deleteMarket(MarketDto dto, Long id){
        Optional<MarketEntity> optionalMarket = marketRepository.findById(id);
        if (optionalMarket.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        MarketEntity marketEntity = optionalMarket.get();
        if ((dto.getPassword().equals(marketEntity.getPassword())) && (dto.getWriter().equals(marketEntity.getWriter()))){
            marketRepository.deleteById(id);
        }
        else throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }
    public MarketDto updateMarketImage(String password,  MultipartFile Image, Long id ){
        Optional<MarketEntity> optionalMarket = marketRepository.findById(id);
        if (optionalMarket.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        MarketEntity marketEntity = optionalMarket.get();
        if (!(password.equals(marketEntity.getPassword())) ) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        else {
            // 2-1. 폴더만 만드는 과정
            String profileDir = String.format("media/%d/", id);
            try {
                Files.createDirectories(Path.of(profileDir));
            } catch (IOException e) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
            }
            // 2-2. 확장자를 포함한 이미지 이름 만들기
            String originalFilename = Image.getOriginalFilename();
            String[] fileNameSplit = originalFilename.split("\\.");
            String extension = fileNameSplit[fileNameSplit.length - 1];
            String profileFilename = "items." + extension;

            // 2-3. 폴더와 파일 경로를 포함한 이름 만들기
            String profilePath = profileDir + profileFilename;

            // 3. MultipartFile 을 저장하기
            try {
                Image.transferTo(Path.of(profilePath));
            } catch (IOException e) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
            }
            // 4. UserEntity 업데이트 (정적 프로필 이미지를 회수할 수 있는 URL)
            marketEntity.setImage_url(String.format("/static/%d/%s", id, profileFilename));
            return MarketDto.fromEntity(marketRepository.save(marketEntity));
        }
    }
}
