package com.airbng.admin.domain.review;

import com.airbng.admin.domain.PendingLockerImage;
import com.airbng.domain.base.BaseStatus;
import com.airbng.domain.base.BaseTime;
import com.airbng.domain.jimtype.JimType;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PendingLocker extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pendingLockerId;

    @Column(nullable = false)
    private String lockerName;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String addressEnglish;

    @Column(nullable = false)
    private String addressDetail;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "VARCHAR(10)")
    private BaseStatus status;

    @OneToMany(mappedBy = "pendingLocker")
    private Set<PendingLockerImage> pendingLockerImages;

    @OneToMany(mappedBy = "pendingLocker")
    @Builder.Default
    private Set<PendingLockerJimtype> pendingLockerJimtypes = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Long memberId;

    public boolean validatePendingLockerJimtype(JimType jimtype){
        return pendingLockerJimtypes.stream()
                .anyMatch(pendingLockerJimtype ->
                        Objects.equals(pendingLockerJimtype.getJimtypeId(), jimtype.getJimTypeId()));
    }
}
