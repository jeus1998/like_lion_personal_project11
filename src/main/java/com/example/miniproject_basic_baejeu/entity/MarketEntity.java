package com.example.miniproject_basic_baejeu.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;


@Entity
@Data
@Table(name = "sales_item")
public class MarketEntity {
    //  제목, 설명, 최소 가격, 작성자, 비밀번호
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    private String title; // 제목

    @NonNull
    private String description; // 설명

    private String image_url;

    @NonNull
    private Long min_price_wanted; // 최소 가격

    private String status;

    public MarketEntity() {

    }
    @OneToMany(mappedBy = "salesItem")
    private List<CommentEntity> commentEntityList = new ArrayList<>();

    @OneToMany(mappedBy = "salesItem")
    private List<NegotiationEntity> negotiationList = new ArrayList<>();

    @ManyToOne(fetch = FetchType.EAGER)
    private UserEntity user;

    // mappedBy는 양방향
    // 양쪽의 entity에 모두 저장해야지 정상적으로 테이블에 저장되는걸 확인 가능하다.
    // @JoinColumn은 단방향
}
