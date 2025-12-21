package pl.petgo.backend.dto.User;

import java.time.Instant;

public record AddressResponse(
        Long addressId,
        String label,
        String homeNumber,
        Double lat,
        Double lon,
        String city,
        String street,
        String zipcode,
        Instant createdAt
) {
}
