package tableOrder.category.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import tableOrder.category.dto.request.RequestCategoryDto;
import tableOrder.category.service.CategoriesService;
import tableOrder.common.dto.ResponseDto;

@Tag(name = "카테고리 API", description = "카테고리 관련 API")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CategoriesController {

    private final CategoriesService categoriesService;

    /**
     * 카테고리명 수정 API
     * - 카테고리 번호와 새 이름을 받아 카테고리명을 변경합니다.
     * - 성공 시 200 OK와 성공 메시지 반환
     * - 실패(카테고리 없음 등) 시 400 Bad Request와 에러 메시지 반환
     */
    @PatchMapping("/categories/updateName")
    public ResponseEntity<?> changeCategoryName(
            @RequestBody @Validated RequestCategoryDto.UpdateCategory updateCategory) {
        try {
            categoriesService.changeCategoriesName(updateCategory);
            return ResponseEntity.ok("카테고리명이 성공적으로 변경되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * 카테고리 생성 API
     * - RequestCategoryDto.InsertCategory 요청을 받아 카테고리를 등록합니다.
     * - 성공 시 201(Created)과 메시지 반환
     * - 유효하지 않은 데이터(IllegalArgumentException)는 400(Bad Request)로 처리
     */

    @PostMapping("/categories")
    public ResponseEntity<String> insertCategory(
            @RequestBody @Validated RequestCategoryDto.InsertCategory insertCategory) {
        try {
            categoriesService.insertCategory(insertCategory);

            return ResponseEntity.status(200).body("카테고리 등록 완료");
        } catch (IllegalArgumentException e) {

            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * 카테고리 소프트 삭제 API
     * - RequestCategoryDto.DeleteCategory 요청을 받아 카테고리를 소프트 삭제합니다.
     * - 성공 시 200(OK)과 메시지 반환
     * - 유효하지 않은 데이터(IllegalArgumentException)는 400(Bad Request)로 처리
     */
    @PatchMapping("/categories/soft-delete")
    public ResponseEntity<String> softDeleteCategory(@RequestBody @Validated RequestCategoryDto.SoftDeleteCategory deleteCategory) {
        try {
            categoriesService.softDeleteCategory(deleteCategory);

            return ResponseEntity.status(200).body("카테고리 삭제 완료");
        } catch (IllegalArgumentException e) {

            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


}
