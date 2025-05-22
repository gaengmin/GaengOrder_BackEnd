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
import tableOrder.tables.dto.response.ResponseTablesDto;
import tableOrder.tables.service.TablesService;

@Tag(name = "매장 테이블 관련 API", description = "테이블 생성, 조회, 수정, 삭제 기능 제공")
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api")
public class TablesController {

    private final TablesService tablesService;

    @Operation(
            summary = "테이블 접속 관련 Read",
            description = "손님이 QR이나 웹페이지를 통한 접속시 유효한 테이블 정보인지 확인 | tableNo | tableCode | storeNo | 주문상태 : status " )
    @GetMapping("/tables/{storeNo}/{tableCode}")
    public ResponseEntity<ResponseTablesDto.ResponseTableInfoDto> getTablesData(@PathVariable Long storeNo, @PathVariable String tableCode){
        ResponseTablesDto.ResponseTableInfoDto tableInfoDto = tablesService.getTableData(storeNo, tableCode);
        return ResponseEntity.ok(tableInfoDto);
    }

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
