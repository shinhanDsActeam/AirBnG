package com.airbng.core.domain.image;

import com.airbng.core.domain.Locker;
import com.airbng.core.domain.base.BaseStatus;
import com.airbng.core.domain.base.BaseTime;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Builder;

import static jakarta.persistence.FetchType.LAZY;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LockerImage extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long lockerImageId;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "locker_id", nullable = false)
    private Locker locker;

    @OneToOne
    @JoinColumn(name = "image_id", nullable = false)
    private Image image;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "VARCHAR(10)")
    private BaseStatus status;
}
