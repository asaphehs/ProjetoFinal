package com.asaphe.partidasfutebol.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RetrospectoDTO {
    private int vitorias;
    private int empates;
    private int derrotas;
    private int golsFeitos;
    private int golsSofridos;
}
