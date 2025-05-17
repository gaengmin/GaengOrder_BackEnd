package tableOrder.category.mapper;

import org.apache.ibatis.annotations.*;
import tableOrder.category.dto.request.RequestCategoryDto;

@Mapper
public interface CategoriesMapper {
    //카테고리 존재 수
    @Select("SELECT COUNT(*) FROM CATEGORIES where store_no = #{storeNo}")
    int getCategoriesCnt(@Param("storeNo") Long storeNo);


    //카테고리 번호로 매장 등록번호 찾기
    @Select("SELECT STORE_NO FROM categories WHERE categories_no = #{categoriesNo}")
    Long getStoreNoByCategoryNo(@Param("categoriesNo") Long categoriesNo);


    //사업자 번호로 매장 id 찾기
    @Select("SELECT user_id FROM users WHERE store_no = #{userStoreNo} and user_id = #{userId}")
    String findByStoreUserId(@Param("userStoreNo") Long userStoreNo, @Param("userId") String userid);

    //데이터 삽입시
    @Insert("INSERT INTO categories (store_no, name, position) VALUES (#{storeNo}, #{name}, #{position})")
    void insertCategory(@Param("storeNo") Long storeNo,
                        @Param("name") String name,
                        @Param("position") int position);

    //카테고리 소프트 삭제
    @Update("UPDATE CATEGORIES SET soft_delete = 'Y' Where categories_no = #{value}")
    void softDeleteCategoriesByNo(Long id);

    //카테고리 명 변경
    @Update("UPDATE CATEGORIES SET name = #{name} WHERE categories_no = #{categoriesNo}")
    Integer changeCategoriesName(@Param("categoriesNo") Long categoriesNo, @Param("name") String name);

    // 카테고리 존재 여부 확인 (1:존재, 0:없음)
    @Select("SELECT COUNT(*) FROM categories WHERE categories_no = #{categoryNo} AND soft_delete = 'N'")
    Integer existsByCategoryNo(@Param("categoryNo") Long categoryNo);

    // 카테고리 존재 여부 확인(1: 존재, 0: 없음)
    @Select("SELECT COUNT(*) FROM CATEGORIES WHERE store_no = #{storeNo} AND name = #{name} AND soft_delete = 'N'")
    int countByStoreNoAndName(@Param("storeNo") Long storeNo, @Param("name") String name);

    // 카테고리 순서
    @Update("UPDATE categories SET position = #{position} WHERE categories_no = #{categoriesNo}")
    void updateCategoryOrder(@Param("categoriesNo") Long categoriesNo, @Param("position") int position);
}
