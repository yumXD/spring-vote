package com.vote.service;

import com.vote.dto.ElectionTimerFormDto;
import com.vote.entity.Election;
import com.vote.entity.ElectionTimer;
import com.vote.exception.ElectionInProgressException;
import com.vote.repository.ElectionRepository;
import com.vote.repository.ElectionTimerRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
public class ElectionStartService {
    private final ElectionRepository electionRepository;
    private final ElectionTimerRepository electionTimerRepository;

    public Election validateElection(Long electionId) {
        Election election = electionRepository.findById(electionId).orElseThrow(EntityNotFoundException::new);
        return election;
    }


    public ElectionTimer startElection(ElectionTimerFormDto electionTimerFormDto, Long electionId) {
        //선거 찾기
        Election election = electionRepository.findById(electionId).orElseThrow(EntityNotFoundException::new);

        ElectionTimer electionTimer = ElectionTimer.createElectionTimer(electionTimerFormDto);
        electionTimer.addElection(election);

        electionTimerRepository.save(electionTimer);
        return electionTimer;
    }

    // 투표 강제 종료하기
    public void forceEndVoting(Long electionId) {
        Election election = electionRepository.findById(electionId).orElseThrow(EntityNotFoundException::new);

        ElectionTimer electionTimer = election.getElectionTimer();

        if (electionTimer == null) {
            throw new ElectionInProgressException("투표가 아직 시작되지 않았습니다.", electionId);
        }

        if (LocalDateTime.now().isAfter(election.getElectionTimer().getEndDate())) {
            throw new ElectionInProgressException("이미 투표가 종료되었습니다.", electionId);
        }


        LocalDateTime forcedEndTime = LocalDateTime.now();
        Duration forcedDuration = Duration.between(electionTimer.getStartTime(), forcedEndTime);
        electionTimer.setDuration(forcedDuration);

        electionTimerRepository.save(electionTimer);
    }
}
