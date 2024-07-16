package com.good_proyects.foro_hub.services;
import com.good_proyects.foro_hub.exceptions.BadRequestExcepton;
import com.good_proyects.foro_hub.exceptions.ResourceNotFoundException;
import com.good_proyects.foro_hub.models.Respuesta;
import com.good_proyects.foro_hub.models.Tema;
import com.good_proyects.foro_hub.models.Usuario;
import com.good_proyects.foro_hub.models.dtos.respuesta.RespuestaDTO;
import com.good_proyects.foro_hub.repository.iRespuestaRepository;
import com.good_proyects.foro_hub.repository.iTemaRepository;
import com.good_proyects.foro_hub.repository.iUsuarioRepository;
import com.good_proyects.foro_hub.services.iServices.iRespuestaService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class RespuestaService implements iRespuestaService {

    private final iRespuestaRepository respuestaRepository;
    private final iUsuarioRepository usuarioRepository;
    private final iTemaRepository temaRepository;

    @Override
    public List<RespuestaDTO> findAll() {
        List<Respuesta> respuesta = respuestaRepository.findAll();
        return respuesta.stream()
                .map(this::manejoRespuesta)
                .collect(Collectors.toList());
    }

    @Override
    public Page<RespuestaDTO> paginate(Pageable pageable) {
        Page<Respuesta> respuesta = respuestaRepository.findAll(pageable);
        return respuesta.map(this::manejoRespuesta);
    }

    @Override
    public RespuestaDTO findById(Integer id) {
        Respuesta respuesta = respuestaRepository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("ERROR ID: Respuesta no encontrada!"));
    return manejoRespuesta(respuesta);
    }

    @Override
    public RespuestaDTO save(RespuestaDTO respuestaDTO) {


        if (respuestaDTO.getUsuarioId() == null || respuestaDTO.getTemaId() == null) {
            throw new IllegalArgumentException("Usuario ID and Tema ID must not be null");
        }

        // Verificar si ya existe una respuesta con el mismo mensaje para el mismo tema
        boolean existeRespuesta = respuestaRepository.existsByMensajeRespuestaAndTemaIdId(respuestaDTO.getMensajeRespuesta(), respuestaDTO.getTemaId());
        if (existeRespuesta) {
            throw new BadRequestExcepton("La respuesta ya existe para este tema");
        }

        Usuario usuario =  usuarioRepository.findById(respuestaDTO.getUsuarioId())
                .orElseThrow(() -> new ResourceNotFoundException("ERROR ID: usuario id no encontrado!"));

        Tema tema = temaRepository.findById(respuestaDTO.getTemaId())
                .orElseThrow(() -> new ResourceNotFoundException("ERROR ID: tema id no encontrado!"));

        Respuesta respuesta = new Respuesta();
        respuesta.setMensajeRespuesta(respuestaDTO.getMensajeRespuesta());
        respuesta.setTemaId(tema);
        respuesta.setUsuarioId(usuario);
        respuesta.setActivo(Boolean.TRUE);
        respuesta.setCreatedAt(LocalDateTime.now());

        Respuesta savedRespuesta = respuestaRepository.save(respuesta);
        return manejoRespuesta(savedRespuesta);
    }


    @Override
    public RespuestaDTO update(Integer id, RespuestaDTO respuestaDTO) {
        Respuesta respuesta = respuestaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Respuesta no encontrado con ID: " + id));

        boolean existeRespuesta = respuestaRepository.existsByMensajeRespuestaAndTemaIdIdAndIdNot(respuestaDTO.getMensajeRespuesta(), respuestaDTO.getTemaId(), id);

        if (existeRespuesta) {
            throw new BadRequestExcepton("La respuesta ya existe para este tema");
        }

        Usuario usuario =  usuarioRepository.findById(respuestaDTO.getUsuarioId())
                .orElseThrow(() -> new ResourceNotFoundException("ERROR ID: usuario id no encontrado!"));

        Tema tema = temaRepository.findById(respuestaDTO.getTemaId())
                .orElseThrow(() -> new ResourceNotFoundException("ERROR ID: tema id no encontrado!"));

        respuesta.setMensajeRespuesta(respuestaDTO.getMensajeRespuesta());
        respuesta.setUpdatedAt(LocalDateTime.now());

        Respuesta savedRespuesta = respuestaRepository.save(respuesta);
        return manejoRespuesta(savedRespuesta);
    }

    @Override
    public Boolean delete(Integer id) {
        respuestaRepository.deleteById(id);
        return true;
    }

    public RespuestaDTO manejoRespuesta(Respuesta respuesta){
        RespuestaDTO respuestaDTO = new ModelMapper().map(respuesta, RespuestaDTO.class);
        //RespuestaDTO respuestaDTO = new RespuestaDTO();
//        respuestaDTO.setId(respuesta.getId());
//        respuestaDTO.setMensajeRespuesta(respuesta.getMensajeRespuesta());
//        respuestaDTO.setTemaId((respuesta.getTemaId().getId()));
//        respuestaDTO.setUsuarioId(respuesta.getUsuarioId().getId());
//        respuestaDTO.setActivo(respuesta.getActivo());
//        respuestaDTO.setCreatedAt(respuesta.getCreatedAt());
//        respuestaDTO.setUpdatedAt(respuesta.getUpdatedAt());
        return  respuestaDTO;
    }

}
