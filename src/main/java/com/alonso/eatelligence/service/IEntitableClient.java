package com.alonso.eatelligence.service;

import com.alonso.eatelligence.model.dto.ClienteRegistroDTO;
import com.alonso.eatelligence.model.entity.Usuario;

public interface IEntitableClient {
    public Usuario clientDTOtoEntity(ClienteRegistroDTO cliente);
}