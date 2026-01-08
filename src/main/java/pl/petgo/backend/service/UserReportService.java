package pl.petgo.backend.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.petgo.backend.domain.User;
import pl.petgo.backend.domain.UserReport;
import pl.petgo.backend.domain.ReportStatus;
import pl.petgo.backend.dto.report.UserReportRequest;
import pl.petgo.backend.dto.report.UserReportResponse;
import pl.petgo.backend.repository.UserReportRepository;
import pl.petgo.backend.repository.UserRepository;
import pl.petgo.backend.security.AppUserDetails;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserReportService {

    private final UserReportRepository userReportRepository;
    private final UserRepository userRepository;

    @Transactional
    public UserReportResponse createUserReport(UserReportRequest request, AppUserDetails reporterDetails) {
        User reporter = reporterDetails.getUser();

        if (reporter.getUserId().equals(request.getReportedUserId())) {
            throw new IllegalArgumentException("You cannot report yourself.");
        }

        User reportedUser = userRepository.findById(request.getReportedUserId())
                .orElseThrow(() -> new EntityNotFoundException("User with ID " + request.getReportedUserId() + " not found"));

        UserReport report = UserReport.builder()
                .reporter(reporter)
                .reported(reportedUser)
                .reason(request.getReason())
                .details(request.getDetails())
                .status(ReportStatus.PENDING)
                .build();

        UserReport savedReport = userReportRepository.save(report);

        log.info("User {} reported User {} for reason: {}", reporter.getUserId(), reportedUser.getUserId(), request.getReason());

        return UserReportResponse.builder()
                .reportId(savedReport.getReportId())
                .reportedUserId(savedReport.getReported().getUserId())
                .status(savedReport.getStatus())
                .createdAt(savedReport.getCreatedAt())
                .build();
    }
}