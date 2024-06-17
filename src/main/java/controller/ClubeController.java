package controller;

import dto.ClubeDTO;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import service.ClubeService;

@RestController
@RequestMapping("/api/clubes")
@Validated
public class ClubeController {

    @Autowired
    private ClubeService clubeService;

    //1.Cadastrar Clube
    @PostMapping
    public ResponseEntity<ClubeDTO> cadastrarClube(@Valid @RequestBody ClubeDTO clubeDTO) {
        try {
            return new ResponseEntity<>(clubeService.cadastrarClube(clubeDTO), HttpStatus.CREATED);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    //2.Editar Clube
    @PutMapping("/{id}")
    public ResponseEntity<ClubeDTO> atualizarClube(@PathVariable Long id, @Valid @RequestBody ClubeDTO clubeDTO) {
        try {
            return ResponseEntity.ok(clubeService.atualizarClube(id, clubeDTO));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    //3.Inativar Clube
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> inativarClube(@PathVariable Long id) {
        try {
            clubeService.inativarClube(id);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e){
            return ResponseEntity.notFound().build();
        }
    }

    //4.Buscar Clube
    @GetMapping("/{id}")
    public ResponseEntity<ClubeDTO> buscarClubePorId(@PathVariable Long id){
        try {
            return ResponseEntity.ok(clubeService.buscarClubePorId(id));
        } catch (EntityNotFoundException e){
            return ResponseEntity.notFound().build();
        }
    }

    //5. Listar Clubes
    @GetMapping
    public Page<ClubeDTO> listarClubes(Pageable pageable){
        return clubeService.listarClubes(pageable);
    }
}
