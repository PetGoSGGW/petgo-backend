package pl.petgo.backend.mapper;

import org.mapstruct.*;
import pl.petgo.backend.domain.Breed;
import pl.petgo.backend.domain.Dog;
import pl.petgo.backend.domain.DogPhoto;
import pl.petgo.backend.dto.dog.*;

import static org.mapstruct.InjectionStrategy.CONSTRUCTOR;
import static org.mapstruct.MappingConstants.ComponentModel.SPRING;
import static org.mapstruct.ReportingPolicy.IGNORE;

@Mapper(componentModel = SPRING, unmappedTargetPolicy = IGNORE, injectionStrategy = CONSTRUCTOR)
public interface DogMapper {
    @Mapping(target = "ownerId", source = "dog.owner.userId")
    @Mapping(target = "breed", source = "dog.breed")
    @Mapping(target = "photos", source = "dog.photos")
    DogDto toDto(Dog dog);

    BreedDto toBreedDto(Breed breed);

    DogPhotoDto toPhotoDto(DogPhoto photo);

    @Mapping(target = "dogId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "photos", ignore = true)
    @Mapping(target = "isActive", constant = "true")
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "breed", ignore = true)
    Dog toEntity(DogCreateRequestDto request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "dogId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "photos", ignore = true)
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "breed", ignore = true)
    void updateEntityFromDto(DogUpdateRequestDto request, @MappingTarget Dog entity);
}
