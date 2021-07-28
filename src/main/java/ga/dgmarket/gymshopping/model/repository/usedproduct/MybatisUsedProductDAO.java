package ga.dgmarket.gymshopping.model.repository.usedproduct;

import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import ga.dgmarket.gymshopping.domain.UsedFavorites;
import ga.dgmarket.gymshopping.domain.UsedProduct;
import ga.dgmarket.gymshopping.domain.UsedProductExtend;
import ga.dgmarket.gymshopping.exception.DMLException;

@Repository
public class MybatisUsedProductDAO implements UsedProductDAO{
	@Autowired
	private SqlSessionTemplate sqlSessionTemplate;
	
	//중고거래 상품을 등록하는 메서드. 이미지는 별도로 업로드 from.성일
	public UsedProduct insert(UsedProduct usedProduct) throws DMLException{
		int result = sqlSessionTemplate.insert("UsedProductExtend.insert", usedProduct);
		if(result == 0) {
			 throw new DMLException("상품 등록 실패");
		}else {			
			return usedProduct;
		}
	}

	//중고 상품 전체 조회
	public List selectAll(int member_id) {
		return sqlSessionTemplate.selectList("UsedProductExtend.selectAll", member_id);
	}
	
	//상품 상세 목록 가져오기
	public UsedProductExtend getDetail(UsedProductExtend usedProductExtend) {
		return sqlSessionTemplate.selectOne("UsedProductExtend.getDetail", usedProductExtend);
	}
	
	//상품에 대한 찜 갯수 count
	public UsedFavorites getFavoritesCount(int used_product_id) {
		return sqlSessionTemplate.selectOne("UsedFavorites.getFavoritesCount", used_product_id);
	}
	
	//상품에 대한 태그들 가져오기
	public List getProductTag(int used_product_id) {
		return sqlSessionTemplate.selectList("UsedTag.getProductTag", used_product_id);
	}
}