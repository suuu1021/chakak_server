-- 유저 타입 더미 (예시)
INSERT INTO user_type (TYPE_CODE, TYPE_NAME, CREATED_AT, UPDATED_AT) VALUES ('user', '일반유저', '2025-09-05', '2025-09-05');
INSERT INTO user_type (TYPE_CODE, TYPE_NAME, CREATED_AT, UPDATED_AT) VALUES ('photographer', '포토그래퍼', '2025-09-05', '2025-09-05');

-- 유저 더미 20명 (10명 일반, 10명 포토그래퍼)
INSERT INTO user_account (user_id, email, password, user_type_id, status, created_at, updated_at) VALUES
(1, 'user1@example.com', 'pw1', 1, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 'user2@example.com', 'pw2', 1, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 'user3@example.com', 'pw3', 1, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(4, 'user4@example.com', 'pw4', 1, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(5, 'user5@example.com', 'pw5', 1, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(6, 'user6@example.com', 'pw6', 1, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(7, 'user7@example.com', 'pw7', 1, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(8, 'user8@example.com', 'pw8', 1, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(9, 'user9@example.com', 'pw9', 1, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(10, 'user10@example.com', 'pw10', 1, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(11, 'photo1@example.com', 'pw11', 2, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(12, 'photo2@example.com', 'pw12', 2, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(13, 'photo3@example.com', 'pw13', 2, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(14, 'photo4@example.com', 'pw14', 2, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(15, 'photo5@example.com', 'pw15', 2, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(16, 'photo6@example.com', 'pw16', 2, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(17, 'photo7@example.com', 'pw17', 2, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(18, 'photo8@example.com', 'pw18', 2, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(19, 'photo9@example.com', 'pw19', 2, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(20, 'photo10@example.com', 'pw20', 2, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 유저 프로필 더미 (각 유저 1:1)
INSERT INTO user_profile (user_profile_id, user_id, nick_name, introduce, image_data, created_at, updated_at) VALUES
(1, 1, '유저1', '', '', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 2, '유저2', '', '', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 3, '유저3', '', '', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(4, 4, '유저4', '', '', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(5, 5, '유저5', '', '', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(6, 6, '유저6', '', '', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(7, 7, '유저7', '', '', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(8, 8, '유저8', '', '', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(9, 9, '유저9', '', '', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(10, 10, '유저10', '', '', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(11, 11, '포토1', '', '', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(12, 12, '포토2', '', '', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(13, 13, '포토3', '', '', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(14, 14, '포토4', '', '', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(15, 15, '포토5', '', '', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(16, 16, '포토6', '', '', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(17, 17, '포토7', '', '', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(18, 18, '포토8', '', '', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(19, 19, '포토9', '', '', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(20, 20, '포토10', '', '', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 포토그래퍼 프로필 더미 (포토그래퍼만)
INSERT INTO photographer_profile (photographer_profile_id, user_id, business_name, introduction, location, experience_years, status, created_at, update_at) VALUES
(1, 11, '포토1상호', '소개1', '서울', 5, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 12, '포토2상호', '소개2', '부산', 3, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 13, '포토3상호', '소개3', '대구', 7, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(4, 14, '포토4상호', '소개4', '인천', 2, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(5, 15, '포토5상호', '소개5', '광주', 4, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(6, 16, '포토6상호', '소개6', '대전', 6, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(7, 17, '포토7상호', '소개7', '울산', 1, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(8, 18, '포토8상호', '소개8', '경기', 8, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(9, 19, '포토9상호', '소개9', '강원', 2, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(10, 20, '포토10상호', '소개10', '제주', 10, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 예약 카테고리, 서비스 더미 (간단 예시)
-- 컬럼명은 엔티티에 맞게 category_id, category_name 사용
INSERT INTO photographer_category (category_id, category_name) VALUES (1, '웨딩');
-- photo_service_info 스키마에 맞게 컬럼명으로 변경
INSERT INTO photo_service_info (photo_service_info_id, photographer_profile_id, title, description, image_data, created_at, updated_at) VALUES (1, 1, '스냅촬영', '기본 스냅 서비스', '', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 예약 더미 (user 1~10이 포토그래퍼 11~20에게 예약)
INSERT INTO booking_info (booking_info_id, user_profile_id, photographer_profile_id, photographer_category_id, photo_service_info_id, booking_date, booking_time, location, budget, special_request, status, participant_count, shooting_duration, created_at, updated_at) VALUES
(1, 1, 1, 1, 1, '2025-09-10', '10:00:00', '서울', 100000, '', 'REQUESTED', 2, 60, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 2, 2, 1, 1, '2025-09-11', '11:00:00', '부산', 120000, '', 'REQUESTED', 3, 90, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 3, 3, 1, 1, '2025-09-12', '12:00:00', '대구', 90000, '', 'REQUESTED', 1, 30, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(4, 4, 4, 1, 1, '2025-09-13', '13:00:00', '인천', 110000, '', 'REQUESTED', 4, 120, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(5, 5, 5, 1, 1, '2025-09-14', '14:00:00', '광주', 130000, '', 'REQUESTED', 2, 60, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(6, 6, 6, 1, 1, '2025-09-15', '15:00:00', '대전', 80000, '', 'REQUESTED', 2, 45, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(7, 7, 7, 1, 1, '2025-09-16', '16:00:00', '울산', 95000, '', 'REQUESTED', 3, 90, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(8, 8, 8, 1, 1, '2025-09-17', '17:00:00', '경기', 105000, '', 'REQUESTED', 2, 60, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(9, 9, 9, 1, 1, '2025-09-18', '18:00:00', '강원', 99000, '', 'REQUESTED', 1, 30, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(10, 10, 10, 1, 1, '2025-09-19', '19:00:00', '제주', 115000, '', 'REQUESTED', 2, 60, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 예약 취소 더미 (user 1,2,3이 예약 취소)
INSERT INTO booking_cancel_info (cancellation_id, booking_info_id, user_profile_id, cancel_reason_text, penalty_amount, refund_amount, created_at) VALUES
(1, 1, 1, '일정 변경', 10000, 90000, CURRENT_TIMESTAMP),
(2, 2, 2, '개인 사정', 5000, 115000, CURRENT_TIMESTAMP),
(3, 3, 3, '기타', 0, 90000, CURRENT_TIMESTAMP);
