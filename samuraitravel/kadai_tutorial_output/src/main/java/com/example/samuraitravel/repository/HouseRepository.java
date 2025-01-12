package com.example.samuraitravel.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.samuraitravel.entity.House;

public interface HouseRepository extends JpaRepository<House, Integer> {
	//jpaリポジトリの引数となる<エンティティのクラス型, 主キーのデータ型>は
	//クラス型はエンティティとして作成したのがHouseクラスなのでHouse、
	//データ型は、エンティティでマッピング（紐づけ）したのがhousesテーブルであり、
	//housesテーブル内の主キーidがint型なので参照型のIntegerを指定している。
	
	public Page<House> findByNameLike(String keyword, Pageable pageable);
	//Likeキーワード使用。findByNameLikeというメソッドを定義することで、SQLのLIKE句と同様のクエリを実行可能。

	public Page<House> findByNameLikeOrAddressLikeOrderByCreatedAtDesc(String nameKeyword, String addressKeyword, Pageable pageable);
	public Page<House> findByNameLikeOrAddressLikeOrderByPriceAsc(String nameKeyword, String addressKeyword, Pageable pageable);
	public Page<House> findByAddressLikeOrderByCreatedAtDesc(String area, Pageable pageable);
	public Page<House> findByAddressLikeOrderByPriceAsc(String area, Pageable pageable);
	public Page<House> findByPriceLessThanEqualOrderByCreatedAtDesc(Integer price, Pageable pageable);
	public Page<House> findByPriceLessThanEqualOrderByPriceAsc(Integer price, Pageable pageable);
	public Page<House> findAllByOrderByCreatedAtDesc(Pageable pageable);
	public Page<House> findAllByOrderByPriceAsc(Pageable pageable);
	
	public List<House> findTop10ByOrderByCreatedAtDesc();
}
