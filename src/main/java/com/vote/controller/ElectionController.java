package com.vote.controller;

import com.vote.dto.CandidateSearchDto;
import com.vote.dto.ElectionFormDto;
import com.vote.entity.Candidate;
import com.vote.entity.Election;
import com.vote.service.ElectionService;
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

import java.security.Principal;
import java.util.List;
import java.util.Optional;

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

    @GetMapping(value = {"/elections", "/elections/{page}"})
    public String candidateManage(CandidateSearchDto candidateSearchDto, @PathVariable("page") Optional<Integer> page, Model model) {
        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 10);
        Page<Election> elections = electionService.getAdminCandidatePage(candidateSearchDto, pageable);
        model.addAttribute("elections", elections);
        model.addAttribute("candidateSearchDto", candidateSearchDto);
        model.addAttribute("maxPage", 5);
        return "election/electionMng";
    }

    @GetMapping("/election/{electionId}")
    public String electionSearch(@PathVariable("electionId") Long electionId, Model model) {
        try {
            ElectionFormDto electionFormDto = electionService.getElectionDtl(electionId);
            String email = electionService.getEmail(electionId);
            List<Candidate> candidates = electionService.getCandidates(electionId);
            model.addAttribute("electionFormDto", electionFormDto);
            model.addAttribute("candidates", candidates);
            model.addAttribute("email", email);
        } catch (EntityNotFoundException e) {
            model.addAttribute("errorMessage", "존재하지 않는 선거 정보입니다.");
            return "error/error";
        }
        return "election/electionSearch";
    }

    @GetMapping("/election/update/{electionId}")
    public String electionUpdate(@PathVariable("electionId") Long electionId, Principal principal, Model model) {

        try {
            ElectionFormDto electionFormDto = electionService.getElectionDtl(electionId);
            model.addAttribute("electionFormDto", electionFormDto);
        } catch (EntityNotFoundException e) {
            model.addAttribute("errorMessage", "존재하지 않는 선거 수정 페이지입니다.");
            return "error/error";
        }
        return "election/electionForm";
    }

    @PostMapping("/election/update/{electionId}")
    public String electionUpdate(@Valid ElectionFormDto electionFormDto, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "election/electionForm";
        }

        try {
            electionService.updateElection(electionFormDto);
        } catch (Exception e) {
            model.addAttribute("errorMessage", "선거 수정 중 에러가 발생하였습니다.");
            return "error/error";
        }
        return "redirect:/election/" + electionFormDto.getId();
    }
}
