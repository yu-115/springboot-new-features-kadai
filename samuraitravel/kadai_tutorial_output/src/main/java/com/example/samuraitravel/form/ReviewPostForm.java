package com.example.samuraitravel.form;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ReviewPostForm {
	@NotBlank(message = "評価点数を選択してください。")
	private String score;
	
	@NotBlank(message = "コメントを入力してください。")
	private String comment;

}
