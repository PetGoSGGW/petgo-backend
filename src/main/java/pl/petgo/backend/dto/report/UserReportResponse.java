package pl.petgo.backend.dto.report;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.petgo.backend.domain.ReportStatus;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserReportResponse {
    private Long reportId;
    private Long reportedUserId;
    private ReportStatus status;
    private Instant createdAt;
}