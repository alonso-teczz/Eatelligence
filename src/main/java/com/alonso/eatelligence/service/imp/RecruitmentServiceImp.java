package com.alonso.eatelligence.service.imp;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alonso.eatelligence.model.dto.EmpleadoDTO;
import com.alonso.eatelligence.model.entity.RecruitmentToken;
import com.alonso.eatelligence.model.entity.Restaurante;
import com.alonso.eatelligence.model.entity.NombreRol;
import com.alonso.eatelligence.repository.IRecruitmentRepository;
import com.alonso.eatelligence.service.IRecruitmentService;

@Service
public class RecruitmentServiceImp implements IRecruitmentService {

    @Autowired
    private IRecruitmentRepository recruitmentTokenRepository;

    @Override
    public RecruitmentToken findTokenByUsername(String username) {
        return this.recruitmentTokenRepository.findByUsername(username);
    }

    @Override
    public RecruitmentToken findTokenByUsernameAndRol(String username, NombreRol rol) {
        return this.recruitmentTokenRepository.findByUsernameAndRol(username, rol);
    }

    @Override
    public RecruitmentToken create(EmpleadoDTO empleado, Restaurante r) {
        return this.recruitmentTokenRepository.save(RecruitmentToken.builder()
            .token(UUID.randomUUID().toString())
            .username(empleado.getUsername())
            .rol(NombreRol.valueOf(empleado.getRol()))
            .restaurante(r)
            .fechaCreacion(LocalDateTime.now())
            .fechaExpiracion(LocalDateTime.now().plusMinutes(30))
            .build()
        );
    }

    @Override
    public RecruitmentToken save(RecruitmentToken recruitmentToken) {
        return this.recruitmentTokenRepository.save(recruitmentToken);
    }

    @Override
    public Optional<RecruitmentToken> findByToken(String token) {
        return this.recruitmentTokenRepository.findByToken(token);
    }

    @Override
    public void delete(RecruitmentToken recruitmentToken) {
        this.recruitmentTokenRepository.delete(recruitmentToken);
    }

    @Override
    public RecruitmentToken findValidTokenByUsername(String username) {
        return this.recruitmentTokenRepository.findByUsernameAndFechaExpiracionAfter(username, LocalDateTime.now())
            .orElse(null);
    }
}