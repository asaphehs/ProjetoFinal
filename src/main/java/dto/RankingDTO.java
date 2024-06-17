package dto;

import lombok.Data;

@Data
public class RankingDTO {
    private Long clubeId;
    private String nomeClube;
    private int totalJogos;
    private int vitorias;
    private int empates;
    private int derrotas;
    private int golsFeitos;
    private int golsSofridos;
    private int pontos;
}
