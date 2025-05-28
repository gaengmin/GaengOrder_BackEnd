package tableOrder.ordersItem.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import tableOrder.ordersItem.dto.request.RequestOrderItemsDto;
import tableOrder.ordersItem.dto.request.RequestOrderItemsDto.*;
import tableOrder.ordersItem.service.OrdersItemService;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class OrdersItemController {

    private final OrdersItemService ordersItemService;

    /**
     * TODO :
     * 메뉴 부분 취소,
     * 메뉴 부분 취소에 필요한 것은,
     * */
    @Operation(
            summary = "ORDERS가 관리하는 메뉴 부분 취소",
            description = "ordersItemsNo가 필요하고, 그 데이터를 가져오고, 거기서 취소하는 데이터 생각"
    )
    @PostMapping("/ordersItem/{orderNo}/cancel")
    @PreAuthorize("hasAuthority('ORDERS')")
    @SecurityRequirement(name = "Access")
    public ResponseEntity<?> partCancelOrdersItems(@PathVariable Long orderNo, @RequestBody @Validated RequestOrderItemsDto.OrdersMenuCancelDto ordersMenuCancelDto) {
        ordersItemService.partCancelOrdersItems(orderNo, ordersMenuCancelDto);

        return ResponseEntity.ok("메뉴 부분 취소");
    }
    /** 이미 주문 된 메뉴 Free 처리 */
}
