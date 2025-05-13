package com.alonso.eatelligence.repository;

import com.alonso.eatelligence.model.entity.OpcionMenu;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

@Repository
public interface IMenuRepository extends CrudRepository<OpcionMenu, Long> {

    @Query("""
    SELECT DISTINCT om
    FROM OpcionMenu om
    JOIN om.roles r
    JOIN r.usuarioRoles ur
    JOIN ur.usuario u
    WHERE u.username = :username
        AND om.activo = true
    ORDER BY om.seccion, om.orden
    """)
    List<OpcionMenu> findOpcionesByUsername(String username);

    Optional<OpcionMenu> findByUrl(String url);
}
