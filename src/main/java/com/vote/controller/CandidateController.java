package com.vote.controller;

import com.vote.dto.CandidateFormDto;
import com.vote.dto.CandidateSearchDto;
import com.vote.entity.Candidate;
import com.vote.service.CandidateService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class CandidateController {
    private final CandidateService candidateService;

    @GetMapping("/admin/candidate/new")
    public String candidateForm(Model model) {
        model.addAttribute("candidateFormDto", new CandidateFormDto());
        return "candidate/candidateForm";
    }

    @PostMapping("/admin/candidate/new")
    public String candidateNew(@Valid CandidateFormDto candidateFormDto, BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {
            return "candidate/candidateForm";
        }

        try {
            candidateService.saveCandidate(candidateFormDto);
        } catch (Exception e) {
            model.addAttribute("errorMessage", "상품 등록 중 에러가 발생하였습니다.");
            return "candidate/candidateForm";
        }

        return "redirect:/";
    }

    @GetMapping("/admin/candidate/{candidateId}")
    public String candidateDtl(@PathVariable("candidateId") Long candidateId, Model model) {
        try {
            CandidateFormDto candidateFormDto = candidateService.getCandidateDtl(candidateId);
            model.addAttribute("candidateFormDto", candidateFormDto);
        } catch (EntityNotFoundException e) {
            model.addAttribute("errorMessage", "존재하지 않는 후보자입니다.");
            model.addAttribute("candidateFormDto", new CandidateFormDto());
            return "candidate/candidateForm";
        }
        return "candidate/candidateForm";
    }

    @PostMapping("/admin/candidate/{candidateId}")
    public String candidateUpdate(@Valid CandidateFormDto candidateFormDto, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "candidate/candidateForm";
        }

        try {
            candidateService.updateCandidate(candidateFormDto);
        } catch (Exception e) {
            model.addAttribute("errorMessage", "후보자 수정 중 에러가 발생하였습니다.");
            return "candidate/candidateForm";
        }
        return "redirect:/";
    }

    @GetMapping(value = {"/admin/candidates", "/admin/candidates/{page}"})
    public String candidateManage(CandidateSearchDto candidateSearchDto, @PathVariable("page") Optional<Integer> page, Model model) {
        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 3);
        Page<Candidate> candidates = candidateService.getAdminCandidatePage(candidateSearchDto, pageable);
        model.addAttribute("candidates", candidates);
        model.addAttribute("candidateSearchDto", candidateSearchDto);
        model.addAttribute("maxPage", 5);
        return "candidate/candidateMng";
    }
}