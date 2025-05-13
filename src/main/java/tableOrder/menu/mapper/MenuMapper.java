package tableOrder.menu.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import tableOrder.menu.dto.request.RequestMenuDto;

@Mapper
public interface MenuMapper {

    /**동적처리로 해서 메뉴판매상태를 조절 -> N이면 , Y-> N으로 */
/*    @Update("UPDATE MENU SET menu_status ='Y'\n" +
            "where menu_no = 5;")*/

    @Insert("INSERT INTO MENU (categories_no, menu_name, menu_price, description) " +
            "VALUES (#{categoriesNo}, #{menuName}, #{menuPrice}, #{description})")
    void addMenu(RequestMenuDto.AddMenuDto addMenuDto);

    @Select("select count(*) from menu where categories_no = #{value}")
    int cntByMenu(Long categoriesNo);

    @Update("UPDATE MENU SET soft_delete = 'Y' Where categories_no = #{value}")
    void softDeleteMenuByCategoriesNo(Long categoriesNo);


}
