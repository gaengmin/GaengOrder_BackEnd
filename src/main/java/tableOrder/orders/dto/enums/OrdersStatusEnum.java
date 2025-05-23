package tableOrder.orders.dto.enums;

/**
 * 주문상태 Enum 모음
 */
public enum OrdersStatusEnum {
    READY, //주문 대기
    CONFIRMED, //주문 확인
    COOKING, //조리 중
    SERVED, //서빙 중
    PAID,
    DIRTY,
    CANCELLED,
    CLEAN;


    //추가 주문이 왔을 시 다시 주문 대기 상태로
    public OrdersStatusEnum additionalMenu() {
        return READY;
    }

    public OrdersStatusEnum next() {
        switch (this) {
            case READY:
                return CONFIRMED;
            case CONFIRMED:
                return COOKING;
            case COOKING:
                return SERVED;
            case SERVED:
                return PAID;
            case PAID:
                return DIRTY;
            case CANCELLED:
                return CANCELLED;
            case CLEAN:
                throw new IllegalStateException("새로운 주문을 받아야하는 테이블입니다." + this);
            default:
                throw new IllegalStateException("Unknown status: " + this);
        }
    }

}