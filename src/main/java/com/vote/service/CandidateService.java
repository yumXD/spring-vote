package com.vote.service;

import com.vote.dto.CandidateFormDto;
import com.vote.dto.CandidateSearchDto;
import com.vote.entity.Candidate;
import com.vote.repository.CandidateRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CandidateService {
    private final CandidateRepository candidateRepository;

    public Candidate saveCandidate(CandidateFormDto candidateFormDto) {

        //후보자 등록
        Candidate candidate = candidateFormDto.toEntity();
        candidateRepository.save(candidate);

        return candidate;
    }

    @Transactional(readOnly = true)
    public CandidateFormDto getCandidateDtl(Long id) {
        Candidate candidate = candidateRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        return CandidateFormDto.of(candidate);
    }

    public Candidate updateCandidate(CandidateFormDto candidateFormDto) {

        //후보자 수정
        Candidate candidate = candidateRepository.findById(candidateFormDto.getId()).orElseThrow(EntityNotFoundException::new);
        candidate.updateCandidate(candidateFormDto);

        return candidate;
    }

    @Transactional(readOnly = true)
    public Page<Candidate> getAdminCandidatePage(CandidateSearchDto candidateSearchDto, Pageable pageable) {
        return candidateRepository.getAdminCandidatePage(candidateSearchDto, pageable);
    }
}