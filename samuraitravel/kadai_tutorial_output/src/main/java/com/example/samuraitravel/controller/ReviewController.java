package com.example.samuraitravel.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.samuraitravel.entity.House;
import com.example.samuraitravel.entity.Review;
import com.example.samuraitravel.entity.User;
import com.example.samuraitravel.form.ReviewEditForm;
import com.example.samuraitravel.form.ReviewPostForm;
import com.example.samuraitravel.repository.HouseRepository;
import com.example.samuraitravel.repository.ReviewRepository;
import com.example.samuraitravel.repository.UserRepository;
import com.example.samuraitravel.security.UserDetailsImpl;
import com.example.samuraitravel.service.ReviewService;

@Controller
@RequestMapping("/houses/{id}/reviews")
public class ReviewController {
	private final ReviewRepository reviewRepository;
	private final HouseRepository houseRepository;
	private final UserRepository userRepository;
	private final ReviewService reviewService;
	
	public ReviewController(ReviewRepository reviewRepository, HouseRepository houseRepository, UserRepository userRepository, ReviewService reviewService) {
		this.reviewRepository = reviewRepository;
		this.houseRepository = houseRepository;
		this.userRepository = userRepository;
		this.reviewService = reviewService;
	}
	
	@GetMapping
	public String index(@PathVariable(name = "id") Integer id, Model model, @PageableDefault(page = 0, size = 10, sort = "id", direction = Direction.ASC) Pageable pageable, @AuthenticationPrincipal UserDetailsImpl userDetailsImpl) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		
		Page<Review> reviewPage = reviewRepository.findByHouseId(id, pageable);
		House house = houseRepository.getReferenceById(id);
		
		//ログイン済みの場合のみ実行（未ログインの場合、ユーザー情報を読み込まない）
		SecurityContextHolder.getContext().getAuthentication(); if (authentication != null && authentication.isAuthenticated() && !(authentication.getPrincipal() instanceof String)) {
			User user = userRepository.getReferenceById(userDetailsImpl.getUser().getId());
			Integer userId = user.getId();
			model.addAttribute("userId", userId);
		}
		
		model.addAttribute("reviewPage", reviewPage);
		model.addAttribute("house", house);
		
		return "reviews/index";
	}
	
	@GetMapping("/post")
	public String post(@PathVariable(name = "id") Integer id, Model model) {
		House house = houseRepository.getReferenceById(id);
		
		model.addAttribute("house", house);
		model.addAttribute("reviewPostForm", new ReviewPostForm());
		
		return "reviews/post";
	}
	
	@PostMapping("/create")
	public String create(@PathVariable(name = "id") Integer id, @ModelAttribute @Validated ReviewPostForm reviewPostForm, BindingResult bindingResult, RedirectAttributes redirectAttributes, @AuthenticationPrincipal UserDetailsImpl userDetailsImpl, Model model) {
		House house = houseRepository.findById(id).orElse(null);
		if (bindingResult.hasErrors()) {
			model.addAttribute("house", house);
			return "reviews/post";
		}
		
		reviewService.create(reviewPostForm, id, userDetailsImpl);
		redirectAttributes.addFlashAttribute("successMessage", "レビューを投稿しました。");
		
		return "redirect:/houses/{id}";
	}
	
	@GetMapping("/{userId}")
	public String edit(@PathVariable(name = "id") Integer id, @PathVariable(name = "userId") Integer userId, Model model) {
		House house = houseRepository.getReferenceById(id);
		List<Review> reviews = reviewRepository.findByHouseIdAndUserId(id, userId);
		
		Review review = reviews.stream().findFirst().orElse(null);//取得したレビューリストから最初の要素のみを取得（1つしか存在しない前提）
		
		ReviewEditForm reviewEditForm = new ReviewEditForm(review.getId(), review.getScore(), review.getReviewText());
		
		model.addAttribute("house", house);
		model.addAttribute("reviews", reviews);
		model.addAttribute("reviewEditForm", reviewEditForm);
		
		return "reviews/edit";
	}
	
	@PostMapping("/{userId}/update")
	public String update(@PathVariable(name = "id") Integer id, @PathVariable(name = "userId") Integer userId, @ModelAttribute @Validated ReviewEditForm reviewEditForm, BindingResult bindingResult, RedirectAttributes redirectAttributes, @AuthenticationPrincipal UserDetailsImpl userDetailsImpl, Model model) {
		House house = houseRepository.findById(id).orElse(null);
		if (bindingResult.hasErrors()) {
			model.addAttribute("house", house);
			return "reviews/edit";
		}
		
		reviewService.update(reviewEditForm, id, userId, userDetailsImpl);
		redirectAttributes.addFlashAttribute("successMessage", "レビューを編集しました。");
		
		return "redirect:/houses/{id}/reviews";
	}
	
	@PostMapping("/{userId}/delete")
	public String delete(@PathVariable(name = "id") Integer id, @PathVariable(name = "userId") Integer userId, @ModelAttribute @Validated ReviewEditForm reviewEditForm, BindingResult bindingResult, RedirectAttributes redirectAttributes, @AuthenticationPrincipal UserDetailsImpl userDetailsImpl, Model model) {
		List<Review> reviews = reviewRepository.findByHouseIdAndUserId(id, userId);
		Review review = reviews.stream().findFirst().orElse(null);
		reviewRepository.deleteById(review.getId());
		
		redirectAttributes.addFlashAttribute("successMessage", "レビューを削除しました。");
		
		return "redirect:/houses/{id}";
	}
	
}
