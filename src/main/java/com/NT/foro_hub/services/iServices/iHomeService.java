package com.good_proyects.foro_hub.services.iServices;
import com.good_proyects.foro_hub.models.dtos.tema.Genero;
import com.good_proyects.foro_hub.models.dtos.tema.TemaDto;

import java.time.LocalDate;
import java.util.List;

public interface iHomeService {

    List<TemaDto> findByGenero(Genero genero);
    List<TemaDto> getTemasByDate(LocalDate localDate);


}
