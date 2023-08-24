package com.vote.repository;

import com.vote.entity.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long>, QuerydslPredicateExecutor<Vote>, VoteRepositoryCustom {
    Vote findByElectionIdAndMemberId(Long electionId, Long memberId);
}
