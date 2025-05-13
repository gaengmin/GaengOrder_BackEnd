package tableOrder.menu.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tableOrder.menu.dto.request.RequestMenuDto;
import tableOrder.menu.mapper.MenuMapper;

@Service
@RequiredArgsConstructor
public class MenuService {
    private final MenuMapper menuMapper;

    public void updateMenuStatus(Long menuNo){
       int cnt = menuMapper.updateMenuStatus(menuNo);
       if(cnt ==0){
           throw new IllegalArgumentException("메뉴 번호가 없는 메뉴입니다.");
       }
    }
    public void addMenu(RequestMenuDto.AddMenuDto addMenuDto) {
        int cnt = menuMapper.addMenu(addMenuDto);

        if(cnt ==0){
            throw new IllegalArgumentException("메뉴 등록에 실패하였습니다");
        }
    }

    public void updateMenuData(Long menuNo, RequestMenuDto.UpdateMenuDto updateMenudto) {
        int cnt = menuMapper.updateMenuData(menuNo, updateMenudto);

        if(cnt == 0){
            throw  new IllegalArgumentException("메뉴 수정에 실패하였습니다.");
        }
    }
}
