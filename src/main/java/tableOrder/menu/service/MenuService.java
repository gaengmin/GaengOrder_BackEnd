package tableOrder.menu.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tableOrder.auth.util.AbstractAuthValidator;
import tableOrder.auth.util.SecurityUtil;
import tableOrder.category.dto.response.ResponseCategoryDto;
import tableOrder.category.mapper.CategoriesMapper;
import tableOrder.menu.dto.request.RequestMenuDto;
import tableOrder.menu.dto.response.ResponseMenuDto;
import tableOrder.menu.mapper.MenuMapper;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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

    /**
     *
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
        int cntPosition = menuMapper.countActiveMenusByCategoryNo(addMenuDto.getCategoriesNo());
        int newPosition = cntPosition + 1;
        int cnt = menuMapper.addMenu(addMenuDto, newPosition);

        if (cnt == 0) {
            throw new IllegalArgumentException("메뉴 등록에 실패하였습니다");
        }
    }

    /**
     *
     * */
    @Transactional
    @PreAuthorize("hasAnyAuthority('ADMIN','ORDERS')")
    public void updateMenuStatus(Long menuNo) {
        Long userStoreNo = SecurityUtil.getCurrentUsersStoreNo();
        String userId = SecurityUtil.getCurrentUserId();

        //공통으로 권한 체크
        verifyStoreOwner(userStoreNo, userId, "메뉴 판매 여부");

        //2.메뉴 정보가 존재하는지 파악
        int menuCnt = countActiveMenu(menuNo);

        if(menuCnt == 0 ){
            throw new IllegalArgumentException("메뉴가 존재하지 않습니다.");
        }

        int updateStatus = menuMapper.updateMenuStatus(menuNo);
        if (updateStatus == 0) {
            throw new IllegalArgumentException("메뉴 상태가 변경 되지않았습니다");
        }
    }


    /**
     * TODO
     * 1-카테고리번호가 변경되면 새로운 positionnig 맨뒤로
     * 2.menu_name이 바뀌면 이름변경체크
     * 3.가격
     * 4.description
     * */
    @Transactional
    @PreAuthorize("hasAuthority('ADMIN')")
    public void updateMenuData(Long menuNo, RequestMenuDto.UpdateMenuDto updateMenudto) {
        // 1. 현재 로그인한 사용자의 매장 번호와 권한 추출
        Long userStoreNo = SecurityUtil.getCurrentUsersStoreNo();
        String userId = SecurityUtil.getCurrentUserId();

        verifyStoreOwner(userStoreNo, userId, "메뉴 정보 변경");

        //2.메뉴 정보가 존재하는지 파악
        int menuCnt = countActiveMenu(menuNo);

        if(menuCnt == 0 ){
            throw new IllegalArgumentException("메뉴가 존재하지 않습니다.");
        }

        //3. 비교할 데이터 확인
        Long currentCategory = menuMapper.findCategoryNoByMenuNo(menuNo);
        int currentMenuPosition = menuMapper.findMenuPositionByMenuNo(menuNo);
        String currentMenu = menuMapper.findMenuNameByMenuNo(menuNo);

        //4.메뉴명 변경
        if(updateMenudto.getMenuName()!=null && !updateMenudto.getMenuName().equals(currentMenu)){
            int duplicateCnt = menuMapper.countByMenuName(updateMenudto.getMenuName());
            if (duplicateCnt > 0) {
                throw new IllegalArgumentException("메뉴명이 현재 존재하고 있습니다.");
            }
        }

        //5.가격변경
        if(updateMenudto.getMenuPrice()!= null && updateMenudto.getMenuPrice().doubleValue()<0){
            throw new IllegalArgumentException("가격은 0원 이상이어야합니다.");
        }

        //6.카테고리가 변경되었다면
        if(updateMenudto.getCategoriesNo()!= null && !updateMenudto.getCategoriesNo().equals(currentCategory)){
            int menusCnt = menuMapper.countActiveMenusByCategoryNo(updateMenudto.getCategoriesNo());
            int lastPosition = menusCnt+1;

            /**기존 카테고리 리포지션닝*/
            menuMapper.adjustPositionAfterDeletion(currentCategory,currentMenuPosition);

                    RequestMenuDto.UpdateMenuDto positionUpdateDto = RequestMenuDto.UpdateMenuDto.builder()
                    .categoriesNo(updateMenudto.getCategoriesNo())
                    .menuName(updateMenudto.getMenuName())
                    .menuPrice(updateMenudto.getPosition())
                    .description(updateMenudto.getDescription())
                    .position(lastPosition)
                    .build();

            menuMapper.updateMenuData(menuNo, positionUpdateDto);
        }else{
            menuMapper.updateMenuData(menuNo, updateMenudto);

        }


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

    //메뉴 소프트 삭제
    @Transactional
    @PreAuthorize("hasAuthority('ADMIN')")
    public void softDeleteMenu(Long menuNo) {
        // 1. 현재 로그인한 사용자의 매장 번호와 권한 추출
        Long userStoreNo = SecurityUtil.getCurrentUsersStoreNo();
        String userId = SecurityUtil.getCurrentUserId();

        verifyStoreOwner(userStoreNo, userId, "메뉴 삭제 처리");

        //2.메뉴 정보가 존재하는지 파악
        int menuCnt = countActiveMenu(menuNo);

        if(menuCnt == 0 ){
            throw new IllegalArgumentException("메뉴가 존재하지 않습니다.");
        }
        Long currentCategory = menuMapper.findCategoryNoByMenuNo(menuNo);
        int currentMenuPosition = menuMapper.findMenuPositionByMenuNo(menuNo);
        String currentMenuName = menuMapper.findMenuNameByMenuNo(menuNo);

        //3.메뉴삭제할 메뉴를 제외한 나머지 포지셔닝
        menuMapper.adjustPositionAfterDeletion(currentCategory,currentMenuPosition);

        // 날짜 포맷팅 (예: 20250519)
        String formattedDate = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);

        String menuName = formattedDate +"까지 판매 : "+currentMenuName;
        System.out.println(menuName);
        //4.메뉴 soft-delete
        menuMapper.softDeleteAndRenameMenu(menuNo, menuName);

    }


    /**
     * TODO : 해야할 내용
     * 판매중 / 판매중단 메뉴에 따라 보이도록
     *
     * */
    public List<ResponseCategoryDto.ResponseListMenuDto> findMenusByCategory(int categoriesNo, int storeNo) {

        return menuMapper.findMenusByCategorySorted(categoriesNo, storeNo);

    }

    /**단일 메뉴 정보*/
    public ResponseMenuDto.ResponseMenuDetailDto getMenuDataDetails(Long menuId) {
        ResponseMenuDto.ResponseMenuDetailDto menuDetailDto;
        if(menuMapper.countByMenuNo(menuId) == 0){
            throw new IllegalArgumentException("해당 메뉴가 존재하지 않거나 현재 판매하지 않는 메뉴입니다.");
        }
        menuDetailDto = menuMapper.getMenuDataDetails(menuId);

        if(menuDetailDto==null){
            throw new IllegalArgumentException("해당 메뉴를 존재하지 않거나 현재 판매하지 않는 메뉴입니다.");
        }

        return menuDetailDto;
    }

    /**더보기용*/
    public List<ResponseMenuDto.ResponseMenuDataDto> getMenusForLoadMore(Long storeNo, String keyword, int size, int offset) {
        return menuMapper.searchMenusForLoadMore(storeNo, keyword, size, offset);
    }

    /*
     * 소프트삭제가 되지 않은 메뉴 갯수 조회
     * */
    private int countActiveMenu(Long menuNo){

        return menuMapper.countActiveMenuByMenuNo(menuNo);
    }



}
