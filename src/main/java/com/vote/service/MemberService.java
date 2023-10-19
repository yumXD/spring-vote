package com.vote.service;

import com.vote.entity.Member;
import com.vote.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;

    public Member savedMember(Member member) {
        validateDuplicateEmail(member);
        return memberRepository.save(member);
    }

    private void validateDuplicateEmail(Member member) {
        Member findMember = memberRepository.findByEmail(member.getEmail());
        if (findMember != null) {
            throw new IllegalStateException("이미 가입된 이메일입니다.");
        }
    }

    public Member getMember(String email) {
        return memberRepository.findByEmail(email);
    }
}
