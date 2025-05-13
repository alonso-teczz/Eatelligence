package com.alonso.eatelligence.service;

import com.alonso.eatelligence.model.entity.Restaurante;
import com.alonso.eatelligence.model.entity.Usuario;
import com.alonso.eatelligence.model.entity.VerificationToken;

public interface Tokenizable {
    public VerificationToken forUser(Usuario usuario, Integer intentos);
    public VerificationToken forRestaurant(Restaurante restaurante, Integer intentos);
}
