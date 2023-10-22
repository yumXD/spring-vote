package com.vote.controller;

import com.vote.dto.ElectionTimerFormDto;
import com.vote.service.ElectionService;
import com.vote.service.ElectionStartService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class ElectionTimerController {
    private final ElectionStartService electionStartService;
    private final ElectionService electionService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/election/{electionId}/start")
    public String startElection(@PathVariable("electionId") Long electionId, Principal principal, Model model) {

        electionService.isAccessAllowed(electionId, principal.getName());
        electionService.isElectionInProgress(electionId, electionId);

        try {
            electionStartService.validateElection(electionId);
            model.addAttribute("electionTimerFormDto", new ElectionTimerFormDto());
            model.addAttribute("electionId", electionId);
        } catch (EntityNotFoundException e) {
            model.addAttribute("errorMessage", "존재하지 않는 투표시작 페이지 입니다.");
            return "error/error";
        }
        return "timer/electionTimerForm";
    }

    @PostMapping("/election/{electionId}/start")
    public String startElection(@Valid ElectionTimerFormDto electionTimerFormDto, BindingResult bindingResult, @PathVariable("electionId") Long electionId, Model model) {
        if (bindingResult.hasErrors()) {
            return "timer/electionTimerForm";
        }

        try {
            electionStartService.startElection(electionTimerFormDto, electionId);
        } catch (Exception e) {
            model.addAttribute("errorMessage", "투표시작 중 에러가 발생하였습니다.");
            return "error/error";
        }
        return "redirect:/election/" + electionId;
    }

    @PostMapping("/election/{electionId}/end")
    public String forceEndElection(@PathVariable("electionId") Long electionId) {

        electionStartService.forceEndVoting(electionId);

        return "redirect:/election/" + electionId;
    }
}
