package com.alonso.eatelligence.service.imp;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;

import com.alonso.eatelligence.model.dto.EmpleadoDTO;
import com.alonso.eatelligence.model.entity.RecruitmentToken;
import com.alonso.eatelligence.model.entity.Rol.NombreRol;
import com.alonso.eatelligence.repository.IRecruitmentRepository;
import com.alonso.eatelligence.service.IRecruitmentService;

public class RecruitmentServiceImp implements IRecruitmentService {

    @Autowired
    private IRecruitmentRepository recruitmentTokenRepository;

    @Override
    public RecruitmentToken findByUsername(String username) {
        return this.recruitmentTokenRepository.findByUsername(username);
    }

    @Override
    public RecruitmentToken findByUsernameAndRole(String username, NombreRol role) {
        return this.recruitmentTokenRepository.findByUsernameAndRole(username, role);
    }

    @Override
    public RecruitmentToken createToken(EmpleadoDTO empleado) {
        return this.recruitmentTokenRepository.save(RecruitmentToken.builder()
            .token(UUID.randomUUID().toString())
            .username(empleado.getUsername())
            .rol(empleado.getRol())
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

}