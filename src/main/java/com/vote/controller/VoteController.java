package com.vote.controller;

import com.vote.dto.CandidatesVoteSearchDto;
import com.vote.dto.VoteFormDto;
import com.vote.entity.Candidate;
import com.vote.entity.Election;
import com.vote.exception.DuplicateVoteException;
import com.vote.service.ElectionService;
import com.vote.service.VoteService;
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
import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class VoteController {
    private final VoteService voteService;
    private final ElectionService electionService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/election/{electionId}/vote")
    public String doVote(@PathVariable("electionId") Long electionId,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        try {
            Election election = electionService.findById(electionId);
            List<Candidate> candidates = voteService.isVotingInProgress(election);
            log.info("\"{}\" 선거 투표 페이지", election.getTitle());
            model.addAttribute("voteFormDto", new VoteFormDto());
            model.addAttribute("candidates", candidates);
            model.addAttribute("electionId", electionId);
        } catch (EntityNotFoundException ex) {
            log.error("존재하지 않는 선거");
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/elections";
        }
        return "vote/voteForm";
    }

    @PostMapping("/election/{electionId}/vote")
    public String doVote(@Valid VoteFormDto voteFormDto,
                         BindingResult bindingResult,
                         @PathVariable("electionId") Long electionId,
                         Principal principal,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            List<Candidate> candidates = electionService.getCandidates(electionId);
            model.addAttribute("candidates", candidates);
            log.error("투표 처리 에러");
            return "vote/voteForm";
        }

        try {
            voteService.doVote(principal.getName(), electionId, voteFormDto.getCandidateId());
            log.info("\"{}\" 이메일님 투표 처리 하였습니다.", principal.getName());
        } catch (DuplicateVoteException ex) {
            log.error("\"{}\" 이메일님 투표 처리 예외 발생", principal.getName());
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/election/" + electionId;
        }
        redirectAttributes.addFlashAttribute("successMessage", "투표 처리 되었습니다.");
        return "redirect:/election/" + electionId;
    }

    @GetMapping("/election/{electionId}/status")
    public String voteStatus(@PathVariable("electionId") Long electionId,
                             Model model,
                             RedirectAttributes redirectAttributes) {

        try {
            Long totalVotes = voteService.getElectionTotalVotes(electionId);
            List<CandidatesVoteSearchDto> candidatesVoteCount = voteService.getCandidateVoteStatistics(electionId);
            List<CandidatesVoteSearchDto> topCandidates = voteService.getTopVotedCandidate(electionId);
            model.addAttribute("totalVotes", totalVotes);
            model.addAttribute("candidatesVoteCount", candidatesVoteCount);
            model.addAttribute("topCandidates", topCandidates);
        } catch (EntityNotFoundException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/elections";
        }
        return "vote/voteStatus";
    }
}
