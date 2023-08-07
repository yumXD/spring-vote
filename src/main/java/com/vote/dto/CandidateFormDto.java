package com.vote.dto;

import com.vote.entity.Candidate;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CandidateFormDto {
    private Long id;

    @NotBlank(message = "후보자 이름은 필수 입력값 입니다.")
    private String name;

    @NotBlank(message = "후보자 설명은 필수 입력값 입니다.")
    private String description;

    public Candidate toEntity() {
        Candidate candidate = new Candidate();
        candidate.setName(name);
        candidate.setDescription(description);
        return candidate;
    }
}
