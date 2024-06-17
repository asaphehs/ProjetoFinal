package service;

import dto.EstadioDTO;
import jakarta.persistence.EntityNotFoundException;
import model.Estadio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import repository.EstadioRepository;

@Service
public class EstadioService {

    @Autowired
    private EstadioRepository estadioRepository;

    //Metodo para cadastrar estadio
    public EstadioDTO cadastrarEstadio(EstadioDTO estadioDTO) {
        validarDadosEstadio(estadioDTO);

        boolean estadioExists = estadioRepository.findAll().stream()
                .anyMatch(e -> e.getNome().equalsIgnoreCase(estadioDTO.getNome()));
        if (estadioExists) {
            throw new IllegalArgumentException("Estádio já existe");
        }

        Estadio estadio = dtoToEntity(estadioDTO);
        Estadio savedEstadio = estadioRepository.save(estadio);
        return entityToDto(savedEstadio);
    }

    private EstadioDTO entityToDto(Estadio estadio) {
        EstadioDTO estadioDTO = new EstadioDTO();
        estadioDTO.setId(estadio.getId());
        estadioDTO.setNome(estadio.getNome());
        estadioDTO.setCidade(estadio.getCidade());
        return estadioDTO;
    }

    private Estadio dtoToEntity(EstadioDTO estadioDTO) {
        Estadio estadio = new Estadio();
        estadio.setNome(estadioDTO.getNome());
        estadio.setCidade(estadioDTO.getCidade());
        return estadio;
    }

    private void validarDadosEstadio(EstadioDTO estadioDTO) {
        if (estadioDTO.getNome() == null || estadioDTO.getNome().length() < 3) {
            throw new IllegalArgumentException("Nome do estádio precisa de no mínimo 3 letras");
        }
        if (estadioDTO.getCidade() == null || estadioDTO.getCidade().isEmpty()) {
            throw new IllegalArgumentException("Cidade do estádio é obrigatória");
        }

    }

    //Metodo para editar estadio
    public EstadioDTO atualizarEstadio(Long id, EstadioDTO estadioDTO) {
        validarDadosEstadio(estadioDTO);

        Estadio existingEstadio = estadioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Estádio não encontrado"));

        boolean estadioExists = estadioRepository.findAll().stream()
                .anyMatch(e -> e.getNome().equalsIgnoreCase(estadioDTO.getNome()) && !e.getId().equals(id));
        if (estadioExists) {
            throw new IllegalArgumentException("Esádio já existe");
        }

        existingEstadio.setNome(estadioDTO.getNome());
        existingEstadio.setCidade(estadioDTO.getCidade());
        Estadio updatedEstadio = estadioRepository.save(existingEstadio);
        return entityToDto(updatedEstadio);
    }

    //Metodo buscar estadio por id
    public EstadioDTO buscarEstadioPorId(Long id) {
        Estadio estadio = estadioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Estádio não encontrado"));
        return entityToDto(estadio);
    }

    public Page<EstadioDTO> listarEstadios(Pageable pageable) {
        return estadioRepository.findAll(pageable).map(this::entityToDto);
    }
}
