package com.asaphe.partidasfutebol.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity //Marca como entidade mapeando p table do BD
@Table(name = "clubes")
@Data//Gerar getters e stters
@NoArgsConstructor
@AllArgsConstructor
public class Clube {

    @Id //Define PK do BD
    @GeneratedValue(strategy = GenerationType.IDENTITY) //Gera auto o val do ID
    @Column(nullable = false, updatable = false)
    private Long id;

    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 2, message = "Nome deve ter pelo menos 2 caracteres")
    @Column(name = "nome_clube", length = 100, nullable = false) //A coluna não pode ser nula
    private String nome;

    @NotBlank(message = "Estado é obrigatório")
    @Size(min = 2, max = 2, message = "Sigla do Estado deve ter 2 caracteres")
    @Column(name = "estado_clube", length = 2, nullable = false)
    private String estado;

    @NotNull(message = "Data de criação é obrigatória")
    @Column(name = "data_criacao", nullable = false)
    private LocalDate dataCriacao;

    @NotNull
    @Column(name = "ativo", nullable = false)
    private boolean ativo;


}
