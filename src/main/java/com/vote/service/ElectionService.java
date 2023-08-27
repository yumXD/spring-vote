package com.vote.service;

import com.vote.dto.CandidateSearchDto;
import com.vote.dto.ElectionFormDto;
import com.vote.entity.Candidate;
import com.vote.entity.Election;
import com.vote.entity.Member;
import com.vote.exception.CertificationException;
import com.vote.exception.ValidateElectionStartException;
import com.vote.repository.ElectionRepository;
import com.vote.repository.MemberRepository;
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

    private final MemberRepository memberRepository;

    public Election saveElection(ElectionFormDto electionFormDto, String email) {
        //회원 찾기
        Member member = memberRepository.findByEmail(email);

        if (member == null) {
            throw new IllegalStateException("존재하지 않는 회원입니다.");
        }

        //선거 등록
        Election election = electionFormDto.toEntity();

        member.addElection(election);

        electionRepository.save(election);

        return election;
    }

    @Transactional(readOnly = true)
    public ElectionFormDto getElectionDtl(Long id) {
        Election election = electionRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        ElectionFormDto electionFormDto = ElectionFormDto.of(election);

        // 선거 시작 설정
        if (election.getElectionStart() != null) {
            electionFormDto.setEndTime(election.getElectionStart().getEndDate());
            electionFormDto.setIsActive(election.getElectionStart().getIsActive());
        }

        return electionFormDto;
    }

    @Transactional(readOnly = true)
    public Page<Election> getAdminCandidatePage(CandidateSearchDto candidateSearchDto, Pageable pageable) {
        return electionRepository.getAdminElectionPage(candidateSearchDto, pageable);
    }

    public List<Candidate> getCandidates(Long electionId) {
        Election election = electionRepository.findById(electionId).orElseThrow(EntityNotFoundException::new);
        return election.getCandidates();
    }

    public String getEmail(Long electionId) {
        Election election = electionRepository.findById(electionId).orElseThrow(EntityNotFoundException::new);
        return election.getMember().getEmail();
    }

    public Long updateElection(ElectionFormDto electionFormDto) {
        //선거 수정
        Election election = electionRepository.findById(electionFormDto.getId()).orElseThrow(EntityNotFoundException::new);
        election.updateElection(electionFormDto);
        return election.getId();
    }

    public Election getElection(Long electionId) {
        return electionRepository.findById(electionId).orElseThrow(EntityNotFoundException::new);
    }

    public void certification(Long electionId, String email) {
        Election election = electionRepository.findById(electionId).orElse(null);

        if (election != null) {
            if (!election.getMember().getEmail().equals(email)) {
                throw new CertificationException("인증이 없어 권한이 없습니다.");
            }
        }

    }

    // 선거가 시작되었는지 검증하기
    public void validateElectionStart(Long electionId) {
        Election election = electionRepository.findById(electionId).orElseThrow(EntityNotFoundException::new);

        if (election.getElectionStart() != null) {
            throw new ValidateElectionStartException("투표가 시작 혹은 종료되어 권한이 없습니다.");
        }
    }

    // 선거 삭제
    public void deleteElection(Long electionId) {
        Election election = electionRepository.findById(electionId).orElseThrow(EntityNotFoundException::new);
        this.electionRepository.delete(election);
    }
}
