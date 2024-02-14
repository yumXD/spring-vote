package com.vote.service;

import com.vote.dto.UserFormDto;
import com.vote.entity.Users;
import com.vote.repository.AdminUserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminUserService {
    private final AdminUserRepository adminUserRepository;

    public Page<Users> getAdminUserPage(int page, String kw) {
        List<Sort.Order> sorts = new ArrayList<>();
        //sorts.add(Sort.Order.desc("regTime"));
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
        return this.adminUserRepository.findAllByKeyword(kw, pageable);
    }

    public Users findById(Long id) {
        return adminUserRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 회원 정보입니다."));
    }

    public UserFormDto getUserDtl(Long memberId) {
        Users users = findById(memberId);
        return UserFormDto.of(users);
    }

    public Users updatePassword(UserFormDto userFormDto, Users users, PasswordEncoder passwordEncoder) {
        users.updatePassword(userFormDto.getPassword(), passwordEncoder);
        return users;
    }
}
