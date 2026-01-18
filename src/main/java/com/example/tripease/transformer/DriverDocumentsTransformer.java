package com.example.tripease.transformer;

import com.example.tripease.dto.response.DriverDocumentsResponse;
import com.example.tripease.model.DriverDocuments;

/**
 * Transformer for driver documents response DTOs.
 * Centralizes all DriverDocumentsResponse construction logic.
 */
public class DriverDocumentsTransformer {

    private DriverDocumentsTransformer() {
        // Private constructor to prevent instantiation
    }

    /**
     * Convert DriverDocuments entity to response DTO with success message
     */
    public static DriverDocumentsResponse toResponse(DriverDocuments docs, String message) {
        return DriverDocumentsResponse.builder()
                .documentId(docs.getDocumentId())
                .documentStatus(docs.getDocumentStatus())
                .rejectedReason(docs.getRejectedReason())
                .message(message)
                .build();
    }

    /**
     * Build response for successful document submission
     */
    public static DriverDocumentsResponse toSubmissionSuccess(DriverDocuments docs) {
        return DriverDocumentsResponse.builder()
                .documentId(docs.getDocumentId())
                .documentStatus(docs.getDocumentStatus())
                .message("Documents submitted successfully. Verification pending.")
                .build();
    }

    /**
     * Build response for document status retrieval
     */
    public static DriverDocumentsResponse toStatusResponse(DriverDocuments docs) {
        return DriverDocumentsResponse.builder()
                .documentId(docs.getDocumentId())
                .documentStatus(docs.getDocumentStatus())
                .rejectedReason(docs.getRejectedReason())
                .message("Document status retrieved successfully")
                .build();
    }

    /**
     * Build response for document approval
     */
    public static DriverDocumentsResponse toApprovalSuccess(DriverDocuments docs) {
        return DriverDocumentsResponse.builder()
                .documentId(docs.getDocumentId())
                .documentStatus(docs.getDocumentStatus())
                .message("Documents approved successfully")
                .build();
    }

    /**
     * Build response for document rejection
     */
    public static DriverDocumentsResponse toRejectionSuccess(DriverDocuments docs, String reason) {
        return DriverDocumentsResponse.builder()
                .documentId(docs.getDocumentId())
                .documentStatus(docs.getDocumentStatus())
                .rejectedReason(reason)
                .message("Documents rejected")
                .build();
    }

    /**
     * Build response for pending documents list (validator view)
     */
    public static DriverDocumentsResponse toPendingDocumentResponse(DriverDocuments doc) {
        return DriverDocumentsResponse.builder()
                .documentId(doc.getDocumentId())
                .documentStatus(doc.getDocumentStatus())
                .driverId(doc.getDriver().getDriverId())
                .driverName(doc.getDriver().getName())
                .driverEmail(doc.getDriver().getEmailId())
                .driverLicenseNumber(doc.getDriverLicenseNumber())
                .aadhaarNumber(doc.getAadhaarNumber())
                .panCardNumber(doc.getPanCardNumber())
                .message("Pending verification")
                .build();
    }
}
