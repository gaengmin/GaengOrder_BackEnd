package tableOrder.tables.service;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tableOrder.auth.util.AbstractAuthValidator;
import tableOrder.auth.util.SecurityUtil;
import tableOrder.category.mapper.CategoriesMapper;
import tableOrder.common.enums.SoftDelete;
import tableOrder.tables.dto.request.RequestTablesDto;
import tableOrder.tables.entity.Tables;
import tableOrder.tables.repository.TablesRepository;

@Service
public class TablesService extends AbstractAuthValidator {

    private final TablesRepository tablesRepository;

    public TablesService(CategoriesMapper categoriesMapper, TablesRepository tablesRepository) {
        super(categoriesMapper);
        this.tablesRepository = tablesRepository;

    }

    @Override
    protected void verifyStoreOwner(Long userStoreNo, String userId, String methodName) {
        super.verifyStoreOwner(userStoreNo, userId, methodName);
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

        verifyStoreOwner(userStoreNo, userId, "매장 내 테이블 번호를 저장합니다.");

        validateTableCodeDuplicate(addStoreTableDto.getTableCode(), addStoreTableDto.getStoreNo(), SoftDelete.N, null);

        // 서비스 코드
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


    private Tables findTableData(Long tableNo){
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
