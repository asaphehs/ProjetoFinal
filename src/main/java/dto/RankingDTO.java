package dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RankingDTO {
    private Long clubeId;
    private String nomeClube;
    private int jogos;
    private int vitorias;
    private int gols;
    private int pontos;
}
