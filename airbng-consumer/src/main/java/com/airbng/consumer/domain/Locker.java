package com.airbng.consumer.domain;

import com.airbng.common.base.Available;
import com.airbng.common.base.BaseStatus;
import com.airbng.common.base.BaseTime;
import com.airbng.consumer.domain.image.LockerImage;
import com.airbng.consumer.domain.jimtype.JimType;
import com.airbng.consumer.domain.jimtype.LockerJimType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Locker extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long lockerId;

    @Column(nullable = false)
    private String lockerName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Available isAvailable;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String addressEnglish;

    @Column(nullable = false)
    private String addressDetail;

    @Column(nullable = false)
    private Double latitude; // 위도

    @Column(nullable = false)
    private Double longitude; // 경도

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "VARCHAR(10)")
    private BaseStatus status;

    @OneToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member keeper;

    @OneToMany(mappedBy = "locker", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private Set<LockerImage> lockerImages;

    @OneToMany(mappedBy = "locker", cascade = CascadeType.REMOVE, orphanRemoval = true)
    @Builder.Default
    private Set<LockerJimType> lockerJimTypes = new HashSet<>();

    @OneToMany(mappedBy = "locker", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private Set<Zzim> zzims;

    @Column(nullable = false)
    @ColumnDefault("0")
    private Long reservationCount;

    public boolean validateLockerJimtype(JimType jimType){
        return lockerJimTypes.stream()
                .anyMatch(lockerJimType ->
                        Objects.equals(lockerJimType.getJimType().getJimTypeId(), jimType.getJimTypeId()));
    }

    public void updateIsAvailable(){
        isAvailable = isAvailable.equals(Available.YES)?Available.NO:Available.YES;
    }
}