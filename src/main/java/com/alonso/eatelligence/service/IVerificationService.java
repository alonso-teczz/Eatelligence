package com.alonso.eatelligence.service;

import java.util.Optional;

import com.alonso.eatelligence.model.entity.VerificationToken;

public interface IVerificationService {
    Optional<VerificationToken> findByToken(String token);
    void delete(VerificationToken token);
}
