package tableOrder.menu.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import tableOrder.menu.dto.request.RequestMenuDto;
import tableOrder.menu.dto.response.ResponseMenuDto;
import tableOrder.menu.service.MenuService;

import java.util.List;
import java.util.Map;

@Tag(name = "메뉴 API", description = "메뉴 생성, 조회, 수정, 삭제 기능 제공")
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
     * <p>
     * ## Orders 구현
     * 1) 메뉴 판매 여부 가능  /api/menus/{menuId}/availability 완료
     * <p>
     * ## 고객 페이지 메뉴 조회
     * GET /api/categories-with-menus -> 카테고리 Controller에서 만듬
     * GET /api/menus/{menuId} ->
     */


    /**고객일 때 더보기 화면으로 나올 수 있게 하는 */
    @Operation(
            summary = "고객 메뉴 더보기(검색 포함)",
            description = "매장 번호와(필수), 검색어(옵션), offset, size를 받아 해당 매장의 메뉴를 더보기/로드 형태로 조회합니다. hasNext=true면 더 가져올 것이 있는 것.keyword 파라미터로 이름 검색 가능"
    )
    @GetMapping("/menus/client/search/{storedNo}")
    public  ResponseEntity<Map<String, Object>> getSearchMenuData(
            @PathVariable Long storedNo,
            @RequestParam(required = false) String keyword, @RequestParam(defaultValue = "0") int offset, @RequestParam(defaultValue = "10" )int size) {
        List<ResponseMenuDto.ResponseMenuDataDto> menus = menuService.getMenusForLoadMore(storedNo, keyword, size, offset);
        boolean hasNext = menus.size() == size; // 다음 데이터가 더 있는지 여부
        Map<String, Object> response = Map.of(
                "menus", menus,
                "hasNext", hasNext,
                "nextOffset", offset + size
        );
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "메뉴 상세 조회",
            description = "메뉴 ID로 상세 정보를 조회합니다."
    )

    @GetMapping("/menus/{menuId}")
    ResponseEntity<ResponseMenuDto.ResponseMenuDetailDto> getMenuDataDetails(@PathVariable Long menuId) {
        ResponseMenuDto.ResponseMenuDetailDto menuDetailDto = menuService.getMenuDataDetails(menuId);

        return ResponseEntity.ok(menuDetailDto);
    }

    @Operation(
            summary = "메뉴 등록",
            description = "관리자 권한으로 메뉴를 등록합니다."
    )
    @PostMapping("/menus")
    @PreAuthorize("hasAuthority('ADMIN')")
    @SecurityRequirement(name = "Access")
    ResponseEntity<?> registrationMenu(@RequestBody @Validated RequestMenuDto.AddMenuDto addMenuDto) {
        menuService.registrationMenu(addMenuDto);

        return ResponseEntity.status(200).body("메뉴 등록 성공");
    }

    /**
     * 메뉴 판매 여부 수정
     */
    @Operation(
            summary = "메뉴 판매 여부 수정",
            description = "메뉴 번호를 받아 판매/중지 여부를 토글합니다. ADMIN/ORDERS 권한 필요"
    )
    @PatchMapping("/menus/{menuNo}/availability")
    @PreAuthorize("hasAnyAuthority('ADMIN','ORDERS')")
    @SecurityRequirement(name = "Access")
    ResponseEntity<?> addMenu(@PathVariable Long menuNo) {
        menuService.updateMenuStatus(menuNo);

        return ResponseEntity.status(200).body("메뉴 판매 상태 변화 성공");
    }


    /**
     * 메뉴 순서 변경
     */
    @Operation(
            summary = "메뉴 순서 변경",
            description = "관리자가 메뉴의 진열 순서를 변경합니다. Body에 메뉴ID(Long) 리스트를 새로운 순서로 전달"
    )
    @PatchMapping("/menus/reposition")
    @PreAuthorize("hasAuthority('ADMIN')")
    @SecurityRequirement(name = "Access")
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
    /**
     * 메뉴 데이터 수정 - if 가격이라든가? 메뉴명이라든가?
     */
    @Operation(
            summary = "메뉴 정보 수정",
            description = "관리자가 메뉴명, 가격, 카테고리 등 메뉴 상세 정보를 수정합니다."
    )
    @PatchMapping("/menus/{menuNo}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @SecurityRequirement(name = "Access")
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
    @Operation(
            summary = "메뉴 소프트 삭제",
            description = "관리자가 메뉴를 판매에서 제외(소프트딜리트)합니다."
    )
    @PatchMapping("/menus/{menuNo}/softDelete")
    @PreAuthorize("hasAuthority('ADMIN')")
    @SecurityRequirement(name = "Access")
    ResponseEntity<?> softDeleteMenuData(@PathVariable Long menuNo) {
        menuService.softDeleteMenu(menuNo);

        return ResponseEntity.status(200).body("메뉴 판매에 대한 삭제 성공");
    }


}
