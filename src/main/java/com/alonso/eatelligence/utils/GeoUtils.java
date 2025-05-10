package com.alonso.eatelligence.utils;

public class GeoUtils {
    private static final double R = 6_371;

    /**
     * Calcula la distancia en kilómetros entre dos puntos en la superficie de la Tierra
     * especificados por sus latitudes y longitudes en grados.
     *
     * @param lat1 Latitud del primer punto en grados.
     * @param lon1 Longitud del primer punto en grados.
     * @param lat2 Latitud del segundo punto en grados.
     * @param lon2 Longitud del segundo punto en grados.
     * @return La distancia entre los dos puntos en kilómetros.
     */

    public static double haversine(
            double lat1, double lon1,
            double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2)
                 + Math.cos(Math.toRadians(lat1))
                 * Math.cos(Math.toRadians(lat2))
                 * Math.sin(dLon/2) * Math.sin(dLon/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return R * c;
    }
}