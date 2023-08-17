package com.vote.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VoteFormDto {
    @NotNull(message = "후보자를 선택해주세요")
    private Long candidateId;
}
