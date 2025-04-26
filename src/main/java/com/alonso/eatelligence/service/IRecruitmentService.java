package com.alonso.eatelligence.service;

import java.util.Optional;

import com.alonso.eatelligence.model.dto.EmpleadoDTO;
import com.alonso.eatelligence.model.entity.RecruitmentToken;
import com.alonso.eatelligence.model.entity.Rol.NombreRol;

public interface IRecruitmentService {
    Optional<RecruitmentToken> findByToken(String token);
    RecruitmentToken findByUsername(String username);
    RecruitmentToken findByUsernameAndRole(String username, NombreRol role);
    RecruitmentToken createToken(EmpleadoDTO empleado);
    RecruitmentToken save(RecruitmentToken recruitmentToken);
    void delete(RecruitmentToken rt);
}
