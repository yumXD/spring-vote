package com.vote.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ElectionStartFormDto {
    private Long Id;

    @NotNull(message = "제한시간은 필수 입력값입니다.")
    @Min(value = 60, message = "제한시간은 최소 1분(60) 이상이어야 합니다.") // 제한시간이 1분 보다 커야 함
    @Max(value = 10080, message = "제한시간은 최대 7일(10080) 이내여야 합니다.")  // 최대 7일 (7*24*60 분)
    private Long durationInSeconds;
}
