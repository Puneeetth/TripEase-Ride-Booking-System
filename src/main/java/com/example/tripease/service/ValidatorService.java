package com.example.tripease.service;

import com.example.tripease.Enum.DocumentStatus;
import com.example.tripease.dto.response.DriverDocumentsResponse;
import com.example.tripease.exception.ResourceNotFoundException;
import com.example.tripease.model.DriverDocuments;
import com.example.tripease.repository.DriverDocumentsRepository;
import com.example.tripease.transformer.DriverDocumentsTransformer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ValidatorService {

        @Autowired
        private DriverDocumentsRepository driverDocumentsRepository;

        public List<DriverDocumentsResponse> getPendingDocuments() {
                return driverDocumentsRepository.findAll()
                                .stream()
                                .filter(doc -> doc.getDocumentStatus() == DocumentStatus.PENDING)
                                .map(DriverDocumentsTransformer::toPendingDocumentResponse)
                                .collect(Collectors.toList());
        }

        @Transactional
        public DriverDocumentsResponse approveDocument(int driverId) {
                DriverDocuments docs = driverDocumentsRepository.findByDriverDriverId(driverId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "No documents found for driver ID: " + driverId));

                docs.setDocumentStatus(DocumentStatus.APPROVED);
                docs.setRejectedReason(null);
                driverDocumentsRepository.save(docs);

                return DriverDocumentsTransformer.toApprovalSuccess(docs);
        }

        @Transactional
        public DriverDocumentsResponse rejectDocument(int driverId, String reason) {
                DriverDocuments docs = driverDocumentsRepository.findByDriverDriverId(driverId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "No documents found for driver ID: " + driverId));

                docs.setDocumentStatus(DocumentStatus.REJECTED);
                docs.setRejectedReason(reason);
                driverDocumentsRepository.save(docs);

                return DriverDocumentsTransformer.toRejectionSuccess(docs, reason);
        }
}
