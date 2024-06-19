package com.asaphe.partidasfutebol.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.asaphe.partidasfutebol.model.Partida;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConfrontoDiretoDTO {
    private List<Partida> partidas;
    private int vitoriasClube1;
    private int empates;
    private int derrotasClube1;
    private int golsClube1;
    private int golsClube2;
}
