package tableOrder.menu.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import tableOrder.menu.dto.request.RequestMenuDto;
import tableOrder.menu.service.MenuService;

import java.awt.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MenuController {
    private final MenuService menuService;

    /** Admin 구역
     * 1) 메뉴추가 기능
     * 2) 메뉴정보 수정 기능
     * 3) 메뉴 삭제 기능
     * 4) 메뉴 판매 여부 기능
     * */

    @PostMapping("/menu")
    @PreAuthorize("hasRole('ADMIN')")
    ResponseEntity<?> addMenu(@RequestBody @Validated RequestMenuDto.AddMenuDto addMenuDto) {
        menuService.addMenu(addMenuDto);

        return ResponseEntity.status(200).body("메뉴 등록 성공");
    }


    /*메뉴 데이터 수정 - if 가격이라든가? 메뉴명이라든가? */
    @PatchMapping("/menu/{menuNo}/updateMenuData")
    @PreAuthorize("hasAnyRole('ADMIN')")
    ResponseEntity<?> updateMenuData(@PathVariable Long menuNo, @RequestBody @Validated RequestMenuDto.UpdateMenuDto updateMenudto) {
        menuService.updateMenuData(menuNo, updateMenudto);

        return ResponseEntity.status(200).body("메뉴 판매 상태 변화 성공");
    }

    /** Orders 기능
     *
     *
     * @param menuNo
     * @return
     */

    /**메뉴 판매 여부 수정*/
    @PatchMapping("/menu/{menuNo}/updateMenuStatus")
    @PreAuthorize("hasAnyRole('ADMIN','ORDERS')")
    ResponseEntity<?> addMenu(@PathVariable Long menuNo) {
        menuService.updateMenuStatus(menuNo);

        return ResponseEntity.status(200).body("메뉴 판매 상태 변화 성공");
    }



}
