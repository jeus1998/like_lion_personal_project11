package com.example.miniproject_basic_baejeu.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

// Spring에 저장하고 싶은 사용자 정보

@Entity
@Table(name = "users")
@Data
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 필수항목
    @Column(nullable = false, unique = true)
    private String username; // 아이디
    @Column(nullable = false, unique = true)
    private String password; // 비밀번호

    // 부수적으로 전화번호, 이메일, 주소 정보를 기입할 수 있다.
    private String address; // 주소
    private String email; // 이메일
    private String phone; // 전화번호

    @OneToMany(mappedBy = "user")
    private List<MarketEntity> salesItemEntityList = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<CommentEntity> commentEntityList = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<NegotiationEntity> negotiationEntityList = new ArrayList<>();
}