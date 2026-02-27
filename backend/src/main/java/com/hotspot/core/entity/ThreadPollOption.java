package com.hotspot.core.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "thread_poll_options")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ThreadPollOption {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "poll_id", nullable = false)
    private ThreadPoll poll;

    @Column(nullable = false)
    private String optionText;

    @Column(name = "display_order", nullable = false)
    private int displayOrder;
}
