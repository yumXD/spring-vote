package com.vote.repository;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.vote.dto.CandidatesVoteSearchDto;
import com.vote.entity.QCandidate;
import com.vote.entity.QVote;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.stream.Collectors;

public class VoteRepositoryCustomImpl implements VoteRepositoryCustom {
    private JPAQueryFactory queryFactory;

    public VoteRepositoryCustomImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Long findElectionTotalVotes(Long electionId) {

        QVote vote = QVote.vote;
        return queryFactory.select(vote.count())
                .from(vote)
                .where(vote.election.id.eq(electionId))
                .fetchOne();
    }

    @Override
    public List<CandidatesVoteSearchDto> findCandidateVoteStatistics(Long electionId) {
        QCandidate candidate = QCandidate.candidate;
        QVote vote = QVote.vote;

        List<Tuple> results = queryFactory.select(candidate.name, vote.count())
                .from(candidate)
                .leftJoin(vote).on(vote.candidate.eq(candidate))
                .where(candidate.election.id.eq(electionId))
                .groupBy(candidate.name)
                .fetch();
        return results.stream()
                .map(tuple -> new CandidatesVoteSearchDto(tuple.get(candidate.name), tuple.get(vote.count())))
                .collect(Collectors.toList());
    }
}
