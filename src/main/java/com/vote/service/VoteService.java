package com.vote.service;

import com.vote.dto.CandidatesVoteSearchDto;
import com.vote.entity.Candidate;
import com.vote.entity.Election;
import com.vote.entity.Member;
import com.vote.entity.Vote;
import com.vote.exception.DuplicateVoteException;
import com.vote.repository.ElectionRepository;
import com.vote.repository.VoteRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

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

    public void validateDuplicateVote(String username, Long electionId) {
        Member member = memberService.getMember(username);
        Vote findVote = voteRepository.findByElectionIdAndMemberId(electionId, member.getId());
        if (findVote != null) {
            throw new DuplicateVoteException("이미 투표했습니다.");
        }
    }

    // 각 선거의 총 투표수 가져오기
    public Long getElectionTotalVotes(Long electionId) {
        electionRepository.findById(electionId).orElseThrow(EntityNotFoundException::new);
        Long totalVotesForElection = voteRepository.findElectionTotalVotes(electionId);
        if (totalVotesForElection == null) {
            return 0L;
        }
        return totalVotesForElection;
    }

    // 각 선거의 후보자들의 득표수 가져오기
    public List<CandidatesVoteSearchDto> getCandidateVoteStatistics(Long electionId) {
        return voteRepository.findCandidateVoteStatistics(electionId);
    }

    // 최고 득표자를 가져오기
    public List<CandidatesVoteSearchDto> getTopVotedCandidate(Long electionId) {
        List<CandidatesVoteSearchDto> candidates = getCandidateVoteStatistics(electionId);
        Long maxVotes = candidates.stream()
                .map(CandidatesVoteSearchDto::getVoteCount)
                .max(Long::compare)
                .orElse(0L);  // 기본값 설정

        return candidates.stream()
                .filter(candidate -> candidate.getVoteCount().equals(maxVotes) && candidate.getVoteCount() > 0)
                .collect(Collectors.toList());
    }
}
