package com.vote.repository;

import com.vote.dto.CandidatesVoteSearchDto;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VoteRepositoryCustom {
    Long findElectionTotalVotes(Long electionId);
    List<CandidatesVoteSearchDto> findCandidateVoteStatistics(Long electionId);
}