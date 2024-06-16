package service;

import dto.ClubeDTO;
import jakarta.persistence.EntityNotFoundException;
import model.Clube;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import repository.ClubeRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClubeService {

    @Autowired
    private ClubeRepository clubeRepository;

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

    public List<ClubeDTO> listarClubes() {
        return clubeRepository.findAll()
                .stream()
                .map(this::entityToDto)
                .collect(Collectors.toList());
    }
}
