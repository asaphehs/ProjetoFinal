package service;

import dto.ConfrontoDiretoDTO;
import dto.PartidaDTO;
import jakarta.persistence.EntityNotFoundException;
import model.Clube;
import model.Estadio;
import model.Partida;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import repository.ClubeRepository;
import repository.EstadioRepository;
import repository.PartidaRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PartidaService {

    @Autowired
    private PartidaRepository partidaRepository;

    @Autowired
    private ClubeRepository clubeRepository;

    @Autowired
    private EstadioRepository estadioRepository;

    //Metodo cadastrar partida
    public PartidaDTO cadastrarPartida(PartidaDTO partidaDTO) {
        validarDadosPartida(partidaDTO, false);

        Partida partida = dtoToEntity(partidaDTO);
        Partida savedPartida = partidaRepository.save(partida);
        return entityToDto(savedPartida);
    }

    private PartidaDTO entityToDto(Partida partida) {
        PartidaDTO partidaDTO = new PartidaDTO();
        partidaDTO.setId(partida.getId());
        partidaDTO.setClubeMandanteId(partida.getClubeMandante().getId());
        partidaDTO.setClubeVisitanteId(partida.getClubeVisitante().getId());
        partidaDTO.setGolsMandante(partida.getGolsMandante());
        partidaDTO.setGolsVisitante(partida.getGolsVisitante());
        partidaDTO.setDataHora(partida.getDataHora());
        return partidaDTO;
    }

    private Partida dtoToEntity(PartidaDTO partidaDTO) {
        Partida partida = new Partida();
        partida.setClubeMandante(clubeRepository.findById(partidaDTO.getClubeMandanteId()).orElseThrow(() -> new EntityNotFoundException("Clube mandante não encontrado")));
        partida.setClubeVisitante(clubeRepository.findById(partidaDTO.getClubeVisitanteId()).orElseThrow(() -> new EntityNotFoundException("Clube visitante não encontrado")));
        partida.setEstadio(estadioRepository.findById(partidaDTO.getEstadioId()).orElseThrow(() -> new EntityNotFoundException("Estádio não encontrado")));
        partida.setGolsMandante(partidaDTO.getGolsMandante());
        partida.setGolsVisitante(partidaDTO.getGolsVisitante());
        partida.setDataHora(partidaDTO.getDataHora());
        return partida;
    }


    private void validarDadosPartida(PartidaDTO partidaDTO, boolean isUpdate) {
        //Verificar os clubes
        if (partidaDTO.getClubeMandanteId().equals(partidaDTO.getClubeVisitanteId())) {
            throw new IllegalArgumentException("Os clubes não podem ser iguais");
        }

        Clube clubeMandante = clubeRepository.findById(partidaDTO.getClubeMandanteId())
                .orElseThrow(() -> new EntityNotFoundException("Clube mandante não encontrado"));
        Clube clubeVisitante = clubeRepository.findById(partidaDTO.getClubeVisitanteId())
                .orElseThrow(() -> new EntityNotFoundException("Clube visitante não encontrado"));

        if (!clubeMandante.isAtivo() || !clubeVisitante.isAtivo()) {
            throw new IllegalArgumentException("Os clubes da partida não podem estar inativos");
        }

        //Verificar estadio
        Estadio estadio = estadioRepository.findById(partidaDTO.getEstadioId())
                .orElseThrow(() -> new EntityNotFoundException("Estádio não encontrado"));

        //Verificador de gols
        if (partidaDTO.getGolsMandante() < 0 || partidaDTO.getGolsVisitante() < 0) {
            throw new IllegalArgumentException("O número de gols não pode ser negativo");
        }

        //Verificador de Data e Hora
        if (partidaDTO.getDataHora().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("A partida não pode ser no passado");
        }

        if (clubeMandante.getDataCriacao().isAfter(partidaDTO.getDataHora().toLocalDate()) ||
                clubeVisitante.getDataCriacao().isAfter(partidaDTO.getDataHora().toLocalDate())) {
            throw new IllegalArgumentException("A data da partida não pode ser anterior à data de criação dos clubes");
        }

        //Verificador de partidas com horario proximo
        List<Partida> partidas = partidaRepository.findAll();
        for (Partida partida : partidas) {
            if (partida.getClubeMandante().equals(clubeMandante) || partida.getClubeMandante().equals(clubeVisitante) ||
                    partida.getClubeVisitante().equals(clubeMandante) || partida.getClubeVisitante().equals(clubeVisitante)) {
                long horasDeDiferenca = ChronoUnit.HOURS.between(partida.getDataHora(), partidaDTO.getDataHora());
                if (Math.abs(horasDeDiferenca) < 48) {
                    throw new IllegalArgumentException("Não pode ter uma partida marcada com menos de 48 horas de diferenca para os clubes envolvidos");
                }
            }
        }

        //Verificar se ja tem jogo no estadio
        for (Partida partida : partidas) {
            if (partida.getEstadio().equals(estadio) && partida.getDataHora().toLocalDate().equals(partidaDTO.getDataHora().toLocalDate())) {
                throw new IllegalArgumentException("O estádio não pode ter duas partidas no mesmo dia");
            }
        }
    }

    // Metodo para editar
    public PartidaDTO atualizarPartida(Long id, PartidaDTO partidaDTO) {
        validarDadosPartida(partidaDTO, true);

        Partida existingPartida = partidaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Partida não encontrada"));
        existingPartida.setClubeMandante(clubeRepository.findById(partidaDTO.getClubeMandanteId()).orElseThrow(() -> new EntityNotFoundException("Clube mandante não encontrado")));
        existingPartida.setClubeVisitante(clubeRepository.findById(partidaDTO.getClubeVisitanteId()).orElseThrow(() -> new EntityNotFoundException("Clube visitante não encontrado")));
        existingPartida.setEstadio(estadioRepository.findById(partidaDTO.getEstadioId()).orElseThrow(() -> new EntityNotFoundException("Estádio não encontrado")));
        existingPartida.setGolsMandante(partidaDTO.getGolsMandante());
        existingPartida.setGolsVisitante(partidaDTO.getGolsVisitante());
        existingPartida.setDataHora(partidaDTO.getDataHora());

        Partida updatedPartida = partidaRepository.save(existingPartida);
        return entityToDto(updatedPartida);
    }

    //Metodo para deletar
    public void removerPartida(Long id) {
        Partida partida = partidaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Partida não encontrada"));
        partidaRepository.delete(partida);
    }

    //Metodo para buscar partida por id
    public PartidaDTO buscarPartidaPorId(Long id) {
        Partida partida = partidaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Partida não encontrada"));
        return entityToDto(partida);
    }

    public Page<PartidaDTO> listarPartidas(Pageable pageable) {
        return partidaRepository.findAll(pageable).map(this::convertToDto);
    }

    private PartidaDTO convertToDto(Partida partida) {
        PartidaDTO dto = new PartidaDTO();
        dto.setId(partida.getId());
        dto.setClubeMandanteId(partida.getClubeMandante().getId());
        dto.setClubeVisitanteId(partida.getClubeVisitante().getId());
        dto.setEstadioId(partida.getEstadio().getId());
        dto.setGolsMandante(partida.getGolsMandante());
        dto.setGolsVisitante(partida.getGolsVisitante());
        dto.setDataHora(partida.getDataHora());
        return dto;
    }

    //BUSCAS AVANÇADAS
    //Metodo dos confrontos diretos
    public ConfrontoDiretoDTO getConfrontosDiretos(Long clube1Id, Long clube2Id, Boolean goleadas) {
        Clube clube1 = clubeRepository.findById(clube1Id)
                .orElseThrow(() -> new EntityNotFoundException("Clube 1 não encontrado"));
        Clube clube2 = clubeRepository.findById(clube2Id)
                .orElseThrow(() -> new EntityNotFoundException("Clube 2 não encontrado"));

        List<Partida> partidas = partidaRepository.findByClubeMandanteAndClubeVisitanteOrClubeVisitanteAndClubeMandante(clube1, clube2);

        if (goleadas != null && goleadas) {
            partidas = partidas.stream()
                    .filter(partida -> Math.abs(partida.getGolsMandante() - partida.getGolsVisitante()) >= 3)
                    .collect(Collectors.toList());
        }

        return calcularConfrontoDireto(partidas, clube1Id, clube2Id);
    }

    private ConfrontoDiretoDTO calcularConfrontoDireto(List<Partida> partidas, Long clube1Id, Long clube2Id) {
        int vitoriasClube1 = 0;
        int derrotasClube1 = 0;
        int empates = 0;
        int golsClube1 = 0;
        int golsClube2 = 0;

        for (Partida partida : partidas) {
            int golsMandante = partida.getGolsMandante();
            int golsVisitante = partida.getGolsVisitante();
            Long mandanteId = partida.getClubeMandante().getId();
            Long visitanteId = partida.getClubeVisitante().getId();

            if (golsMandante > golsVisitante) {
                if (mandanteId.equals(clube1Id)) {
                    vitoriasClube1++;
                } else {
                    derrotasClube1++;
                }
            } else if (golsMandante < golsVisitante) {
                if (visitanteId.equals(clube1Id)) {
                    vitoriasClube1++;
                } else {
                    derrotasClube1++;
                }
            } else {
                empates++;
            }

            golsClube1 += mandanteId.equals(clube1Id) ? golsMandante : visitanteId.equals(clube1Id) ? golsVisitante : 0;
            golsClube2 += mandanteId.equals(clube2Id) ? golsMandante : visitanteId.equals(clube2Id) ? golsVisitante : 0;
        }

        ConfrontoDiretoDTO confrontoDiretoDTO = new ConfrontoDiretoDTO();
        confrontoDiretoDTO.setPartidas(partidas);
        confrontoDiretoDTO.setVitoriasClube1(vitoriasClube1);
        confrontoDiretoDTO.setEmpates(empates);
        confrontoDiretoDTO.setDerrotasClube1(derrotasClube1);
        confrontoDiretoDTO.setGolsClube1(golsClube1);
        confrontoDiretoDTO.setGolsClube2(golsClube2);

        return confrontoDiretoDTO;
    }

    //FILTROS AVANÇADOS
    public List<Partida> encontrarPartidasComGoleada() {
        return partidaRepository.encontrarPartidasComGoleada();
    }

    public List<Partida> encontrarPartidasComoMandante(Clube clube) {
        return partidaRepository.encontrarPartidasComoMandante(clube);
    }

    public List<Partida> encontrarPartidasComoVisitante(Clube clube) {
        return partidaRepository.encontrarPartidasComoVisitante(clube);
    }
}
