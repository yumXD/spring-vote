package com.vote.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.vote.dto.CandidateSearchDto;
import com.vote.entity.Election;
import com.vote.entity.QElection;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.thymeleaf.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

public class ElectionRepositoryCustomImpl implements ElectionRepositoryCustom {

    private JPAQueryFactory queryFactory;

    public ElectionRepositoryCustomImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    private BooleanExpression regDtsAfter(String searchDateType) {
        LocalDateTime dateTime = LocalDateTime.now();

        if (StringUtils.equals("all", searchDateType) || searchDateType == null) {
            return null;
        } else if (StringUtils.equals("1d", searchDateType)) {
            dateTime = dateTime.minusDays(1);
        } else if (StringUtils.equals("1w", searchDateType)) {
            dateTime = dateTime.minusWeeks(1);
        } else if (StringUtils.equals("1m", searchDateType)) {
            dateTime = dateTime.minusMonths(1);
        } else if (StringUtils.equals("6m", searchDateType)) {
            dateTime = dateTime.minusMonths(6);
        }

        return QElection.election.regTime.after(dateTime);
    }

    private BooleanExpression searchByLike(String searchBy, String searchQuery) {
        if (StringUtils.equals("title", searchBy)) {
            return QElection.election.title.like("%" + searchQuery + "%");
        } else if (StringUtils.equals("createdBy", searchBy)) {
            return QElection.election.createdBy.like("%" + searchQuery + "%");
        }
        return null;
    }

    @Override
    public Page<Election> getAdminElectionPage(CandidateSearchDto candidateSearchDto, Pageable pageable) {

        List<Election> content = queryFactory
                .selectFrom(QElection.election)
                .where(regDtsAfter(candidateSearchDto.getSearchDateType()),
                        searchByLike(candidateSearchDto.getSearchBy(), candidateSearchDto.getSearchQuery()))
                .orderBy(QElection.election.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory.select(Wildcard.count).from(QElection.election)
                .where(regDtsAfter(candidateSearchDto.getSearchDateType()),
                        searchByLike(candidateSearchDto.getSearchBy(), candidateSearchDto.getSearchQuery()))
                .fetchOne();


        return new PageImpl<>(content, pageable, total);
    }
}
