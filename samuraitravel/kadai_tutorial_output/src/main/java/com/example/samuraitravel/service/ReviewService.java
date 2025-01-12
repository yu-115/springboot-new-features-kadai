package com.example.samuraitravel.service;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.samuraitravel.entity.House;
import com.example.samuraitravel.entity.Review;
import com.example.samuraitravel.entity.User;
import com.example.samuraitravel.form.ReviewEditForm;
import com.example.samuraitravel.form.ReviewPostForm;
import com.example.samuraitravel.repository.HouseRepository;
import com.example.samuraitravel.repository.ReviewRepository;
import com.example.samuraitravel.repository.UserRepository;
import com.example.samuraitravel.security.UserDetailsImpl;

@Service
public class ReviewService {
	private final ReviewRepository reviewRepository;
	private final HouseRepository houseRepository;
	private final UserRepository userRepository;
	
	public ReviewService(ReviewRepository reviewRepository, HouseRepository houseRepository, UserRepository userRepository) {
		this.reviewRepository = reviewRepository;
		this.houseRepository = houseRepository;
		this.userRepository = userRepository;
	}

	@Transactional
	public void create(ReviewPostForm reviewPostForm, Integer id, @AuthenticationPrincipal UserDetailsImpl userDetailsImpl) {
		Review review = new Review();
		House house = houseRepository.getReferenceById(id);
		User user = userRepository.getReferenceById(userDetailsImpl.getUser().getId());
		
		review.setHouseId(house.getId());
		review.setUserId(user.getId());
		review.setUserName(user.getName());
		review.setReviewText(reviewPostForm.getComment());
		review.setScore(Integer.parseInt(reviewPostForm.getScore()));
		
		reviewRepository.save(review);
	}
	
	@Transactional
	public void update(ReviewEditForm reviewEditForm, Integer id, Integer userId, @AuthenticationPrincipal UserDetailsImpl userDetailsImpl) {
		List<Review> reviews = reviewRepository.findByHouseIdAndUserId(id, userId);
		Review review = reviews.stream().findFirst().orElse(null);
		House house = houseRepository.getReferenceById(id);
		User user = userRepository.getReferenceById(userDetailsImpl.getUser().getId());
		
		review.setHouseId(house.getId());
		review.setUserId(user.getId());
		review.setUserName(user.getName());
		review.setReviewText(reviewEditForm.getReviewText());
		review.setScore(reviewEditForm.getScore());
		
		reviewRepository.save(review);
	}
}
