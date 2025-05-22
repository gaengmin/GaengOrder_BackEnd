package tableOrder.orders.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tableOrder.common.enums.SoftDelete;
import tableOrder.menu.dto.response.ResponseMenuDto;
import tableOrder.menu.mapper.MenuMapper;
import tableOrder.menu.service.MenuService;
import tableOrder.orders.dto.request.RequestOrdersDto;
import tableOrder.orders.mapper.OrdersMapper;
import tableOrder.ordersItem.mapper.OrdersItemsMapper;
import tableOrder.ordersStatusLog.mapper.OrdersStatusLogMapper;

import java.awt.*;

@Service
@RequiredArgsConstructor
public class OrdersService {

    private final OrdersMapper ordersMapper;
    private final OrdersItemsMapper ordersItemsMapper;
    private final OrdersStatusLogMapper ordersStatusLogMapper;
    private final MenuMapper menuMapper;


    /***/
    @Transactional
    public void createOrder(RequestOrdersDto.CreateOrderDto createOrderDto) {

        //주문 항목 검증 후 입력
        for(RequestOrdersDto.OrderItemDto orderItemDto : createOrderDto.getOrderItems()) {
            validateMenu(orderItemDto.getMenuNo()); //주문검증
        }
        //주문 금액 계산
        long totalPrice = 0;
        for (RequestOrdersDto.OrderItemDto item : createOrderDto.getOrderItems()) {
            totalPrice += item.getMenuPrice() * item.getQuantity();
        }
    }



    /**
     *
     * */

    //메뉴 검증
     private void validateMenu(Long menuNo){
         ResponseMenuDto.MenuValidateDto validateDto = menuMapper.existMenuByNo(menuNo);
        if(validateDto == null) {
            throw new IllegalArgumentException("존재하지 않거나 판매 중인 메뉴가 아님");
        }
        if(validateDto.getSoftDelete() == SoftDelete.Y || validateDto.getMenuStatus() == 'Y'){
            throw new IllegalArgumentException("판매 중인 메뉴가 아닙니다.");
        }
    }

}
