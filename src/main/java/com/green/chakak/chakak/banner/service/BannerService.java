package com.green.chakak.chakak.banner.service;

import com.green.chakak.chakak._global.errors.exception.Exception400;
import com.green.chakak.chakak._global.errors.exception.Exception404;
import com.green.chakak.chakak._global.errors.exception.Exception500;
import com.green.chakak.chakak._global.utils.FileUploadUtil;
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
    private final FileUploadUtil fileUploadUtil;


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


    public BannerResponse.BannerDetailDTO getBannerDetail(Long id) {
        Banner banner = bannerRepository.findByBannerId(id)
                .orElseThrow(() -> new Exception404("해당 배너가 존재하지 않습니다."));

        BannerResponse.BannerDetailDTO dto = new BannerResponse.BannerDetailDTO(banner);
        // TODO: 관리자 권한 체크 로직 추가 시 canEdit 설정
        dto.setCanEdit(true); // 임시로 true

        return dto;
    }


    @Transactional
    public void saveBanner(BannerRequest.CreateDTO createDTO) {
        try {

            log.info("배너 생성 요청: {}", createDTO);

            validateCreate(createDTO);

            String imageUrl = null;
            imageUrl = fileUploadUtil.saveBase64ImageWithType(createDTO.getImageData(), "Image", "banner");

            log.debug("이미지 등록 완료: {} ", imageUrl);

            if (createDTO.getDisplayOrder() == null) {
                Integer maxOrder = bannerRepository.findMaxDisplayOrder();
                createDTO.setDisplayOrder(maxOrder + 1);
            }

            Banner banner = createDTO.CreateDtoToEntity();
            banner.setImageUrl(imageUrl);
            Banner savedBanner = bannerRepository.save(banner);

            log.info("배너 생성 완료. ID: {}", savedBanner.getBannerId());

        } catch (Exception400 | Exception404 e) {
            throw e;
        }  catch (Exception e) {
            log.error("배너 생성 중 서버 오류 발생", e);
            throw new Exception500("배너 생성 중 서버 오류가 발생했습니다");
        }

    }


    @Transactional
    public void updateBanner(Long id, BannerRequest.UpdateDTO updateDTO) {
        log.info("배너 수정 요청. ID: {}, 데이터: {}", id, updateDTO);


        Banner banner = bannerRepository.findByBannerId(id)
                .orElseThrow(() -> new Exception404("해당 배너가 존재하지 않습니다."));

        if (updateDTO.getImageData() != null && !updateDTO.getImageData().isBlank()) {

            if (banner.getImageUrl() != null && !banner.getImageUrl().isBlank()) {
                fileUploadUtil.deleteFile(banner.getImageUrl());
            }

            String imageUrl = fileUploadUtil.saveBase64ImageWithType(updateDTO.getImageData(), "Image", "banner");
            banner.setImageUrl(imageUrl);
        }

        banner.updateFromDto(updateDTO);
        Banner updatedBanner = bannerRepository.save(banner);

        log.info("배너 수정 완료. ID: {}", updatedBanner.getBannerId());
    }


    @Transactional
    public void deleteBanner(Long id) {
        log.info("배너 삭제 요청. ID: {}", id);

        Banner banner = bannerRepository.findByBannerId(id)
                .orElseThrow(() -> new Exception404("해당 배너가 존재하지 않습니다."));


        bannerRepository.deleteById(id);

        log.info("배너 삭제 완료. ID: {}", id);
    }


    @Transactional
    public BannerResponse.BannerDetailDTO toggleBannerStatus(Long id) {
        log.info("배너 상태 변경 요청. ID: {}", id);

        Banner banner = bannerRepository.findByBannerId(id)
                .orElseThrow(() -> new Exception404("해당 배너가 존재하지 않습니다."));

        log.info("현재 상태 (토글 전): {}", banner.isActive());

        banner.toggleActive();

        log.info("변경된 상태 (토글 후): {}", banner.isActive());


        return new BannerResponse.BannerDetailDTO(banner);
    }

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

    @Transactional
    public void updateDisplayOrder(Long id, Integer newOrder) {
        Banner banner = bannerRepository.findByBannerId(id)
                .orElseThrow(() -> new Exception404("해당 배너가 존재하지 않습니다."));

        Integer oldOrder = banner.getDisplayOrder();
        banner.setDisplayOrder(newOrder);
        bannerRepository.save(banner);

        log.info("배너 표시 순서 변경. ID: {}, {} -> {}", id, oldOrder, newOrder);
    }

    private void validateCreate(BannerRequest.CreateDTO createDTO) {
        if (createDTO == null) {
            throw new Exception400("배너 생성 요청 데이터가 없습니다");
        }
    }


}