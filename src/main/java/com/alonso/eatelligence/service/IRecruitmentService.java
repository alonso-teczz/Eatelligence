package com.alonso.eatelligence.service;

import java.util.Optional;

import com.alonso.eatelligence.model.dto.EmpleadoDTO;
import com.alonso.eatelligence.model.entity.RecruitmentToken;
import com.alonso.eatelligence.model.entity.Restaurante;
import com.alonso.eatelligence.model.entity.NombreRol;

public interface IRecruitmentService {
    Optional<RecruitmentToken> findByToken(String token);
    RecruitmentToken findTokenByUsername(String username);
    RecruitmentToken findTokenByUsernameAndRol(String username, NombreRol rol);
    RecruitmentToken findValidTokenByUsername(String username);
    RecruitmentToken create(EmpleadoDTO empleado, Restaurante r);
    RecruitmentToken save(RecruitmentToken recruitmentToken);
    void delete(RecruitmentToken rt);
}
