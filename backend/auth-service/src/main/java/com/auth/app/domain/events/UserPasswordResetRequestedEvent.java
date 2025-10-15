package com.auth.app.domain.events;

import lombok.Data;
import com.auth.app.domain.model.PasswordResetTokenModel;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@AllArgsConstructor
@Builder
public class UserPasswordResetRequestedEvent {

    private PasswordResetTokenModel token;
    private String frontendUrl;
}
