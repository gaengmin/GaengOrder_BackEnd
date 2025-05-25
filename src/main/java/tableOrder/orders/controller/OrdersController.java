package tableOrder.orders.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tableOrder.orders.dto.request.RequestOrdersDto;
import tableOrder.orders.dto.response.ResponseOrdersDto;
import tableOrder.orders.service.OrdersService;

/**
 * TODO :
 * 1) 메뉴별 취소
 * 2) 영수증 기능?
 * 3) 결제 처리는 그냥 일단 안하고, STATUS PAID로 그냥 처리
 * 4) Tables 관련해서 현재 상태에 따라 주문 추가주문인지, 새로운 주문인지, 주문 가능 여부 등등
 * */

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "주문 관련 API", description = "주문, 상태변경, 주문 취소 ")
public class OrdersController {
    private final OrdersService ordersService;

/*    @Operation(
            summary = "현재 손님의 주문 내역 조회 API",
            description = "현재 테이블 주문 내역"
    )
    @GetMapping("/orders/{ordersNo}")
    public ResponseEntity<ResponseOrdersDto> getOrders(@PathVariable("ordersNo") Long ordersNo) {


        return ResponseEntity.ok()
    }*/
    @Operation(
            summary = "손님이 주문하는 API",
            description = " 손님이 주문하는 API -> STATUS : READY만드는 것"
    )
    @PostMapping("/orders")
    public ResponseEntity<?> createOrder(@RequestBody @Valid RequestOrdersDto.CreateOrderDto createOrderDto) {
        ordersService.createOrder(createOrderDto);

        return ResponseEntity.ok("주문이 완료");
    }

    @Operation(
            summary = "ORDERS가 관리하는 주문 상태 변경 API",
            description = " 주문 상태변경  API : STATUS : 다음단계로 이동"
    )
    @PostMapping("/orders/{orderNo}")
    @PreAuthorize("hasAuthority('ORDERS')")
    public ResponseEntity<?> updateOrderStatus(@PathVariable Long orderNo) {
        ordersService.updateStatus(orderNo);
        return ResponseEntity.ok("상태 변경 완료");
    }

    @Operation(
            summary = "ORDERS가 관리하는 주문 취소 API",
            description = "주문 취소 API : STATUS : CANCELED"
    )
    @PostMapping("/orders/{orderNo}/cancel")
    @PreAuthorize("hasAuthority('ORDERS')")
    public ResponseEntity<?> cancelOrderStatus(@PathVariable Long orderNo, @RequestBody RequestOrdersDto.CancelOrderDto cancelOrderDto) {
        ordersService.cancelStatus(orderNo, cancelOrderDto);
        return ResponseEntity.ok("주문 취소 완료");
    }

    @Operation(
            summary = "추가 주문 API",
            description = "추가 주문 API"
    )
    @PostMapping("/orders/{orderNo}/add")
    public ResponseEntity<?> additionOrderStatus(@PathVariable Long orderNo, @RequestBody RequestOrdersDto.AdditionalMenuDto additionalMenuDto) {
        ordersService.additionalMenu(orderNo, additionalMenuDto);
        return ResponseEntity.ok("추가 주문 완료");
    }
}
