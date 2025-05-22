package tableOrder.orders.dto.enums;

/**
 * 주문상태 Enum 모음
 */
public enum OrdersStatusEnum {
    READY, //주문 대기
    CONFIRMED, //주문 확인
    COOKING, //조리 중
    SERVED, //서빙 중
    CANCELLED,
    CLEAN; // -> READY로 바로 전환 가기


    public OrdersStatusEnum next() {
        switch (this) {
            case READY:
                return CONFIRMED;
            case CONFIRMED:
                return COOKING;
            case COOKING:
                return SERVED;
            case SERVED:
                return CLEAN;
            case CLEAN:
                return READY;
            case CANCELLED:
                return CANCELLED;
            default:
                throw new IllegalStateException("Unknown status: " + this);
        }
    }

}