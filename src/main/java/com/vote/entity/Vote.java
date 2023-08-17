package com.vote.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "vote")
@Getter
@Setter
@ToString
public class Vote extends BaseEntity {
    @Id
    @Column(name = "vote_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "candidate_id")
    private Candidate candidate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "election_id")
    private Election election;

    public static Vote createVote(Member member, Election election, Candidate candidate) {
        Vote vote = new Vote();
        member.addVote(vote);
        election.addVote(vote);
        candidate.addVote(vote);
        return vote;
    }
}
