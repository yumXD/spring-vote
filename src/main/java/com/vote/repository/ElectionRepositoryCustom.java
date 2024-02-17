package com.vote.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.vote.dto.CandidateSearchDto;
import com.vote.entity.Election;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.thymeleaf.util.StringUtils;

import java.time.LocalDateTime;

import static com.vote.entity.QElection.election;

@Slf4j
@Repository
public class ElectionRepositoryCustom extends Querydsl4RepositorySupport {

    public ElectionRepositoryCustom() {
        super(Election.class);
    }

    private BooleanExpression regDtsAfter(String searchDateType) {
        LocalDateTime dateTime = LocalDateTime.now();

        if (StringUtils.isEmpty(searchDateType) || "all".equals(searchDateType)) {
            return null;
        } else if ("1d".equals(searchDateType)) {
            dateTime = dateTime.minusDays(1);
        } else if ("1w".equals(searchDateType)) {
            dateTime = dateTime.minusWeeks(1);
        } else if ("1m".equals(searchDateType)) {
            dateTime = dateTime.minusMonths(1);
        } else if ("6m".equals(searchDateType)) {
            dateTime = dateTime.minusMonths(6);
        }

        return election.regTime.after(dateTime);
    }

    private BooleanExpression searchByLike(String searchBy, String searchQuery) {
        if (StringUtils.isEmpty(searchBy) || StringUtils.isEmpty(searchQuery)) {
            return null;
        }

        switch (searchBy) {
            case "title":
                return election.title.like("%" + searchQuery + "%");
            case "createdBy":
                return election.createdBy.like("%" + searchQuery + "%");
            default:
                return null;
        }
    }

    public Page<Election> getAdminElectionPage(CandidateSearchDto candidateSearchDto, Pageable pageable) {
        BooleanExpression datePredicate = regDtsAfter(candidateSearchDto.getSearchDateType());
        BooleanExpression keywordPredicate = searchByLike(candidateSearchDto.getSearchBy(), candidateSearchDto.getSearchQuery());

        return applyPagination(pageable, queryFactory -> queryFactory.selectFrom(election)
                .where(datePredicate, keywordPredicate)
                .orderBy(election.id.desc()));
    }
}
