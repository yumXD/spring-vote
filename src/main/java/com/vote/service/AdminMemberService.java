package com.vote.service;

import com.vote.dto.MemberFormDto;
import com.vote.entity.Member;
import com.vote.repository.AdminMemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminMemberService {
    private final AdminMemberRepository adminMemberRepository;
    public Page<Member> getAdminMemberPage(int page, String kw) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("regTime"));
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
        return this.adminMemberRepository.findAllByKeyword(kw, pageable);
    }

    public MemberFormDto getMemberDtl(Long memberId) {
        Member member = adminMemberRepository.findById(memberId).orElseThrow(EntityNotFoundException::new);
        return MemberFormDto.of(member);
    }
}
