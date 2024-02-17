package com.vote.service;

import com.vote.entity.Users;
import com.vote.repository.UserRepositoryCustom;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class UserDetailService implements UserDetailsService {
    private final UserRepositoryCustom userRepositoryCustom;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Users users = userRepositoryCustom.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 회원입니다."));
        log.info("{} 이메일을 가진 회원 로그인", email);
        return users;
    }
}
