package service;

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


}
