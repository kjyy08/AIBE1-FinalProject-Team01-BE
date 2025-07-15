package kr.co.amateurs.server.service.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${oauth.success-redirect-url}")
    private String frontendUrl;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendPasswordResetEmail(String to, String resetToken) {
        try {
            String resetUrl = frontendUrl + "/reset-password?token=" + resetToken;

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject("[Amateurs] 비밀번호 재설정 요청");
            message.setText(buildPasswordResetEmailContent(resetUrl));

            mailSender.send(message);

            log.info("비밀번호 재설정 이메일 전송 완료: {}", maskEmail(to));

        } catch (Exception e) {
            log.error("비밀번호 재설정 이메일 전송 실패: {}, 오류: {}", maskEmail(to), e.getMessage());
            throw new RuntimeException("이메일 전송에 실패했습니다.", e);
        }
    }

    private String buildPasswordResetEmailContent(String resetUrl) {
        return String.format("""
            안녕하세요, Amateurs입니다.
            
            비밀번호 재설정을 요청하셨습니다.
            아래 링크를 클릭하여 새로운 비밀번호를 설정해 주세요.
            
            비밀번호 재설정 링크:
            %s
            
            ⚠️ 주의사항:
            - 이 링크는 30분 후에 만료됩니다.
            - 본인이 요청하지 않았다면 이 메일을 무시해 주세요.
            - 보안을 위해 링크를 다른 사람과 공유하지 마세요.
            
            감사합니다.
            Amateurs 팀
            """, resetUrl);
    }

    private String maskEmail(String email) {
        if (email == null || !email.contains("@")) {
            return "***";
        }
        String[] parts = email.split("@");
        String localPart = parts[0];
        String domain = parts[1];

        if (localPart.length() <= 3) {
            return localPart.charAt(0) + "***@" + domain;
        } else {
            return localPart.substring(0, 3) + "***@" + domain;
        }
    }
}