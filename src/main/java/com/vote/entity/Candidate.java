package com.vote.entity;

import com.vote.dto.CandidateFormDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "candidate")
@Getter
@Setter
@ToString
public class Candidate extends BaseEntity {
    @Id
    @Column(name = "candidate_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "election_id")
    private Election election;

    public void updateCandidate(CandidateFormDto candidateFormDto) {
        this.name = candidateFormDto.getName();
        this.description = candidateFormDto.getDescription();
    }
}
