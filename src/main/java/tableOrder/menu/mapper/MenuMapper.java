package tableOrder.menu.mapper;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.apache.ibatis.annotations.*;
import tableOrder.menu.dto.request.RequestMenuDto;

import java.util.List;

@Mapper
public interface MenuMapper {

    //Mapper파일 쿼리 있음
    /**동적처리로 해서 메뉴판매상태를 조절 -> N이면 , Y-> N으로 */
    int updateMenuStatus(Long menuNo);

    /** 메뉴 등록 */
    @Insert("INSERT INTO MENU (categories_no, menu_name, menu_price, description, position) " +
            "VALUES (#{addMenuDto.categoriesNo}, #{addMenuDto.menuName}, #{addMenuDto.menuPrice}, #{addMenuDto.description}, #{position})")
    int addMenu(@Param("addMenuDto") RequestMenuDto.AddMenuDto addMenuDto, @Param("position") int position);

    @Select("select count(*) from MENU where categories_no = #{value}")
    int cntByMenu(Long categoriesNo);

    @Select("select count(*) from menu where menu_no = #{menuNo}")
    int cntByMenuNo(Long menuNo);





    @Update("UPDATE MENU SET soft_delete = 'Y' Where categories_no = #{value}")
    void softDeleteMenuByCategoriesNo(Long categoriesNo);

    int updateMenuData(@Param("menuNo") Long menuNo, @Param("dto") RequestMenuDto.UpdateMenuDto dto);


    /**중복 메뉴명 체크하는 쿼리*/
    @Select("SELECT COUNT(*) FROM menu WHERE categories_no = #{categoriesNo} AND menu_name = #{menuName} AND soft_delete = 'N'")
    int countByStoreNoAndMenuName(@Param("categoriesNo") Long categoriesNo, @Param("menuName") String menuName);


    /** 현재 존재하는 카테고리 수*/
    @Select("SELECT COUNT(*) FROM menu WHERE categories_no = #{categoriesNo} AND soft_delete = 'N'")
    int cntPosition(Long categoriesNo);

 /*   @Update("UPDATE MENU SET POSITION = #{position} WHERE MENU_NO = #{menuNo}")
    void updateMenuOrder(@Param("menuNo") Long menuNo, @Param("position") int position);*/

    /**ForEach을 통한 Update문 작성*/
    void updateMenuForEachOrder(@Param ("positions") List<RequestMenuDto.PositionMenuDto> positions);
}
