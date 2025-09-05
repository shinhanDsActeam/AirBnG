package com.airbng.domain.image;

import com.airbng.domain.base.BaseStatus;
import com.airbng.domain.base.BaseTime;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Builder;
import org.springframework.lang.NonNull;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Image extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long imageId;

    @Column(nullable = false, columnDefinition = "VARCHAR(1000)")
    private String url;

    @Column(nullable = false)
    private String uploadName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "VARCHAR(10) DEFAULT 'ACTIVE'" )
    private BaseStatus status = BaseStatus.ACTIVE;

    // com.airbng.domain.image.Image
    public static Image withId(Long imageId) {
        return Image.builder()
                .imageId(imageId)
                .url("https://example.com/images/profile" + imageId + ".jpg")
                .uploadName("profile" + imageId + ".jpg")
                .status(BaseStatus.ACTIVE)
                .build();
    }

}
