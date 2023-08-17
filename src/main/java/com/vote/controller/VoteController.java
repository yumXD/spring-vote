package com.vote.controller;

import com.vote.dto.VoteFormDto;
import com.vote.entity.Candidate;
import com.vote.service.ElectionService;
import com.vote.service.VoteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class VoteController {
    private final VoteService voteService;
    private final ElectionService electionService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/election/{electionId}/vote")
    public String voteForm(@PathVariable("electionId") Long electionId, Model model) {
        List<Candidate> candidates = electionService.getCandidates(electionId);
        model.addAttribute("voteFormDto", new VoteFormDto());
        model.addAttribute("candidates", candidates);
        model.addAttribute("electionId", electionId);
        return "vote/voteForm";
    }

    @PostMapping("/election/{electionId}/vote")
    public String voteDo(@Valid VoteFormDto voteFormDto, BindingResult bindingResult, @PathVariable("electionId") Long electionId, @AuthenticationPrincipal User user, Model model) {
        if (bindingResult.hasErrors()) {
            List<Candidate> candidates = electionService.getCandidates(electionId);
            model.addAttribute("candidates", candidates);
            return "vote/voteForm";
        }

        try {
            voteService.createVote(user.getUsername(), electionId, voteFormDto.getCandidateId());
        } catch (Exception e) {
            model.addAttribute("errorMessage", "투표 중 에러가 발생하였습니다.");
            return "error/error";
        }
        return "redirect:/";
    }
}
