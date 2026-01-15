package pl.petgo.backend.dto.user;

public record CreateAddressRequest(
        String label,
        String homeNumber,
        Double lat,
        Double lon,
        String city,
        String street,
        String zipcode
) {
}
