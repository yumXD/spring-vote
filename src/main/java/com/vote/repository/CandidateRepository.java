package com.vote.repository;

import com.vote.entity.Candidate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface CandidateRepository extends JpaRepository<Candidate, Long>, QuerydslPredicateExecutor<Candidate>, CandidateRepositoryCustom {
}
