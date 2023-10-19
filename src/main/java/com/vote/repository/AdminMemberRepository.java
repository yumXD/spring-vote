package com.vote.repository;

import com.vote.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminMemberRepository extends JpaRepository<Member, Long> {
    @Query("select "
            + "distinct m "
            + "from Member m "
            + "where "
            + "   m.username like %:kw% "
            + "   or m.email like %:kw% ")
    Page<Member> findAllByKeyword(@Param("kw") String kw, Pageable pageable);
}
