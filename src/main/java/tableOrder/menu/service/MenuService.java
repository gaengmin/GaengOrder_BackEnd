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

import java.util.ArrayList;
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
    @PreAuthorize("hasAuthority('ADMIN')")
    public void registrationMenu(@Valid RequestMenuDto.AddMenuDto addMenuDto) {
        Long userStoreNo = SecurityUtil.getCurrentUsersStoreNo();
        String userId = SecurityUtil.getCurrentUserId();


        //공통으로 권한 체크
        verifyStoreOwner(userStoreNo, userId, "메뉴 등록");

        //중복 메뉴명 체크
        int duplicateCount = menuMapper.countByStoreNoAndMenuName(addMenuDto.getCategoriesNo(), addMenuDto.getMenuName().trim());
        if (duplicateCount > 0) {
            throw new IllegalArgumentException("이미 존재하는 메뉴명입니다." + addMenuDto.getMenuName());
        }

        //메뉴순서정보 찾고 넣기
        int cntPosition = menuMapper.cntPosition(addMenuDto.getCategoriesNo());
        int newPosition = cntPosition + 1;
        int cnt = menuMapper.addMenu(addMenuDto, newPosition);

        if (cnt == 0) {
            throw new IllegalArgumentException("메뉴 등록에 실패하였습니다");
        }
    }

    /*
     *
     * */
    @Transactional
    @PreAuthorize("hasAnyAuthority('ADMIN','ORDERS')")
    public void updateMenuStatus(Long menuNo) {
        Long userStoreNo = SecurityUtil.getCurrentUsersStoreNo();
        String userId = SecurityUtil.getCurrentUserId();

        //공통으로 권한 체크
        verifyStoreOwner(userStoreNo, userId, "메뉴 판매 여부");

        //메뉴 존재 여부 조회
        int cnt = menuMapper.cntByMenuNo(menuNo);

        if (cnt == 0) {
            throw new IllegalArgumentException("메뉴가 존재 하지 않습니다.");
        }
        int updateStatus = menuMapper.updateMenuStatus(menuNo);
        if (updateStatus == 0) {
            throw new IllegalArgumentException("메뉴 상태가 변경 되지않았습니다");
        }
    }


    public void updateMenuData(Long menuNo, RequestMenuDto.UpdateMenuDto updateMenudto) {

    }


    /**
     * 카테고리 별 메뉴가 10개 이상일 가능성이 적기 때문에 개별 UPDATE를 사용할까?
     * 아니면 foreach를 통한 성능 비교
     */
    @Transactional
    @PreAuthorize("hasAuthority('ADMIN')")
    public void repositionMenuData(List<Long> menuPositions) {
        // 1. 현재 로그인한 사용자의 매장 번호와 권한 추출
        Long userStoreNo = SecurityUtil.getCurrentUsersStoreNo();
        String userId = SecurityUtil.getCurrentUserId();

        verifyStoreOwner(userStoreNo, userId, "메뉴 순서 변경");


        List<RequestMenuDto.PositionMenuDto> positions = new ArrayList<>();


        for (int i = 0; i < menuPositions.size(); i++) {
            positions.add(new RequestMenuDto.PositionMenuDto(menuPositions.get(i), i + 1));
        }
        menuMapper.updateMenuForEachOrder(positions);


    }
}
