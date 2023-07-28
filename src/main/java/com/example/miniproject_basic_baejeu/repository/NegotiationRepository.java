package com.example.miniproject_basic_baejeu.repository;

import com.example.miniproject_basic_baejeu.entity.NegotiationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
public interface NegotiationRepository extends JpaRepository<NegotiationEntity, Long> {
    List<NegotiationEntity> findByPasswordAndWriter(String password, String writer);
    @Query("SELECT n FROM NegotiationEntity n " +
            "JOIN n.salesItem m " +
            "WHERE n.status = :status AND m.id = :itemId")
    List<NegotiationEntity> findByStatusAndItemId(@Param("status") String status, @Param("itemId") Long itemId);

}



