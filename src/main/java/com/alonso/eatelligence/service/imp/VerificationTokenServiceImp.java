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
    public VerificationToken forUser(Usuario usuario, Integer intentosReenvio) {
        Optional<VerificationToken> vtExistente = this.tokenRepository.findByUsuario(usuario);
    
        if (vtExistente.isPresent()) {
            VerificationToken token = vtExistente.get();
            token.setIntentosReenvio(intentosReenvio);
            token.setToken(UUID.randomUUID().toString());
            token.setFechaExpiracion(LocalDateTime.now().plusMinutes(15));
            token.setUltimoIntento(LocalDateTime.now());
            return this.tokenRepository.save(token);
        }
    
        return this.tokenRepository.save(
            VerificationToken.builder()
                .usuario(usuario)
                .token(UUID.randomUUID().toString())
                .fechaExpiracion(LocalDateTime.now().plusMinutes(15))
                .intentosReenvio(intentosReenvio)
                .ultimoIntento(LocalDateTime.now())
                .tipo(VerificationToken.TipoVerificacion.USUARIO)
                .build()
        );
    }

    @Override
    public VerificationToken forRestaurant(Restaurante restaurante, Integer intentosReenvio) {
        Optional<VerificationToken> vtExistente = this.tokenRepository.findByRestaurante(restaurante);
    
        if (vtExistente.isPresent()) {
            VerificationToken token = vtExistente.get();
            token.setIntentosReenvio(intentosReenvio);
            token.setToken(UUID.randomUUID().toString());
            token.setFechaExpiracion(LocalDateTime.now().plusHours(1));
            token.setUltimoIntento(LocalDateTime.now());
            return this.tokenRepository.save(token);
        }
    
        return this.tokenRepository.save(
            VerificationToken.builder()
                .restaurante(restaurante)
                .token(UUID.randomUUID().toString())
                .fechaExpiracion(LocalDateTime.now().plusHours(1))
                .intentosReenvio(intentosReenvio)
                .ultimoIntento(LocalDateTime.now())
                .tipo(VerificationToken.TipoVerificacion.RESTAURANTE)
                .build()
        );
    }

    public Long calcularTiempoBloqueoSegundos(Integer intentosReenvio) {
        return (long) Math.pow(2, intentosReenvio) * 5 * 60;
    }

    public String formatearTiempoEspera(Long segundos) {
        long minutos = segundos / 60;

        if (minutos < 60) {
            return minutos + (minutos == 1 ? " minuto" : " minutos");
        }

        long horas = minutos / 60;
        return horas + (horas == 1 ? " hora" : " horas");
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
