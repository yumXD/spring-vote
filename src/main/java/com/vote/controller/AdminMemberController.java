package com.vote.controller;

import com.vote.dto.MemberFormDto;
import com.vote.entity.Member;
import com.vote.service.AdminMemberService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/admin")
@Controller
@RequiredArgsConstructor
public class AdminMemberController {
    private final AdminMemberService adminMemberService;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/members")
    public String list(Model model, @RequestParam(value = "page", defaultValue = "0") int page, @RequestParam(value = "kw", defaultValue = "") String kw) {
        Page<Member> members = this.adminMemberService.getAdminMemberPage(page, kw);
        model.addAttribute("kw", kw);
        model.addAttribute("members", members);
        return "member/memberMng";
    }

    @GetMapping("/member/{memberId}")
    public String memberDtlSearch(@PathVariable("memberId") Long memberId, Model model) {
        try {
            MemberFormDto memberFormDto = adminMemberService.getMemberDtl(memberId);
            model.addAttribute("memberFormDto", memberFormDto);
        } catch (EntityNotFoundException e) {
            model.addAttribute("errorMessage", "존재하지 않는 회원 정보입니다.");
            return "error/error";
        }
        return "member/memberDtlSearch";
    }

    @GetMapping("/member/{memberId}/update-pw")
    public String memberUpdatePw(@PathVariable("memberId") Long memberId, Model model) {
        try {
            MemberFormDto memberFormDto = adminMemberService.getMemberDtl(memberId);
            model.addAttribute("memberFormDto", memberFormDto);
        } catch (EntityNotFoundException e) {
            model.addAttribute("errorMessage", "존재하지 않는 회원 정보입니다.");
            return "error/error";
        }
        return "member/memberForm";
    }

    @PostMapping("/member/{memberId}/update-pw")
    public String memberUpdatePw(@Valid MemberFormDto memberFormDto, BindingResult bindingResult, @PathVariable("memberId") Long memberId, Model model) {

        if (bindingResult.hasErrors()) {
            return "member/memberForm";
        }

        try {
            adminMemberService.updatePassword(memberFormDto, memberId, passwordEncoder);
        } catch (EntityNotFoundException e) {
            model.addAttribute("errorMessage", "존재하지 않는 회원 정보입니다.");
            return "error/error";
        }
        return "redirect:/";
    }
}
