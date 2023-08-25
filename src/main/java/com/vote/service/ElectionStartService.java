package com.vote.service;

import com.vote.dto.ElectionStartFormDto;
import com.vote.entity.Election;
import com.vote.entity.ElectionStart;
import com.vote.repository.ElectionRepository;
import com.vote.repository.ElectionStartRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
