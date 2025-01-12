package com.example.samuraitravel.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.samuraitravel.entity.Favorite;
import com.example.samuraitravel.entity.House;
import com.example.samuraitravel.entity.User;
import com.example.samuraitravel.repository.FavoriteRepository;
import com.example.samuraitravel.repository.HouseRepository;
import com.example.samuraitravel.repository.UserRepository;
import com.example.samuraitravel.security.UserDetailsImpl;
import com.example.samuraitravel.service.FavoriteService;

@Controller
@RequestMapping("/")
public class FavoriteController {
	private final FavoriteRepository favoriteRepository;
	private final HouseRepository houseRepository;
	private final UserRepository userRepository;
	private final FavoriteService favoriteService;
	
	public FavoriteController(FavoriteRepository favoriteRepository, HouseRepository houseRepository, UserRepository userRepository, FavoriteService favoriteService) {
		this.favoriteRepository = favoriteRepository;
		this.houseRepository = houseRepository;
		this.userRepository = userRepository;
		this.favoriteService = favoriteService;
	}
	
	@GetMapping("/favorite")
	public String index(Model model, @PageableDefault(page = 0, size = 10, sort = "id", direction = Direction.ASC) Pageable pageable, @AuthenticationPrincipal UserDetailsImpl userDetailsImpl) {
		User user = userRepository.getReferenceById(userDetailsImpl.getUser().getId());
		Integer userId = user.getId();
		List<Favorite> favorites = favoriteRepository.findByUserId(userId);
		Page<Favorite> favoritePage = favoriteRepository.findByUserId(userId, pageable);
		
		Map<Integer, House> houseMap = new HashMap<>();
		for (Favorite favorite : favorites) {
			House house = houseRepository.findById(favorite.getHouseId()).orElse(null);
			if (house != null) {
				houseMap.put(favorite.getHouseId(), house);
			}
		}
		
		model.addAttribute("favorites", favorites);
		model.addAttribute("favoritePage", favoritePage);
		model.addAttribute("houseMap", houseMap);

		return "favorites/index";
	}
	
	@PostMapping("/houses/{id}/favoriteRegister")
	public String favoriteRegister(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes, @AuthenticationPrincipal UserDetailsImpl userDetailsImpl, Model model) {
		
		favoriteService.favoriteRegister(id, userDetailsImpl);
		redirectAttributes.addFlashAttribute("successMessage", "お気に入りに登録しました。");
		
		return "redirect:/houses/{id}";
	}
	
	@PostMapping("/houses/{id}/favoriteDelete")
	public String favoriteDelete(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes, @AuthenticationPrincipal UserDetailsImpl userDetailsImpl, Model model) {
		
		favoriteService.favoriteDelete(id, userDetailsImpl);
		redirectAttributes.addFlashAttribute("successMessage", "お気に入りを解除しました。");
		
		return "redirect:/houses/{id}";
	}

}
