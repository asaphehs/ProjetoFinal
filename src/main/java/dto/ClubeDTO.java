package dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ClubeDTO {

    private Long id;
    private String nome;
    private String estado;
    private LocalDate dataCriacao;
    private boolean ativo;
}
