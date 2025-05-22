package tableOrder.menu.mapper;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.apache.ibatis.annotations.*;
import org.springframework.security.core.parameters.P;
import org.springframework.transaction.annotation.Transactional;
import tableOrder.category.dto.response.ResponseCategoryDto;
import tableOrder.menu.dto.request.RequestMenuDto;
import tableOrder.menu.dto.response.ResponseMenuDto;

import java.util.List;

@Mapper
public interface MenuMapper {


    /**
     * MenuMapper.xml
     * [메뉴 상태 토글]
     * - menu_status를 Y <-> N으로 전환
     * - soft_delete='N'인 메뉴만 대상
     */
    int updateMenuStatus(Long menuNo);

    /**
     * [신규 메뉴 등록]
     * - position: 해당 카테고리의 마지막 순서+1 값 필요
     * - soft_delete 기본값 'N'으로 설정됨
     */
    @Insert("INSERT INTO MENU (categories_no, menu_name, menu_price, description, position) " +
            "VALUES (#{addMenuDto.categoriesNo}, #{addMenuDto.menuName}, #{addMenuDto.menuPrice}, #{addMenuDto.description}, #{position})")
    int addMenu(@Param("addMenuDto") RequestMenuDto.AddMenuDto addMenuDto, @Param("position") int position);

    /**
     * [카테고리별 활성 메뉴 수 조회]
     * - soft_delete='N'인 메뉴만 카운트
     * - 실제 노출 가능한 메뉴 수 확인용
     */
    @Select("SELECT COUNT(*) FROM MENU WHERE categories_no = #{value} AND soft_delete = 'N'")
    int countActiveMenusByCategoryNo(Long categoriesNo);

    /**
     * [활성 메뉴 존재 여부 확인]
     * - 삭제되지 않은 메뉴만 대상으로 존재 여부 체크
     * - 메뉴 조회 전 유효성 검증용
     */
    @Select("SELECT COUNT(*) FROM menu WHERE menu_no = #{menuNo} AND soft_delete = 'N'")
    int countActiveMenuByMenuNo(Long menuNo);


    /**
     * [메뉴명 중복 검사]
     * - 동일 카테고리 내에서 삭제되지 않은 메뉴 기준
     * - (categoriesNo, menuName) 복합 유니크 검증
     */
    @Select("SELECT COUNT(*) FROM menu WHERE categories_no = #{categoriesNo} AND menu_name = #{menuName} AND soft_delete = 'N'")
    int countByStoreNoAndMenuName(@Param("categoriesNo") Long categoriesNo, @Param("menuName") String menuName);

    @Select("SELECT COUNT(*) FROM MENU WHERE menu_name = #{menuName}")
    int countByMenuName(@Param("menuName") String menuName);


    /**
     * [카테고리 연관 메뉴 일괄 삭제]
     * - 특정 카테고리 소속 모든 메뉴 soft_delete 처리
     * - 주의: 실제 삭제가 아닌 논리 삭제
     */
    @Update("UPDATE MENU SET soft_delete = 'Y' WHERE categories_no = #{value}")
    void softDeleteMenuByCategoriesNo(Long categoriesNo);

    @Update("UPDATE MENU SET soft_delete = 'Y', menu_status = 'Y',menu_name = #{menuName} WHERE menu_no = #{menuNo}")
    void softDeleteAndRenameMenu(@Param("menuNo") Long menuNo, @Param("menuName") String menuName);



    @Update("UPDATE MENU SET POSITION = #{position} WHERE MENU_NO = #{menuNo}")
    void updateMenuOrder(@Param("menuNo") Long menuNo, @Param("position") int position);

    /**
     * [메뉴 순서 벌크 업데이트]
     * - XML에서 <foreach>로 동적 쿼리 생성
     * - PositionMenuDto 리스트 필요 (menuNo, position 포함)
     */
    void updateMenuForEachOrder(@Param("positions") List<RequestMenuDto.PositionMenuDto> positions);

    @Select("SELECT categories_no FROM menu where menu_no = #{menuNo} and soft_delete='N'")
    Long findCategoryNoByMenuNo(Long menuNo);

    @Select("SELECT menu_name from menu where menu_no = #{menuNo} and soft_delete='N'")
    String findMenuNameByMenuNo(Long menuNo);

    @Select("SELECT count(*) from menu where menu_no = #{menuNo} and soft_delete='N'")
    int countByMenuNo(Long menuNo);

    void updateMenuData(@Param("menuNo") Long menuNo,@Param("dto") RequestMenuDto.UpdateMenuDto updateMenudto);

    @Select("SELECT position from menu where menu_no = #{menuNo}")
    int findMenuPositionByMenuNo(Long menuNo);

    @Update("UPDATE menu " +
            "SET position = position - 1 " +
            "WHERE categories_no = #{currentCategory} " +
            "AND position > #{currentMenuPosition} " +
            "AND soft_delete = 'N'")
    void adjustPositionAfterDeletion(@Param("currentCategory") Long currentCategory, @Param("currentMenuPosition") int currentMenuPosition);


    /**메뉴 반환 쿼리*/
    List<ResponseCategoryDto.ResponseListMenuDto> findMenusByCategorySorted(@Param("categoriesNo")int categoriesNo, @Param("storeNo") int storeNo);

    ResponseMenuDto.ResponseMenuDetailDto getMenuDataDetails(Long menuId);


    List<ResponseMenuDto.ResponseMenuDataDto> searchMenusForLoadMore(@Param("storeNo")Long storeNo, @Param("keyword")String keyword, @Param("size")int size, @Param("offset")int offset);

    /**메뉴 존재 여부*/
    @Select("SELECT count(*) FROM MENU WHERE menu_no = #{menuNo}")
    int existMenuByNo(@Param("menuNo") Long menuNo);
}
