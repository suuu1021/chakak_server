package com.green.chakak.chakak.banner.service;

import com.green.chakak.chakak._global.errors.exception.Exception404;
import com.green.chakak.chakak._global.errors.exception.Exception500;
import com.green.chakak.chakak.banner.domain.Banner;
import com.green.chakak.chakak.banner.service.repository.BannerRepository;
import com.green.chakak.chakak.banner.service.request.BannerRequest;
import com.green.chakak.chakak.banner.service.response.BannerResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class BannerService {

    private final BannerRepository bannerRepository;

    /**
     * 활성화된 배너 목록 조회 (Flutter 앱용)
     */
    public List<BannerResponse.BannerListDTO> getActiveBanners() {
        LocalDateTime now = LocalDateTime.now();
        List<Banner> banners = bannerRepository.findActiveAndNotExpiredBanners(now);

        List<BannerResponse.BannerListDTO> bannerList = new ArrayList<>();
        for (Banner banner : banners) {
            BannerResponse.BannerListDTO dto = new BannerResponse.BannerListDTO(banner);
            bannerList.add(dto);
        }
        return bannerList;
    }

    /**
     * 전체 배너 목록 조회 (관리자용 - 페이징)
     */
    public List<BannerResponse.BannerListDTO> getBannerList(int page, int size, String keyword) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("displayOrder").ascending().and(Sort.by("createdAt").descending()));
        Page<Banner> bannerPage;

        if (keyword != null && !keyword.trim().isEmpty()) {
            bannerPage = bannerRepository.findByKeyword(keyword.trim(), pageable);
        } else {
            bannerPage = bannerRepository.findAllByOrderByDisplayOrderAscCreatedAtDesc(pageable);
        }

        List<BannerResponse.BannerListDTO> bannerList = new ArrayList<>();
        for (Banner banner : bannerPage.getContent()) {
            BannerResponse.BannerListDTO dto = new BannerResponse.BannerListDTO(banner);
            bannerList.add(dto);
        }
        return bannerList;
    }

    /**
     * 배너 상세 조회
     */
    public BannerResponse.BannerDetailDTO getBannerDetail(Long id) {
        Banner banner = bannerRepository.findByBannerId(id)
                .orElseThrow(() -> new Exception404("해당 배너가 존재하지 않습니다."));

        BannerResponse.BannerDetailDTO dto = new BannerResponse.BannerDetailDTO(banner);
        // TODO: 관리자 권한 체크 로직 추가 시 canEdit 설정
        dto.setCanEdit(true); // 임시로 true

        return dto;
    }

    /**
     * 배너 생성
     */
    @Transactional
    public void saveBanner(BannerRequest.CreateDTO createDTO) {
        log.info("배너 생성 요청: {}", createDTO);

        // displayOrder가 없으면 마지막 순서로 설정
        if (createDTO.getDisplayOrder() == null) {
            Integer maxOrder = bannerRepository.findMaxDisplayOrder();
            createDTO.setDisplayOrder(maxOrder + 1);
        }

        Banner banner = createDTO.CreateDtoToEntity();
        Banner savedBanner = bannerRepository.save(banner);

        if (savedBanner.getBannerId() == null) {
            throw new Exception500("배너 생성이 처리되지 않았습니다.");
        }

        log.info("배너 생성 완료. ID: {}", savedBanner.getBannerId());
    }

    /**
     * 배너 수정
     */
    @Transactional
    public void updateBanner(Long id, BannerRequest.UpdateDTO updateDTO) {
        log.info("배너 수정 요청. ID: {}, 데이터: {}", id, updateDTO);

        Banner banner = bannerRepository.findByBannerId(id)
                .orElseThrow(() -> new Exception404("해당 배너가 존재하지 않습니다."));

        // TODO: 권한 체크 로직 추가 (관리자만 수정 가능)

        banner.updateFromDto(updateDTO);
        Banner updatedBanner = bannerRepository.save(banner);

        log.info("배너 수정 완료. ID: {}", updatedBanner.getBannerId());
    }

    /**
     * 배너 삭제
     */
    @Transactional
    public void deleteBanner(Long id) {
        log.info("배너 삭제 요청. ID: {}", id);

        Banner banner = bannerRepository.findByBannerId(id)
                .orElseThrow(() -> new Exception404("해당 배너가 존재하지 않습니다."));

        // TODO: 권한 체크 로직 추가 (관리자만 삭제 가능)

        bannerRepository.deleteById(id);

        log.info("배너 삭제 완료. ID: {}", id);
    }

    /**
     * 배너 활성화/비활성화 토글
     */
//    @Transactional
//    public BannerResponse.BannerDetailDTO toggleBannerStatus(Long id) {
//        log.info("배너 상태 변경 요청. ID: {}", id);
//
//        Banner banner = bannerRepository.findByBannerId(id)
//                .orElseThrow(() -> new Exception404("해당 배너가 존재하지 않습니다."));
//
//        banner.toggleActive();
//        Banner updatedBanner = bannerRepository.save(banner);
//
//        log.info("배너 상태 변경 완료. ID: {}, 새 상태: {}", id, updatedBanner.getIsActive());
//
//        return new BannerResponse.BannerDetailDTO(updatedBanner);
//    }

    @Transactional
    public BannerResponse.BannerDetailDTO toggleBannerStatus(Long id) {
        log.info("배너 상태 변경 요청. ID: {}", id);

        Banner banner = bannerRepository.findByBannerId(id)
                .orElseThrow(() -> new Exception404("해당 배너가 존재하지 않습니다."));

        // 토글 전 상태 확인
        log.info("현재 상태 (토글 전): {}", banner.isActive());

        banner.toggleActive();

        // 토글 후 상태 확인
        log.info("변경된 상태 (토글 후): {}", banner.isActive());

        // save() 없이 바로 DTO 변환
        return new BannerResponse.BannerDetailDTO(banner);
    }

    /**
     * 만료된 배너들 자동 비활성화 (스케줄러용)
     */
    @Transactional
    public int deactivateExpiredBanners() {
        LocalDateTime now = LocalDateTime.now();
        List<Banner> expiredBanners = bannerRepository.findExpiredBanners(now);

        int deactivatedCount = 0;
        for (Banner banner : expiredBanners) {
            if (banner.isActive()) {
                banner.setActive(false);
                bannerRepository.save(banner);
                deactivatedCount++;
                log.info("만료된 배너 비활성화. ID: {}, 제목: {}", banner.getBannerId(), banner.getTitle());
            }
        }

        if (deactivatedCount > 0) {
            log.info("만료된 배너 {}개를 비활성화했습니다.", deactivatedCount);
        }

        return deactivatedCount;
    }

    /**
     * 배너 표시 순서 변경
     */
    @Transactional
    public void updateDisplayOrder(Long id, Integer newOrder) {
        Banner banner = bannerRepository.findByBannerId(id)
                .orElseThrow(() -> new Exception404("해당 배너가 존재하지 않습니다."));

        Integer oldOrder = banner.getDisplayOrder();
        banner.setDisplayOrder(newOrder);
        bannerRepository.save(banner);

        log.info("배너 표시 순서 변경. ID: {}, {} -> {}", id, oldOrder, newOrder);
    }


}