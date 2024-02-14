package com.vote.service;

import com.vote.dto.CandidateSearchDto;
import com.vote.dto.ElectionFormDto;
import com.vote.entity.Candidate;
import com.vote.entity.Election;
import com.vote.entity.Users;
import com.vote.exception.AccessAllowedException;
import com.vote.exception.ElectionInProgressException;
import com.vote.repository.ElectionRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ElectionService {
    private final ElectionRepository electionRepository;

    private final UserService userService;

    public Election saveElection(ElectionFormDto electionFormDto, String email) {
        //회원 찾기
        Users users = userService.findByEmail(email).orElseThrow(() -> new IllegalStateException("존재하지 않는 회원입니다."));

        //선거 등록
        Election election = electionFormDto.toEntity();

        users.addElection(election);

        electionRepository.save(election);

        return election;
    }

    public Election findById(Long id) {
        return electionRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 선거 정보입니다."));
    }

    @Transactional(readOnly = true)
    public ElectionFormDto getElectionDtl(Long id) {
        Election election = findById(id);
        ElectionFormDto electionFormDto = ElectionFormDto.of(election);

        // 선거 시작 설정
        if (election.getElectionTimer() != null) {
            electionFormDto.setEndTime(election.getElectionTimer().getEndDate());
            electionFormDto.setIsActive(election.getElectionTimer().getIsActive());
        }

        return electionFormDto;
    }

    @Transactional(readOnly = true)
    public Page<Election> getAdminCandidatePage(CandidateSearchDto candidateSearchDto, Pageable pageable) {
        return electionRepository.getAdminElectionPage(candidateSearchDto, pageable);
    }

    public List<Candidate> getCandidates(Long electionId) {
        Election election = findById(electionId);
        return election.getCandidates();
    }

    public String getEmail(Long electionId) {
        Election election = findById(electionId);
        return election.getUsers().getEmail();
    }

    public Election updateElection(ElectionFormDto electionFormDto) {
        Election election = findById(electionFormDto.getId());
        election.updateElection(electionFormDto);
        return election;
    }

    public void deleteElection(Long electionId) {
        Election election = findById(electionId);
        this.electionRepository.delete(election);
    }

    public void isAccessAllowed(Long electionId, String email) {
        Election election = findById(electionId);
        if (!election.getUsers().getEmail().equals(email)) {
            throw new AccessAllowedException("접근 권한이 없습니다.");
        }
    }

    // 선거가 시작되었는지 검증하기
    public void isElectionInProgress(Long electionId) {
        Election election = findById(electionId);
        if (election.getElectionTimer() != null) {
            throw new ElectionInProgressException("투표가 시작 혹은 종료되어 권한이 없습니다.", electionId);
        }
    }
}
