package com.example.tripease.dto.response;

import com.example.tripease.Enum.DocumentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DriverDocumentsResponse {
    private Long documentId;
    private DocumentStatus documentStatus;
    private String rejectedReason;
    private String message;
}
