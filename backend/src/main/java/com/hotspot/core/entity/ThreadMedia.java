package com.hotspot.core.entity;

import com.hotspot.core.enums.MediaType;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "thread_media")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ThreadMedia {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "thread_id", nullable = false)
    private Thread thread;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MediaType mediaType;

    @Column(nullable = false)
    private String url;

    @Column(name = "display_order", nullable = false)
    private int displayOrder;
}
