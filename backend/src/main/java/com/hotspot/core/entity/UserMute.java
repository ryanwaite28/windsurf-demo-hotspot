package com.hotspot.core.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "user_mutes", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"muter_id", "muted_id"})
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserMute {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "muter_id", nullable = false)
    private User muter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "muted_id", nullable = false)
    private User muted;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;
}
