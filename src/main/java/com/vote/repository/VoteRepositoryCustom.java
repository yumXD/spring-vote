package com.vote.repository;

import com.querydsl.core.types.Projections;
import com.vote.dto.CandidatesVoteSearchDto;
import com.vote.entity.Vote;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.vote.entity.QCandidate.candidate;
import static com.vote.entity.QVote.vote;

@Repository
public class VoteRepositoryCustom extends Querydsl4RepositorySupport {

    public VoteRepositoryCustom() {
        super(Vote.class);
    }

    public Optional<Vote> findByElectionIdAndUsersId(Long electionId, Long usersId) {
        return Optional.ofNullable(selectFrom(vote)
                .where(vote.election.id.eq(electionId)
                        .and(vote.election.users.id.eq(usersId)))
                .fetchOne());
    }

    public Long findElectionTotalVotes(Long electionId) {
        return select(vote.count())
                .from(vote)
                .where(vote.election.id.eq(electionId))
                .fetchOne();
    }

    public List<CandidatesVoteSearchDto> findCandidateVoteStatistics(Long electionId) {
        return select(
                Projections.constructor(
                        CandidatesVoteSearchDto.class,
                        candidate.name,
                        vote.count()
                ))
                .from(candidate)
                .leftJoin(vote).on(vote.candidate.eq(candidate))
                .where(candidate.election.id.eq(electionId))
                .groupBy(candidate.name)
                .fetch();
    }
}
