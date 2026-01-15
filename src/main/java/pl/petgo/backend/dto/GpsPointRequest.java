package pl.petgo.backend.dto;

public record GpsPointRequest(Long sessionId, Double latitude, Double longitude) {}
