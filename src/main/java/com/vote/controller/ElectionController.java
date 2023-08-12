package com.vote.controller;

import com.vote.dto.ElectionFormDto;
import com.vote.service.ElectionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class ElectionController {
    private final ElectionService electionService;

    @GetMapping("/election/new")
    public String electionForm(Model model) {
        model.addAttribute("electionFormDto", new ElectionFormDto());
        return "election/electionForm";
    }

    @PostMapping("/election/new")
    public String electionNew(@Valid ElectionFormDto electionFormDto, BindingResult bindingResult, Principal principal, Model model) {
        if (bindingResult.hasErrors()) {
            return "election/electionForm";
        }

        try {
            electionService.saveElection(electionFormDto, principal.getName());
        } catch (Exception e) {
            model.addAttribute("errorMessage", "선거 등록 중 에러가 발생하였습니다.");
            return "error/error";
        }

        return "redirect:/";
    }
}
