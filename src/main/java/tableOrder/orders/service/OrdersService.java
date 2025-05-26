package tableOrder.orders.service;

import jakarta.transaction.Transactional;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import tableOrder.auth.util.AbstractAuthValidator;
import tableOrder.auth.util.SecurityUtil;
import tableOrder.category.mapper.CategoriesMapper;
import tableOrder.menu.mapper.MenuMapper;
import tableOrder.orders.dto.enums.OrdersStatusEnum;
import tableOrder.orders.dto.request.RequestOrdersDto;
import tableOrder.orders.dto.response.ResponseOrdersDto;
import tableOrder.orders.mapper.OrdersMapper;
import tableOrder.ordersItem.mapper.OrdersItemsMapper;
import tableOrder.ordersStatusLog.dto.request.RequestOrdersStatusLogDto;
import tableOrder.ordersStatusLog.mapper.OrdersStatusLogMapper;

@Service
public class OrdersService extends AbstractAuthValidator {

    private final OrdersMapper ordersMapper;
    private final OrdersItemsMapper ordersItemsMapper;
    private final OrdersStatusLogMapper ordersStatusLogMapper;
    private final MenuMapper menuMapper;
    private final OrdersValidateMethod ordersValidateMethod;

    public OrdersService(CategoriesMapper categoriesMapper, OrdersMapper ordersMapper, OrdersItemsMapper ordersItemsMapper, OrdersStatusLogMapper ordersStatusLogMapper, MenuMapper menuMapper, OrdersValidateMethod ordersValidateMethod) {
        super(categoriesMapper);
        this.ordersMapper = ordersMapper;
        this.ordersItemsMapper = ordersItemsMapper;
        this.ordersStatusLogMapper = ordersStatusLogMapper;
        this.menuMapper = menuMapper;
        this.ordersValidateMethod = ordersValidateMethod;
    }


    //메뉴 취소
    @Transactional
    @PreAuthorize("hasAuthority('ORDERS')")
    public void cancelStatus(Long orderNo, RequestOrdersDto.CancelOrderDto cancelOrderDto) {
        Long userStoreNo = SecurityUtil.getCurrentUsersStoreNo();
        String userId = SecurityUtil.getCurrentUserId();

        //공통으로 권한 체크
        verifyStoreOwner(userStoreNo, userId, "주문 다음 단계체크");
        //주문검증
        ordersValidateMethod.validateOrderExists(orderNo);
        //주문 상태 문자열 조회
        String statusStr = ordersValidateMethod.getOrderStatusString(orderNo);
        //주문 상태 문자열 → Enum 변환
        OrdersStatusEnum currentOrderStatus = ordersValidateMethod.parseOrderStatus(statusStr);

        //주문 취소
        ordersMapper.cancelOrderAndResetAmount(OrdersStatusEnum.CANCELLED.name(), orderNo, cancelOrderDto.getCancelReason());
        ordersItemsMapper.allCancelMenu(orderNo);
        //Builder를 통한 저장
        RequestOrdersStatusLogDto logDto = RequestOrdersStatusLogDto.of(orderNo, currentOrderStatus, OrdersStatusEnum.CANCELLED, userId);
        ordersStatusLogMapper.saveLogData(logDto);

    }

    @Transactional
    @PreAuthorize("hasAuthority('ORDERS')")
    public void updateStatus(Long orderNo) {
        Long userStoreNo = SecurityUtil.getCurrentUsersStoreNo();
        String userId = SecurityUtil.getCurrentUserId();

        //공통으로 권한 체크
        verifyStoreOwner(userStoreNo, userId, "주문 다음 단계체크");

        //주문검증
        ordersValidateMethod.validateOrderExists(orderNo);
        //주문 상태 문자열 조회
        String statusStr = ordersValidateMethod.getOrderStatusString(orderNo);
        //주문 상태 문자열 → Enum 변환
        OrdersStatusEnum currentOrderStatus = ordersValidateMethod.parseOrderStatus(statusStr);
        //취소 상태 검증
        ordersValidateMethod.validateNotCancelled(currentOrderStatus, orderNo);

        /**단계별로 바로 넘어감.*/
        OrdersStatusEnum nextStatus = currentOrderStatus.next();

        ordersMapper.updateOrdersStatus(nextStatus.name(), orderNo);

        //Builder를 통한 저장
        RequestOrdersStatusLogDto logDto = RequestOrdersStatusLogDto.of(orderNo, currentOrderStatus, nextStatus, userId);
        ordersStatusLogMapper.saveLogData(logDto);
    }

    /**
     * 주문 등록 과정
     */
    @Transactional
    public void createOrder(RequestOrdersDto.CreateOrderDto createOrderDto) {
        //주문상태 체크 후 CLEAN이 아니며 주문 불가 상태
        String currentStatus = ordersMapper.checkOrdersStatusByTableNo(createOrderDto.getTableNo());
        if (currentStatus == null) {
            // 테이블에 주문 이력이 없는 경우 → CLEAN 상태로 간주
            currentStatus = OrdersStatusEnum.CLEAN.name();
        }
        if (OrdersStatusEnum.valueOf(currentStatus) != OrdersStatusEnum.CLEAN) {
            throw new IllegalArgumentException("현재 주문이 진행 중인 테이블입니다." + currentStatus);
        }
        //주문 항목 검증 후 입력
        for (RequestOrdersDto.OrderItemDto orderItemDto : createOrderDto.getOrderItems()) {
            ordersValidateMethod.validateMenu(orderItemDto.getMenuNo()); //주문검증
        }
        //주문 금액 계산
        long totalPrice = 0;
        for (RequestOrdersDto.OrderItemDto item : createOrderDto.getOrderItems()) {
            totalPrice += item.getMenuPrice() * item.getQuantity();
        }
        RequestOrdersDto.CreateOrderDtoWithTotalPrice orderWithTotalPriceDto = RequestOrdersDto.CreateOrderDtoWithTotalPrice.of(
                createOrderDto.getTableNo(),
                createOrderDto.getStoreNo(),
                totalPrice
        );

        // 주문 데이터 저장
        ordersMapper.saveOrders(orderWithTotalPriceDto);

        Long orderNo = orderWithTotalPriceDto.getOrderNo();
        System.out.println("orderNO : orderNo : " + orderNo);
        // 주문별 메뉴 저장
        ordersItemsMapper.saveOrdersItems(orderNo, createOrderDto.getOrderItems(), 0L);

        //Log에 저장
        RequestOrdersStatusLogDto logDto = RequestOrdersStatusLogDto.of(orderNo, OrdersStatusEnum.valueOf(currentStatus), OrdersStatusEnum.READY, null);
        ordersStatusLogMapper.saveLogData(logDto);
    }

    //추가 메뉴
    public void additionalMenu(Long orderNo, RequestOrdersDto.AdditionalMenuDto additionalMenuDto) {
        //주문검증
        ordersValidateMethod.validateOrderExists(orderNo);
        //주문 항목 검증 후 입력
        for (RequestOrdersDto.OrderItemDto orderItemDto : additionalMenuDto.getOrderItems()) {
            ordersValidateMethod.validateMenu(orderItemDto.getMenuNo()); //주문검증
        }

        //주문 금액 계산
        ResponseOrdersDto.AdditionalNeedData needData = ordersMapper.getTotalPriceByOrdersNo(orderNo);
        Long totalPrice = needData.getTotalPrice();
        for (RequestOrdersDto.OrderItemDto item : additionalMenuDto.getOrderItems()) {
            totalPrice += item.getMenuPrice() * item.getQuantity();
        }
        OrdersStatusEnum statusEnum = OrdersStatusEnum.valueOf(needData.getOrderStatus()).additionalMenu();

        //주문데이터 업데이트
        RequestOrdersDto.AdditionalUpdateMenuDto addMenuDto = RequestOrdersDto.AdditionalUpdateMenuDto.of(orderNo, totalPrice, statusEnum.name(), " 추가 주문", needData.getAdditionalOrder() + 1);
        ordersMapper.updateAdditionMenu(addMenuDto);
        // 주문별 메뉴 저장
        ordersItemsMapper.saveOrdersItems(orderNo, additionalMenuDto.getOrderItems(), needData.getAdditionalOrder() + 1L);

        //Log에 저장
        RequestOrdersStatusLogDto logDto = RequestOrdersStatusLogDto.of(orderNo, statusEnum, OrdersStatusEnum.READY, null);
        ordersStatusLogMapper.saveLogData(logDto);

    }

    //고객용 영수증 및 현재 주문 데이터 확인
    public ResponseOrdersDto.ReceiptDto getReceiptData(Long storeNo, Long ordersNo) {
        //주문번호 조회
        ordersValidateMethod.validateOrderExists(ordersNo, storeNo);

        return ordersMapper.getReceiptData(ordersNo, storeNo);
    }

}
