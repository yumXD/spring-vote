package com.vote.service;

import com.vote.dto.CandidateFormDto;
import com.vote.entity.Candidate;
import com.vote.repository.CandidateRepository;
import lombok.RequiredArgsConstructor;
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
}
