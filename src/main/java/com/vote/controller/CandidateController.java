package com.vote.controller;

import com.vote.dto.CandidateFormDto;
import com.vote.entity.Candidate;
import com.vote.entity.Election;
import com.vote.service.CandidateService;
import com.vote.service.ElectionService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
@Slf4j
public class CandidateController {
    private final CandidateService candidateService;

    private final ElectionService electionService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/election/{electionId}/candidate/new")
    public String candidateNew(@PathVariable("electionId") Long electionId,
                               Principal principal,
                               Model model,
                               RedirectAttributes redirectAttributes) {

        electionService.isAccessAllowed(electionId, principal.getName());
        electionService.isElectionInProgress(electionId);

        try {
            Election election = electionService.findById(electionId);
            log.info("\"{}\" 선거 후보자 추가 페이지", election.getTitle());
            model.addAttribute("electionId", electionId);
            model.addAttribute("candidateFormDto", new CandidateFormDto());
        } catch (EntityNotFoundException ex) {
            log.error("{} 존재하지 않는 선거 ", electionId);
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/elections";
        }
        return "candidate/candidateForm";
    }

    @PostMapping("/election/{electionId}/candidate/new")
    public String candidateNew(@PathVariable("electionId") Long electionId,
                               @Valid CandidateFormDto candidateFormDto,
                               BindingResult bindingResult,
                               Model model,
                               RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            log.error("후보자 추가 에러");
            return "candidate/candidateForm";
        }

        try {
            Candidate candidate = candidateService.saveCandidate(electionId, candidateFormDto);
            log.info("\"{}\" 후보자 추가 성공", candidate.getName());
        } catch (EntityNotFoundException ex) {
            log.error("{} 선거의 후보자 추가 예외 발생", electionId);
            model.addAttribute("errorMessage", ex.getMessage());
            return "candidate/candidateForm";
        }
        redirectAttributes.addFlashAttribute("successMessage", "후보자 추가 성공했습니다.");
        return "redirect:/election/" + electionId;
    }

    @GetMapping("/election/{electionId}/candidate/{candidateId}")
    public String candidateDtl(@PathVariable("electionId") Long electionId,
                               @PathVariable("candidateId") Long candidateId,
                               Model model,
                               RedirectAttributes redirectAttributes) {

        try {
            CandidateFormDto candidateFormDto = candidateService.getCandidateDtl(electionId, candidateId);
            String email = electionService.getEmail(electionId);
            log.info("\"{}\" 후보자 상세 정보 조회 페이지", candidateFormDto.getName());
            model.addAttribute("email", email);
            model.addAttribute("electionId", electionId);
            model.addAttribute("candidateFormDto", candidateFormDto);
        } catch (EntityNotFoundException ex) {
            log.error("존재하지 않는 선거 혹은 존재하지 않는 후보자");
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/elections";
        }
        return "candidate/candidateDtl";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/election/{electionId}/candidate/update/{candidateId}")
    public String candidateUpdate(@PathVariable("electionId") Long electionId,
                                  @PathVariable("candidateId") Long candidateId,
                                  Principal principal,
                                  Model model,
                                  RedirectAttributes redirectAttributes) {

        electionService.isAccessAllowed(electionId, principal.getName());
        electionService.isElectionInProgress(electionId);

        try {
            CandidateFormDto candidateFormDto = candidateService.getCandidateDtl(electionId, candidateId);
            log.info("\"{}\" 후보자 수정 페이지", candidateFormDto.getName());
            model.addAttribute("electionId", electionId);
            model.addAttribute("candidateFormDto", candidateFormDto);
        } catch (EntityNotFoundException ex) {
            log.error("존재하지 않는 선거 혹은 존재하지 않는 후보자");
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/elections";
        }
        return "candidate/candidateForm";
    }

    @PostMapping("/election/{electionId}/candidate/update/{candidateId}")
    public String candidateUpdate(@Valid CandidateFormDto candidateFormDto,
                                  BindingResult bindingResult,
                                  @PathVariable("electionId") Long electionId,
                                  @PathVariable("candidateId") Long candidateId,
                                  Model model,
                                  RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            log.error("후보자 수정 에러");
            return "candidate/candidateForm";
        }

        try {
            Candidate candidate = candidateService.updateCandidate(candidateFormDto, electionId, candidateId);
            log.info("\"{}\" 후보자 수정 성공", candidate.getName());
        } catch (Exception ex) {
            log.error("{} 선거의 후보자 수정 예외 발생", electionId);
            model.addAttribute("errorMessage", ex.getMessage());
            return "candidate/candidateForm";
        }
        redirectAttributes.addFlashAttribute("successMessage", "후보자 수정 성공했습니다.");
        return "redirect:/election/" + electionId;
    }
}
