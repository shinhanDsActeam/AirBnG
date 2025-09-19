package com.airbng.api.admin.dto.command;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LockerViewStatusCommand {
    private String memberId;
}
