package com.vote.repository;

import com.vote.entity.Election;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ElectionRepository extends JpaRepository<Election, Long>, QuerydslPredicateExecutor<Election>, ElectionRepositoryCustom {
}
