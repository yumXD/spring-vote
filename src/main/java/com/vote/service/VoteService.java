package com.vote.service;

import com.vote.entity.Candidate;
import com.vote.entity.Election;
import com.vote.entity.Member;
import com.vote.entity.Vote;
import com.vote.repository.ElectionRepository;
import com.vote.repository.VoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class VoteService {
    private final VoteRepository voteRepository;
    private final ElectionRepository electionRepository;
    private final MemberService memberService;
    private final ElectionService electionService;
    private final CandidateService candidateService;

    public Vote createVote(String username, Long electionId, Long candidateId) {
        Member member = memberService.getMember(username);
        Election election = electionService.getElection(electionId);
        Candidate candidate = candidateService.getCandidate(candidateId);

        Vote vote = Vote.createVote(member, election, candidate);

        voteRepository.save(vote);
        return vote;
    }
}
