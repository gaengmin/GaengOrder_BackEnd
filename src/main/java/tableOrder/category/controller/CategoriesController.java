package tableOrder.category.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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

    @PostMapping("/insertCategories")
    public ResponseEntity<String> insertCategory(
            @RequestBody RequestCategoryDto.InsertCategory insertCategory) {
        try {
            categoriesService.insertCategory(insertCategory);

            return ResponseEntity.status(200).body("카테고리 등록 완료");
        } catch (IllegalArgumentException e) {

            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PatchMapping("/deleteCategories")
    public ResponseEntity<String> deleteCategory(@RequestBody RequestCategoryDto.DeleteCategory deleteCategory) {
        try {
            categoriesService.deleteCategory(deleteCategory);

            return ResponseEntity.status(200).body("카테고리 삭제 완료");
        } catch (IllegalArgumentException e) {

            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
