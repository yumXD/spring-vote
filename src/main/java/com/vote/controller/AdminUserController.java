package com.vote.controller;

import com.vote.dto.UserFormDto;
import com.vote.entity.Users;
import com.vote.service.AdminUserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/admin")
public class AdminUserController {
    private final AdminUserService adminUserService;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/users")
    public String list(Model model,
                       @RequestParam(value = "page", defaultValue = "0") int page,
                       @RequestParam(value = "kw", defaultValue = "") String kw) {
        Page<Users> users = this.adminUserService.getAdminUserPage(page, kw);
        log.info("{} 회원 조회 페이지", kw);
        model.addAttribute("kw", kw);
        model.addAttribute("users", users);
        return "user/userMng";
    }

    @GetMapping("/users/{userId}")
    public String userDtlSearch(@PathVariable("userId") Long userId,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        try {
            UserFormDto userFormDto = adminUserService.getUserDtl(userId);
            model.addAttribute("userFormDto", userFormDto);
            log.info("{} 회원 상세 조회 페이지", userFormDto.getName());
        } catch (EntityNotFoundException ex) {
            log.error("{} 존재하지 않는 회원", userId);
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/admin/users";
        }
        return "user/userDtlSearch";
    }

    @GetMapping("/users/{userId}/update-pw")
    public String userUpdatePw(@PathVariable("userId") Long userId,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        try {
            UserFormDto userFormDto = adminUserService.getUserDtl(userId);
            model.addAttribute("userFormDto", userFormDto);
            log.info("{} 회원 수정 (비밀번호) 페이지", userFormDto.getName());
        } catch (EntityNotFoundException ex) {
            log.error("{} 존재하지 않는 회원", userId);
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/admin/users";
        }
        return "user/userForm";
    }

    @PostMapping("/users/{userId}/update-pw")
    public String userUpdatePw(@Valid UserFormDto userFormDto,
                               BindingResult bindingResult,
                               @PathVariable("userId") Long userId,
                               Model model,
                               RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            log.error("{} 회원 수정 (비밀번호) 에러", userFormDto.getName());
            return "user/userForm";
        }

        try {
            adminUserService.updatePassword(userFormDto, adminUserService.findById(userId), passwordEncoder);
            log.info("{} 회원 수정 (비밀번호) 성공", userFormDto.getName());
        } catch (Exception ex) {
            log.error("회원 수정 (비밀번호) 실패");
            model.addAttribute("errorMessage", "회원 수정에 실패하였습니다.");
            return "user/userForm";
        }
        redirectAttributes.addFlashAttribute("successMessage", "비밀번호가 성공적으로 수정되었습니다.");
        return "redirect:/admin/users";
    }
}
