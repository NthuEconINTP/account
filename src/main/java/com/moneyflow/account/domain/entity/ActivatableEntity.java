package com.moneyflow.account.domain.entity;

import java.time.LocalDateTime;

public interface ActivatableEntity {
	Boolean getIsActive();
    void setIsActive(Boolean active);

    LocalDateTime getCreatedAt();
    void setCreatedAt(LocalDateTime time);

    LocalDateTime getLastUpdatedAt();
    void setLastUpdatedAt(LocalDateTime time);
}
