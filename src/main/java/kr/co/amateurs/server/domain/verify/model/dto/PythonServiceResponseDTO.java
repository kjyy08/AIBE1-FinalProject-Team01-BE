package kr.co.amateurs.server.domain.verify.model.dto;

public record PythonServiceResponseDTO(
    boolean success,
    String error,
    DataDTO data
) {
    public static record DataDTO(
        boolean isValid,
        String extractedText,
        String message,
        String detailMessage,
        int ocrScore,
        int layoutScore,
        int totalScore
    ) {}
} 