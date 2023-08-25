package com.vote.entity;

import com.vote.dto.ElectionStartFormDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Duration;
import java.time.LocalDateTime;

@Entity
@Table(name = "election_start")
@Getter
@Setter
public class ElectionStart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime startTime;

    // 투표 지속 시간 (예: 1시간, 24시간 등)
    @Column(nullable = false)
    private Duration duration;

    private Boolean isActive;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "election_id")
    private Election election;

    // 투표 종료 시간을 계산하는 메소드
    public LocalDateTime getEndDate() {
        return startTime.plus(duration);
    }

    public void addElection(Election election) {
        election.setElectionStart(this);
        this.setElection(election);
    }

    public static ElectionStart createElectionStart(ElectionStartFormDto electionStartFormDto) {
        ElectionStart electionStart = new ElectionStart();
        electionStart.setStartTime(LocalDateTime.now());
        electionStart.setIsActive(true);
        electionStart.setDuration(Duration.ofSeconds(electionStartFormDto.getDurationInSeconds()));
        return electionStart;
    }
}