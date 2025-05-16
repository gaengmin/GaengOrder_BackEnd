package tableOrder.auth.util;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import tableOrder.category.mapper.CategoriesMapper;

public abstract class AbstractAuthValidator {


    protected final CategoriesMapper categoriesMapper;

    @Autowired
    public AbstractAuthValidator(CategoriesMapper categoriesMapper) {
        this.categoriesMapper = categoriesMapper;
    }

    // 공통 권한 체크 메서드
    protected void verifyStoreOwner(Long userStoreNo, String userId, String methodName) {
        String findUserId = categoriesMapper.findByStoreUserId(userStoreNo);
        if (findUserId == null) {
            throw new IllegalArgumentException("매장 정보가 존재하지 않습니다.");
        }
        if (!userId.equals(findUserId)) {
            throw new AccessDeniedException(methodName + "은 본인 매장에서만 가능합니다.");
        }
    }
}