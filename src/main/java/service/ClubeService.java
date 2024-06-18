package service;

import dto.*;
import jakarta.persistence.EntityNotFoundException;
import model.Clube;
import model.Partida;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import repository.ClubeRepository;
import repository.PartidaRepository;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ClubeService {

    @Autowired
    private ClubeRepository clubeRepository;

    @Autowired
    private PartidaRepository partidaRepository;

    public ClubeDTO cadastrarClube(ClubeDTO clubeDTO) {
        if (clubeRepository.findByNomeAndEstado(clubeDTO.getNome(), clubeDTO.getEstado()).isPresent()) {
            throw new IllegalStateException("Clube com o mesmo nome e Estado já existe");
        }
        Clube clube = dtoToEntity(clubeDTO);
        Clube savedClube = clubeRepository.save(clube);
        return entityToDto(savedClube);
    }

    //Conversao de DTO para entity
    private Clube dtoToEntity(ClubeDTO clubeDTO) {
        Clube clube = new Clube();
        clube.setId(clubeDTO.getId());
        clube.setNome(clubeDTO.getNome());
        clube.setEstado(clubeDTO.getEstado());
        clube.setDataCriacao(clubeDTO.getDataCriacao());
        clube.setAtivo(clubeDTO.isAtivo());
        return clube;
    }

    //Conversao de Entiy para DTO
    private ClubeDTO entityToDto(Clube clube) {
        ClubeDTO clubeDTO = new ClubeDTO();
        clubeDTO.setId(clube.getId());
        clubeDTO.setNome(clube.getNome());
        clubeDTO.setEstado(clube.getEstado());
        clubeDTO.setDataCriacao(clube.getDataCriacao());
        clubeDTO.setAtivo(clube.isAtivo());
        return clubeDTO;
    }

    //Metodo para atualizar dados
    public ClubeDTO atualizarClube(Long id, ClubeDTO clubeDTO) {
        Clube existingClube = clubeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Clube não encontrado"));
        if (!existingClube.getNome().equals(clubeDTO.getNome()) || !existingClube.getEstado().equals(clubeDTO.getEstado())) {
            if (clubeRepository.findByNomeAndEstado(clubeDTO.getNome(), clubeDTO.getEstado()).isPresent()) {
                throw new IllegalStateException("Clube com o mesmo nome e Estado já existe");
            }
        }

        existingClube.setNome(clubeDTO.getNome());
        existingClube.setEstado(clubeDTO.getEstado());
        existingClube.setDataCriacao(clubeDTO.getDataCriacao());
        existingClube.setAtivo(clubeDTO.isAtivo());
        Clube updatedClube = clubeRepository.save(existingClube);
        return entityToDto(updatedClube);
    }

    //Metodo para inativar clube
    public void inativarClube(Long id) {
        Clube clube = clubeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Clube não encontrado"));
        clube.setAtivo(false);
        clubeRepository.save(clube);
    }

    //Metodo para buscar clube por id
    public ClubeDTO buscarClubePorId(Long id) {
        Clube clube = clubeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Clube não encontrado"));
        return entityToDto(clube);
    }


    public Page<ClubeDTO> listarClubes(Pageable pageable) {
        return clubeRepository.findAll(pageable).map(this::entityToDto);
    }

    //METODOS DE BUSCAS AVANÇADAS
    //Metodo do retrospecto geral
    public RetrospectoDTO getRetrospecto(Long id) {
        Clube clube = clubeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Clube não encontrado"));

        List<Partida> partidasMandante = partidaRepository.findByClubeMandante(clube);
        List<Partida> partidasVisitante = partidaRepository.findByClubeVisitante(clube);

        int vitorias = 0, empates = 0, derrotas = 0, golsFeitos = 0, golsSofridos = 0;

        for (Partida partida : partidasMandante) {
            golsFeitos += partida.getGolsMandante();
            golsSofridos += partida.getGolsVisitante();
            if (partida.getGolsMandante() > partida.getGolsVisitante()) {
                vitorias++;
            } else if (partida.getGolsMandante() < partida.getGolsVisitante()) {
                derrotas++;

            } else {
                empates++;
            }
        }

        for (Partida partida : partidasVisitante) {
            golsFeitos += partida.getGolsVisitante();
            golsSofridos += partida.getGolsMandante();
            if (partida.getGolsVisitante() > partida.getGolsMandante()) {
                vitorias++;
            } else if (partida.getGolsVisitante() < partida.getGolsMandante()) {
                derrotas++;
            } else {
                empates++;
            }
        }
        return new RetrospectoDTO(vitorias, empates, derrotas, golsFeitos, golsSofridos);
    }


    //Metodo retrospecto clube contra seus adversarios
    public RetrospectoContraAdversarioDTO getRetrospectoContraAdversarios(Long id) {
        Clube clube = clubeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Clube não encontrado"));

        List<Partida> partidasMandante = partidaRepository.findByClubeMandante(clube);
        List<Partida> partidasVisitante = partidaRepository.findByClubeVisitante(clube);

        Map<Clube, AdversarioDTO> retrospecto = new HashMap<>();

        for (Partida partida : partidasMandante) {
            Clube adversario = partida.getClubeVisitante();
            retrospecto.computeIfAbsent(adversario, k -> new AdversarioDTO(adversario.getId(), adversario.getNome(), 0, 0, 0, 0, 0));
            AdversarioDTO adversarioDTO = retrospecto.get(adversario);
            adversarioDTO.setGolsFeitos(adversarioDTO.getGolsFeitos() + partida.getGolsMandante());
            adversarioDTO.setGolsSofridos(adversarioDTO.getGolsSofridos() + partida.getGolsVisitante());
            if (partida.getGolsMandante() > partida.getGolsVisitante()) {
                adversarioDTO.setVitorias(adversarioDTO.getVitorias() + 1);
            } else if (partida.getGolsMandante() < partida.getGolsVisitante()) {
                adversarioDTO.setDerrotas(adversarioDTO.getDerrotas() + 1);
            } else {
                adversarioDTO.setEmpates(adversarioDTO.getEmpates() + 1);
            }
        }

        for (Partida partida : partidasVisitante) {
            Clube adversario = partida.getClubeMandante();
            retrospecto.computeIfAbsent(adversario, k -> new AdversarioDTO(adversario.getId(), adversario.getNome(), 0, 0, 0, 0, 0));
            AdversarioDTO adversarioDTO = retrospecto.get(adversario);
            adversarioDTO.setGolsFeitos(adversarioDTO.getGolsFeitos() + partida.getGolsVisitante());
            adversarioDTO.setGolsSofridos(adversarioDTO.getGolsSofridos() + partida.getGolsMandante());
            if (partida.getGolsVisitante() > partida.getGolsMandante()) {
                adversarioDTO.setVitorias(adversarioDTO.getVitorias() + 1);
            } else if (partida.getGolsVisitante() < partida.getGolsMandante()) {
                adversarioDTO.setDerrotas(adversarioDTO.getDerrotas() + 1);
            } else {
                adversarioDTO.setEmpates(adversarioDTO.getEmpates() + 1);
            }
        }
        return new RetrospectoContraAdversarioDTO(retrospecto.values());
    }

    //Metodos do ranking
    public List<RankingDTO> rankearClubesPorJogos() {
        return clubeRepository.findAll().stream()
                .filter(clube -> partidaRepository.contarPartidasPorClube(clube) > 0)
                .map(this::toRankingDTO)
                .sorted(Comparator.comparingInt(RankingDTO::getJogos).reversed())
                .collect(Collectors.toList());
    }

    public List<RankingDTO> rankearClubesPorVitorias() {
        return clubeRepository.findAll().stream()
                .filter(clube -> partidaRepository.contarVitoriasPorClube(clube) > 0)
                .map(this::toRankingDTO)
                .sorted(Comparator.comparingInt(RankingDTO::getVitorias).reversed())
                .collect(Collectors.toList());
    }

    public List<RankingDTO> rankearClubesPorGols() {
        return clubeRepository.findAll().stream()
                .filter(clube -> partidaRepository.somarGolsPorClube(clube) > 0)
                .map(this::toRankingDTO)
                .sorted(Comparator.comparingInt(RankingDTO::getGols).reversed())
                .collect(Collectors.toList());
    }

    public List<RankingDTO> rankearClubesPorPontos() {
        return clubeRepository.findAll().stream()
                .filter(clube -> calcularPontosPorClube(clube) > 0)
                .map(this::toRankingDTO)
                .sorted(Comparator.comparingInt(RankingDTO::getPontos).reversed())
                .collect(Collectors.toList());
    }

    private RankingDTO toRankingDTO(Clube clube) {
        RankingDTO dto = new RankingDTO();
        dto.setClubeId(clube.getId());
        dto.setNomeClube(clube.getNome());
        dto.setJogos(partidaRepository.contarPartidasPorClube(clube));
        dto.setVitorias(partidaRepository.contarVitoriasPorClube(clube));
        dto.setGols(partidaRepository.somarGolsPorClube(clube));
        dto.setPontos(calcularPontosPorClube(clube));
        return dto;
    }

    private int calcularPontosPorClube(Clube clube) {
        List<Partida> partidas = partidaRepository.encontrarPartidasPorClube(clube);
        int pontos = 0;

        for (Partida partida : partidas) {
            if (partida.getClubeMandante().equals(clube)) {
                if (partida.getGolsMandante() > partida.getGolsVisitante()) {
                    pontos += 3; // Vitória
                } else if (partida.getGolsMandante() == partida.getGolsVisitante()) {
                    pontos += 1; // Empate
                }
            } else if (partida.getClubeVisitante().equals(clube)) {
                if (partida.getGolsVisitante() > partida.getGolsMandante()) {
                    pontos += 3; // Vitória
                } else if (partida.getGolsVisitante() == partida.getGolsMandante()) {
                    pontos += 1; // Empate
                }
            }
        }
        return pontos;
    }

}

