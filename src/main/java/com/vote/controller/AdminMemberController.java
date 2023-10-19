package com.vote.controller;

import com.vote.dto.MemberFormDto;
import com.vote.entity.Member;
import com.vote.service.AdminMemberService;
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
public class AdminMemberController {
    private final AdminMemberService adminMemberService;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/members")
    public String list(Model model,
                       @RequestParam(value = "page", defaultValue = "0") int page,
                       @RequestParam(value = "kw", defaultValue = "") String kw) {
        Page<Member> members = this.adminMemberService.getAdminMemberPage(page, kw);
        log.info("{} 회원 조회 페이지", kw);
        model.addAttribute("kw", kw);
        model.addAttribute("members", members);
        return "member/memberMng";
    }

    @GetMapping("/member/{memberId}")
    public String memberDtlSearch(@PathVariable("memberId") Long memberId,
                                  Model model,
                                  RedirectAttributes redirectAttributes) {
        try {
            MemberFormDto memberFormDto = adminMemberService.getMemberDtl(memberId);
            model.addAttribute("memberFormDto", memberFormDto);
            log.info("{} 회원 상세 조회 페이지", memberFormDto.getName());
        } catch (EntityNotFoundException ex) {
            log.error("{} 존재하지 않는 회원", memberId);
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/admin/members";
        }
        return "member/memberDtlSearch";
    }

    @GetMapping("/member/{memberId}/update-pw")
    public String memberUpdatePw(@PathVariable("memberId") Long memberId,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {
        try {
            MemberFormDto memberFormDto = adminMemberService.getMemberDtl(memberId);
            model.addAttribute("memberFormDto", memberFormDto);
            log.info("{} 회원 수정 (비밀번호) 페이지", memberFormDto.getName());
        } catch (EntityNotFoundException ex) {
            log.error("{} 존재하지 않는 회원", memberId);
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/admin/members";
        }
        return "member/memberForm";
    }

    @PostMapping("/member/{memberId}/update-pw")
    public String memberUpdatePw(@Valid MemberFormDto memberFormDto,
                                 BindingResult bindingResult,
                                 @PathVariable("memberId") Long memberId,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            log.error("{} 회원 수정 (비밀번호) 에러", memberFormDto.getName());
            return "member/memberForm";
        }

        try {
            adminMemberService.updatePassword(memberFormDto, adminMemberService.findById(memberId), passwordEncoder);
            log.info("{} 회원 수정 (비밀번호) 성공", memberFormDto.getName());
        } catch (Exception ex) {
            log.error("회원 수정 (비밀번호) 실패");
            model.addAttribute("errorMessage", "회원 수정에 실패하였습니다.");
            return "member/memberForm";
        }
        redirectAttributes.addFlashAttribute("successMessage", "비밀번호가 성공적으로 수정되었습니다.");
        return "redirect:/admin/members";
    }
}
