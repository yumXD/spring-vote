package com.vote.repository;

import com.vote.entity.ElectionTimer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ElectionTimerRepository extends JpaRepository<ElectionTimer, Long> {
}
