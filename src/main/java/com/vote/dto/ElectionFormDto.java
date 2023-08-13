package com.vote.dto;

import com.vote.entity.Election;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ElectionFormDto {
    private Long id;

    @NotBlank(message = "선거 제목은 필수 입력값 입니다.")
    private String title;

    private LocalDateTime endTime;

    private Boolean isActive;

    public static ElectionFormDto of(Election election) {
        ElectionFormDto electionFormDto = new ElectionFormDto();
        electionFormDto.setId(election.getId());
        electionFormDto.setTitle(election.getTitle());
        return electionFormDto;
    }

    public Election toEntity() {
        Election election = new Election();
        election.setTitle(title);
        return election;
    }
}
