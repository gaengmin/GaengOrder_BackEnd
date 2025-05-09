package tableOrder.stores.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import tableOrder.stores.dto.RequestDto.RequestStoresDto;
import tableOrder.stores.service.StoresService;

import java.util.List;

@Tag(name = "매장 API", description = "매장 관련 API")
@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class StoresController {

    private final StoresService storesService;

    /**
     * 매장 등록 API
     * - RequestStoresDto.RequestInsertDto 요청을 받아 새로운 매장을 등록합니다.
     * - 입력값 유효성 검증(@Valid) 적용: 필수값 누락 또는 형식 오류 시 400 Bad Request 반환
     * - 성공 시 "매장 등록 완료" 메시지와 함께 201 Created 반환
     * - 비즈니스 로직 예외 발생 시 400 Bad Request와 예외 메시지 반환
     *
     */
    @PostMapping("/superAdmin/stores")
    public ResponseEntity<String> insertStores(@RequestBody @Validated RequestStoresDto.RequestInsertDto requestInsertDto){
        log.info("{}  {} 테스트", requestInsertDto.getBusinessNo(), requestInsertDto.getStoreTel());

        try{
            log.info("매장을 등록합니다.");
            storesService.insertStores(requestInsertDto);
            return ResponseEntity.status(200).body("매장 등록 완료");
        }catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


}
