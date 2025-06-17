package com.airbng.domain.image;

import com.airbng.domain.base.BaseTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Builder;
import org.springframework.lang.NonNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Image extends BaseTime {
//    @NonNull
    private Long imageId;
    @NonNull
    private String url;
    @NonNull
    private String uploadName;

    // com.airbng.domain.image.Image
    public static Image withId(Long imageId) {
        return Image.builder()
                .imageId(imageId)
                .url("https://example.com/images/profile" + imageId + ".jpg")
                .uploadName("profile" + imageId + ".jpg")
                .build();
    }

}
