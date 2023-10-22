package com.vote.entity;

import com.vote.dto.ElectionTimerFormDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Duration;
import java.time.LocalDateTime;

@Entity
@Table(name = "election_timer")
@Getter
@Setter
public class ElectionTimer {
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
        election.setElectionTimer(this);
        this.setElection(election);
    }

    public static ElectionTimer createElectionTimer(ElectionTimerFormDto electionTimerFormDto) {
        ElectionTimer electionTimer = new ElectionTimer();
        electionTimer.setStartTime(LocalDateTime.now());
        electionTimer.setIsActive(true);
        electionTimer.setDuration(Duration.ofSeconds(electionTimerFormDto.getDurationInSeconds()));
        return electionTimer;
    }
}