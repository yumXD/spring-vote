package com.vote.controller;

import com.vote.dto.CandidateFormDto;
import com.vote.dto.CandidateSearchDto;
import com.vote.entity.Candidate;
import com.vote.service.CandidateService;
import com.vote.service.ElectionService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class CandidateController {
    private final CandidateService candidateService;

    private final ElectionService electionService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/election/{electionId}/candidate/new")
    public String candidateForm(@PathVariable("electionId") Long electionId, Principal principal, Model model) {
        //후보자 추가 폼

        try {
            model.addAttribute("electionId", electionId);
            model.addAttribute("candidateFormDto", new CandidateFormDto());
        } catch (EntityNotFoundException e) {
            model.addAttribute("errorMessage", "존재하지 않는 선거 페이지 입니다.");
            return "error/error";
        }
        return "candidate/candidateForm";
    }

    @PostMapping("/election/{electionId}/candidate/new")
    public String candidateNew(@PathVariable("electionId") Long electionId, @Valid CandidateFormDto candidateFormDto, BindingResult bindingResult, Model model) {
        //후보자 추가 처리
        if (bindingResult.hasErrors()) {
            return "candidate/candidateForm";
        }

        try {
            candidateService.saveCandidate(electionId, candidateFormDto);
        } catch (Exception e) {
            model.addAttribute("errorMessage", "후보자 등록 중 에러가 발생하였습니다.");
            return "error/error";
        }
        return "redirect:/election/" + electionId;
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/election/{electionId}/candidate/update/{candidateId}")
    public String candidateDtl(@PathVariable("electionId") Long electionId, @PathVariable("candidateId") Long candidateId, Principal principal, Model model) {
        //후보자 수정 폼 페이지

        try {
            CandidateFormDto candidateFormDto = candidateService.getCandidateDtl(candidateId);
            model.addAttribute("electionId", electionId);
            model.addAttribute("candidateFormDto", candidateFormDto);
        } catch (EntityNotFoundException e) {
            model.addAttribute("errorMessage", "존재하지 않는 후보자입니다.");
            return "error/error";
        }
        return "candidate/candidateForm";
    }

    @PostMapping("/election/{electionId}/candidate/update/{candidateId}")
    public String candidateUpdate(@Valid CandidateFormDto candidateFormDto, BindingResult bindingResult, @PathVariable("electionId") Long electionId, @PathVariable("candidateId") Long candidateId, Model model) {
        //특정 후보자 수정처리
        if (bindingResult.hasErrors()) {
            return "candidate/candidateForm";
        }

        try {
            candidateService.updateCandidate(candidateFormDto);
        } catch (Exception e) {
            model.addAttribute("errorMessage", "후보자 수정 중 에러가 발생하였습니다.");
            return "error/error";
        }
        return "redirect:/election/" + electionId + "/candidate/" + candidateId;
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

    @GetMapping("/election/{electionId}/candidate/{candidateId}")
    public String candidateDtlSearch(@PathVariable("electionId") Long electionId, @PathVariable("candidateId") Long candidateId, Model model) {
        //특정 후보자 조회
        try {
            CandidateFormDto candidateFormDto = candidateService.getCandidateDtl(candidateId);
            String email = electionService.getEmail(electionId);
            model.addAttribute("email", email);
            model.addAttribute("electionId", electionId);
            model.addAttribute("candidateFormDto", candidateFormDto);
        } catch (EntityNotFoundException e) {
            model.addAttribute("errorMessage", "존재하지 않는 후보자입니다.");
            return "error/error";
        }
        return "candidate/candidateDtlSearch";
    }
}
