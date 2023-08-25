package com.vote.repository;

import com.vote.entity.ElectionStart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ElectionStartRepository extends JpaRepository<ElectionStart, Long> {
}
