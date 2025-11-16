package pl.petgo.backend.utils;

import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class CollectionUtil {

    private CollectionUtil (){
    }

    public static <T> Collector<T, ?, Optional<T>> zeroOrOne() {
        return Collectors.collectingAndThen(
                Collectors.toList(),
                list -> list.size() == 1
                        ? Optional.of(list.get(0))
                        : Optional.empty()
        );
    }
}
