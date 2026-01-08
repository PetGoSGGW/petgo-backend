package pl.petgo.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import pl.petgo.backend.dto.report.UserReportRequest;
import pl.petgo.backend.dto.report.UserReportResponse;
import pl.petgo.backend.security.AppUserDetails;
import pl.petgo.backend.service.UserReportService;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@Tag(name = "User Reports Module", description = "Endpoints for reporting inappropriate user behavior")
public class UserReportController {

    private final UserReportService userReportService;

    @Operation(
            summary = "Report a user",
            description = "Allows an authenticated user to report another user for inappropriate behavior. Reporting yourself is not allowed."
    )
    @PostMapping
    public ResponseEntity<UserReportResponse> createReport(
            @Valid @RequestBody UserReportRequest request,
            @AuthenticationPrincipal AppUserDetails userDetails
    ) {
        UserReportResponse response = userReportService.createUserReport(request, userDetails);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}