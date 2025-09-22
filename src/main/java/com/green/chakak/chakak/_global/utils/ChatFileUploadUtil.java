package com.green.chakak.chakak._global.utils;

import com.green.chakak.chakak._global.errors.exception.Exception400;
import com.green.chakak.chakak._global.errors.exception.Exception500;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.UUID;

@Component
@Slf4j
public class ChatFileUploadUtil {

    @Value("${file.upload.chat-path:./uploads/chat}")
    private String chatUploadPath;

    @Value("${file.upload.base-url:http://10.0.2.2:8080}")  // http:// 추가
    private String baseUrl;

    /**
     * 채팅용 Base64 이미지를 파일로 저장하고 URL 반환
     */
    public String saveChatImage(String base64Data, String originalFileName) {
        try {
            log.info("=== 채팅 이미지 저장 시작 ===");
            log.info("originalFileName: {}", originalFileName);
            log.info("base64Data 길이: {}", base64Data != null ? base64Data.length() : "null");

            // Base64 데이터 검증 및 파싱
            String[] parts = parseBase64Data(base64Data);
            String mimeType = parts[0];
            String base64Content = parts[1];

            log.info("파싱된 MIME 타입: {}", mimeType);

            // gif 파일 제외 검증
            if ("image/gif".equalsIgnoreCase(mimeType)) {
                throw new Exception400("GIF 파일은 채팅에서 지원하지 않습니다.");
            }

            // 파일 확장자 결정
            String extension = getExtensionFromMimeType(mimeType);
            log.info("결정된 확장자: {}", extension);

            // 고유한 파일명 생성 (채팅용)
            String fileName = generateChatFileName(originalFileName, extension);
            log.info("생성된 파일명: {}", fileName);

            // 채팅 전용 디렉토리 경로 생성
            String datePath = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            Path directoryPath = Paths.get(chatUploadPath, datePath);
            log.info("디렉토리 경로: {}", directoryPath.toAbsolutePath());

            createDirectoryIfNotExists(directoryPath);

            // 파일 경로 생성
            Path filePath = directoryPath.resolve(fileName);
            log.info("최종 파일 경로: {}", filePath.toAbsolutePath());

            // Base64 디코딩 및 파일 저장
            byte[] decodedBytes = Base64.getDecoder().decode(base64Content);
            log.info("디코딩된 바이트 크기: {} bytes", decodedBytes.length);

            saveFile(filePath, decodedBytes);

            // 채팅 이미지 URL 생성 및 반환
            String fileUrl = String.format("%s/uploads/chat/%s/%s",
                    baseUrl, datePath, fileName);

            log.info("=== 채팅 이미지 저장 완료: {} ===", fileUrl);
            return fileUrl;

        } catch (Exception e) {
            log.error("=== 채팅 이미지 저장 실패 ===");
            log.error("에러 메시지: {}", e.getMessage());
            log.error("에러 스택:", e);
            throw new Exception500("채팅 이미지 저장 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * Base64 데이터 파싱 (기존 FileUploadUtil과 동일)
     */
    private String[] parseBase64Data(String base64Data) {
        if (base64Data == null || base64Data.trim().isEmpty()) {
            throw new Exception400("Base64 데이터가 비어있습니다");
        }

        log.info("받은 Base64 데이터 시작 200자: {}",
                base64Data.length() > 200 ? base64Data.substring(0, 200) + "..." : base64Data);

        String mimeType = "image/jpeg"; // 기본값
        String base64Content;

        if (base64Data.startsWith("data:")) {
            int commaIndex = base64Data.indexOf(",");
            if (commaIndex == -1) {
                throw new Exception400("Base64 데이터에서 콤마(,)를 찾을 수 없습니다. 예: data:image/jpeg;base64,내용");
            }

            String header = base64Data.substring(0, commaIndex);
            base64Content = base64Data.substring(commaIndex + 1);

            log.info("헤더 부분: {}", header);

            // MIME 타입 추출
            if (header.contains(":") && header.contains(";")) {
                String[] headerParts = header.split(":");
                if (headerParts.length > 1) {
                    String typeAndEncoding = headerParts[1];
                    String[] typeParts = typeAndEncoding.split(";");
                    if (typeParts.length > 0) {
                        mimeType = typeParts[0];
                    }
                }
            }
        } else {
            log.info("data: 접두사 없는 순수 Base64 데이터로 처리");
            base64Content = base64Data;
        }

        // Base64 데이터 정리
        base64Content = base64Content.replaceAll("\\s+", "");

        // Base64 데이터 유효성 검증
        try {
            while (base64Content.length() % 4 != 0) {
                base64Content += "=";
            }
            Base64.getDecoder().decode(base64Content);
            log.info("Base64 디코딩 테스트 성공");
        } catch (IllegalArgumentException e) {
            log.error("Base64 디코딩 실패: {}", e.getMessage());
            throw new Exception400("유효하지 않은 Base64 데이터입니다: " + e.getMessage());
        }

        return new String[]{mimeType, base64Content};
    }

    /**
     * MIME 타입에서 파일 확장자 추출 (gif 제외)
     */
    private String getExtensionFromMimeType(String mimeType) {
        switch (mimeType.toLowerCase()) {
            case "image/jpeg":
            case "image/jpg":
                return "jpg";
            case "image/png":
                return "png";
            case "image/webp":
                return "webp";
            default:
                throw new Exception400("지원하지 않는 이미지 형식입니다: " + mimeType + " (jpg, png, webp만 지원)");
        }
    }

    /**
     * 채팅용 고유한 파일명 생성
     */
    private String generateChatFileName(String originalFileName, String extension) {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

        if (originalFileName != null && !originalFileName.trim().isEmpty()) {
            // 원본 파일명에서 확장자 제거
            String nameWithoutExt = originalFileName.replaceAll("\\.[^.]*$", "");
            return String.format("chat_%s_%s_%s.%s", nameWithoutExt, timestamp, uuid.substring(0, 8), extension);
        } else {
            return String.format("chat_image_%s_%s.%s", timestamp, uuid.substring(0, 8), extension);
        }
    }

    /**
     * 디렉토리 생성
     */
    private void createDirectoryIfNotExists(Path directoryPath) throws IOException {
        if (!Files.exists(directoryPath)) {
            Files.createDirectories(directoryPath);
            log.info("디렉토리 생성: {}", directoryPath);
        }
    }

    /**
     * 파일 저장
     */
    private void saveFile(Path filePath, byte[] data) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(filePath.toFile())) {
            fos.write(data);
            fos.flush();
        }
    }
}