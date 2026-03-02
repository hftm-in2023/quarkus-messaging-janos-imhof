package ch.hftm.messaging;

public record ValidationResponse(long sourceId, String sourceType, boolean valid) {}
