package com.vote.repository;

import com.vote.entity.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminUserRepository extends JpaRepository<Users, Long> {
    @Query("select "
            + "distinct m "
            + "from Users m "
            + "where "
            + "   m.username like %:kw% "
            + "   or m.email like %:kw% ")
    Page<Users> findAllByKeyword(@Param("kw") String kw, Pageable pageable);
}
