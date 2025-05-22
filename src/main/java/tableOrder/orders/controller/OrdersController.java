package tableOrder.orders.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tableOrder.orders.dto.request.RequestOrdersDto;
import tableOrder.orders.service.OrdersService;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class OrdersController {
    private final OrdersService ordersService;

    public ResponseEntity<?> createOrder(@RequestBody @Valid RequestOrdersDto.CreateOrderDto createOrderDto){
        ordersService.createOrder(createOrderDto);

        return ResponseEntity.ok("주문이 완료");
    }

}
