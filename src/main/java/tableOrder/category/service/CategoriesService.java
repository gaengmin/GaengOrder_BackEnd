package tableOrder.category.service;

import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

    /**
     * 카테고리명을 수정하는 메소드
     * @param categoriesNo 카테고리 번호
     * @param updateCategory 변경할 이름 정보를 담은 DTO
     * @throws IllegalArgumentException 해당 카테고리가 존재하지 않거나, 이름 변경이 실패한 경우
     */
    @PreAuthorize("hasRole('ADMIN')")
    public void changeCategoriesName(Long categoriesNo, RequestCategoryDto.UpdateCategory updateCategory) {
        if (categoriesMapper.existsByCategoryNo(categoriesNo) != 0) {
            int updatedRows = categoriesMapper.changeCategoriesName(categoriesNo, updateCategory.getName());
            if (updatedRows == 0) {
                throw new IllegalArgumentException("카테고리명 변경 실패: 존재하지 않는 카테고리이거나, 이미 변경된 이름입니다.");
            }
        } else {
            throw new IllegalArgumentException("카테고리명 변경 실패: 존재하지 않는 카테고리입니다.");
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

        String businessNo = insertCategory.getBusinessNo(); //사업자 번호 조회
        if (businessNo != null) {
            Long storeNo = categoriesMapper.findByStoreNo(businessNo);

            categoriesMapper.insertCategory(storeNo, insertCategory.getName());
        }
    }

    /*
     * @DATE 25.05.04
     * 카테고리 소프트 삭제 메서드
     * - 해당 카테고리에 연결된 메뉴가 있을 경우 메뉴도 함께 소프트 삭제
     * - 트랜잭션 처리로 데이터 일관성 보장
     * */
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    //카테고리 삭제시 메뉴가 있다면 메뉴까지 함께 소프트 처리함.
    public void softDeleteCategory(Long categoriesNo, RequestCategoryDto.SoftDeleteCategory softDeleteCategory) {

        // 1. 카테고리 번호 유효성 검사
        if (categoriesNo == null || categoriesNo <= 0) {
            throw new IllegalArgumentException("유효하지 않은 카테고리 번호입니다.");
        }

        // 2. 카테고리 존재 여부 확인
        Integer categoryExists = categoriesMapper.existsByCategoryNo(categoriesNo); // 1:존재, 0:없음
        if (categoryExists == null || categoryExists == 0) {
            throw new IllegalArgumentException("존재하지 않는 카테고리입니다.");
        }

        //먼저 cnt 메뉴 갯수 조회 카테고리에 밑에 있는 메뉴 개수
        int menuCnt = menuMapper.cntByMenu(categoriesNo);

        //메뉴가 하나라도 있으면 해당 카테고리의 모든 메뉴 소프트 삭제
        if (menuCnt > 0) {
            menuMapper.softDeleteMenuByCategoriesNo(categoriesNo);
        }
        if (softDeleteCategory.getSoftDelete() != null && softDeleteCategory.getSoftDelete() == SoftDelete.N) {
            categoriesMapper.softDeleteCategoriesByNo(categoriesNo);
        }
    }
}
