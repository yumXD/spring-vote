package com.vote.controller;

import com.vote.dto.CandidateSearchDto;
import com.vote.dto.ElectionFormDto;
import com.vote.entity.Candidate;
import com.vote.entity.Election;
import com.vote.service.ElectionService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ElectionController {
    private final ElectionService electionService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/election/new")
    public String electionForm(Model model) {
        model.addAttribute("electionFormDto", new ElectionFormDto());
        log.info("선거 등록 페이지");
        return "election/electionForm";
    }

    @PostMapping("/election/new")
    public String electionNew(@Valid ElectionFormDto electionFormDto,
                              BindingResult bindingResult,
                              Principal principal,
                              Model model,
                              RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            log.error("선거 등록 에러");
            return "election/electionForm";
        }

        try {
            electionService.saveElection(electionFormDto, principal.getName());
            log.info("선거 등록 성공");
        } catch (IllegalStateException ex) {
            log.error("선거 등록 예외 발생");
            model.addAttribute("errorMessage", ex.getMessage());
            return "election/electionForm";
        }
        redirectAttributes.addFlashAttribute("successMessage", "선거 등록 성공하였습니다.");
        return "redirect:/elections";
    }

    @GetMapping(value = {"/elections", "/elections/{page}"})
    public String candidateManage(CandidateSearchDto candidateSearchDto, @PathVariable("page") Optional<Integer> page, Model model) {
        log.info("선거 전체 조회 페이지네이션");
        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 10);
        Page<Election> elections = electionService.getAdminCandidatePage(candidateSearchDto, pageable);
        model.addAttribute("elections", elections);
        model.addAttribute("candidateSearchDto", candidateSearchDto);
        model.addAttribute("maxPage", 5);
        return "election/electionMng";
    }

    @GetMapping("/election/{electionId}")
    public String electionDtl(@PathVariable("electionId") Long electionId,
                              Model model,
                              RedirectAttributes redirectAttributes) {
        try {
            ElectionFormDto electionFormDto = electionService.getElectionDtl(electionId);
            String email = electionService.getEmail(electionId);
            List<Candidate> candidates = electionService.getCandidates(electionId);
            model.addAttribute("electionFormDto", electionFormDto);
            model.addAttribute("candidates", candidates);
            model.addAttribute("email", email);
            log.info("\"{}\" 선거 상세 페이지", electionFormDto.getTitle());
        } catch (EntityNotFoundException ex) {
            log.error("{} 존재하지 않는 선거 상세 페이지", electionId);
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/elections";
        }
        return "election/electionDtl";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/election/update/{electionId}")
    public String electionUpdate(@PathVariable("electionId") Long electionId,
                                 Principal principal,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {
        // 선거 수정 폼
        electionService.certification(electionId, principal.getName());
        electionService.validateElectionStart(electionId);

        try {
            ElectionFormDto electionFormDto = electionService.getElectionDtl(electionId);
            log.info("\"{}\" 선거 수정 페이지", electionFormDto.getTitle());
            model.addAttribute("electionFormDto", electionFormDto);
        } catch (EntityNotFoundException ex) {
            log.error("{} 존재하지 않는 선거 수정 페이지", electionId);
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/elections";
        }
        return "election/electionForm";
    }

    @PostMapping("/election/update/{electionId}")
    public String electionUpdate(@Valid ElectionFormDto electionFormDto,
                                 BindingResult bindingResult,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            log.error("선거 수정 에러");
            return "election/electionForm";
        }

        try {
            electionService.updateElection(electionFormDto);
            log.info("선거 수정 성공");
        } catch (EntityNotFoundException ex) {
            log.error("선거 수정 예외처리");
            model.addAttribute("errorMessage", ex.getMessage());
            return "election/electionForm";
        }
        redirectAttributes.addFlashAttribute("successMessage", "선거 수정 성공하였습니다.");
        return "redirect:/election/" + electionFormDto.getId();
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/election/delete/{electionId}")
    public String deleteElection(Principal principal,
                                 @PathVariable("electionId") Long electionId,
                                 RedirectAttributes redirectAttributes) {
        electionService.certification(electionId, principal.getName());
        try {
            electionService.deleteElection(electionId);
            log.info("선거 삭제 성공");
        } catch (EntityNotFoundException ex) {
            log.error("{} 선거 삭제 실패", electionId);
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/elections";
        }
        redirectAttributes.addFlashAttribute("successMessage", "선거 삭제 성공하였습니다.");
        return "redirect:/elections";
    }
}
