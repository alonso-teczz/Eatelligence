package com.alonso.eatelligence.service.imp;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alonso.eatelligence.model.entity.Restaurante;
import com.alonso.eatelligence.model.entity.Usuario;
import com.alonso.eatelligence.model.entity.VerificationToken;
import com.alonso.eatelligence.repository.IVerificationTokenRepository;
import com.alonso.eatelligence.service.IVerificationService;
import com.alonso.eatelligence.service.Tokenizable;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VerificationTokenServiceImp implements IVerificationService, Tokenizable {

    @Autowired
    private final IVerificationTokenRepository tokenRepository;
    
    @Override
    public VerificationToken forUser(Usuario usuario) {
        return this.tokenRepository.save(
            VerificationToken.builder()
                .token(UUID.randomUUID().toString())
                .fechaExpiracion(LocalDateTime.now().plusMinutes(15))
                .tipo(VerificationToken.VerificationType.USUARIO)
                .usuario(usuario)
                .build()
        );
    }

    @Override
    public VerificationToken forRestaurant(Restaurante restaurante) {
        return this.tokenRepository.save(
            VerificationToken.builder()
                .token(UUID.randomUUID().toString())
                .fechaExpiracion(LocalDateTime.now().plusHours(1))
                .tipo(VerificationToken.VerificationType.RESTAURANTE)
                .restaurante(restaurante)
                .build()
        );
    }

    @Override
    public Optional<VerificationToken> findByToken(String token) {
        return this.tokenRepository.findByToken(token);
    }
    
    public VerificationToken save(VerificationToken token) {
        return this.tokenRepository.save(token);
    }

    public void delete(VerificationToken token) {
        this.tokenRepository.delete(token);
    }
}
