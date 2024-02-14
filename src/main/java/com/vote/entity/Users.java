package com.vote.entity;

import com.vote.constant.Role;
import com.vote.dto.UserFormDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "user")
@Getter
@Setter
@ToString
public class Users implements UserDetails {
    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    @Column(unique = true)
    private String email;

    private String password;

    private String address;

    @Enumerated(EnumType.STRING)
    private Role role;

    private LocalDate birth;

    @OneToMany(mappedBy = "users", cascade = CascadeType.ALL)
    private List<Election> elections = new ArrayList<>();

    @OneToMany(mappedBy = "users", cascade = CascadeType.ALL)
    private List<Vote> votes = new ArrayList<>();

    public static Users createUser(UserFormDto userFormDto, PasswordEncoder passwordEncoder) {
        Users users = new Users();
        users.setUsername(userFormDto.getName());
        users.setEmail(userFormDto.getEmail());
        users.setAddress(userFormDto.getAddress());
        String password = passwordEncoder.encode(userFormDto.getPassword());
        users.setPassword(password);
        users.setBirth(userFormDto.getBirth());
        users.setRole(Role.ADMIN);
        return users;
    }

    public void updatePassword(String password, PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(password);
    }


    public void addElection(Election election) {
        elections.add(election);
        election.setUsers(this);
    }

    public void addVote(Vote vote) {
        votes.add(vote);
        vote.setUsers(this);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 사용자의 권한을 반환합니다.
        return Collections.singleton(new SimpleGrantedAuthority("ROLE_" + role.toString()));
    }

    public String getActualUsername() {
        return username;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // 사용자 계정이 만료되지 않았다면 true를 반환
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // 사용자 계정이 잠겨 있지 않다면 true를 반환
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // 사용자 자격 증명(비밀번호)이 만료되지 않았다면 true를 반환
    }

    @Override
    public boolean isEnabled() {
        return true; // 사용자 계정이 활성화되었다면 true를 반환
    }
}
