package com.airbng.consumer.domain;

import com.airbng.consumer.domain.base.Role;
import com.airbng.common.base.BaseStatus;
import com.airbng.common.base.BaseTime;
import com.airbng.consumer.domain.image.Image;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Builder;

import java.util.Set;

import static jakarta.persistence.FetchType.LAZY;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Member extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false)
    private String password;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "VARCHAR(10)")
    private BaseStatus status;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "profile_image_id")
    private Image profileImage;

    @OneToMany(mappedBy = "member")
    private Set<Zzim> zzims;

    public void updateInfo(String email, String name, String phone, String nickname, Image profileImage) {
        this.email = email;
        this.name = name;
        this.phone = phone;
        this.nickname = nickname;
        this.profileImage = profileImage;
    }

    public Member(Long memberId, String role) {
        this.memberId = memberId;
        this.role = Role.valueOf(role);
    }
}
