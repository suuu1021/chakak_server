package com.green.chakak.chakak.booking.service.response;

import com.green.chakak.chakak.booking.domain.BookingCancelInfo;
import lombok.Builder;
import lombok.Data;

public class BookingCancelInfoResponse {

    @Data
    public static class BookingCancelInfoGetResponse {
        private long cancellationId;
        private Long bookingId;
        private Long userId;
        private String cancelReasonText;
        private int penaltyAmount;
        private int refundAmount;
        private String createdAt;

        @Builder
        public BookingCancelInfoGetResponse(long cancellationId, Long bookingId, Long userId, String cancelReasonText, int penaltyAmount, int refundAmount, String createdAt) {
            this.cancellationId = cancellationId;
            this.bookingId = bookingId;
            this.userId = userId;
            this.cancelReasonText = cancelReasonText;
            this.penaltyAmount = penaltyAmount;
            this.refundAmount = refundAmount;
            this.createdAt = createdAt;
        }


        public static BookingCancelInfoGetResponse from(BookingCancelInfo entity) {
            return BookingCancelInfoGetResponse.builder()
                    .cancellationId(entity.getCancellationId())
                    .bookingId(entity.getBookingInfo() != null ? entity.getBookingInfo().getBookingInfoId() : null)
                    .userId(entity.getUserProfile() != null ? entity.getUserProfile().getUserProfileId() : null)
                    .cancelReasonText(entity.getCancelReasonText())
                    .penaltyAmount(entity.getPenaltyAmount())
                    .refundAmount(entity.getRefundAmount())
                    .createdAt(entity.getCreatedAt() != null ? entity.getCreatedAt().toString() : null)
                    .build();
        }
    }

    @Data
    public static class BookingCancelInfoListResponse {
        private long cancellationId;
        private Long bookingId;
        private Long userId;
        private String cancelReasonText;
        private int penaltyAmount;
        private int refundAmount;
        private String createdAt;

        @Builder
        public BookingCancelInfoListResponse(long cancellationId, Long bookingId, Long userId, String cancelReasonText, int penaltyAmount, int refundAmount, String createdAt) {
            this.cancellationId = cancellationId;
            this.bookingId = bookingId;
            this.userId = userId;
            this.cancelReasonText = cancelReasonText;
            this.penaltyAmount = penaltyAmount;
            this.refundAmount = refundAmount;
            this.createdAt = createdAt;
        }

        public static BookingCancelInfoListResponse from(BookingCancelInfo entity) {
            return BookingCancelInfoListResponse.builder()
                    .cancellationId(entity.getCancellationId())
                    .bookingId(entity.getBookingInfo() != null ? entity.getBookingInfo().getBookingInfoId() : null)
                    .userId(entity.getUserProfile() != null ? entity.getUserProfile().getUserProfileId() : null)
                    .cancelReasonText(entity.getCancelReasonText())
                    .penaltyAmount(entity.getPenaltyAmount())
                    .refundAmount(entity.getRefundAmount())
                    .createdAt(entity.getCreatedAt().toString())
                    .build();
        }
    }
}