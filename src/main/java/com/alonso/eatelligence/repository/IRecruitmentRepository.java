package com.alonso.eatelligence.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.alonso.eatelligence.model.entity.RecruitmentToken;
import com.alonso.eatelligence.model.entity.NombreRol;

@Repository
public interface IRecruitmentRepository extends JpaRepository<RecruitmentToken, Long> {
    Optional<RecruitmentToken> findByToken(String token);
    RecruitmentToken findByUsername(String username);
    RecruitmentToken findByUsernameAndRol(String username, NombreRol rol);
    Optional<RecruitmentToken> findByUsernameAndFechaExpiracionAfter(String username, LocalDateTime now);
}
