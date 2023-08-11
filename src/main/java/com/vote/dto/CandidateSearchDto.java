package com.vote.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CandidateSearchDto {
    private String searchDateType;
    private String searchBy;
    private String searchQuery = "";
}
