package tableOrder.tables.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import tableOrder.tables.dto.request.RequestTablesDto;
import tableOrder.tables.service.TablesService;

@Tag(name = "매장 테이블 관련 API", description = "테이블 생성, 조회, 수정, 삭제 기능 제공")
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api")
public class TablesController {

    private final TablesService tablesService;


    @Operation(
            summary = "(관리자 기능)매장 테이블 생성",
            description = "ADMIN 권한이 있는 사용자만이 테이블을 생성할 수 있음"
    )
    @PostMapping("/tables")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> addStoreTables(@RequestBody @Validated RequestTablesDto.addStoreTableDto addStoreTableDto){
        tablesService.addStoreTables(addStoreTableDto);

        return ResponseEntity.ok("매장에 테이블 생성");
    }

    @Operation(
            summary = "(관리자기능)매장 테이블 수정",
            description = "ADMIN 권한이 있는 사용자만이 매장 테이블명 수정"
    )
    @PatchMapping("/tables/{tableNo}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> modifyStoreTables(@PathVariable Long tableNo, @RequestParam @NotBlank String tableCode){
        tablesService.modifyStoreTables(tableNo, tableCode);


        return ResponseEntity.ok("매장 테이블 번호 수정");
    }

    @Operation(
            summary = "(관리자기능)매장 테이블 Soft_delete",
            description = "ADMIN 권한이 있는 사용자만이 매장 테이블 soft_delete"
    )
    @PatchMapping("/tables/softDelete/{tableNo}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> modifyStoreTables(@PathVariable Long tableNo){
        tablesService.softDeleteStoreTables(tableNo);


        return ResponseEntity.ok("매장 테이블 번호 수정");
    }
}
