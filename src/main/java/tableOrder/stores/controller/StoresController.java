package tableOrder.stores.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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

    @PostMapping("/insertStores")
    public ResponseEntity<String> insertStores(@RequestBody RequestStoresDto.RequestInsertDto requestInsertDto){
        log.info(requestInsertDto.getBusinessNo() + "  " + requestInsertDto.getStoreTel() + " 테스트 ");
        try{
            log.info("매장을 등록합니다.");
            storesService.insertStores(requestInsertDto);
            return ResponseEntity.status(200).body("매장 등록 완료");
        }catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


}
