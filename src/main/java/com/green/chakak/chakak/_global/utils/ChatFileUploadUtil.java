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

    @Value("${file.upload.base-url:http://10.0.2.2:8080}")
    private String baseUrl;

    public String saveChatImage(String base64Data, String originalFileName) {
        log.info("--- 1. ChatFileUploadUtil: saveChatImage 시작 ---");
        try {
            log.info("원본 파일명: {}, Base64 데이터 길이: {}", originalFileName, base64Data != null ? base64Data.length() : "null");

            String[] parts = parseBase64Data(base64Data);
            String mimeType = parts[0];
            String base64Content = parts[1];
            log.info("MIME 타입 파싱 완료: {}", mimeType);

            if ("image/gif".equalsIgnoreCase(mimeType)) {
                throw new Exception400("GIF 파일은 채팅에서 지원하지 않습니다.");
            }

            String extension = getExtensionFromMimeType(mimeType);
            String fileName = generateChatFileName(originalFileName, extension);
            log.info("고유 파일명 생성 완료: {}", fileName);

            String datePath = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            Path directoryPath = Paths.get(chatUploadPath, datePath);
            log.info("파일 저장 디렉토리 경로: {}", directoryPath.toAbsolutePath());

            createDirectoryIfNotExists(directoryPath);

            Path filePath = directoryPath.resolve(fileName);
            log.info("최종 파일 절대 경로: {}", filePath.toAbsolutePath());

            byte[] decodedBytes = Base64.getDecoder().decode(base64Content);
            log.info("Base64 디코딩 완료. 바이트 크기: {} bytes", decodedBytes.length);

            saveFile(filePath, decodedBytes);
            log.info("파일 시스템에 저장 완료.");

            String fileUrl = String.format("%s/uploads/chat/%s/%s", baseUrl, datePath, fileName);
            log.info("--- 2. ChatFileUploadUtil: 이미지 URL 생성 완료: {} ---", fileUrl);
            return fileUrl;

        } catch (Exception e) {
            log.error("!!! ChatFileUploadUtil: 이미지 저장 중 심각한 오류 발생 !!!", e);
            throw new Exception500("채팅 이미지 저장 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    private String[] parseBase64Data(String base64Data) {
        if (base64Data == null || base64Data.trim().isEmpty()) {
            throw new Exception400("Base64 데이터가 비어있습니다");
        }
        String mimeType = "image/jpeg";
        String base64Content;
        if (base64Data.startsWith("data:")) {
            int commaIndex = base64Data.indexOf(",");
            if (commaIndex == -1) throw new Exception400("잘못된 형식의 Base64 데이터입니다.");
            String header = base64Data.substring(0, commaIndex);
            base64Content = base64Data.substring(commaIndex + 1);
            if (header.contains(":") && header.contains(";")) {
                mimeType = header.split(":")[1].split(";")[0];
            }
        } else {
            base64Content = base64Data;
        }
        return new String[]{mimeType, base64Content.replaceAll("\\s+", "")};
    }

    private String getExtensionFromMimeType(String mimeType) {
        switch (mimeType.toLowerCase()) {
            case "image/jpeg": case "image/jpg": return "jpg";
            case "image/png": return "png";
            case "image/webp": return "webp";
            default: throw new Exception400("지원하지 않는 이미지 형식입니다: " + mimeType);
        }
    }

    private String generateChatFileName(String originalFileName, String extension) {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        if (originalFileName != null && !originalFileName.trim().isEmpty()) {
            String nameWithoutExt = originalFileName.replaceAll("\\.[^.]*$", "");
            return String.format("chat_%s_%s_%s.%s", nameWithoutExt, timestamp, uuid.substring(0, 8), extension);
        }
        return String.format("chat_image_%s_%s.%s", timestamp, uuid.substring(0, 8), extension);
    }

    private void createDirectoryIfNotExists(Path directoryPath) throws IOException {
        if (!Files.exists(directoryPath)) {
            Files.createDirectories(directoryPath);
            log.info("새 디렉토리 생성: {}", directoryPath);
        }
    }

    private void saveFile(Path filePath, byte[] data) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(filePath.toFile())) {
            fos.write(data);
            fos.flush();
        }
    }
}
