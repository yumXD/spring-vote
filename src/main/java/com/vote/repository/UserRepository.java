package com.vote.repository;

import com.vote.entity.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByEmail(String email);

    @Query("select "
            + "distinct m "
            + "from Users m "
            + "where "
            + "   m.username like %:kw% "
            + "   or m.email like %:kw% ")
    Page<Users> findAllByKeyword(@Param("kw") String kw, Pageable pageable);
}
