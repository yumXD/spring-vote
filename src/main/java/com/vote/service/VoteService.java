package com.vote.service;

import com.vote.dto.CandidatesVoteSearchDto;
import com.vote.entity.Candidate;
import com.vote.entity.Election;
import com.vote.entity.Member;
import com.vote.entity.Vote;
import com.vote.exception.DuplicateVoteException;
import com.vote.exception.ElectionInProgressException;
import com.vote.repository.VoteRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class VoteService {
    private final VoteRepository voteRepository;
    private final MemberService memberService;
    private final ElectionService electionService;
    private final CandidateService candidateService;


    public Vote doVote(String email, Long electionId, Long candidateId) {
        Member member = memberService.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 회원입니다."));
        Election election = electionService.findById(electionId);
        Candidate candidate = candidateService.findById(candidateId);

        //중복 투표 체크..
        isVoted(electionId, member);

        Vote vote = Vote.createVote(member, election, candidate);
        voteRepository.save(vote);
        return vote;
    }

    public void isVoted(Long electionId, Member member) {
        Vote findVote = voteRepository.findByElectionIdAndMemberId(electionId, member.getId());
        if (findVote != null) {
            throw new DuplicateVoteException("이미 투표했습니다.");
        }
    }

    // 각 선거의 총 투표수 가져오기
    public Long getElectionTotalVotes(Long electionId) {
        //투표가 종료되었는지..
        isVotingFinished(electionId);

        Long totalVotesForElection = voteRepository.findElectionTotalVotes(electionId);
        if (totalVotesForElection == null) {
            return 0L;
        }
        return totalVotesForElection;
    }

    public void isVotingFinished(Long electionId) {
        Election election = electionService.findById(electionId);

        if (election.getElectionTimer() == null) {
            throw new ElectionInProgressException("선거 시작 정보가 설정되지 않아 시작되지 않았습니다.", electionId);
        }

        if (!LocalDateTime.now().isAfter(election.getElectionTimer().getEndDate())) {
            throw new ElectionInProgressException("투표 시간이 종료되지 않았습니다.", electionId);
        }
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

    // 투표가 진행중인지 검증하기
    public List<Candidate> isVotingInProgress(Election election) {

        if (election.getElectionTimer() == null) {
            throw new ElectionInProgressException("선거 시작 정보가 설정되지 않았습니다.", election.getId());
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = election.getElectionTimer().getStartTime();
        LocalDateTime end = election.getElectionTimer().getEndDate();

        if (!(now.isAfter(start) && now.isBefore(end))) {
            throw new ElectionInProgressException("투표는 현재 진행 중이 아닙니다.", election.getId());
        }

        return election.getCandidates();
    }
}
