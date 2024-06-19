package com.asaphe.partidasfutebol.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data //evitar boilerplate
@NoArgsConstructor
@Entity //entidade da tabela do BD
public class Partida {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "clube_mandante_id")
    @NotNull
    private Clube clubeMandante;

    @ManyToOne
    @JoinColumn(name = "clube_visitante_id")
    @NotNull
    private Clube clubeVisitante;

    @NotNull
    private int golsMandante;

    @NotNull
    private int golsVisitante;

    @ManyToOne
    @JoinColumn(name = "estadio_id")
    @NotNull
    private Estadio estadio;

    @NotNull
    private LocalDateTime dataHora;
}
