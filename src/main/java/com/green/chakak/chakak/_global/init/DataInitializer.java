package com.green.chakak.chakak._global.init;

import com.green.chakak.chakak.account.domain.User;
import com.green.chakak.chakak.account.domain.UserProfile;
import com.green.chakak.chakak.account.domain.UserType;
import com.green.chakak.chakak.account.service.repository.UserJpaRepository;
import com.green.chakak.chakak.account.service.repository.UserProfileJpaRepository;
import com.green.chakak.chakak.account.service.repository.UserTypeRepository;
import com.green.chakak.chakak.admin.domain.Admin;
import com.green.chakak.chakak.admin.service.repository.AdminJpaRepository;
import com.green.chakak.chakak.booking.domain.BookingInfo;
import com.green.chakak.chakak.booking.domain.BookingStatus;
import com.green.chakak.chakak.booking.service.repository.BookingInfoJpaRepository;
import com.green.chakak.chakak.photo.domain.PhotoServiceInfo;
import com.green.chakak.chakak.photo.domain.PhotoServiceReview;
import com.green.chakak.chakak.photo.domain.PhotoServiceCategory;
import com.green.chakak.chakak.photo.domain.PhotoServiceMapping;
import com.green.chakak.chakak.photo.domain.PriceInfo;
import com.green.chakak.chakak.photo.service.repository.PhotoCategoryJpaRepository;
import com.green.chakak.chakak.photo.service.repository.PhotoMappingRepository;
import com.green.chakak.chakak.photo.service.repository.PhotoServiceJpaRepository;
import com.green.chakak.chakak.photo.service.repository.PhotoServiceReviewJpaRepository;
import com.green.chakak.chakak.photo.service.repository.PriceInfoJpaRepository;
import com.green.chakak.chakak.photographer.domain.PhotographerProfile;
import com.green.chakak.chakak.photographer.domain.PhotographerCategory;
import com.green.chakak.chakak.photographer.service.repository.PhotographerCategoryRepository;
import com.green.chakak.chakak.photographer.service.repository.PhotographerRepository;
import com.green.chakak.chakak.portfolios.domain.Portfolio;
import com.green.chakak.chakak.portfolios.domain.PortfolioCategory;
import com.green.chakak.chakak.portfolios.domain.PortfolioImage;
import com.green.chakak.chakak.portfolios.domain.PortfolioMap;
import com.green.chakak.chakak.portfolios.service.repository.PortfolioCategoryJpaRepository;
import com.green.chakak.chakak.portfolios.service.repository.PortfolioImageJpaRepository;
import com.green.chakak.chakak.portfolios.service.repository.PortfolioJpaRepository;
import com.green.chakak.chakak.portfolios.service.repository.PortfolioMapJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import com.green.chakak.chakak.community.domain.Post;
import com.green.chakak.chakak.community.domain.Reply;
import com.green.chakak.chakak.community.domain.Like;
import com.green.chakak.chakak.community.repository.PostJpaRepository;
import com.green.chakak.chakak.community.repository.ReplyJpaRepository;
import com.green.chakak.chakak.community.repository.LikeJpaRepository;


import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

	private final UserTypeRepository userTypeRepository;
	private final UserJpaRepository userJpaRepository;
	private final UserProfileJpaRepository userProfileJpaRepository;
	private final PhotographerRepository photographerRepository;
	private final PhotographerCategoryRepository photographerCategoryRepository;
	private final AdminJpaRepository adminJpaRepository;
	private final PhotoServiceJpaRepository photoServiceJpaRepository;
	private final PhotoCategoryJpaRepository photoServiceCategoryRepository;
	private final PortfolioCategoryJpaRepository portfolioCategoryRepository;
	private final PortfolioJpaRepository portfolioRepository;
	private final PortfolioImageJpaRepository portfolioImageRepository;
	private final PortfolioMapJpaRepository portfolioMapRepository;
	private final PhotoMappingRepository photoMappingRepository;
	private final PriceInfoJpaRepository priceInfoJpaRepository;
	private final BookingInfoJpaRepository bookingInfoJpaRepository;
	private final PostJpaRepository postJpaRepository;
	private final ReplyJpaRepository replyJpaRepository;
	private final LikeJpaRepository likeJpaRepository;

    private final PhotoServiceReviewJpaRepository photoServiceReviewJpaRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {

        System.out.println("=== DataInitializer 실행 시작 ===");

		// 데이터가 이미 존재하는지 확인
		long existingUsers = userJpaRepository.count();
		long existingBookings = bookingInfoJpaRepository.count();
        long existingReviews = photoServiceReviewJpaRepository.count();

		System.out.println("=== 데이터 초기화 상태 확인 ===");
		System.out.println("기존 유저 수: " + existingUsers);
		System.out.println("기존 예약 수: " + existingBookings);
        System.out.println("기존 리뷰 수: " + existingReviews);

        // UserType 생성 (항상 먼저 실행)
        UserType userRole = createUserTypeIfNotExists("user", "일반회원");
        UserType photographerRole = createUserTypeIfNotExists("photographer", "사진작가");
        UserType adminRole = createUserTypeIfNotExists("admin", "관리자");

        // 포토그래퍼 카테고리 생성 (항상 실행)
        createPhotographerCategories();

        // 포토서비스 카테고리 생성 (항상 실행)
        createPhotoServiceCategories();

        // 포트폴리오 카테고리 생성 (항상 실행)
        createPortfolioCategories();

        // 기본 유저 데이터가 없으면 생성
        if (existingUsers == 0) {
            System.out.println("=== 기본 유저 데이터 생성 시작 ===");

            // 1. 일반 유저 및 프로필 생성
            createGeneralUsers(userRole);

            // 2. 사진작가 유저 및 프로필 생성
            createPhotographerUsers(photographerRole);

            // 3. 관리자 생성
            createAdmin(adminRole);

            System.out.println("=== 기본 유저 데이터 생성 완료 ===");
        }

        // 포토 서비스 데이터 확인 및 생성
        long existingPhotoServices = photoServiceJpaRepository.count();
        if (existingPhotoServices == 0) {
            System.out.println("=== 포토 서비스 데이터 생성 시작 ===");

            // 4. 포토 서비스 정보 생성 (PriceInfo 포함)
            createPhotoServices();

            // 5. 포트폴리오 생성
            createPortfolios();

            System.out.println("=== 포토 서비스 데이터 생성 완료 ===");
        }

        // 예약 데이터 생성 (의존성 체크 후)
        if (existingBookings == 0) {
            System.out.println("=== 예약 데이터 생성 전 의존성 체크 ===");

            long userCount = userProfileJpaRepository.count();
            long photographerCount = photographerRepository.count();
            long serviceCount = photoServiceJpaRepository.count();
            long priceCount = priceInfoJpaRepository.count();

            System.out.println("UserProfile 수: " + userCount);
            System.out.println("PhotographerProfile 수: " + photographerCount);
            System.out.println("PhotoServiceInfo 수: " + serviceCount);
            System.out.println("PriceInfo 수: " + priceCount);

            if (userCount > 0 && photographerCount > 0 && serviceCount > 0 && priceCount > 0) {
                System.out.println("=== 의존성 확인 완료. 예약 데이터 생성 시작 ===");
                createBookingData();
            } else {
                System.err.println("예약 데이터 생성에 필요한 의존성이 부족합니다!");
                System.err.println("필요한 데이터: UserProfile, PhotographerProfile, PhotoServiceInfo, PriceInfo");
            }
        } else {
            System.out.println("예약 데이터가 이미 존재합니다 (총 " + existingBookings + "개).");

            // 사용자 ID 1의 예약 데이터 확인
            List<BookingInfo> userBookings = bookingInfoJpaRepository.findByUserId(1L);
            System.out.println("User ID 1의 예약 수: " + userBookings.size());
            if (!userBookings.isEmpty()) {
                System.out.println("첫 번째 예약: " + userBookings.get(0).getBookingInfoId() +
                        ", 상태: " + userBookings.get(0).getStatus());
            }
        }

// 커뮤니티 데이터 확인 및 생성
		long existingPosts = postJpaRepository.count();
		if (existingPosts == 0) {
			System.out.println("=== 커뮤니티 데이터 생성 시작 ===");
			createCommunityData();
			System.out.println("=== 커뮤니티 데이터 생성 완료 ===");
		}

        // [추가] 리뷰 데이터 생성 (예약 데이터가 있어야 함)
        if (existingReviews == 0 && bookingInfoJpaRepository.count() > 0) {
            System.out.println("=== 리뷰 데이터 생성 시작 ===");
            createReviewData();
            System.out.println("=== 리뷰 데이터 생성 완료 ===");
        } else if (existingReviews > 0) {
            System.out.println("리뷰 데이터가 이미 존재합니다 (총 " + existingReviews + "개).");
        }

		System.out.println("데이터 초기화 완료!");
	}

    private UserType createUserTypeIfNotExists(String typeCode, String typeName) {
        return userTypeRepository.findByTypeCode(typeCode).orElseGet(() -> {
            UserType newUserType = new UserType();
            newUserType.setTypeCode(typeCode);
            newUserType.setTypeName(typeName);
            newUserType.setCreatedAt(LocalDateTime.now());
            newUserType.setUpdatedAt(LocalDateTime.now());
            return userTypeRepository.save(newUserType);
        });
    }

    private void createPhotographerCategories() {
        String[] categoryNames = {"웨딩", "프로필", "가족사진", "커플", "졸업사진", "돌잔치"};

        for (String categoryName : categoryNames) {
            photographerCategoryRepository.findByCategoryName(categoryName).orElseGet(() -> {
                PhotographerCategory category = new PhotographerCategory();
                category.setCategoryName(categoryName);
                return photographerCategoryRepository.save(category);
            });
        }
    }

    private void createPhotoServiceCategories() {
        String[][] categories = {
                {"웨딩", "https://images.unsplash.com/photo-1519741497674-611481863552?w=300&h=300&fit=crop"},
                {"가족사진", "https://images.unsplash.com/photo-1511895426328-dc8714191300?w=300&h=300&fit=crop"},
                {"프로필", "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=300&h=300&fit=crop&crop=face"},
                {"커플", "https://images.unsplash.com/photo-1516589178581-6cd7833ae3b2?w=300&h=300&fit=crop"},
                {"졸업사진", "https://images.unsplash.com/photo-1541339907198-e08756dedf3f?w=300&h=300&fit=crop"},
                {"돌잔치", "https://images.unsplash.com/photo-1515488764276-beab7607c1e6?w=300&h=300&fit=crop"}
        };

        for (String[] category : categories) {
            photoServiceCategoryRepository.findByCategoryName(category[0]).orElseGet(() -> {
                PhotoServiceCategory serviceCategory = new PhotoServiceCategory();
                serviceCategory.setCategoryName(category[0]);
                serviceCategory.setCategoryImageData(category[1]);
                return photoServiceCategoryRepository.save(serviceCategory);
            });
        }
    }

    private void createGeneralUsers(UserType userRole) {
        System.out.println("=== 일반 유저 생성 시작 ===");
        for (int i = 1; i <= 10; i++) {
            // User 생성 (평문 비밀번호)
            User newUser = User.builder()
                    .email(String.format("user%d@example.com", i))
                    .password("123456")
                    .userType(userRole)
                    .status(User.UserStatus.ACTIVE)
                    .emailVerified(true)
                    .build();
            User savedUser = userJpaRepository.save(newUser);
            System.out.println("User 저장 완료 - ID: " + savedUser.getUserId() + ", Email: " + savedUser.getEmail());

            // UserProfile 생성
            UserProfile userProfile = UserProfile.builder()
                    .user(savedUser)
                    .nickName(String.format("일반유저%d", i))
                    .introduce(String.format("안녕하세요, 유저 %d입니다.", i))
                    .imageData(String.format("https://picsum.photos/200/200?random=%d", i + 10))
                    .createdAt(Timestamp.from(Instant.now()))
                    .updatedAt(Timestamp.from(Instant.now()))
                    .build();
            UserProfile savedProfile = userProfileJpaRepository.save(userProfile);
            System.out.println("UserProfile 저장 완료 - ID: " + savedProfile.getUserProfileId() +
                    ", User ID: " + savedProfile.getUser().getUserId() +
                    ", NickName: " + savedProfile.getNickName());
        }
        System.out.println("=== 일반 유저 생성 완료 ===");
    }

    private void createPhotographerUsers(UserType photographerRole) {
        for (int i = 1; i <= 10; i++) {
            // User 생성 (평문 비밀번호)
            User newPhotographerUser = User.builder()
                    .email(String.format("photo%d@example.com", i))
                    .password("123456")
                    .userType(photographerRole)
                    .status(User.UserStatus.ACTIVE)
                    .emailVerified(true)
                    .build();
            User savedUser = userJpaRepository.save(newPhotographerUser);

            // PhotographerProfile 생성
            PhotographerProfile photographerProfile = PhotographerProfile.builder()
                    .user(savedUser)
                    .businessName(String.format("감성스튜디오%d호점", i))
                    .introduction(String.format("최고의 순간을 담아드립니다. 감성스튜디오 %d입니다.", i))
                    .status("ACTIVE")
                    .location("서울")
                    .experienceYears(i)
                    .profileImageUrl(String.format("https://picsum.photos/300/300?random=%d", i))
                    .build();
            photographerRepository.save(photographerProfile);
        }
    }

    private void createAdmin(UserType adminRole) {
        Admin newAdmin = Admin.builder()
                .adminName("superadmin")
                .password("super1234")
                .userType(adminRole)
                .build();
        adminJpaRepository.save(newAdmin);
    }

    private void createPhotoServices() {
        List<PhotographerProfile> photographers = photographerRepository.findAll();
        List<PhotoServiceCategory> serviceCategories = photoServiceCategoryRepository.findAll();

        String[] serviceTypes = {
                "웨딩촬영", "프로필촬영", "가족사진", "돌잔치", "커플촬영",
                "졸업사진", "브랜딩촬영", "제품촬영", "인물사진", "야외촬영"
        };

        String[] sampleDescriptions = {
                "소중한 순간을 아름답게 담아드립니다. 전문적인 장비와 오랜 경험으로 최고의 결과물을 제공합니다.",
                "자연스럽고 감성적인 사진으로 여러분의 특별한 날을 기록해드립니다.",
                "트렌디하면서도 클래식한 감성을 담은 촬영 서비스를 제공합니다.",
                "고객 맞춤형 컨셉으로 진행되는 프리미엄 촬영 서비스입니다.",
                "따뜻하고 자연스러운 분위기의 촬영으로 소중한 추억을 만들어드립니다."
        };

        String[] sampleImageData = {
                "https://picsum.photos/400/300?random=1",
                "https://picsum.photos/400/300?random=2",
                "https://picsum.photos/400/300?random=3",
                "https://picsum.photos/400/300?random=4",
                "https://picsum.photos/400/300?random=5"
        };

        Random random = new Random();

        for (PhotographerProfile photographer : photographers) {
            int serviceCount = 2 + random.nextInt(2); // 2 또는 3개

            for (int j = 0; j < serviceCount; j++) {
                int typeIndex = random.nextInt(serviceTypes.length);
                int descIndex = random.nextInt(sampleDescriptions.length);
                int imageIndex = random.nextInt(sampleImageData.length);
                int categoryIndex = random.nextInt(serviceCategories.size());

                // PhotoServiceInfo 생성
                PhotoServiceInfo photoService = PhotoServiceInfo.builder()
                        .photographerProfile(photographer)
                        .title(serviceTypes[typeIndex])
                        .description(sampleDescriptions[descIndex])
                        .imageData(sampleImageData[imageIndex])
                        .createdAt(Timestamp.from(Instant.now()))
                        .updatedAt(Timestamp.from(Instant.now()))
                        .build();

                PhotoServiceInfo savedPhotoService = photoServiceJpaRepository.save(photoService);

                // PhotoServiceMapping 생성
                PhotoServiceMapping mapping = PhotoServiceMapping.builder()
                        .photoServiceInfo(savedPhotoService)
                        .photoServiceCategory(serviceCategories.get(categoryIndex))
                        .createdAt(Timestamp.from(Instant.now()))
                        .build();

                photoMappingRepository.save(mapping);

                // PriceInfo 더미데이터 생성
                createPriceInfoForService(savedPhotoService);
            }
        }
    }

    private void createPriceInfoForService(PhotoServiceInfo photoService) {
        String[] priceTitles = {"에센셜", "프리미엄", "시그니처"};
        String[] equipmentOptions = {
                "기본 촬영장비, 자연광 활용",
                "전문 조명장비, 다양한 렌즈",
                "스튜디오 조명, 고급 장비"
        };

        for (int k = 0; k < 3; k++) {
            int basePrice = 200000 + (k * 150000);
            int participants = 2 + k;
            int duration = 90 + (k * 30);
            int outfits = 1 + k;
            boolean makeup = k > 0;

            PriceInfo priceInfo = PriceInfo.builder()
                    .photoServiceInfo(photoService)
                    .title(priceTitles[k])
                    .price(basePrice)
                    .participantCount(participants)
                    .shootingDuration(duration)
                    .outfitChanges(outfits)
                    .specialEquipment(equipmentOptions[k])
                    .isMakeupService(makeup)
                    .createdAt(Timestamp.from(Instant.now()))
                    .updatedAt(Timestamp.from(Instant.now()))
                    .build();

            priceInfoJpaRepository.save(priceInfo);
        }
    }

    private void createPortfolioCategories() {
        String[][] categories = {
                {"웨딩촬영", "1"},
                {"인물촬영", "2"},
                {"가족사진", "3"},
                {"커플촬영", "4"}
        };

        for (String[] category : categories) {
            portfolioCategoryRepository.findByCategoryName(category[0]).orElseGet(() -> {
                PortfolioCategory portfolioCategory = new PortfolioCategory();
                portfolioCategory.setCategoryName(category[0]);
                portfolioCategory.setSortOrder(Integer.parseInt(category[1]));
                portfolioCategory.setIsActive(true);
                portfolioCategory.setCreatedAt(LocalDateTime.now());
                portfolioCategory.setUpdatedAt(LocalDateTime.now());
                return portfolioCategoryRepository.save(portfolioCategory);
            });
        }
    }

    private void createPortfolios() {
        List<PhotographerProfile> photographers = photographerRepository.findAll();
        List<PortfolioCategory> categories = portfolioCategoryRepository.findAll();

        String[] portfolioTitles = {
                "로맨틱 웨딩 컬렉션", "자연스러운 일상 순간들", "따뜻한 가족 이야기",
                "감성적인 커플 스냅", "클래식 포트레이트", "야외 웨딩 촬영",
                "스튜디오 인물 사진", "아이들과의 소중한 시간", "도심 속 커플 촬영",
                "빈티지 웨딩 스타일"
        };

        String[] portfolioDescriptions = {
                "소중한 순간을 영원히 간직할 수 있도록 아름답게 담아냅니다.",
                "자연스럽고 진솔한 모습을 포착하여 특별한 추억을 만들어드립니다.",
                "가족의 사랑과 행복이 가득한 순간들을 따뜻하게 기록합니다.",
                "두 사람만의 특별한 이야기를 감성적으로 표현해드립니다.",
                "개성과 매력을 살린 프로페셔널한 인물 사진을 제공합니다."
        };

        Random random = new Random();

        for (PhotographerProfile photographer : photographers) {
            int portfolioCount = 2 + random.nextInt(3); // 2~4개

            for (int i = 0; i < portfolioCount; i++) {
                int titleIndex = random.nextInt(portfolioTitles.length);
                int descIndex = random.nextInt(portfolioDescriptions.length);
                int categoryIndex = random.nextInt(categories.size());

                // 포트폴리오 생성
                Portfolio portfolio = new Portfolio();
                portfolio.setPhotographerProfile(photographer);
                portfolio.setTitle(portfolioTitles[titleIndex] + " Vol." + (i + 1));
                portfolio.setDescription(portfolioDescriptions[descIndex]);
                portfolio.setThumbnailUrl(String.format("https://picsum.photos/600/400?random=%d",
                        (photographer.getPhotographerProfileId().intValue() * 10) + i + 100));
                portfolio.setCreatedAt(LocalDateTime.now());
                portfolio.setUpdatedAt(LocalDateTime.now());

                Portfolio savedPortfolio = portfolioRepository.save(portfolio);

                // 포트폴리오 이미지들 생성 (3-6개)
                int imageCount = 3 + random.nextInt(4); // 3~6개
                for (int j = 0; j < imageCount; j++) {
                    PortfolioImage portfolioImage = new PortfolioImage();
                    portfolioImage.setPortfolio(savedPortfolio);
                    portfolioImage.setImageUrl(String.format("https://picsum.photos/800/600?random=%d",
                            (photographer.getPhotographerProfileId().intValue() * 100) + (i * 10) + j + 200));
                    portfolioImage.setIsMain(j == 0); // 첫 번째 이미지를 메인으로 설정
                    portfolioImage.setCreatedAt(LocalDateTime.now());
                    portfolioImageRepository.save(portfolioImage);
                }

                // 포트폴리오-카테고리 매핑 생성
                PortfolioMap portfolioMap = new PortfolioMap();
                portfolioMap.setPortfolio(savedPortfolio);
                portfolioMap.setPortfolioCategory(categories.get(categoryIndex));
                portfolioMap.setCreatedAt(LocalDateTime.now());
                portfolioMapRepository.save(portfolioMap);
            }
        }
    }

    private void createBookingData() {
        System.out.println("=== 예약 데이터 생성 시작 ===");

        // 필요한 데이터들을 미리 조회
        List<UserProfile> allUserProfiles = userProfileJpaRepository.findAll();
        List<PhotographerProfile> allPhotographers = photographerRepository.findAll();
        List<PhotoServiceInfo> allPhotoServices = photoServiceJpaRepository.findAll();
        List<PriceInfo> allPriceInfos = priceInfoJpaRepository.findAll();

        System.out.println("=== 기본 데이터 확인 ===");
        System.out.println("전체 UserProfile 수: " + allUserProfiles.size());
        System.out.println("전체 PhotographerProfile 수: " + allPhotographers.size());
        System.out.println("전체 PhotoServiceInfo 수: " + allPhotoServices.size());
        System.out.println("전체 PriceInfo 수: " + allPriceInfos.size());

        // 일반 유저만 필터링
        List<UserProfile> generalUsers = allUserProfiles.stream()
                .filter(profile -> {
                    try {
                        return profile.getUser() != null &&
                                profile.getUser().getUserType() != null &&
                                "user".equals(profile.getUser().getUserType().getTypeCode());
                    } catch (Exception e) {
                        System.err.println("UserProfile 필터링 중 오류: " + e.getMessage());
                        return false;
                    }
                })
                .collect(Collectors.toList());

        System.out.println("=== 필터링 결과 ===");
        System.out.println("일반 유저 수: " + generalUsers.size());

        // 각 유저의 상세 정보 출력
        for (int i = 0; i < Math.min(5, generalUsers.size()); i++) {
            UserProfile user = generalUsers.get(i);
            System.out.println("User " + (i+1) + ": ID=" + user.getUserProfileId() +
                    ", UserID=" + user.getUser().getUserId() +
                    ", Email=" + user.getUser().getEmail() +
                    ", NickName=" + user.getNickName());
        }

        // 필요한 데이터가 모두 있는지 확인
        if (generalUsers.isEmpty()) {
            System.err.println("일반 유저가 없습니다!");
            return;
        }

        if (allPhotographers.isEmpty()) {
            System.err.println("포토그래퍼가 없습니다!");
            return;
        }

        if (allPhotoServices.isEmpty()) {
            System.err.println("포토서비스가 없습니다!");
            return;
        }

        if (allPriceInfos.isEmpty()) {
            System.err.println("가격정보가 없습니다!");
            return;
        }

        Random random = new Random();
        BookingStatus[] statuses = BookingStatus.values();
        int totalBookingsCreated = 0;

		// 각 일반 유저에 대해 예약 생성
		for (UserProfile user : generalUsers) {
			int bookingsForUser = 2 + random.nextInt(3); // 2-4개
			System.out.println("\n유저 '" + user.getNickName() + "' (Profile ID: " + user.getUserProfileId() +
					", User ID: " + user.getUser().getUserId() + ")에게 " +
					bookingsForUser + "개의 예약 생성 중...");

            for (int i = 0; i < bookingsForUser; i++) {
                try {
                    // 랜덤 포토그래퍼 선택
                    PhotographerProfile randomPhotographer = allPhotographers.get(random.nextInt(allPhotographers.size()));
                    System.out.println("  선택된 포토그래퍼: " + randomPhotographer.getBusinessName());

                    // 해당 포토그래퍼의 서비스 찾기
                    List<PhotoServiceInfo> photographerServices = allPhotoServices.stream()
                            .filter(service -> service.getPhotographerProfile().getPhotographerProfileId()
                                    .equals(randomPhotographer.getPhotographerProfileId()))
                            .collect(Collectors.toList());

                    if (photographerServices.isEmpty()) {
                        System.out.println("  포토그래퍼의 서비스가 없습니다. 다음 예약으로 넘어갑니다.");
                        continue;
                    }

                    PhotoServiceInfo randomService = photographerServices.get(random.nextInt(photographerServices.size()));
                    System.out.println("  선택된 서비스: " + randomService.getTitle());

                    // 해당 서비스의 가격 정보 찾기
                    List<PriceInfo> servicePrices = allPriceInfos.stream()
                            .filter(price -> price.getPhotoServiceInfo().getServiceId()
                                    .equals(randomService.getServiceId()))
                            .collect(Collectors.toList());

                    if (servicePrices.isEmpty()) {
                        System.out.println("  서비스의 가격 정보가 없습니다. 다음 예약으로 넘어갑니다.");
                        continue;
                    }

                    PriceInfo randomPrice = servicePrices.get(random.nextInt(servicePrices.size()));
                    System.out.println("  선택된 가격: " + randomPrice.getTitle() + " (" + randomPrice.getPrice() + "원)");

                    // 예약 날짜 및 시간 생성
                    LocalDate bookingDate = LocalDate.now().minusDays(random.nextInt(180));
                    LocalTime startTime = LocalTime.of(9 + random.nextInt(10), random.nextInt(2) * 30);
                    BookingStatus randomStatus = statuses[random.nextInt(statuses.length)];

                    // 예약 생성
                    BookingInfo bookingInfo = BookingInfo.builder()
                            .userProfile(user)
                            .photographerProfile(randomPhotographer)
                            .photoServiceInfo(randomService)
                            .priceInfo(randomPrice)
                            .bookingDate(bookingDate)
                            .bookingTime(startTime)
                            .status(randomStatus)
                            .build();

                    BookingInfo savedBooking = bookingInfoJpaRepository.save(bookingInfo);
                    totalBookingsCreated++;

                    System.out.println("  ✓ 예약 생성 성공 - Booking ID: " + savedBooking.getBookingInfoId() +
                            ", 상태: " + randomStatus + ", 날짜: " + bookingDate);

                } catch (Exception e) {
                    System.err.println("  ✗ 예약 생성 중 오류 발생: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }

		System.out.println("\n=== 예약 데이터 생성 완료 ===");
		System.out.println("총 생성된 예약 수: " + totalBookingsCreated);
		System.out.println("DB에 저장된 총 예약 수: " + bookingInfoJpaRepository.count());

        // User ID 1~3의 예약 확인
        for (int userId = 1; userId <= Math.min(3, generalUsers.size()); userId++) {
            List<BookingInfo> userBookings = bookingInfoJpaRepository.findByUserId((long) userId);
            System.out.println("User ID " + userId + "의 예약 수: " + userBookings.size());

			for (BookingInfo booking : userBookings) {
				System.out.println("  - Booking ID: " + booking.getBookingInfoId() +
						", 상태: " + booking.getStatus() +
						", 날짜: " + booking.getBookingDate());
			}
		}
	}

	// 새로 추가할 메서드들
	private void createCommunityData() {
		// 기존 방식으로 유저 조회 - findAll()을 사용하고 필터링
		List<User> allUsers = userJpaRepository.findAll();

		List<User> adminUsers = allUsers.stream()
				.filter(user -> "admin".equals(user.getUserType().getTypeCode()))
				.collect(Collectors.toList());

		List<User> generalUsers = allUsers.stream()
				.filter(user -> "user".equals(user.getUserType().getTypeCode()))
				.collect(Collectors.toList());

		// 관리자 공지글 생성 (3개)
		if (!adminUsers.isEmpty()) {
			createAdminPosts(adminUsers.get(0));
		}

		// 일반 사용자 게시글 생성 (17개)
		if (!generalUsers.isEmpty()) {
			createGeneralUserPosts(generalUsers);

			// 댓글 생성
			createReplies(adminUsers, generalUsers);

			// 좋아요 생성
			createLikes(generalUsers);
		}
	}

	private void createAdminPosts(User adminUser) {
		String[] titleKeywords = {
				"스냅촬영 작가님 추천", "프로필 촬영 베스트", "한강 스냅촬영 명소",
				"웨딩 촬영 가이드", "스튜디오 이용 안내"
		};

		String[] contentTemplates = {
				"안녕하세요! 찰칵 관리팀입니다.\n\n%s에 대한 공지사항을 안내드립니다.\n\n✨ 주요 내용:\n• 전문 작가님들의 엄선된 포트폴리오\n• 고객 만족도 95%% 이상 보장\n• 다양한 컨셉과 스타일 제공\n\n📍 추천 포인트:\n• 합리적인 가격대\n• 전문적인 장비 사용\n• 친절한 상담 서비스\n\n궁금한 점이 있으시면 언제든 문의해주세요!\n\n감사합니다.",

				"찰칵을 이용해주시는 고객님들께 감사드립니다.\n\n%s 관련하여 유용한 정보를 공유드립니다.\n\n💡 촬영 팁:\n1. 사전 상담을 통한 컨셉 공유\n2. 날씨와 시간대 고려\n3. 의상과 소품 미리 준비\n4. 자연스러운 표정 연습\n\n더 나은 서비스 제공을 위해 항상 노력하겠습니다.",

				"회원 여러분 안녕하세요.\n\n%s에 대한 업데이트된 정보를 알려드립니다.\n\n🌟 새로운 서비스:\n• 온라인 상담 예약 시스템\n• 실시간 작가님 매칭\n• 촬영 후 빠른 보정 서비스\n\n앞으로도 최고의 촬영 경험을 제공하도록 하겠습니다."
		};

		Random random = new Random();

		for (int i = 1; i <= 3; i++) {
			String keyword = titleKeywords[random.nextInt(titleKeywords.length)];
			String title = keyword + String.format(" %d차 업데이트", i);
			String content = String.format(contentTemplates[random.nextInt(contentTemplates.length)], keyword);

			Post adminPost = Post.builder()
					.user(adminUser)
					.title(title)
					.content(content)
					.status(Post.PostStatus.ACTIVE)
					.build();

			Post savedPost = postJpaRepository.save(adminPost);

			// 조회수, 좋아요, 댓글 수 설정
			savedPost.setViewCount(5000 + (i * 1500) + random.nextInt(3000));
			savedPost.setLikeCount(80 + (i * 30) + random.nextInt(50));
			savedPost.setReplyCount(25 + (i * 10) + random.nextInt(20));
			postJpaRepository.save(savedPost);

			System.out.println("관리자 게시글 생성: " + title);
		}
	}

	private void createGeneralUserPosts(List<User> generalUsers) {
		String[] postCategories = {
				"후기", "질문", "정보공유", "추천", "팁", "경험담"
		};

		String[] titleTemplates = {
				"%s에서 %s 촬영 %s",
				"%s 스냅촬영 %s 공유합니다",
				"%s %s 완전 만족!",
				"%s에서 찍은 %s 후기",
				"%s 촬영 시 %s 꿀팁",
				"%s %s 추천드려요",
				"%s에서의 %s 경험"
		};

		String[] locations = {
				"연남동", "홍대", "강남", "청담", "성수", "한강공원",
				"경복궁", "북촌", "이태원", "압구정", "신사동", "가로수길"
		};

		String[] photoTypes = {
				"커플스냅", "가족사진", "프로필", "졸업사진", "웨딩스냅",
				"돌잔치", "브랜딩", "반려동물", "우정사진", "개인화보"
		};

		String[] contentTemplates = {
				"%s에서 %s 촬영했는데 정말 만족스러워요! 작가님이 친절하시고 결과물도 예뻐서 추천드립니다. 특히 %s 때문에 선택했는데 기대 이상이었어요.",

				"%s 관련해서 궁금한 게 있어서 글 올려요. %s에서 촬영 고려 중인데 경험 있으신 분들 조언 부탁드려요! %s 때문에 고민이에요.",

				"%s 촬영 꿀팁 공유합니다! %s에서 찍을 때 %s 참고하시면 더 예쁜 사진 나올 거에요. 저도 이번에 처음 알았는데 정말 도움됐어요.",

				"드디어 %s 촬영 완료했습니다! %s에서 찍었는데 분위기가 정말 좋더라고요. %s 덕분에 만족스러운 결과 얻었어요. 추천합니다!",

				"%s에 대해 정보 공유드려요. %s 지역에서 괜찮은 곳 찾고 계신 분들께 도움이 될 것 같아요. %s 고려하시면 좋을 것 같습니다."
		};

		Random random = new Random();

		for (int i = 1; i <= 17; i++) {
			User randomUser = generalUsers.get(random.nextInt(generalUsers.size()));
			String category = postCategories[random.nextInt(postCategories.length)];
			String location = locations[random.nextInt(locations.length)];
			String photoType = photoTypes[random.nextInt(photoTypes.length)];

			String title = String.format(
					titleTemplates[random.nextInt(titleTemplates.length)],
					location, photoType, category
			);

			String content = String.format(
					contentTemplates[random.nextInt(contentTemplates.length)],
					photoType, location, category
			);

			Post generalPost = Post.builder()
					.user(randomUser)
					.title(title)
					.content(content)
					.status(Post.PostStatus.ACTIVE)
					.build();

			generalPost.setImageUrl(String.format("https://picsum.photos/600/400?random=%d", 10 + i));

			Post savedPost = postJpaRepository.save(generalPost);

			// 조회수, 좋아요, 댓글 수 설정
			savedPost.setViewCount(50 + random.nextInt(2000));
			savedPost.setLikeCount(random.nextInt(50));
			savedPost.setReplyCount(random.nextInt(30));
			postJpaRepository.save(savedPost);

			System.out.println("일반 게시글 생성: " + title);
		}
	}

	private void createReplies(List<User> adminUsers, List<User> generalUsers) {
		List<Post> allPosts = postJpaRepository.findAll();
		List<User> allUsers = new ArrayList<>();
		allUsers.addAll(adminUsers);
		allUsers.addAll(generalUsers);

		String[] replyTemplates = {
				"정말 유용한 정보네요! 감사합니다 😊",
				"저도 비슷한 경험이 있어서 공감되네요!",
				"혹시 추천해주신 작가님 연락처 알 수 있을까요?",
				"사진 정말 예쁘게 나왔네요! 부럽습니다",
				"좋은 정보 공유해주셔서 감사해요",
				"저도 그곳에서 촬영해봤는데 정말 좋더라고요",
				"다음에 저도 참고해서 촬영해봐야겠어요",
				"궁금했던 내용인데 덕분에 해결됐어요!",
				"작가님 실력이 정말 좋으신 것 같네요",
				"가격대는 어느 정도였나요?",
				"날씨는 어떠셨나요? 촬영에 지장 없으셨어요?",
				"보정은 얼마나 걸리셨나요?",
				"의상은 어떤 걸로 준비하셨어요?",
				"시간대 추천해주세요!",
				"저도 곧 촬영 예정인데 팁 감사해요!"
		};

		Random random = new Random();

		for (Post post : allPosts) {
			int replyCount = 1 + random.nextInt(8); // 1-8개 댓글

			for (int i = 0; i < replyCount; i++) {
				User randomUser = allUsers.get(random.nextInt(allUsers.size()));
				String replyContent = replyTemplates[random.nextInt(replyTemplates.length)];

				Reply reply = Reply.builder()
						.post(post)
						.user(randomUser)
						.content(replyContent)
						.status(Reply.ReplyStatus.ACTIVE)
						.build();

				replyJpaRepository.save(reply);
			}

			// 실제 댓글 수로 업데이트
			post.setReplyCount(replyCount);
			postJpaRepository.save(post);
		}

		System.out.println("댓글 생성 완료");
	}

	private void createLikes(List<User> generalUsers) {
		List<Post> allPosts = postJpaRepository.findAll();
		Random random = new Random();

		for (Post post : allPosts) {
			int likeCount = random.nextInt(30); // 0-29개 좋아요

			// 중복 방지를 위한 Set
			Set<User> likedUsers = new HashSet<>();

			while (likedUsers.size() < likeCount && likedUsers.size() < generalUsers.size()) {
				User randomUser = generalUsers.get(random.nextInt(generalUsers.size()));
				likedUsers.add(randomUser);
			}

			for (User user : likedUsers) {
				Like like = Like.builder()
						.post(post)
						.user(user)
						.build();

				likeJpaRepository.save(like);
			}

			// 실제 좋아요 수로 업데이트
			post.setLikeCount(likedUsers.size());
			postJpaRepository.save(post);
		}

		System.out.println("좋아요 생성 완료");
	}

    // [추가] 더미 리뷰 데이터 생성 메서드
    private void createReviewData() {
        System.out.println("=== 리뷰 데이터 생성 시작 ===");
        // 'COMPLETED' 상태인 예약 목록을 가져옵니다.
        List<BookingInfo> completedBookings = bookingInfoJpaRepository.findByStatus(BookingStatus.REVIEWED);
        if (completedBookings.isEmpty()) {
            System.out.println("완료된 예약이 없어 리뷰를 생성할 수 없습니다.");
            return;
        }

        String[] sampleReviews = {
                "인생 최고의 사진을 건졌습니다! 작가님이 정말 친절하고 프로페셔널하세요. 모든 순간이 즐거웠습니다.",
                "결과물은 만족스럽지만, 예약 시간에 조금 늦으셔서 아쉬웠습니다. 그래도 사진은 정말 예쁘게 나왔어요.",
                "가성비 최고의 스냅 사진! 이 가격에 이런 퀄리티라니, 정말 만족합니다.",
                "부모님 결혼기념일 선물로 드렸는데 너무 좋아하셨어요. 감사합니다!",
                null, // 내용 없는 리뷰
                "분위기도 잘 이끌어주시고, 보정본도 빠르게 받을 수 있어서 좋았습니다."
        };

        Random random = new Random();
        int reviewsCreated = 0;

        for (BookingInfo booking : completedBookings) {
            // 이미 해당 예약에 대한 리뷰가 있는지 확인
            if (photoServiceReviewJpaRepository.findByBookingInfo(booking).isPresent()) {
                continue;
            }

            PhotoServiceReview review = PhotoServiceReview.builder()
                    .photoServiceInfo(booking.getPhotoServiceInfo())
                    .user(booking.getUserProfile().getUser())
                    .bookingInfo(booking)
                    .rating(BigDecimal.valueOf(3.0 + random.nextDouble() * 2.0)
                            .setScale(1, RoundingMode.HALF_UP)) // 3.0 ~ 5.0 사이의 평점
                    .reviewContent(sampleReviews[random.nextInt(sampleReviews.length)])
                    .build();

            photoServiceReviewJpaRepository.save(review);
            reviewsCreated++;
        }
        System.out.println("총 " + reviewsCreated + "개의 리뷰 데이터 생성 완료.");
    }
}