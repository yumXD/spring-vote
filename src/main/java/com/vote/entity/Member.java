package com.vote.entity;

import com.vote.constant.Role;
import com.vote.dto.MemberFormDto;
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
@Table(name = "member")
@Getter
@Setter
@ToString
public class Member extends BaseEntity implements UserDetails {
    @Id
    @Column(name = "member_id")
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

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Election> elections = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Vote> votes = new ArrayList<>();

    public void addElection(Election election) {
        elections.add(election);
        election.setMember(this);
    }

    public static Member createMember(MemberFormDto memberFormDto, PasswordEncoder passwordEncoder) {
        Member member = new Member();
        member.setUsername(memberFormDto.getName());
        member.setEmail(memberFormDto.getEmail());
        member.setAddress(memberFormDto.getAddress());
        String password = passwordEncoder.encode(memberFormDto.getPassword());
        member.setPassword(password);
        member.setBirth(memberFormDto.getBirth());
        member.setRole(Role.ADMIN);
        return member;
    }

    public void addVote(Vote vote) {
        votes.add(vote);
        vote.setMember(this);
    }

    public void updatePassword(String password, PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(password);
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
