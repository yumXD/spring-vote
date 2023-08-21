package com.vote.controller;

import com.vote.entity.Member;
import com.vote.service.AdminMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping("/admin")
@Controller
@RequiredArgsConstructor
public class AdminMemberController {
    private final AdminMemberService adminMemberService;

    @GetMapping("/members")
    public String list(Model model, @RequestParam(value = "page", defaultValue = "0") int page, @RequestParam(value = "kw", defaultValue = "") String kw) {
        Page<Member> members = this.adminMemberService.getAdminMemberPage(page, kw);
        model.addAttribute("kw", kw);
        model.addAttribute("members", members);
        return "member/memberMng";
    }
}
