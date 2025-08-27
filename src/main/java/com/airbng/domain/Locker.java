package com.airbng.domain;

import com.airbng.domain.base.Available;
import com.airbng.domain.base.BaseStatus;
import com.airbng.domain.base.BaseTime;
import com.airbng.domain.image.LockerImage;
import com.airbng.domain.jimtype.JimType;
import com.airbng.domain.jimtype.LockerJimType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.lang.NonNull;

import java.util.HashSet;
import java.util.List;
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

    @OneToMany(mappedBy = "locker")
    private Set<LockerImage> lockerImages;

    @OneToMany(mappedBy = "locker")
    @Builder.Default
    private Set<LockerJimType> lockerJimTypes = new HashSet<>();

    @OneToMany(mappedBy = "locker")
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
