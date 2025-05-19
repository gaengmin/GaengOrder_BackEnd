package tableOrder.menu.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import tableOrder.menu.dto.request.RequestMenuDto;
import tableOrder.menu.service.MenuService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MenuController {
    private final MenuService menuService;

    /**
     * TODO : 개발 해야할 것
     * ## Admin 구역
     * 1) 메뉴추가 기능 (POST /api/menus) - 완료
     * 4) 메뉴 판매 여부 기능 PATCH  /api/menus/{menuId}/availability - 완료 (ADMIN / ORDERS 완료)
     * 5) 메뉴 순서 PATCH /api/menus/reposition 완료 -> 성능 비교까지
     * 2) 메뉴정보 수정 기능 PATCH /api/menus/{menuNo} -> 테스트까지 완료
     * 3) 메뉴 삭제 기능  PATCH  /api/menus/{menNo}/soft-delete -> positioning까지 해야함. 테스트 완료
     *
     * ## Orders 구현
     * 1) 메뉴 판매 여부 가능  /api/menus/{menuId}/availability 완료
     *
     * ## 고객 페이지 메뉴 조회
     * GET /api/categories-with-menus
     * GET /api/menus/{menuId}
     *

     * */
    @PostMapping("/menus")
    @PreAuthorize("hasAuthority('ADMIN')")
    ResponseEntity<?> registrationMenu(@RequestBody @Validated RequestMenuDto.AddMenuDto addMenuDto) {
        menuService.registrationMenu(addMenuDto);

        return ResponseEntity.status(200).body("메뉴 등록 성공");
    }

    /**메뉴 판매 여부 수정*/
    @PatchMapping("/menus/{menuNo}/availability")
    @PreAuthorize("hasAnyAuthority('ADMIN','ORDERS')")
    ResponseEntity<?> addMenu(@PathVariable Long menuNo) {
        menuService.updateMenuStatus(menuNo);

        return ResponseEntity.status(200).body("메뉴 판매 상태 변화 성공");
    }


    /** 메뉴 순서 변경*/
    @PatchMapping("/menus/reposition")
    @PreAuthorize("hasAuthority('ADMIN')")
    ResponseEntity<?> repositionMenuData(@RequestBody @Validated List<Long> menuPositions) {
        menuService.repositionMenuData(menuPositions);

        return ResponseEntity.status(200).body("메뉴 판매 순서 변화 성공");
    }

    /**
     * TODO
     * 1-카테고리번호가 변경되면 새로운 positionnig 맨뒤로
     * 2.menu_name이 바뀌면 이름변경체크
     * 3.가격
     * 4.description
     * */
    /** 메뉴 데이터 수정 - if 가격이라든가? 메뉴명이라든가? */
    @PatchMapping("/menus/{menuNo}")
    @PreAuthorize("hasAuthority('ADMIN')")
    ResponseEntity<?> updateMenuData(@PathVariable Long menuNo, @RequestBody @Validated RequestMenuDto.UpdateMenuDto updateMenudto) {
        menuService.updateMenuData(menuNo, updateMenudto);

        return ResponseEntity.status(200).body("메뉴에 대한 내용 변화 성공");
    }

    /**
     * TODO
     * 1. 메뉴삭제
     * 메뉴 번호가 들어오면 그 번호 이외의 번호
     * 메뉴 삭제시 판매삭제한 날짜까지 기록
     */
    @PatchMapping("/menus/{menuNo}/softDelete")
    @PreAuthorize("hasAuthority('ADMIN')")
    ResponseEntity<?> softDeleteMenuData(@PathVariable Long menuNo) {
        menuService.softDeleteMenu(menuNo);

        return ResponseEntity.status(200).body("메뉴 판매에 대한 삭제 성공");
    }



}
