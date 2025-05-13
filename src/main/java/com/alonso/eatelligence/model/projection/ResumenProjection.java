package com.alonso.eatelligence.model.projection;

import java.math.BigDecimal;

public interface ResumenProjection {
    Long getId();
    String getNombreComercial();
    Double getPrecioMedio();
    BigDecimal getImporteMinimo();
    String getCiudad();
    String getProvincia();
    Double getLatitud();
    Double getLongitud();
    Integer getTiempoPreparacion();
    String  getHorarioDia();
    Integer getAbiertoAhora();
}
