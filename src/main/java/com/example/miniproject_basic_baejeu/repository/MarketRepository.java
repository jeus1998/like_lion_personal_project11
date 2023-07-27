package com.example.miniproject_basic_baejeu.repository;

import com.example.miniproject_basic_baejeu.entity.MarketEntity;
import org.springframework.data.jpa.repository.JpaRepository;



public interface MarketRepository extends JpaRepository <MarketEntity, Long> {

}
