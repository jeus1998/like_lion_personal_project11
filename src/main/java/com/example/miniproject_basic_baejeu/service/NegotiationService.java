package com.example.miniproject_basic_baejeu.service;

import com.example.miniproject_basic_baejeu.dto.MarketDto;
import com.example.miniproject_basic_baejeu.dto.NegotiationDto;
import com.example.miniproject_basic_baejeu.entity.MarketEntity;
import com.example.miniproject_basic_baejeu.entity.NegotiationEntity;
import com.example.miniproject_basic_baejeu.entity.UserEntity;
import com.example.miniproject_basic_baejeu.repository.CommentRepository;
import com.example.miniproject_basic_baejeu.repository.MarketRepository;
import com.example.miniproject_basic_baejeu.repository.NegotiationRepository;
import com.example.miniproject_basic_baejeu.repository.UserRepository;
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
public class NegotiationService {

    public final MarketRepository marketRepository;
    public final CommentRepository commentRepository;
    public final NegotiationRepository negotiationRepository;
    public final UserRepository userRepository;
    public NegotiationDto createNegotiation(NegotiationDto dto, Long itemId , Authentication authentication){
        String currentUser = authentication.getName();
        Optional<UserEntity> optionalUser = userRepository.findByUsername(currentUser);
        UserEntity user = optionalUser.get();
        Optional<MarketEntity> optionalMarket = marketRepository.findById(itemId);
        if (optionalMarket.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        MarketEntity marketEntity = optionalMarket.get();
        NegotiationEntity entity = new NegotiationEntity();
        entity.setSuggested_price(dto.getSuggested_price());
        entity.setStatus("제안 상태");
        entity.setSalesItem(marketEntity);
        entity.setUser(user);
        return NegotiationDto.fromEntity(negotiationRepository.save(entity));
    }
    public Page<NegotiationDto> search (Authentication authentication, Long page, Long limit){

        List<NegotiationEntity> EntityList = negotiationRepository.findAll();
        List<NegotiationEntity> filteredEntityList = new ArrayList<>();
        for (NegotiationEntity target : EntityList) {
            if (target.getUser().getUsername().equals(authentication.getName())) {
                filteredEntityList.add(target);
            }
        }
        if (!filteredEntityList.isEmpty()){
            Pageable pageable = PageRequest.of(Math.toIntExact(page), Math.toIntExact(limit));
            int startIndex = Math.toIntExact(pageable.getOffset());
            int endIndex = Math.min(startIndex + Math.toIntExact(pageable.getPageSize()), filteredEntityList.size());
            List<NegotiationEntity> pagedEntityList = filteredEntityList.subList(startIndex, endIndex);
            List<NegotiationDto> NegotiationDtoList = new ArrayList<>();
            for (NegotiationEntity target : pagedEntityList) {
                NegotiationDtoList.add(NegotiationDto.fromEntity(target));
            }
            return new PageImpl<>(NegotiationDtoList , pageable, filteredEntityList.size());
        }
        else throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    // 업데이트
    public void update(Long proposalId, NegotiationDto dto, Authentication authentication) {
        Optional<NegotiationEntity> entityOptional = negotiationRepository.findById(proposalId);
        if (entityOptional.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        NegotiationEntity negotiationEntity = entityOptional.get();
        Long item_id = negotiationEntity.getSalesItem().getId(); // 제안의 해당 아이템 아이디
        Long id = negotiationEntity.getId(); // 제안 아이디
        if (dto.getStatus() == null) { // 4 단순 가격 수정
            if (negotiationEntity.getUser().getUsername().equals(authentication.getName())) {
                // suggestedPrice만 수정하기
                negotiationEntity.setSuggested_price(dto.getSuggested_price());
                NegotiationDto.fromEntity(negotiationRepository.save(negotiationEntity));
                return; // 메서드 종료
            }
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        } else if (dto.getStatus().equals("수락") || dto.getStatus().equals("거절")) { // 5,6 판매자가 수정 수락 거절
            Optional<MarketEntity> marketEntity = marketRepository.findById(item_id);
            MarketEntity market = marketEntity.get();
            if (market.getUser().getUsername().equals(authentication.getName())) {
                // status만 수정하기
                negotiationEntity.setStatus(dto.getStatus());
                NegotiationDto.fromEntity(negotiationRepository.save(negotiationEntity));
                return; // 메서드 종료
            }
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        } else if (dto.getStatus().equals("확정")) { // 7. 구매자가 확정
            // 1. 비밀번호 작성자 적절하게 첨부하였나
            if (negotiationEntity.getUser().getUsername().equals(authentication.getName())) {
                // 2. 수락 상태인가
                if (negotiationEntity.getStatus().equals("수락")) {
                    // 3. 구매제안상태 -> 확정
                    negotiationEntity.setStatus("확정");
                    NegotiationDto.fromEntity(negotiationRepository.save(negotiationEntity));

                    // 4. 대상 물품의 상태: 판매완료
                    Optional<MarketEntity> marketEntity = marketRepository.findById(item_id);
                    MarketEntity market = marketEntity.get();
                    market.setStatus("판매완료");
                    MarketDto.fromEntity(marketRepository.save(market));

                    // 5. 다른 구매제안의 상태: 거절
                    List<NegotiationEntity> rejectEntity = negotiationRepository.findByStatusAndItemId("제안 상태", item_id);
                    System.out.println(rejectEntity.size());
                    for (NegotiationEntity target : rejectEntity) {
                        target.setStatus("거절");
                        NegotiationDto.fromEntity(negotiationRepository.save(target));
                    }
                    return; // 메서드 종료
                }
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
            }
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }
    // 삭제
    public void delete(Long proposalId, Authentication authentication){
        Optional<NegotiationEntity> entityOptional = negotiationRepository.findById(proposalId);
        if (entityOptional.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        NegotiationEntity negotiationEntity = entityOptional.get();
        if (authentication.getName().equals(negotiationEntity.getUser().getUsername())){
            negotiationRepository.delete(negotiationEntity);
        }
        else throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }

}
