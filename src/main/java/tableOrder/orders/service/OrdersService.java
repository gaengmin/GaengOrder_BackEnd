package tableOrder.orders.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
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

        //주문 항목 검증
        for(RequestOrdersDto.OrderItemDto orderItemDto : createOrderDto.getOrderItems()) {
            validateMenu(orderItemDto.getMenuNo());


        }

    }



    /**
     *
     * */

    //메뉴 검증
     private void validateMenu(Long menuNo){
        int existMenu = menuMapper.existMenuByNo(menuNo);
        if(existMenu == 0) {
            throw new IllegalArgumentException("존재하지 않거나 판매 중인 메뉴가 아님");
        }
    }

}
