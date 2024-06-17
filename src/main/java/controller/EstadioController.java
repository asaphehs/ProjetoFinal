package controller;

import dto.EstadioDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import service.EstadioService;

@RestController
@RequestMapping("/estadios")
public class EstadioController {

    @Autowired
    private EstadioService estadioService;

    //11.Cadastrar um estadio
    @PostMapping
    public ResponseEntity<EstadioDTO> cadastrarEstadio(@RequestBody EstadioDTO estadioDTO) {
        EstadioDTO novoEstadio = estadioService.cadastrarEstadio(estadioDTO);
        return new ResponseEntity<>(novoEstadio, HttpStatus.CREATED);
    }

    //12.Editar um estadio
    @PutMapping("/{id}")
    public ResponseEntity<EstadioDTO> atualizarEstadio(@PathVariable Long id, @RequestBody EstadioDTO estadioDTO) {
        EstadioDTO estadioAtualizado = estadioService.atualizarEstadio(id, estadioDTO);
        return new ResponseEntity<>(estadioAtualizado, HttpStatus.OK);
    }

    //13.Buscar um estadio
    @GetMapping("/{id}")
    public ResponseEntity<EstadioDTO> buscarEstadioPorId(@PathVariable Long id) {
        EstadioDTO estadioDTO = estadioService.buscarEstadioPorId(id);
        return new ResponseEntity<>(estadioDTO, HttpStatus.OK);
    }

    //14.Listar estadios
    @GetMapping
    public ResponseEntity<Page<EstadioDTO>> listarEstadios(Pageable pageable) {
        Page<EstadioDTO> estadios = estadioService.listarEstadios(pageable);
        return new ResponseEntity<>(estadios, HttpStatus.OK);
    }
}
