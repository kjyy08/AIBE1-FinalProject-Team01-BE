package kr.co.amateurs.server.domain.dto.verify;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VerifyResultDTO {
    private int ocrScore;
    private int layoutScore;
    private int totalScore;
    private String extractedText;
    private String detailMessage;
    private boolean isVerified;
} 