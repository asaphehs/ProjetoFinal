package com.asaphe.partidasfutebol.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PartidaDTO {

    private Long id;
    @NotNull
    private Long clubeMandanteId;
    @NotNull
    private Long clubeVisitanteId;
    @NotNull
    private int golsMandante;
    @NotNull
    private int golsVisitante;
    @NotNull
    private Long estadioId;
    @NotNull
    private LocalDateTime dataHora;
}
