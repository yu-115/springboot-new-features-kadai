package com.example.samuraitravel.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.samuraitravel.entity.Review;


public interface ReviewRepository extends JpaRepository<Review, Integer> {
	List<Review> findByHouseId(Integer houseId);
	List<Review> findByUserId(Integer userId);
	List<Review> findByHouseIdAndUserId(Integer houseId, Integer userId);
	Page<Review> findByHouseId(Integer houseId, Pageable pageable);

}
