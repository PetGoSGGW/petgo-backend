package pl.petgo.backend.dto.review;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import pl.petgo.backend.domain.ReviewType;

public record CreateReviewRequest(
        @NotNull
        Long reservationId,

        @NotNull
        ReviewType reviewType,

        @NotNull
        @Min(1)
        @Max(5)
        Integer rating,

        @Size(max = 1000)
        String comment
) {}
