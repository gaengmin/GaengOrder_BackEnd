package tableOrder.ordersItem.service;

import jakarta.transaction.Transactional;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import tableOrder.auth.util.AbstractAuthValidator;
import tableOrder.auth.util.SecurityUtil;
import tableOrder.category.mapper.CategoriesMapper;
import tableOrder.orders.dto.enums.OrdersStatusEnum;
import tableOrder.orders.mapper.OrdersMapper;
import tableOrder.ordersItem.dto.request.RequestOrderItemsDto;
import tableOrder.ordersItem.dto.response.ResponseOrderItemsDto;
import tableOrder.ordersItem.mapper.OrdersItemsMapper;
import tableOrder.ordersStatusLog.dto.request.RequestOrdersStatusLogDto;
import tableOrder.ordersStatusLog.mapper.OrdersStatusLogMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class OrdersItemService extends AbstractAuthValidator {
    private final OrdersItemsMapper ordersItemsMapper;
    private final OrdersMapper ordersMapper;
    private final OrdersStatusLogMapper ordersStatusLogMapper;

    public OrdersItemService(CategoriesMapper categoriesMapper, OrdersItemsMapper ordersItemsMapper, OrdersMapper ordersMapper, OrdersStatusLogMapper ordersStatusLogMapper) {
        super(categoriesMapper);
        this.ordersItemsMapper = ordersItemsMapper;
        this.ordersMapper = ordersMapper;
        this.ordersStatusLogMapper = ordersStatusLogMapper;
    }


    /**
     * 부분 취소 관련 서비스 메소드
     */
    @Transactional
    @PreAuthorize("hasAuthority('ORDERS')")
    public void partCancelOrdersItems(Long orderNo, RequestOrderItemsDto.OrdersMenuCancelDto ordersMenuCancelDto) {
        Long userStoreNo = SecurityUtil.getCurrentUsersStoreNo();
        String userId = SecurityUtil.getCurrentUserId();

        //공통으로 권한 체크
        verifyStoreOwner(userStoreNo, userId, "주문 부분 취소");

        //주문번호 존재 여부 체크
        existOrdersCheck(orderNo, userStoreNo);


        //먼저 가져와야하는게 주문에 따른 데이터 가져오기(ordersNo에 따른 )
        //1)먼저 주문 데이터가 있는지?
        List<String> menuNames = new ArrayList<>();
        for (int i = 0; i < ordersMenuCancelDto.getCancelRequestsDto().size(); i++) {
            menuNames.add(ordersMenuCancelDto.getCancelRequestsDto().get(i).getMenuName());
        }

        //주문 데이터 가져오기
        List<ResponseOrderItemsDto.MenuQuantityDto> ordersItemsDataList = ordersItemsMapper.findMenuNamesAndQuantitiesByOrdersNo(orderNo, menuNames);

        //주문에 없는 메뉴, 수량 부족 등 유효성 검사
        for (int i = 0; i < ordersMenuCancelDto.getCancelRequestsDto().size(); i++) {
            String menuName = ordersMenuCancelDto.getCancelRequestsDto().get(i).getMenuName();
            int cancelQuantity = ordersMenuCancelDto.getCancelRequestsDto().get(i).getCancelQuantity();
            int orderedQuantity = 0;
            boolean menuExists = false;

            for (int j = 0; j < ordersItemsDataList.size(); j++) {
                ResponseOrderItemsDto.MenuQuantityDto orderItem = ordersItemsDataList.get(j);

                if (orderItem.getMenuName().equals(menuName)) {
                    orderedQuantity += orderItem.getQuantity();
                    menuExists = true;
                }
            }
            if (!menuExists) {
                throw new IllegalArgumentException("주문에 없는 메뉴가 포함되어 있습니다 ; " + menuName);
            }
            if (cancelQuantity > orderedQuantity) {
                throw new IllegalArgumentException("취소 수량이 주문 수량보다 많습니다: " + menuName);
            }
        }

        int totalCancelAMount = 0;
        //이상이 없을 시 실제 부분 취소 작업 실행
        for (int i = 0; i < ordersMenuCancelDto.getCancelRequestsDto().size(); i++) {
            String menuName = ordersMenuCancelDto.getCancelRequestsDto().get(i).getMenuName();
            int cancelQuantity = ordersMenuCancelDto.getCancelRequestsDto().get(i).getCancelQuantity();
            String cancelReason = ordersMenuCancelDto.getCancelRequestsDto().get(i).getCancelReason();

            //해당 메뉴의 미취소 주문 아이템 최신순 조회
            List<ResponseOrderItemsDto.GetCancelDataDto> menuItems = ordersItemsMapper.findMenuItemsByOrdersNoAndMenuName(orderNo, menuName);

            int remainingQty = cancelQuantity;
            for (int j = 0; j < menuItems.size(); j++) {
                if (remainingQty <= 0) break;

                ResponseOrderItemsDto.GetCancelDataDto item = menuItems.get(j);
                int itemQty = item.getQuantity();
                int itemPrice = item.getMenuPrice();

                if (itemQty <= remainingQty) {
                    // 전체 취소
                    totalCancelAMount += (itemQty * itemPrice);
                    ordersItemsMapper.updateAsCanceled(item.getOrdersItemsNo(), cancelReason);
                    remainingQty -= itemQty;
                } else {
                    // 부분 취소: 기존 아이템 수량 차감
                    totalCancelAMount += (remainingQty * itemPrice);
                    String cancelReasonWithQty = cancelReason + " " + (itemQty - remainingQty);
                    ordersItemsMapper.updateQuantity(item.getOrdersItemsNo(), itemQty - remainingQty, cancelReasonWithQty);


                    ResponseOrderItemsDto.InsertCancelDto insertCancelDto =
                            ResponseOrderItemsDto.InsertCancelDto.of(
                                    item.getOrdersNo(),
                                    item.getMenuNo(),
                                    item.getMenuName(),
                                    item.getMenuPrice(),
                                    remainingQty,
                                    cancelReason
                            );

                    ordersItemsMapper.insertCancelHistory(insertCancelDto);
                    remainingQty = 0;
                }
            }
        }

        if (totalCancelAMount > 0) {
            ordersMapper.updateOrderAmount(
                    orderNo,
                    totalCancelAMount
            );
        }

        //로그 남기기..
        OrdersStatusEnum status = OrdersStatusEnum.valueOf(ordersMapper.getOrderStatusByOrderNo(orderNo));
        RequestOrdersStatusLogDto logDto = RequestOrdersStatusLogDto.of(orderNo, status, OrdersStatusEnum.PARTIALLY_CANCELLED, userId);
        ordersStatusLogMapper.saveLogData(logDto);
    }

    //주문번호 존재 여부 체크
    private void existOrdersCheck(Long orderNo, Long storeNo) {
        //주문 존재 여부
        int existCheck = ordersMapper.existOrdersByOrderNoAndStoreNo(orderNo, storeNo);
        if (existCheck == 0) {
            throw new IllegalArgumentException("매장 내 존재하지 않는 주문입니다");
        }
    }
}
