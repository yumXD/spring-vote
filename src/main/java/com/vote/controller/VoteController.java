package com.vote.controller;

import com.vote.dto.CandidatesVoteSearchDto;
import com.vote.dto.VoteFormDto;
import com.vote.entity.Candidate;
import com.vote.service.ElectionService;
import com.vote.service.VoteService;
import jakarta.persistence.EntityNotFoundException;
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
        voteService.validateVotingInProgress(electionId);
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

        //중복 체크
        voteService.validateDuplicateVote(user.getUsername(), electionId);

        try {
            voteService.createVote(user.getUsername(), electionId, voteFormDto.getCandidateId());
        } catch (Exception e) {
            model.addAttribute("errorMessage", "투표 중 에러가 발생하였습니다.");
            return "error/error";
        }
        return "redirect:/";
    }

    @GetMapping("/election/{electionId}/status")
    public String voteStatus(@PathVariable("electionId") Long electionId, Model model) {
        voteService.validateVotingClosure(electionId);
        try {
            Long totalVotes = voteService.getElectionTotalVotes(electionId);
            List<CandidatesVoteSearchDto> candidatesVoteCount = voteService.getCandidateVoteStatistics(electionId);
            List<CandidatesVoteSearchDto> topCandidates = voteService.getTopVotedCandidate(electionId);
            model.addAttribute("totalVotes", totalVotes);
            model.addAttribute("candidatesVoteCount", candidatesVoteCount);
            model.addAttribute("topCandidates", topCandidates);
        } catch (EntityNotFoundException e) {
            model.addAttribute("errorMessage", "존재하지 않는 투표정보 입니다.");
            return "error/error";
        }
        return "vote/voteStatus";
    }
}
