package pl.petgo.backend.dto.dog;

import pl.petgo.backend.domain.Dog;
import pl.petgo.backend.domain.User;
import pl.petgo.backend.dto.user.BasicUserInfoDto;

public record BasicDogInfoDto(Long dogId,
                              String name,
                              BreedDto breed,
                              BasicUserInfoDto ownerInfoDto) {


    public static BasicDogInfoDto from(Dog dog) {
        BreedDto breedDto = new BreedDto(dog.getBreed().getBreedCode(), dog.getBreed().getName());

        User owner = dog.getOwner();
        BasicUserInfoDto ownerInfoDto = BasicUserInfoDto.from(owner);

        return new BasicDogInfoDto(
                dog.getDogId(),
                dog.getName(),
                breedDto,
                ownerInfoDto);
    }
}
