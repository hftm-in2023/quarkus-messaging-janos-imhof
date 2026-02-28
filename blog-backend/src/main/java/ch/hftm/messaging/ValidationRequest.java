package ch.hftm.messaging;

public record ValidationRequest(long sourceId, String sourceType, String text) {}
