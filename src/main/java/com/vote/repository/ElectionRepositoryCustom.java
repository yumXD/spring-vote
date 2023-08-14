package com.vote.repository;

import com.vote.dto.CandidateSearchDto;
import com.vote.entity.Election;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public interface ElectionRepositoryCustom {
    Page<Election> getAdminElectionPage(CandidateSearchDto candidateSearchDto, Pageable pageable);
}
