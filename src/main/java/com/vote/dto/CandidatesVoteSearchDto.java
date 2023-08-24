package com.vote.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CandidatesVoteSearchDto {
    private String candidateName;
    private Long voteCount;

    public CandidatesVoteSearchDto(String candidateName, Long voteCount) {
        this.candidateName = candidateName;
        this.voteCount = voteCount;
    }
}