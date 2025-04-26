package com.alonso.eatelligence.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.alonso.eatelligence.model.entity.RecruitmentToken;
import com.alonso.eatelligence.model.entity.Rol.NombreRol;

public interface IRecruitmentRepository extends JpaRepository<RecruitmentToken, Long> {
    Optional<RecruitmentToken> findByToken(String token);
    RecruitmentToken findByUsername(String username);
    RecruitmentToken findByUsernameAndRole(String username, NombreRol role);
}
