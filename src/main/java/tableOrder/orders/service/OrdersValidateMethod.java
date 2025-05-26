package tableOrder.orders.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tableOrder.common.enums.SoftDelete;
import tableOrder.menu.dto.response.ResponseMenuDto;
import tableOrder.menu.mapper.MenuMapper;
import tableOrder.orders.dto.enums.OrdersStatusEnum;
import tableOrder.orders.mapper.OrdersMapper;

@Service
@RequiredArgsConstructor
public class OrdersValidateMethod {
    private final OrdersMapper ordersMapper;
    private final MenuMapper menuMapper;

    //주문 존재 검증
    void validateOrderExists(Long orderNo, Long storeNo) {
        int cnt = ordersMapper.existOrdersByOrderNoAndStoreNo(orderNo, storeNo);
        if (cnt != 1) {
            throw new IllegalArgumentException("존재하지 않는 주문번호 혹은 매장 번호가 일치하지 않습니다. 주문번호: " + orderNo);
        }
    }

    //주문 존재 검증
    void validateOrderExists(Long orderNo) {
        int cnt = ordersMapper.existOrdersByOrderNo(orderNo);
        if (cnt != 1) {
            throw new IllegalArgumentException("존재하지 않는 주문단계입니다. 주문번호: " + orderNo);
        }
    }

    //주문 상태 문자열 조회
    String getOrderStatusString(Long orderNo) {
        String currentStatus = ordersMapper.getOrderStatusByOrderNo(orderNo);
        if (currentStatus == null) {
            throw new IllegalStateException("주문 상태 정보가 없습니다. 주문번호: " + orderNo);
        }
        return currentStatus;
    }

    //주문 상태 문자열 → Enum 변환
    OrdersStatusEnum parseOrderStatus(String statusStr) {
        try {
            return OrdersStatusEnum.valueOf(statusStr);
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException("존재하지 않는 주문 상태: " + statusStr);
        }
    }

    //취소 상태 검증
    void validateNotCancelled(OrdersStatusEnum status, Long orderNo) {
        if (status == OrdersStatusEnum.CANCELLED) {
            throw new IllegalArgumentException("취소된 주문입니다. 주문번호: " + orderNo);
        }
    }

    //메뉴 검증
    void validateMenu(Long menuNo) {
        ResponseMenuDto.MenuValidateDto validateDto = menuMapper.existMenuByNo(menuNo);
        if (validateDto == null) {
            throw new IllegalArgumentException("존재하지 않거나 판매 중인 메뉴가 아님");
        }
        if (validateDto.getSoftDelete() == SoftDelete.Y || validateDto.getMenuStatus() == 'Y') {
            throw new IllegalArgumentException("판매 중인 메뉴가 아닙니다.");
        }
    }
}
