package ru.test.sms.app;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class Account {
    private final UUID accountId;
    private final Long balance;
}
