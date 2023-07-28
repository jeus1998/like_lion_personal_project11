package com.example.miniproject_basic_baejeu.entity;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NonNull;
import org.antlr.v4.runtime.misc.NotNull;

@Data
@Entity
@Table(name = "comment")
public class CommentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

   /* @NonNull
    private Long item_id;*/
    @NonNull
    private String writer;
    @NonNull
    private String password;
    @NonNull
    private String content;

    private String reply;
    public CommentEntity() {

    }
    @ManyToOne(fetch = FetchType.EAGER)
    private MarketEntity salesItem;  // SalesItem ID
    @ManyToOne(fetch = FetchType.EAGER)
    private UserEntity user;


}
