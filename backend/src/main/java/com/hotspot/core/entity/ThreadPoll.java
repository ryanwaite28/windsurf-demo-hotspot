package com.hotspot.core.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "thread_polls")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ThreadPoll {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "thread_id", nullable = false, unique = true)
    private Thread thread;

    @Column(nullable = false)
    private String question;

    private Instant expiresAt;
}
