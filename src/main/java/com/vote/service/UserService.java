package com.vote.service;

import com.vote.dto.UserFormDto;
import com.vote.entity.Users;
import com.vote.repository.UserRepository;
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
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public Users saveUser(Users users) {
        validateDuplicateEmail(users);
        return userRepository.save(users);
    }

    private void validateDuplicateEmail(Users users) {
        Optional<Users> findMember = userRepository.findByEmail(users.getEmail());
        if (findMember.isPresent()) {
            throw new IllegalStateException("이미 가입된 이메일입니다.");
        }
    }

    public Optional<Users> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Page<Users> getAdminUserPage(int page, String kw) {
        List<Sort.Order> sorts = new ArrayList<>();
        //sorts.add(Sort.Order.desc("regTime"));
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
        return this.userRepository.findAllByKeyword(kw, pageable);
    }

    public Users findById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 회원 정보입니다."));
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
