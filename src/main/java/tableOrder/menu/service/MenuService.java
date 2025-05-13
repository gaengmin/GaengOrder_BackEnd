package tableOrder.menu.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tableOrder.menu.dto.request.RequestMenuDto;
import tableOrder.menu.mapper.MenuMapper;

@Service
@RequiredArgsConstructor
public class MenuService {
    private final MenuMapper menuMapper;

    public void addMenu(RequestMenuDto.AddMenuDto addMenuDto) {
        menuMapper.addMenu(addMenuDto);
    }
}
