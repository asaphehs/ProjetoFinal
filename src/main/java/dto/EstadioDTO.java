package dto;

import lombok.Data;

@Data
public class EstadioDTO {

    private Long id;
    private String nome;
    private String cidade;
    private Integer capacidade;
}
