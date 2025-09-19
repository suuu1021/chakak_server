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
import com.green.chakak.chakak.photo.domain.PhotoServiceCategory;
import com.green.chakak.chakak.photo.domain.PhotoServiceMapping;
import com.green.chakak.chakak.photo.domain.PriceInfo;
import com.green.chakak.chakak.photo.service.repository.PhotoCategoryJpaRepository;
import com.green.chakak.chakak.photo.service.repository.PhotoMappingRepository;
import com.green.chakak.chakak.photo.service.repository.PhotoServiceJpaRepository;
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
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Random;

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

    @Override
    @Transactional
    public void run(String... args) throws Exception {

        // UserType은 Builder가 없으므로 new와 setter를 사용합니다.
        UserType userRole = userTypeRepository.findByTypeCode("user").orElseGet(() -> {
            UserType newUserType = new UserType();
            newUserType.setTypeCode("user");
            newUserType.setTypeName("일반회원");
            newUserType.setCreatedAt(LocalDateTime.now());
            newUserType.setUpdatedAt(LocalDateTime.now());
            return userTypeRepository.save(newUserType);
        });

        UserType photographerRole = userTypeRepository.findByTypeCode("photographer").orElseGet(() -> {
            UserType newPhotographerType = new UserType();
            newPhotographerType.setTypeCode("photographer");
            newPhotographerType.setTypeName("사진작가");
            newPhotographerType.setCreatedAt(LocalDateTime.now());
            newPhotographerType.setUpdatedAt(LocalDateTime.now());
            return userTypeRepository.save(newPhotographerType);
        });

        UserType adminRole = userTypeRepository.findByTypeCode("admin").orElseGet(() -> {
            UserType newAdminType = new UserType();
            newAdminType.setTypeCode("admin");
            newAdminType.setTypeName("관리자");
            newAdminType.setCreatedAt(LocalDateTime.now());
            newAdminType.setUpdatedAt(LocalDateTime.now());
            return userTypeRepository.save(newAdminType);
        });

        // 0. 포토그래퍼 카테고리 생성
        PhotographerCategory weddingPhotographerCategory = photographerCategoryRepository.findByCategoryName("웨딩").orElseGet(() -> {
            PhotographerCategory category = new PhotographerCategory();
            category.setCategoryName("웨딩");
            return photographerCategoryRepository.save(category);
        });

        PhotographerCategory portraitPhotographerCategory = photographerCategoryRepository.findByCategoryName("프로필").orElseGet(() -> {
            PhotographerCategory category = new PhotographerCategory();
            category.setCategoryName("프로필");
            return photographerCategoryRepository.save(category);
        });

        PhotographerCategory familyPhotographerCategory = photographerCategoryRepository.findByCategoryName("가족사진").orElseGet(() -> {
            PhotographerCategory category = new PhotographerCategory();
            category.setCategoryName("가족사진");
            return photographerCategoryRepository.save(category);
        });

        PhotographerCategory couplePhotographerCategory = photographerCategoryRepository.findByCategoryName("커플").orElseGet(() -> {
            PhotographerCategory category = new PhotographerCategory();
            category.setCategoryName("커플");
            return photographerCategoryRepository.save(category);
        });

        PhotographerCategory graduationPhotographerCategory = photographerCategoryRepository.findByCategoryName("졸업사진").orElseGet(() -> {
            PhotographerCategory category = new PhotographerCategory();
            category.setCategoryName("졸업사진");
            return photographerCategoryRepository.save(category);
        });

        PhotographerCategory doljanchiPhotographerCategory = photographerCategoryRepository.findByCategoryName("돌잔치").orElseGet(() -> {
            PhotographerCategory category = new PhotographerCategory();
            category.setCategoryName("돌잔치");
            return photographerCategoryRepository.save(category);
        });

        // 0-1. 포토서비스 카테고리 생성
        PhotoServiceCategory weddingServiceCategory = photoServiceCategoryRepository.findByCategoryName("웨딩").orElseGet(() -> {
            PhotoServiceCategory category = new PhotoServiceCategory();
            category.setCategoryName("웨딩");
            category.setCategoryImageData("https://images.unsplash.com/photo-1519741497674-611481863552?w=300&h=300&fit=crop");
            return photoServiceCategoryRepository.save(category);
        });

        PhotoServiceCategory familyServiceCategory = photoServiceCategoryRepository.findByCategoryName("가족사진").orElseGet(() -> {
            PhotoServiceCategory category = new PhotoServiceCategory();
            category.setCategoryName("가족사진");
            category.setCategoryImageData("https://images.unsplash.com/photo-1511895426328-dc8714191300?w=300&h=300&fit=crop");
            return photoServiceCategoryRepository.save(category);
        });

        PhotoServiceCategory profileServiceCategory = photoServiceCategoryRepository.findByCategoryName("프로필").orElseGet(() -> {
            PhotoServiceCategory category = new PhotoServiceCategory();
            category.setCategoryName("프로필");
            category.setCategoryImageData("https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=300&h=300&fit=crop&crop=face");
            return photoServiceCategoryRepository.save(category);
        });

        PhotoServiceCategory coupleServiceCategory = photoServiceCategoryRepository.findByCategoryName("커플").orElseGet(() -> {
            PhotoServiceCategory category = new PhotoServiceCategory();
            category.setCategoryName("커플");
            category.setCategoryImageData("https://images.unsplash.com/photo-1516589178581-6cd7833ae3b2?w=300&h=300&fit=crop");
            return photoServiceCategoryRepository.save(category);
        });

        PhotoServiceCategory graduationServiceCategory = photoServiceCategoryRepository.findByCategoryName("졸업사진").orElseGet(() -> {
            PhotoServiceCategory category = new PhotoServiceCategory();
            category.setCategoryName("졸업사진");
            category.setCategoryImageData("https://images.unsplash.com/photo-1541339907198-e08756dedf3f?w=300&h=300&fit=crop");
            return photoServiceCategoryRepository.save(category);
        });

        PhotoServiceCategory doljanchiServiceCategory = photoServiceCategoryRepository.findByCategoryName("돌잔치").orElseGet(() -> {
            PhotoServiceCategory category = new PhotoServiceCategory();
            category.setCategoryName("돌잔치");
            category.setCategoryImageData("https://images.unsplash.com/photo-1515488764276-beab7607c1e6?w=300&h=300&fit=crop");
            return photoServiceCategoryRepository.save(category);
        });

        // 1. 일반 유저 및 프로필 10개 생성
        for (int i = 1; i <= 10; i++) {
            // User 생성 (평문 비밀번호 저장으로 원상복귀)
            User newUser = User.builder()
                    .email(String.format("user%d@example.com", i))
                    .password("123456") // 평문 비밀번호 저장
                    .userType(userRole)
                    .status(User.UserStatus.ACTIVE)
                    .emailVerified(true)
                    .build();
            userJpaRepository.save(newUser);

            // UserProfile 생성
            UserProfile userProfile = UserProfile.builder()
                    .user(newUser)
                    .nickName(String.format("일반유저%d", i))
                    .introduce(String.format("안녕하세요, 유저 %d입니다.", i))
                    .createdAt(Timestamp.from(Instant.now()))
                    .updatedAt(Timestamp.from(Instant.now()))
                    .build();
            userProfileJpaRepository.save(userProfile);
        }

        // 2. 사진작가 유저 및 프로필 10개 생성
        for (int i = 1; i <= 10; i++) {
            // User 생성 (평문 비밀번호 저장으로 원상복귀)
            User newPhotographerUser = User.builder()
                    .email(String.format("photo%d@example.com", i))
                    .password("123456") // 평문 비밀번호 저장
                    .userType(photographerRole)
                    .status(User.UserStatus.ACTIVE)
                    .emailVerified(true)
                    .build();
            userJpaRepository.save(newPhotographerUser);

            // PhotographerProfile 생성
            PhotographerProfile photographerProfile = PhotographerProfile.builder()
                    .user(newPhotographerUser)
                    .businessName(String.format("감성스튜디오%d호점", i))
                    .introduction(String.format("최고의 순간을 담아드립니다. 감성스튜디오 %d입니다.", i))
                    .status("ACTIVE")
                    .location("서울")
                    .experienceYears(i)
                    .build();
            photographerRepository.save(photographerProfile);
        }

        Admin newAdmin = Admin.builder()
                .adminName("superadmin")
                .password("super1234")
                .userType(adminRole)
                .build();
        adminJpaRepository.save(newAdmin);

//------------------------------------------------------ 포토서비스 더미데이터(필요시 수정가능) @suuu1021
// 3. 포토 서비스 정보 생성 (각 사진작가당 2-3개씩)
        List<PhotographerProfile> photographers = photographerRepository.findAll();

// 포토서비스 카테고리 배열 생성
        PhotoServiceCategory[] serviceCategories = {
                weddingServiceCategory, familyServiceCategory, profileServiceCategory,
                coupleServiceCategory, graduationServiceCategory, doljanchiServiceCategory
        };

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
                "https://picsum.photos/400/300?random=5",
                "https://picsum.photos/400/300?random=6",
                "https://picsum.photos/400/300?random=7",
                "https://picsum.photos/400/300?random=8",
                "https://picsum.photos/400/300?random=9",
                "https://picsum.photos/400/300?random=10"
        };
        for (PhotographerProfile photographer : photographers) {
            // 각 사진작가당 2-3개의 서비스 생성
            int serviceCount = 2 + (int) (Math.random() * 2); // 2 또는 3개

            for (int j = 0; j < serviceCount; j++) {
                int typeIndex = (int) (Math.random() * serviceTypes.length);
                int descIndex = (int) (Math.random() * sampleDescriptions.length);
                int imageIndex = (int) (Math.random() * sampleImageData.length);
                int categoryIndex = (int) (Math.random() * serviceCategories.length);

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
                        .photoServiceCategory(serviceCategories[categoryIndex])
                        .createdAt(Timestamp.from(Instant.now()))
                        .build();

                photoMappingRepository.save(mapping);

                // PriceInfo 더미데이터 생성
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
                            .photoServiceInfo(savedPhotoService)
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
        }
        //------------------------------------------------------ 포토서비스 더미데이터 끝
        // 4. 포트폴리오 카테고리 생성
        PortfolioCategory weddingCategory = portfolioCategoryRepository.findByCategoryName("웨딩촬영").orElseGet(() -> {
            PortfolioCategory category = new PortfolioCategory();
            category.setCategoryName("웨딩촬영");
            category.setSortOrder(1);
            category.setIsActive(true);
            category.setCreatedAt(LocalDateTime.now());
            category.setUpdatedAt(LocalDateTime.now());
            return portfolioCategoryRepository.save(category);
        });

        PortfolioCategory portraitCategory = portfolioCategoryRepository.findByCategoryName("인물촬영").orElseGet(() -> {
            PortfolioCategory category = new PortfolioCategory();
            category.setCategoryName("인물촬영");
            category.setSortOrder(2);
            category.setIsActive(true);
            category.setCreatedAt(LocalDateTime.now());
            category.setUpdatedAt(LocalDateTime.now());
            return portfolioCategoryRepository.save(category);
        });

        PortfolioCategory familyCategory = portfolioCategoryRepository.findByCategoryName("가족사진").orElseGet(() -> {
            PortfolioCategory category = new PortfolioCategory();
            category.setCategoryName("가족사진");
            category.setSortOrder(3);
            category.setIsActive(true);
            category.setCreatedAt(LocalDateTime.now());
            category.setUpdatedAt(LocalDateTime.now());
            return portfolioCategoryRepository.save(category);
        });

        PortfolioCategory coupleCategory = portfolioCategoryRepository.findByCategoryName("커플촬영").orElseGet(() -> {
            PortfolioCategory category = new PortfolioCategory();
            category.setCategoryName("커플촬영");
            category.setSortOrder(4);
            category.setIsActive(true);
            category.setCreatedAt(LocalDateTime.now());
            category.setUpdatedAt(LocalDateTime.now());
            return portfolioCategoryRepository.save(category);
        });

        List<PortfolioCategory> categories = List.of(weddingCategory, portraitCategory, familyCategory, coupleCategory);

        // 5. 각 사진작가별로 포트폴리오 생성
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

        for (PhotographerProfile photographer : photographers) {
            // 각 사진작가당 2-4개의 포트폴리오 생성
            int portfolioCount = 2 + (int) (Math.random() * 3); // 2~4개

            for (int i = 0; i < portfolioCount; i++) {
                int titleIndex = (int) (Math.random() * portfolioTitles.length);
                int descIndex = (int) (Math.random() * portfolioDescriptions.length);
                int categoryIndex = (int) (Math.random() * categories.size());

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
                int imageCount = 3 + (int) (Math.random() * 4); // 3~6개
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

            // 6. 예약 더미 데이터 생성
            List<UserProfile> users = userProfileJpaRepository.findAll();
            List<PhotoServiceInfo> photoServices = photoServiceJpaRepository.findAll();

            if (users.isEmpty() || photographers.isEmpty() || photoServices.isEmpty()) {
                System.err.println("더미 데이터 생성을 위한 사용자, 사진작가, 서비스가 부족합니다.");
                return;
            }

            Random random = new Random();
            BookingStatus[] statuses = BookingStatus.values();

            for (int i = 0; i < 20; i++) {
                UserProfile randomUser = users.get(random.nextInt(users.size()));
                PhotoServiceInfo randomService = photoServices.get(random.nextInt(photoServices.size()));
                PhotographerProfile randomPhotographer = randomService.getPhotographerProfile();

                // 서비스에 연결된 PriceInfo 목록 가져오기
                List<PriceInfo> prices = priceInfoJpaRepository.findByPhotoServiceInfo(randomService);
                if (prices.isEmpty()) {
                    continue;
                }
                PriceInfo randomPrice = prices.get(random.nextInt(prices.size()));

                // 예약 상태 랜덤 선택
                BookingStatus randomStatus = statuses[random.nextInt(statuses.length)];

                // 예약 날짜 및 시간 랜덤 생성 (최근 한 달 이내)
                LocalDate bookingDate = LocalDate.now().minusDays(random.nextInt(30));
                LocalTime startTime = LocalTime.of(9 + random.nextInt(10), random.nextInt(2) * 30);

                BookingInfo bookingInfo = BookingInfo.builder()
                        .userProfile(randomUser) // User가 아닌 UserProfile 객체를 전달
                        .photographerProfile(randomPhotographer) // 필드명 수정
                        .photoServiceInfo(randomService) // 필드명 수정
                        .priceInfo(randomPrice)
                        .bookingDate(bookingDate)
                        .bookingTime(startTime) // 필드명 수정
                        .status(randomStatus)
                        .build();

                bookingInfoJpaRepository.save(bookingInfo);
            }
        }
    }
}
