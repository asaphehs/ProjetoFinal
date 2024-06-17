package dto;

import lombok.Data;
import model.Partida;

import java.util.ArrayList;
import java.util.List;

@Data
public class ConfrontoDiretoDTO {
    private int vitoriasClube;
    private int empates;
    private int derrotas;
    private int golsFeitosClube;
    private int golsSofridosClube;
    private int vitoriasAdversario;
    private int golsFeitosAdversario;
    private int golsSofridosAdversario;
    private List<Partida> partidas = new ArrayList<>();
}
