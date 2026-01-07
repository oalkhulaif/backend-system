package com.assessment.tasks.service;

import com.assessment.tasks.exception.IdempotencyConflictException;
import com.assessment.tasks.model.IdempotencyRecord;
import com.assessment.tasks.repository.IdempotencyRecordRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.util.Base64;
import java.util.Optional;
import java.util.TreeMap;

@Service
public class IdempotencyService {

    private final IdempotencyRecordRepository repository;
    private final ObjectMapper objectMapper;

    public IdempotencyService(IdempotencyRecordRepository repository, ObjectMapper objectMapper) {
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    public String hashPayload(Object payload) {
        try {
            TreeMap<String, Object> sorted = objectMapper.convertValue(payload, TreeMap.class);
            String json = objectMapper.writeValueAsString(sorted);
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(json.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("Failed", e);
        }
    }

    public Optional<IdempotencyRecord> find(String key) {
        return repository.findById(key);
    }

    public void validate(String key, String payloadHash) {
        Optional<IdempotencyRecord> existing = find(key);
        if (existing.isPresent() && !existing.get().getPayloadHash().equals(payloadHash)) {
            throw new IdempotencyConflictException(key);
        }
    }

    public void save(String key, String payloadHash, String response) {
        IdempotencyRecord record = new IdempotencyRecord();
        record.setIdempotencyKey(key);
        record.setPayloadHash(payloadHash);
        record.setResponse(response);
        repository.save(record);
    }
}