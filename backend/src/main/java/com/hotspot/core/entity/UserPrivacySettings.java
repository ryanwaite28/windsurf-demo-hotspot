package com.hotspot.core.entity;

import com.hotspot.core.enums.Visibility;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "user_privacy_settings")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPrivacySettings {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Visibility profileVisibility = Visibility.PUBLIC;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Visibility postsVisibility = Visibility.PUBLIC;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Visibility locationVisibility = Visibility.FOLLOWERS;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Visibility phoneVisibility = Visibility.NOBODY;
}
