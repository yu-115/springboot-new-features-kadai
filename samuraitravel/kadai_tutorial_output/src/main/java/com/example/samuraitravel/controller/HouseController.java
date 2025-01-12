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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.samuraitravel.entity.Favorite;
import com.example.samuraitravel.entity.House;
import com.example.samuraitravel.entity.Review;
import com.example.samuraitravel.entity.User;
import com.example.samuraitravel.form.ReservationInputForm;
import com.example.samuraitravel.repository.FavoriteRepository;
import com.example.samuraitravel.repository.HouseRepository;
import com.example.samuraitravel.repository.ReviewRepository;
import com.example.samuraitravel.repository.UserRepository;
import com.example.samuraitravel.security.UserDetailsImpl;

@Controller
@RequestMapping("/houses")
public class HouseController {
	private final HouseRepository houseRepository;
	private final ReviewRepository reviewRepository;
	private final UserRepository userRepository;
	private final FavoriteRepository favoriteRepository;
	
	public HouseController(HouseRepository houseRepository, ReviewRepository reviewRepository, UserRepository userRepository, FavoriteRepository favoriteRepository) {
		this.houseRepository = houseRepository;
		this.reviewRepository = reviewRepository;
		this.userRepository = userRepository;
		this.favoriteRepository = favoriteRepository;
	}

	@GetMapping
	public String index(@RequestParam(name = "keyword", required = false) String keyword,
						@RequestParam(name = "area", required = false) String area,
						@RequestParam(name = "price", required = false) Integer price,
						@RequestParam(name = "order", required = false) String order,
						@PageableDefault(page = 0, size = 10, sort = "id", direction = Direction.ASC) Pageable pageable,
						Model model)
	{
		Page<House> housePage;
		
		if (keyword != null && !keyword.isEmpty()) {
			if (order != null && order.equals("priceAsc")) {
				housePage = houseRepository.findByNameLikeOrAddressLikeOrderByPriceAsc("%" + keyword + "%", "%" + keyword + "%", pageable);
			} else {
				housePage = houseRepository.findByNameLikeOrAddressLikeOrderByCreatedAtDesc("%" + keyword + "%", "%" + keyword + "%", pageable);
			}
		} else if (area != null && !area.isEmpty()) {
			if (order != null && order.equals("priceAsc")) {
				housePage = houseRepository.findByAddressLikeOrderByPriceAsc("%" + area + "%", pageable);
			} else {
				housePage = houseRepository.findByAddressLikeOrderByCreatedAtDesc("%" + area + "%", pageable);
			}
		} else if (price != null) {
			if (order != null && order.equals("priceAsc")) {
				housePage = houseRepository.findByPriceLessThanEqualOrderByPriceAsc(price, pageable);
			} else {
				housePage = houseRepository.findByPriceLessThanEqualOrderByCreatedAtDesc(price, pageable);
			}
		} else {
			if (order != null && order.equals("priceAsc")) {
				housePage = houseRepository.findAllByOrderByPriceAsc(pageable);
			} else {
				housePage = houseRepository.findAllByOrderByCreatedAtDesc(pageable);
			}
		}
		
		model.addAttribute("housePage", housePage);
		model.addAttribute("keyword", keyword);
		model.addAttribute("area", area);
		model.addAttribute("price", price);
		model.addAttribute("order", order);
		
		return "houses/index";
	}
	
	@GetMapping("/{id}")
	public String show(@PathVariable(name = "id") Integer id, Model model, @AuthenticationPrincipal UserDetailsImpl userDetailsImpl) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		
		House house = houseRepository.getReferenceById(id);
		List<Review> reviews = reviewRepository.findByHouseId(id);
		List<Review> cutReviews;
		
		boolean doubleCheck = false;

		//ログイン済みの場合のみ実行（未ログインの場合、ユーザー情報を読み込まない）
		SecurityContextHolder.getContext().getAuthentication(); if (authentication != null && authentication.isAuthenticated() && !(authentication.getPrincipal() instanceof String)) {
			User user = userRepository.getReferenceById(userDetailsImpl.getUser().getId());
			Integer userId = user.getId();
			model.addAttribute("userId", userId);
			
			List<Favorite> favorites = favoriteRepository.findByUserIdAndHouseId(userId, id);
			model.addAttribute("favorites", favorites);
			System.out.println("Favorites: " + favorites); // リストの内容をログに出力
			
			for (Review review : reviews) {
				if (review.getHouseId().equals(id) && review.getUserId().equals(userId)) {
					doubleCheck = true;
					break;
				}
			}

			model.addAttribute("doubleCheck", doubleCheck);
		}
		
		//リストのサイズをチェックして、10件に制限する
		if (reviews.size() > 10) {
			cutReviews = reviews.subList(0, 10);
			model.addAttribute("cutReviews", cutReviews);
		}
		
		model.addAttribute("house", house);
		model.addAttribute("reviews", reviews);
		model.addAttribute("reservationInputForm", new ReservationInputForm());
		
		return "houses/show";
	}
}
