package tableOrder.tables.service;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tableOrder.auth.util.AbstractAuthValidator;
import tableOrder.auth.util.SecurityUtil;
import tableOrder.category.mapper.CategoriesMapper;
import tableOrder.common.enums.SoftDelete;
import tableOrder.orders.dto.enums.OrdersStatusEnum;
import tableOrder.orders.mapper.OrdersMapper;
import tableOrder.tables.dto.request.RequestTablesDto;
import tableOrder.tables.dto.response.ResponseTablesDto;
import tableOrder.tables.entity.Tables;
import tableOrder.tables.repository.TablesRepository;

@Service
public class TablesService extends AbstractAuthValidator {

    private final TablesRepository tablesRepository;
    private final OrdersMapper ordersMapper;

    public TablesService(CategoriesMapper categoriesMapper, TablesRepository tablesRepository, OrdersMapper ordersMapper) {
        super(categoriesMapper);
        this.tablesRepository = tablesRepository;
        this.ordersMapper = ordersMapper;
    }

    @Override
    protected void verifyStoreOwner(Long userStoreNo, String userId, String methodName) {
        super.verifyStoreOwner(userStoreNo, userId, methodName);
    }

    public ResponseTablesDto.ResponseTableInfoDto getTableData(Long storeNo, String tableCode) {

        Tables tableData = tablesRepository.findByStoreNoAndTableCode(storeNo, tableCode)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 테이블 정보"));

        if (tableData.getSoftDelete() == SoftDelete.Y) {
            throw new IllegalArgumentException("삭제되거나 사용 불가한 테이블");
        }

        // 2. MyBatis로 최신 주문 상태 조회
        String orderStatus = ordersMapper.checkOrdersStatusByTableNo(tableData.getTableNo());
        OrdersStatusEnum statusEnum;
        if (orderStatus == null) {
            statusEnum = OrdersStatusEnum.CLEAN; //주문이력이 없을 시 CLEAN으로 간주
        } else {
            statusEnum = OrdersStatusEnum.valueOf(orderStatus);
        }
        if(statusEnum == OrdersStatusEnum.CANCELLED || statusEnum == OrdersStatusEnum.DIRTY) {
            throw new IllegalArgumentException("아직 자리 정리가 되지 않은 자리, 직원에게 문의해주세요");
        }

        return ResponseTablesDto.ResponseTableInfoDto.from(tableData, statusEnum.name());
    }


    /**
     * 1)테이블 번호 중복 체크
     * 2)저장
     */
    @Transactional
    @PreAuthorize("hasAuthority('ADMIN')")
    public void addStoreTables(RequestTablesDto.addStoreTableDto addStoreTableDto) {
        Long userStoreNo = SecurityUtil.getCurrentUsersStoreNo();
        String userId = SecurityUtil.getCurrentUserId();

        verifyStoreOwner(userStoreNo, userId, "매장 내 테이블 번호를 저장");

        validateTableCodeDuplicate(addStoreTableDto.getTableCode(), addStoreTableDto.getStoreNo(), SoftDelete.N, null);

        Tables addStoreTable = addStoreTableDto.toEntity(userStoreNo);
        tablesRepository.save(addStoreTable);
    }

    @Transactional
    @PreAuthorize("hasAuthority('ADMIN')")
    public void modifyStoreTables(Long tableNo, String tableCode) {
        Long userStoreNo = SecurityUtil.getCurrentUsersStoreNo();
        String userId = SecurityUtil.getCurrentUserId();

        verifyStoreOwner(userStoreNo, userId, "매장 내 테이블 번호를 수정");

        Tables table = findTableData(tableNo);

        validateTableCodeDuplicate(tableCode, userStoreNo, SoftDelete.N, tableNo);


        // 테이블 이름 수정
        table.changeTableCode(tableCode);
    }

    @Transactional
    @PreAuthorize("hasAuthority('ADMIN')")
    public void softDeleteStoreTables(Long tableNo) {
        Long userStoreNo = SecurityUtil.getCurrentUsersStoreNo();
        String userId = SecurityUtil.getCurrentUserId();

        verifyStoreOwner(userStoreNo, userId, "매장 내 테이블 번호를 삭제");

        Tables table = findTableData(tableNo);

        // 소프트 삭제 처리
        table.changeSoftDelete(SoftDelete.Y);
    }


    private Tables findTableData(Long tableNo) {
        return tablesRepository.findById(tableNo).orElseThrow(() -> new IllegalArgumentException("테이블을 찾을 수 없습니다."));
    }


    private void validateTableCodeDuplicate(String tableCode, Long storeNo, SoftDelete softDelete, Long excludeTableNo) {
        boolean isExist;
        if (excludeTableNo == null) {
            // 생성 시: 전체 중복 체크
            isExist = tablesRepository.existsByTableCodeAndStoreNoAndSoftDelete(tableCode, storeNo, softDelete);
        } else {
            // 수정 시: 자기 자신 제외 중복 체크
            isExist = tablesRepository.existsByTableCodeAndStoreNoAndSoftDeleteAndTableNoNot(
                    tableCode, storeNo, softDelete, excludeTableNo
            );
        }
        if (isExist) {
            throw new IllegalArgumentException("매장 내 이미 사용하는 테이블 코드입니다");
        }
    }


}
