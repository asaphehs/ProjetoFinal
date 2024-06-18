package repository;

import model.Clube;
import model.Partida;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PartidaRepository extends JpaRepository<Partida, Long> {


    List<Partida> findByClubeMandante(Clube clubeMandante);

    List<Partida> findByClubeVisitante(Clube clubeVisitante);

    @Query("SELECT p FROM Partida p WHERE (p.clubeMandante = :clube1 AND p.clubeVisitante = :clube2) OR (p.clubeMandante = :clube2 AND p.clubeVisitante = :clube1)")
    List<Partida> findConfrontosDiretos(@Param("clube1") Clube clube1, @Param("clube2") Clube clube2);

    @Query("SELECT COUNT(p) FROM Partida p WHERE p.clubeMandante = :clube OR p.clubeVisitante = :clube")
    int contarPartidasPorClube(@Param("clube") Clube clube);

    @Query("SELECT COUNT(p) FROM Partida p WHERE (p.clubeMandante = :clube AND p.golsMandante > p.golsVisitante) OR (p.clubeVisitante = :clube AND p.golsVisitante > p.golsMandante)")
    int contarVitoriasPorClube(@Param("clube") Clube clube);

    @Query("SELECT SUM(p.golsMandante) FROM Partida p WHERE p.clubeMandante = :clube")
    Integer somarGolsMandante(@Param("clube") Clube clube);

    @Query("SELECT SUM(p.golsVisitante) FROM Partida p WHERE p.clubeVisitante = :clube")
    Integer somarGolsVisitante(@Param("clube") Clube clube);

    default int somarGolsPorClube(Clube clube) {
        Integer golsMandante = somarGolsMandante(clube);
        Integer golsVisitante = somarGolsVisitante(clube);
        return (golsMandante != null ? golsMandante : 0) + (golsVisitante != null ? golsVisitante : 0);
    }

    @Query("SELECT p FROM Partida p WHERE p.clubeMandante = :clube OR p.clubeVisitante = :clube")
    List<Partida> encontrarPartidasPorClube(@Param("clube") Clube clube);

    @Query("SELECT p FROM Partida p WHERE ABS(p.golsMandante - p.golsVisitante) >= 3")
    List<Partida> encontrarPartidasComGoleada();

    @Query("SELECT p FROM Partida p WHERE p.clubeMandante = :clube AND (p.mandante = true OR p.visitante = true)")
    List<Partida> encontrarPartidasComoMandante(@Param("clube") Clube clube);

    @Query("SELECT p FROM Partida p WHERE p.clubeVisitante = :clube AND (p.mandante = true OR p.visitante = true)")
    List<Partida> encontrarPartidasComoVisitante(@Param("clube") Clube clube);
}
