package com.vote.controller;

import com.vote.dto.UserFormDto;
import com.vote.entity.Users;
import com.vote.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/new")
    public String userForm(Model model) {
        log.info("회원가입 페이지");
        model.addAttribute("userFormDto", new UserFormDto());
        return "user/userForm";
    }

    @PostMapping("/new")
    public String userForm(@Valid UserFormDto userFormDto,
                           BindingResult bindingResult,
                           Model model,
                           RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            log.error("회원가입 에러");
            return "user/userForm";
        }

        try {
            Users users = Users.createUser(userFormDto, passwordEncoder);
            userService.saveUser(users);
            log.info("회원가입 성공");
        } catch (IllegalStateException ex) {
            log.error("이메일 중복 예외 발생");
            model.addAttribute("errorMessage", ex.getMessage());
            return "user/userForm";
        }
        redirectAttributes.addFlashAttribute("successMessage", "회원가입 성공하였습니다.");
        return "redirect:/users/login";
    }

    @GetMapping("/login")
    public String loginUser() {
        log.info("로그인 페이지");
        return "/user/userLoginForm";
    }

    @GetMapping("/login/error")
    public String loginError(Model model) {
        log.error("로그인 에러");
        model.addAttribute("loginErrorMsg", "아이디 또는 비밀번호를 확인해주세요.");
        return "/user/userLoginForm";
    }
}
