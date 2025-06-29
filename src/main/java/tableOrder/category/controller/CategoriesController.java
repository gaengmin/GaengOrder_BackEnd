package tableOrder.category.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import tableOrder.category.dto.request.RequestCategoryDto;
import tableOrder.category.dto.response.ResponseCategoryDto;
import tableOrder.category.service.CategoriesService;
import tableOrder.common.dto.ResponseDto;
import tableOrder.menu.service.MenuService;
import tableOrder.users.dto.security.CustomUserDetails;

import java.util.List;

@Tag(name = "카테고리 API", description = "카테고리 생성, 조회, 수정, 삭제 기능 제공")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CategoriesController {

    private final CategoriesService categoriesService;
    private final MenuService menuService;


    @Operation(
            summary = "고객용, 특정 카테고리의 메뉴 목록 조회",
            description = "storeNo와 categoriesNo를 받아 해당 카테고리의 메뉴 목록을 반환합니다. 판매중단 메뉴는 하단에 정렬."
    )
    @GetMapping("/categories/{categoriesNo}/stores/{storeNo}/menu")
    public ResponseEntity<List<ResponseCategoryDto.ResponseListMenuDto>> getMenusByCategoriesNo(@PathVariable int categoriesNo, @PathVariable int storeNo) {

        List<ResponseCategoryDto.ResponseListMenuDto> menus = menuService.findMenusByCategory(categoriesNo, storeNo);

        return ResponseEntity.ok(menus);
    }


    /**
     * 카테고리 Position 수정 API
     */
    @Operation(
            summary = "카테고리 순서(position) 변경",
            description = "관리자가 카테고리의 순서를 변경합니다. body에 순서대로 배치할 카테고리 ID 리스트(Long) 전달"
    )
    @PreAuthorize("hasAuthority('ADMIN')")
    @SecurityRequirement(name = "Access")
    @PatchMapping("/categories/rePosition")
    public ResponseEntity<?> updateOrder(@Validated @RequestBody List<Long> orderedCategoryIds) {
        categoriesService.updateCategoryOrder(orderedCategoryIds);
        return ResponseEntity.ok("순서가 정상적으로 변경이 되었습니다.");
    }

    /**
     * 카테고리명 수정 API
     * - 카테고리 번호와 새 이름을 받아 카테고리명을 변경합니다.
     * - 성공 시 200 OK와 성공 메시지 반환
     * - 실패(카테고리 없음 등) 시 400 Bad Request와 에러 메시지 반환
     */
    @Operation(
            summary = "카테고리명 수정",
            description = "카테고리 번호와 새로운 이름을 받아 관리자가 카테고리명을 변경합니다."
    )
    @PatchMapping("/categories/{categoriesNo}/updateName")
    @SecurityRequirement(name = "Access")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> changeCategoryName(
            @PathVariable Long categoriesNo,
            @RequestBody @Validated RequestCategoryDto.UpdateCategory updateCategory) {
        categoriesService.changeCategoriesName(categoriesNo, updateCategory);
        return ResponseEntity.ok("카테고리명이 성공적으로 변경되었습니다.");
    }

    /**
     * 카테고리 생성 API
     * - RequestCategoryDto.InsertCategory 요청을 받아 카테고리를 등록합니다.
     * - 성공 시 201(Created)과 메시지 반환
     * - 유효하지 않은 데이터(IllegalArgumentException)는 400(Bad Request)로 처리
     */
    @Operation(
            summary = "카테고리 등록",
            description = "관리자가 새로운 카테고리를 등록합니다."
    )
    @PostMapping("/categories")
    @SecurityRequirement(name = "Access")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<String> insertCategory(
            @RequestBody @Validated RequestCategoryDto.InsertCategory insertCategory) {
        categoriesService.insertCategory(insertCategory);


        return ResponseEntity.status(200).body("카테고리 등록 완료");
    }

    /**
     * 카테고리 소프트 삭제 API
     * - RequestCategoryDto.DeleteCategory 요청을 받아 카테고리를 소프트 삭제합니다.
     * - 성공 시 200(OK)과 메시지 반환
     * - 유효하지 않은 데이터(IllegalArgumentException)는 400(Bad Request)로 처리
     */
    @Operation(
            summary = "카테고리(소프트) 삭제",
            description = "관리자가 카테고리를 소프트 삭제합니다. (논리적 삭제)"
    )
    @PatchMapping("/categories/{categoriesNo}/soft-delete")
    @SecurityRequirement(name = "Access")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<String> softDeleteCategory(@PathVariable Long categoriesNo, @RequestBody @Validated RequestCategoryDto.SoftDeleteCategory deleteCategory) {
        categoriesService.softDeleteCategory(categoriesNo, deleteCategory);

        return ResponseEntity.status(200).body("카테고리 삭제 완료");
    }


}
