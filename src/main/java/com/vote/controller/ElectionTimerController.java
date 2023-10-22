package com.vote.controller;

import com.vote.dto.ElectionTimerFormDto;
import com.vote.entity.Election;
import com.vote.service.ElectionService;
import com.vote.service.ElectionTimerService;
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
public class ElectionTimerController {
    private final ElectionTimerService electionTimerService;
    private final ElectionService electionService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/election/{electionId}/start")
    public String startElection(@PathVariable("electionId") Long electionId,
                                Principal principal,
                                Model model,
                                RedirectAttributes redirectAttributes) {

        electionService.isAccessAllowed(electionId, principal.getName());
        electionService.isElectionInProgress(electionId);

        try {
            Election election = electionService.findById(electionId);
            log.info("\"{}\" 선거 시작 설정 페이지", election.getTitle());
            model.addAttribute("electionTimerFormDto", new ElectionTimerFormDto());
            model.addAttribute("electionId", electionId);
        } catch (EntityNotFoundException ex) {
            log.error("{} 존재하지 않는 선거", electionId);
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/elections";
        }
        return "timer/electionTimerForm";
    }

    @PostMapping("/election/{electionId}/start")
    public String startElection(@Valid ElectionTimerFormDto electionTimerFormDto,
                                BindingResult bindingResult,
                                @PathVariable("electionId") Long electionId,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            log.error("선거 시작 에러");
            return "timer/electionTimerForm";
        }

        try {
            electionTimerService.startElection(electionTimerFormDto, electionId);
            log.info("선거 시작 성공");
        } catch (Exception ex) {
            log.error("선거 시작 예외 발생");
            model.addAttribute("errorMessage", "투표시작 중 에러가 발생하였습니다.");
            return "timer/electionTimerForm";
        }
        redirectAttributes.addFlashAttribute("successMessage", "투표가 시작되었습니다.");
        return "redirect:/election/" + electionId;
    }

    @PostMapping("/election/{electionId}/end")
    public String forceEndElection(@PathVariable("electionId") Long electionId,
                                   RedirectAttributes redirectAttributes) {
        electionTimerService.forceEndVoting(electionId);
        log.info("투표 강제 종료 성공");
        redirectAttributes.addFlashAttribute("successMessage", "투표가 강제 종료되었습니다.");
        return "redirect:/election/" + electionId;
    }
}
