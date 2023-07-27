package com.example.miniproject_basic_baejeu.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NonNull;


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

    @NonNull
    private String writer; // 작성자
    @NonNull
    private String password; // 비밀번호

    public MarketEntity() {

    }
}
