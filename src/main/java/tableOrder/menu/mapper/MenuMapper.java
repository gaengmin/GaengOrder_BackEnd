package tableOrder.menu.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface MenuMapper {

    @Select("select count(*) from menu where categories_no = #{value}")
    int cntByMenu(Long categoriesNo);

    @Update("UPDATE MENU SET soft_delete = 'Y' Where categories_no = #{value}")
    void softDeleteMenuByCategoriesNo(Long categoriesNo);
}
