package pl.petgo.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.petgo.backend.domain.Address;
import pl.petgo.backend.domain.User;
import pl.petgo.backend.dto.User.*;
import pl.petgo.backend.exception.*;
import pl.petgo.backend.repository.AddressRepository;
import pl.petgo.backend.repository.UserRepository;
import pl.petgo.backend.repository.WalletRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final WalletRepository walletRepository;

    private static UserResponse toResponse(User u) {
        if (u == null) return null;
        return new UserResponse(
                u.getUserId(),
                u.getUsername(),
                u.getEmail(),
                u.getRole(),
                u.getFirstName(),
                u.getLastName(),
                u.getDateOfBirth()
        );
    }

    public static AddressResponse toResponseAddress(Address a) {
        if (a == null) return null;

        return new AddressResponse(
                a.getAddressId(),
                a.getLabel(),
                a.getHomeNumber(),
                a.getLatitude(),
                a.getLongitude(),
                a.getCity(),
                a.getStreet(),
                a.getZipcode(),
                a.getCreatedAt()

        );
    }

    public static Address fromCreateAddressDto(CreateAddressRequest dto, User user) {
        Address a = new Address();
        a.setUser(user);
        a.setLabel(dto.label());
        a.setHomeNumber(dto.homeNumber());
        a.setLatitude(dto.lat());
        a.setLongitude(dto.lon());
        a.setCity(dto.city());
        a.setStreet(dto.street());
        a.setZipcode(dto.zipcode());
        return a;
    }

    public UserResponse updateUser(Long id, UpdateUserRequest updatedUserData) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User with" + id + "  does not exits"));

        if (updatedUserData.username() != null && !updatedUserData.username().equals(user.getUsername())) {
            if (userRepository.existsByUsername(updatedUserData.username())){
                throw new DuplicateResourceException("Username already used");
            }
            user.setUsername(updatedUserData.username());
        }

        if (updatedUserData.email() != null && !updatedUserData.email().equals(user.getEmail())) {
            if (userRepository.existsByEmail(updatedUserData.email())) {
                throw new DuplicateResourceException("Email already used");
            }
            user.setEmail(updatedUserData.email());
        }

        if (updatedUserData.firstName() != null) user.setFirstName(updatedUserData.firstName());
        if (updatedUserData.lastName() != null) user.setLastName(updatedUserData.lastName());
        if (updatedUserData.role() != null) user.setRole(updatedUserData.role());
        if (updatedUserData.dateOfBirth() != null) user.setDateOfBirth(updatedUserData.dateOfBirth());

        return toResponse(userRepository.save(user));
    }

    public UserResponse findById(Long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) return null;
        return toResponse(user.get());
    }

    public List<UserResponse> findAll() {
        return userRepository.findAll()
                .stream()
                .map(UserService::toResponse)
                .toList();
    }

    public List<AddressResponse> getAddressesForUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("User with id " + userId + " not found");
        }
        return addressRepository.findByUserUserId(userId)
                .stream()
                .map(UserService::toResponseAddress)
                .toList();
    }

    public AddressResponse addAddress(Long userId, CreateAddressRequest dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Address a = fromCreateAddressDto(dto, user);

        Address saved = addressRepository.save(a);

        return toResponseAddress(saved);
    }
}
