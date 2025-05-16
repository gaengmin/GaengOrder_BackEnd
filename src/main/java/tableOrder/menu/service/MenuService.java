package tableOrder.menu.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tableOrder.auth.util.AbstractAuthValidator;
import tableOrder.auth.util.SecurityUtil;
import tableOrder.category.mapper.CategoriesMapper;
import tableOrder.menu.dto.request.RequestMenuDto;
import tableOrder.menu.mapper.MenuMapper;

import java.util.List;

@Service
public class MenuService extends AbstractAuthValidator {
    private final MenuMapper menuMapper;


    @Autowired
    public MenuService(CategoriesMapper categoriesMapper, MenuMapper menuMapper) {
        super(categoriesMapper);
        this.menuMapper = menuMapper;
    }

    /*
        * -메뉴를 등록하는 메소드
            private Long categoriesNo;
            private String menuName;
            private Integer menuPrice;
            private String description;
        * */
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void registrationMenu(@Valid RequestMenuDto.AddMenuDto addMenuDto) {
        Long userStoreNo = SecurityUtil.getCurrentUsersStoreNo();
        String userId = SecurityUtil.getCurrentUserId();

        //공통으로 권한 체크
        verifyStoreOwner(userStoreNo,userId,"메뉴 등록");

        //중복 메뉴명 체크
        int duplicateCount = menuMapper.countByStoreNoAndMenuName(addMenuDto.getCategoriesNo(), addMenuDto.getMenuName().trim());
        if (duplicateCount > 0) {
            throw new IllegalArgumentException("이미 존재하는 메뉴명입니다."+addMenuDto.getMenuName());
        }

        //메뉴순서정보 찾고 넣기
        int cntPosition = menuMapper.cntPosition(addMenuDto.getCategoriesNo());
        int newPosition = cntPosition + 1;
        int cnt = menuMapper.addMenu(addMenuDto, newPosition);

        if(cnt ==0){
            throw new IllegalArgumentException("메뉴 등록에 실패하였습니다");
        }
    }

    public void updateMenuStatus(Long menuNo){

    }


    public void updateMenuData(Long menuNo, RequestMenuDto.UpdateMenuDto updateMenudto) {

    }


    public void repositionMenuData(List<Long> menuPositions) {
    }
}
