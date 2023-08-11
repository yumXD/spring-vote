package com.vote.repository;

import com.vote.dto.CandidateSearchDto;
import com.vote.entity.Candidate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public interface CandidateRepositoryCustom {
    Page<Candidate> getAdminCandidatePage(CandidateSearchDto candidateSearchDto, Pageable pageable);
}
