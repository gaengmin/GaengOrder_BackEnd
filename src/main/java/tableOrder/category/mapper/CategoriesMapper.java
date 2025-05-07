package tableOrder.category.mapper;

import org.apache.ibatis.annotations.*;
import tableOrder.category.dto.request.RequestCategoryDto;

@Mapper
public interface CategoriesMapper {
    //사업자 번호로 매장 id 찾기
    @Select("SELECT STORE_NO FROM STORES WHERE BUSINESS_NO = #{businessNo}")
    Long findByStoreNo(@Param("businessNo") String businessNo);

    //
    @Insert("INSERT INTO CATEGORIES (store_no, name) VALUES (#{storeNo}, #{name})")
    void insertCategory(@Param("storeNo") Long storeNo, @Param("name") String name);

    //카테고리 소프트 삭제
    @Update("UPDATE CATEGORIES SET soft_delete = 'Y' Where categories_no = #{value}")
    void softDeleteCategoriesByNo(Long id);

    //카테고리 명 변경
    @Update("UPDATE CATEGORIES SET name = #{name} Where categories_no = #{categoriesNo}")
    Integer changeCategoriesName(RequestCategoryDto.UpdateCategory updateCategory);

    // 카테고리 존재 여부 확인 (1:존재, 0:없음)
    @Select("SELECT COUNT(*) FROM categories WHERE categories_no = #{categoryNo} AND soft_delete = 'N'")
    Integer existsByCategoryNo(@Param("categoryNo") Long categoryNo);

}
