package com.vote.service;

import com.vote.dto.ElectionStartFormDto;
import com.vote.entity.Election;
import com.vote.entity.ElectionStart;
import com.vote.exception.ValidateElectionStartException;
import com.vote.repository.ElectionRepository;
import com.vote.repository.ElectionStartRepository;
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
    private final ElectionStartRepository electionStartRepository;

    public Election validateElection(Long electionId) {
        Election election = electionRepository.findById(electionId).orElseThrow(EntityNotFoundException::new);
        return election;
    }


    public ElectionStart startElection(ElectionStartFormDto electionStartFormDto, Long electionId) {
        //선거 찾기
        Election election = electionRepository.findById(electionId).orElseThrow(EntityNotFoundException::new);

        ElectionStart electionStart = ElectionStart.createElectionStart(electionStartFormDto);
        electionStart.addElection(election);

        electionStartRepository.save(electionStart);
        return electionStart;
    }

    // 투표 강제 종료하기
    public void forceEndVoting(Long electionId) {
        Election election = electionRepository.findById(electionId).orElseThrow(EntityNotFoundException::new);

        ElectionStart electionStart = election.getElectionStart();

        if (electionStart == null) {
            throw new ValidateElectionStartException("투표가 아직 시작되지 않았습니다.");
        }

        if (LocalDateTime.now().isAfter(election.getElectionStart().getEndDate())) {
            throw new ValidateElectionStartException("이미 투표가 종료되었습니다.");
        }


        LocalDateTime forcedEndTime = LocalDateTime.now();
        Duration forcedDuration = Duration.between(electionStart.getStartTime(), forcedEndTime);
        electionStart.setDuration(forcedDuration);

        electionStartRepository.save(electionStart);
    }
}
