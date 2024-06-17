package dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PartidaDTO {

    private Long id;
    private Long clubeMandanteId;
    private Long clubeVisitanteId;
    private int golsMandante;
    private int golsVisitante;
    private Long estadioId;
    private LocalDateTime dataHora;
}
