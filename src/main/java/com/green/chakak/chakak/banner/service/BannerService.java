//package com.green.chakak.chakak.banner.service;
//
//import com.green.chakak.chakak.banner.service.response.BannerResponse;
//
//@Service
//@RequiredArgsConstructor
//@Transactional(readOnly = true)
//public class BannerService {
//
//    private final BannerRepository bannerRepository;
//    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
//
//    /**
//     * 활성화되고 만료되지 않은 배너 목록 조회 (Flutter 앱용)
//     */
//    public List<BannerResponseDto> getActiveBanners() {
//        LocalDateTime now = LocalDateTime.now();
//        List<Banner> banners = bannerRepository.findActiveAndNotExpiredBanners(now);
//        return banners.stream()
//                .map(this::convertToResponseDto)
//                .collect(Collectors.toList());
//    }
//
//    /**
//     * 전체 배너 목록 조회 (관리자용)
//     */
//    public List<BannerListResponseDto> getAllBanners() {
//        List<Banner> banners = bannerRepository.findAllByOrderByDisplayOrderAscCreatedAtDesc();
//        return banners.stream()
//                .map(this::convertToListResponseDto)
//                .collect(Collectors.toList());
//    }
//
//    /**
//     * 특정 배너 상세 조회
//     */
//    public BannerResponse.BannerResponseDto getBannerById(Long id) {
//        Banner banner = bannerRepository.findById(id)
//                .orElseThrow(() -> new RuntimeException("배너를 찾을 수 없습니다. ID: " + id));
//        return convertToResponseDto(banner);
//    }
//
//    /**
//     * 배너 생성
//     */
//    @Transactional
//    public BannerResponseDto createBanner(BannerCreateRequestDto requestDto) {
//        // displayOrder가 없으면 마지막 순서로 설정
//        Integer displayOrder = requestDto.getDisplayOrder();
//        if (displayOrder == null) {
//            Integer maxOrder = bannerRepository.findMaxDisplayOrder();
//            displayOrder = maxOrder + 1;
//        }
//
//        // expiresAt 문자열을 LocalDateTime으로 변환
//        LocalDateTime expiresAt = null;
//        if (requestDto.getExpiresAt() != null && !requestDto.getExpiresAt().isEmpty()) {
//            expiresAt = LocalDateTime.parse(requestDto.getExpiresAt(), FORMATTER);
//        }
//
//        Banner banner = Banner.builder()
//                .title(requestDto.getTitle())
//                .subtitle(requestDto.getSubtitle())
//                .imageUrl(requestDto.getImageUrl())
//                .linkUrl(requestDto.getLinkUrl())
//                .displayOrder(displayOrder)
//                .expiresAt(expiresAt)
//                .build();
//
//        Banner savedBanner = bannerRepository.save(banner);
//        return convertToResponseDto(savedBanner);
//    }
//
//    /**
//     * 배너 수정
//     */
//    @Transactional
//    public BannerResponseDto updateBanner(Long id, BannerUpdateRequestDto requestDto) {
//        Banner banner = bannerRepository.findById(id)
//                .orElseThrow(() -> new RuntimeException("배너를 찾을 수 없습니다. ID: " + id));
//
//        // 수정할 필드들 업데이트 (null이 아닌 경우만)
//        if (requestDto.getTitle() != null) {
//            banner.updateTitle(requestDto.getTitle());
//        }
//        if (requestDto.getSubtitle() != null) {
//            banner.updateSubtitle(requestDto.getSubtitle());
//        }
//        if (requestDto.getImageUrl() != null) {
//            banner.updateImageUrl(requestDto.getImageUrl());
//        }
//        if (requestDto.getLinkUrl() != null) {
//            banner.updateLinkUrl(requestDto.getLinkUrl());
//        }
//        if (requestDto.getIsActive() != null) {
//            banner.updateIsActive(requestDto.getIsActive());
//        }
//        if (requestDto.getDisplayOrder() != null) {
//            banner.updateDisplayOrder(requestDto.getDisplayOrder());
//        }
//        if (requestDto.getExpiresAt() != null) {
//            LocalDateTime expiresAt = null;
//            if (!requestDto.getExpiresAt().isEmpty()) {
//                expiresAt = LocalDateTime.parse(requestDto.getExpiresAt(), FORMATTER);
//            }
//            banner.updateExpiresAt(expiresAt);
//        }
//
//        Banner updatedBanner = bannerRepository.save(banner);
//        return convertToResponseDto(updatedBanner);
//    }
//
//    /**
//     * 배너 삭제
//     */
//    @Transactional
//    public void deleteBanner(Long id) {
//        if (!bannerRepository.existsById(id)) {
//            throw new RuntimeException("배너를 찾을 수 없습니다. ID: " + id);
//        }
//        bannerRepository.deleteById(id);
//    }
//
//    /**
//     * 배너 활성화/비활성화
//     */
//    @Transactional
//    public BannerResponseDto toggleBannerStatus(Long id) {
//        Banner banner = bannerRepository.findById(id)
//                .orElseThrow(() -> new RuntimeException("배너를 찾을 수 없습니다. ID: " + id));
//
//        banner.toggleActive();
//        Banner updatedBanner = bannerRepository.save(banner);
//        return convertToResponseDto(updatedBanner);
//    }
//
//    /**
//     * 만료된 배너들 자동 비활성화 (스케줄러에서 호출)
//     */
//    @Transactional
//    public int deactivateExpiredBanners() {
//        LocalDateTime now = LocalDateTime.now();
//        List<Banner> expiredBanners = bannerRepository.findExpiredBanners(now);
//
//        int count = 0;
//        for (Banner banner : expiredBanners) {
//            if (banner.getIsActive()) {
//                banner.updateIsActive(false);
//                bannerRepository.save(banner);
//                count++;
//            }
//        }
//        return count;
//    }
//
//    // Entity -> ResponseDto 변환
//    private BannerResponseDto convertToResponseDto(Banner banner) {
//        return BannerResponseDto.builder()
//                .id(banner.getId())
//                .title(banner.getTitle())
//                .subtitle(banner.getSubtitle())
//                .imageUrl(banner.getImageUrl())
//                .linkUrl(banner.getLinkUrl())
//                .isActive(banner.getIsActive())
//                .createdAt(banner.getCreatedAt().format(FORMATTER))
//                .expiresAt(banner.getExpiresAt() != null ? banner.getExpiresAt().format(FORMATTER) : null)
//                .displayOrder(banner.getDisplayOrder())
//                .updatedAt(banner.getUpdatedAt().format(FORMATTER))
//                .build();
//    }
//
//    // Entity -> ListResponseDto 변환 (이미지 데이터 제외)
//    private BannerListResponseDto convertToListResponseDto(Banner banner) {
//        return BannerListResponseDto.builder()
//                .id(banner.getId())
//                .title(banner.getTitle())
//                .subtitle(banner.getSubtitle())
//                .linkUrl(banner.getLinkUrl())
//                .isActive(banner.getIsActive())
//                .displayOrder(banner.getDisplayOrder())
//                .createdAt(banner.getCreatedAt().format(FORMATTER))
//                .expiresAt(banner.getExpiresAt() != null ? banner.getExpiresAt().format(FORMATTER) : null)
//                .hasImage(banner.getImageUrl() != null && !banner.getImageUrl().isEmpty())
//                .build();
//    }