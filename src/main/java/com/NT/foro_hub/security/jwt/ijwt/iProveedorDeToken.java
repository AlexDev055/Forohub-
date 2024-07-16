package com.good_proyects.foro_hub.security.jwt.ijwt;
import org.springframework.security.core.Authentication;

public interface iProveedorDeToken {

    String crearToken(Authentication authentication);
    Authentication obtenerAuthenticacion(String token);

    boolean validacionToken(String token);

}
