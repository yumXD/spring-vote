package com.vote.service;

import com.vote.dto.CandidateFormDto;
import com.vote.entity.Candidate;
import com.vote.entity.Election;
import com.vote.repository.CandidateRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class CandidateService {
    private final CandidateRepository candidateRepository;
    private final ElectionService electionService;

    public Candidate saveCandidate(Long electionId, CandidateFormDto candidateFormDto) {

        //선거 찾기
        Election election = electionService.findById(electionId);

        //후보자 등록
        Candidate candidate = candidateFormDto.toEntity();

        election.addCandidate(candidate);

        candidateRepository.save(candidate);

        return candidate;
    }

    @Transactional(readOnly = true)
    public CandidateFormDto getCandidateDtl(Long electionId, Long candidateId) {
        List<Candidate> candidates = electionService.getCandidates(electionId);
        Candidate candidate = candidates.stream()
                .filter(c -> c.getId().equals(candidateId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 후보자입니다."));
        return CandidateFormDto.of(candidate);
    }

    public Candidate updateCandidate(CandidateFormDto candidateFormDto, Long electionId, Long candidateId) {
        List<Candidate> candidates = electionService.getCandidates(electionId);
        Candidate candidate = candidates.stream()
                .filter(c -> c.getId().equals(candidateId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 후보자입니다."));

        candidate.updateCandidate(candidateFormDto);

        return candidate;
    }

    public Candidate findById(Long id) {
        return candidateRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 후보자입니다."));
    }
}
