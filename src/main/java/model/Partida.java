package model;

import jakarta.persistence.*;
import lombok.Data;

@Data //evitar boilerplate
@Entity //entidade da tabela do BD
public class Partida {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_partida", nullable = false, updatable = false)
    private Long id;


}
