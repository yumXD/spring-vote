package com.vote.dto;

import com.vote.entity.Member;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
public class MemberFormDto {
    private Long id;

    @NotBlank(message = "이름은 필수 입력 값입니다.")
    private String name;

    @NotEmpty(message = "이메일은 필수 입력 값입니다.")
    @Email(message = "이메일 형식으로 입력해주세요.")
    private String email;

    @NotEmpty(message = "비밀번호는 필수 입력 값입니다.")
    @Length(min = 8, max = 16, message = "비밀번호는 8자 이상, 16자 이하로 입력해주세요.")
    private String password;

    @NotEmpty(message = "주소는 필수 입력 값입니다.")
    private String address;

    @NotNull(message = "생일은 필수 입력 값입니다.")
    @PastOrPresent(message = "생일은 현재 날짜나 과거 날짜여야 합니다.")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birth;

    public static MemberFormDto of(Member member) {
        MemberFormDto memberFormDto = new MemberFormDto();
        memberFormDto.setId(member.getId());
        memberFormDto.setName(member.getActualUsername());
        memberFormDto.setEmail(member.getEmail());
        memberFormDto.setAddress(member.getAddress());
        memberFormDto.setBirth(member.getBirth());
        return memberFormDto;

    }
}
