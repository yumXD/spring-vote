package com.vote.service;

import com.vote.entity.Users;
import com.vote.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
