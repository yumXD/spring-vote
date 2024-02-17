package com.vote.repository;

import com.vote.entity.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static com.vote.entity.QUsers.users;

;

@Repository
public class UserRepositoryCustom extends Querydsl4RepositorySupport {
    public UserRepositoryCustom() {
        super(Users.class);
    }

    public Optional<Users> findByEmail(String email) {
        return Optional.ofNullable(selectFrom(users)
                .where(users.email.eq(email))
                .fetchOne());
    }

    //@Query("SELECT DISTINCT u FROM Users u WHERE u.username LIKE %:kw% OR u.email LIKE %:kw%")
    public Page<Users> findAllByKeyword(String kw, Pageable pageable) {
        return applyPagination(pageable, queryFactory -> queryFactory.selectFrom(users)
                .where(
                        users.username.likeIgnoreCase("%" + kw + "%")
                                .or(users.email.likeIgnoreCase("%" + kw + "%"))
                )
                .distinct());
    }
}
