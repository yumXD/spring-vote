package com.vote.service;

import com.vote.dto.CandidateSearchDto;
import com.vote.dto.ElectionFormDto;
import com.vote.entity.Candidate;
import com.vote.entity.Election;
import com.vote.entity.Member;
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
        return ElectionFormDto.of(election);
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
}
