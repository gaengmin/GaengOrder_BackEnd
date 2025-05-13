package tableOrder.menu.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tableOrder.menu.dto.request.RequestMenuDto;
import tableOrder.menu.service.MenuService;

import java.awt.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MenuController {
    private final MenuService menuService;

    @PostMapping("/menu")
    @PreAuthorize("hasRole('ADMIN')")
    ResponseEntity<?> addMenu(@RequestBody @Validated RequestMenuDto.AddMenuDto addMenuDto) {
        menuService.addMenu(addMenuDto);


        return ResponseEntity.status(200).body("메뉴 등록 성공");
    }

}
