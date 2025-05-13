package tableOrder.menu.mapper;

import org.apache.ibatis.annotations.*;
import tableOrder.menu.dto.request.RequestMenuDto;

@Mapper
public interface MenuMapper {

    /**동적처리로 해서 메뉴판매상태를 조절 -> N이면 , Y-> N으로 */
    int updateMenuStatus(Long menuNo);

    @Insert("INSERT INTO MENU (categories_no, menu_name, menu_price, description) " +
            "VALUES (#{categoriesNo}, #{menuName}, #{menuPrice}, #{description})")
    int addMenu(RequestMenuDto.AddMenuDto addMenuDto);

    @Select("select count(*) from MENU where categories_no = #{value}")
    int cntByMenu(Long categoriesNo);

    @Update("UPDATE MENU SET soft_delete = 'Y' Where categories_no = #{value}")
    void softDeleteMenuByCategoriesNo(Long categoriesNo);

    int updateMenuData(@Param("menuNo") Long menuNo, @Param("dto") RequestMenuDto.UpdateMenuDto dto);
}
