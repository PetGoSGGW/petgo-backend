package pl.petgo.backend.dto.reservation;

import pl.petgo.backend.dto.GpsPointDto;

import java.util.List;

public class GpsRouteDto {

    private final boolean exists;
    private final Double distanceMeters;
    private final List<GpsPointDto> route;

    public GpsRouteDto(
            boolean exists,
            Double distanceMeters,
            List<GpsPointDto> route
    ) {
        this.exists = exists;
        this.distanceMeters = distanceMeters;
        this.route = route;
    }

    public boolean isExists() {
        return exists;
    }

    public Double getDistanceMeters() {
        return distanceMeters;
    }

    public List<GpsPointDto> getRoute() {
        return route;
    }
}
