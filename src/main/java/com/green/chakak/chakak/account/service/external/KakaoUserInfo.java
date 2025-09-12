package com.green.chakak.chakak.account.service.external;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.context.annotation.Profile;

/**
 * 이 DTO는 카카오 AccessToken으로 사용자 정보를 요청했을 때 받는
 * JSON 응답을 매핑하는 클래스
 * 역활 : 카카오 사용자 정보 조회 응답(JSON)을 매핑하는 DTO
 * 범위 : 카카오 API 호출할 때만 사용 (DB에 직접 저장하지 않음)
 * 유저 정보 받기 용 DTO(외부용 DTO) --> 카카오 API 전용
 */
@Data
public class KakaoUserInfo {

    private Long id; // 카카오에서 발급하는 유저 고유 ID(이메일 없을 때 식별자 사용)

    @JsonProperty("kakao_account")
    private KakaoAccount kakaoAccount;

    @Data
    public static class KakaoAccount {
        private String email; // 사용자 이메일 (동의하지 않으면 null이 올 수 있음)
       // private Profile profile;

//        @Data
//        public static class Profile {
//          //  private String nickname; // 유저 닉네임
//
//            @JsonProperty("profile_image_url")
//            private String profileImageUrl; // 프로필 이미지
//
//            @JsonProperty("thumbnail_image_url")
//            private String thumbnailImageUrl; // 썸네일 이미지 URL
//        }
    }
}
