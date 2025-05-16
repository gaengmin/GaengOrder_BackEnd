package tableOrder.category.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tableOrder.auth.util.AbstractAuthValidator;
import tableOrder.auth.util.SecurityUtil;
import tableOrder.category.dto.request.RequestCategoryDto;
import tableOrder.category.mapper.CategoriesMapper;
import tableOrder.common.enums.SoftDelete;
import tableOrder.menu.mapper.MenuMapper;

import java.util.List;

@Slf4j
@Service
public class CategoriesService extends AbstractAuthValidator {


    private final MenuMapper menuMapper;

    @Autowired
    public CategoriesService(CategoriesMapper categoriesMapper, MenuMapper menuMapper) {
        super(categoriesMapper);
        this.menuMapper = menuMapper;
    }

    /**
     *  순서 변경하는 메소드
     * */
    @Transactional
    public void updateCategoryOrder(List<Long> orderedCategoryIds) {
        // 1. 현재 로그인한 사용자의 매장 번호와 권한 추출
        Long userStoreNo = SecurityUtil.getCurrentUsersStoreNo();
        String userId = SecurityUtil.getCurrentUserId();

        verifyStoreOwner(userStoreNo, userId, "카테고리 순서 변경");

        /* {4,1,3} 이 현재 카테고리순서면 4번 1번으로 가는거고
        *  1번이 2번으로 가는 거고
        *  3번이 3번으로 가게  **/

        for(int i = 0; i<orderedCategoryIds.size(); i++) {
            categoriesMapper.updateCategoryOrder(orderedCategoryIds.get(i), i+1);
        }
    }

    /**
     * 순서 제외한 카테고리명을 수정하는 메소드
     * - 매장 소유자 확인 (NULL확인도)
     * - 권한 확인
     * - 중복 카테고리명 확인
     * - 카테고리 변경(-동일한 카테고리 경우는 실패)
     *
     * @param categoriesNo   카테고리 번호
     * @param updateCategory 변경할 이름 정보를 담은 DTO
     * @throws IllegalArgumentException 해당 카테고리가 존재하지 않거나, 이름 변경이 실패한 경우
     */
    @PreAuthorize("hasRole('ADMIN')")
    public void changeCategoriesName(Long categoriesNo, RequestCategoryDto.UpdateCategory updateCategory) {
        // 1. 현재 로그인한 사용자의 매장 번호와 권한 추출
        Long userStoreNo = SecurityUtil.getCurrentUsersStoreNo();
        String userId = SecurityUtil.getCurrentUserId();

        verifyStoreOwner(userStoreNo, userId, "카테고리 수정");

        // 2. 대상 카테고리의 매장 번호 조회
        Long targetStoreNo = categoriesMapper.getStoreNoByCategoryNo(categoriesNo);


        // 4. 기존 로직 (카테고리 존재 여부 확인 및 업데이트)
        if (categoriesMapper.existsByCategoryNo(categoriesNo) == 0) {
            throw new IllegalArgumentException("존재하지 않는 카테고리입니다.");
        }

        //  중복 체크
        int duplicateCnt = categoriesMapper.countByStoreNoAndName(userStoreNo, updateCategory.getName());
        if (duplicateCnt > 0) {
            throw new IllegalArgumentException("해당 매장에 이미 존재하는 카테고리명입니다.");
        }

        int updatedRows = categoriesMapper.changeCategoriesName(categoriesNo, updateCategory.getName());
        if (updatedRows == 0) {
            throw new IllegalArgumentException("카테고리명 변경 실패: 이미 동일한 이름입니다.");
        }
    }


    /*
     * @DATE 25.05.15
     * 카테고리 소프트 삭제 메서드
     * - 해당 카테고리에 연결된 메뉴가 있을 경우 메뉴도 함께 소프트 삭제
     * - 트랜잭션 처리로 데이터 일관성 보장
     * */
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void softDeleteCategory(Long categoriesNo, RequestCategoryDto.SoftDeleteCategory softDeleteCategory) {
        Long userStoreNo = SecurityUtil.getCurrentUsersStoreNo();
        String userId = SecurityUtil.getCurrentUserId();

        verifyStoreOwner(userStoreNo,userId,"카테고리 삭제");

        // 대상 카테고리의 매장 번호 조회
        Long targetStoreNo = categoriesMapper.getStoreNoByCategoryNo(categoriesNo);
        if (targetStoreNo == null) {
            throw new IllegalArgumentException("유효하지 않은 카테고리 번호입니다.");
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


    /**
     * @DATE 25.05.15
     * 카테고리 입력하는 메소드
     * 1. 파라미터 유효성 검사 (완료)
     * 2. 매장 소유자 확인
     * - 카테고리 생성 권한이 여부 확인
     * - 해당 매장이 내 매장인지 확인
     * - 카테고리명 중복인지 파악??
     * - 생성한다.
     * - 매장이 존재하지 않을 경우 예외 발생
     */
    @Transactional
    @PreAuthorize("hasAuthority('ADMIN')")
    public void insertCategory(RequestCategoryDto.InsertCategory insertCategory) {
        //로그인 한 사용자 정보 확인
        Long userStoreNo = SecurityUtil.getCurrentUsersStoreNo();
        String userId = SecurityUtil.getCurrentUserId();
        

        // 유효성 검사
        if (insertCategory.getName() == null || insertCategory.getName().isBlank()) {
            throw new IllegalArgumentException("카테고리명은 필수 입력값입니다.");
        }

        // 매장 소유 및 권한 체크 확인 (Null 체크 추가)
        verifyStoreOwner(userStoreNo, userId, "카테고리 생성");

        //  중복 체크
        int duplicateCnt = categoriesMapper.countByStoreNoAndName(userStoreNo, insertCategory.getName());
        if (duplicateCnt > 0) {
            throw new IllegalArgumentException("해당 매장에 이미 존재하는 카테고리명입니다.");
        }

        // 위치 계산
        int position = categoriesMapper.getCategoriesCnt(userStoreNo);
        int nextPosition = position + 1;
        
        categoriesMapper.insertCategory(userStoreNo, insertCategory.getName(), nextPosition);
    }





}
