package org.telegram.antischool;

public enum UserState {
    START,
    AWAITING_COUNT,
    PROCESSING,
    WOKEN_UP,
    AWAITING_PERIOD,
    AWAITING_TRANSLATE,
    AWAITING_WORD_TO_ADD,
    PAUSE
}
