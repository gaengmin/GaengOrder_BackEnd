package tableOrder.category.service;

import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tableOrder.auth.util.SecurityUtil;
import tableOrder.category.dto.request.RequestCategoryDto;
import tableOrder.category.mapper.CategoriesMapper;
import tableOrder.common.enums.SoftDelete;
import tableOrder.menu.mapper.MenuMapper;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoriesService {
    private final CategoriesMapper categoriesMapper;
    private final MenuMapper menuMapper;


    /*
    * TODO
    *  Categories 기능
    *
    * */

    /**
     * 카테고리명을 수정하는 메소드
     *
     * @param categoriesNo   카테고리 번호
     * @param updateCategory 변경할 이름 정보를 담은 DTO
     * @throws IllegalArgumentException 해당 카테고리가 존재하지 않거나, 이름 변경이 실패한 경우
     */
    @PreAuthorize("hasRole('ADMIN')")
    public void changeCategoriesName(Long categoriesNo, RequestCategoryDto.UpdateCategory updateCategory) {
        // 1. 현재 로그인한 사용자의 매장 번호와 권한 추출
        Long userStoreNo = SecurityUtil.getCurrentUsersStoreNo();
        String role = SecurityUtil.getCurrentUserRole();

        // 2. 대상 카테고리의 매장 번호 조회
        Long targetStoreNo = categoriesMapper.getStoreNoByCategoryNo(categoriesNo);

        // 3. 매장 소유권 검증
        if (!userStoreNo.equals(targetStoreNo)) {
            throw new AccessDeniedException("해당 카테고리 수정 권한이 없습니다.");
        }

        // 4. 기존 로직 (카테고리 존재 여부 확인 및 업데이트)
        if (categoriesMapper.existsByCategoryNo(categoriesNo) == 0) {
            throw new IllegalArgumentException("존재하지 않는 카테고리입니다.");
        }

        int updatedRows = categoriesMapper.changeCategoriesName(categoriesNo, updateCategory.getName());
        if (updatedRows == 0) {
            throw new IllegalArgumentException("카테고리명 변경 실패: 이미 동일한 이름입니다.");
        }
    }


    /**
     * @DATE 25.05.04
     * 카테고리 입력하는 메소드
     * - 사업자 번호로 매장 조회 후 카테고리 생성
     * - 매장이 존재하지 않을 경우 예외 발생
     */
    @PreAuthorize("hasRole('ADMIN')")
    public void insertCategory(RequestCategoryDto.InsertCategory insertCategory) {
        Long userStoreNo = SecurityUtil.getCurrentUsersStoreNo();
        String role = SecurityUtil.getCurrentUserRole();

        // SUPERADMIN은 카테고리 생성 불가
        if (!"ADMIN".equals(role)) {
            throw new AccessDeniedException("카테고리 생성 권한이 없습니다.");
        }

        String businessNo = insertCategory.getBusinessNo();
        if (businessNo == null) {
            throw new IllegalArgumentException("사업자 번호가 필요합니다.");
        }

        Long storeNo = categoriesMapper.findByStoreNo(businessNo);
        if (storeNo == null) {
            throw new IllegalArgumentException("해당 사업자 번호의 매장이 존재하지 않습니다.");
        }

        // ADMIN은 자신의 매장만 허용
        if (!userStoreNo.equals(storeNo)) {
            throw new AccessDeniedException("해당 매장에 카테고리 생성 권한이 없습니다.");
        }

        categoriesMapper.insertCategory(storeNo, insertCategory.getName());
    }

    /*
     * @DATE 25.05.04
     * 카테고리 소프트 삭제 메서드
     * - 해당 카테고리에 연결된 메뉴가 있을 경우 메뉴도 함께 소프트 삭제
     * - 트랜잭션 처리로 데이터 일관성 보장
     * */
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void softDeleteCategory(Long categoriesNo, RequestCategoryDto.SoftDeleteCategory softDeleteCategory) {

        // 1. 현재 사용자 매장 번호 추출
        Long userStoreNo = SecurityUtil.getCurrentUsersStoreNo();

        // 2. 대상 카테고리의 매장 번호 조회
        Long targetStoreNo = categoriesMapper.getStoreNoByCategoryNo(categoriesNo);
        if (targetStoreNo == null) {
            throw new IllegalArgumentException("유효하지 않은 카테고리 번호입니다.");
        }

        // 3. 매장 소유권 검증
        if (!userStoreNo.equals(targetStoreNo)) {
            throw new AccessDeniedException("해당 카테고리 삭제 권한이 없습니다.");
        }

        // 4. 기존 유효성 검사 로직
        if (categoriesNo == null || categoriesNo <= 0) {
            throw new IllegalArgumentException("유효하지 않은 카테고리 번호입니다.");
        }

        Integer categoryExists = categoriesMapper.existsByCategoryNo(categoriesNo);
        if (categoryExists == null || categoryExists == 0) {
            throw new IllegalArgumentException("존재하지 않는 카테고리입니다.");
        }

        // 5. 메뉴 소프트 삭제
        int menuCnt = menuMapper.cntByMenu(categoriesNo);
        if (menuCnt > 0) {
            menuMapper.softDeleteMenuByCategoriesNo(categoriesNo);
        }

        // 6. 카테고리 소프트 삭제
        if (softDeleteCategory.getSoftDelete() == SoftDelete.N) {
            categoriesMapper.softDeleteCategoriesByNo(categoriesNo);
        }
    }
}
