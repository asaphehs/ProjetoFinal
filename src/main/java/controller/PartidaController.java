package controller;

import dto.ConfrontoDiretoDTO;
import dto.PartidaDTO;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import service.PartidaService;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/partidas")
public class PartidaController {

    @Autowired
    private PartidaService partidaService;

    //6.Cadastrar uma partida
    @PostMapping
    public ResponseEntity<PartidaDTO> cadastrarPartida(@Valid @RequestBody PartidaDTO partidaDTO) {
        PartidaDTO novaPartida = partidaService.cadastrarPartida(partidaDTO);
        return ResponseEntity.status(201).body(novaPartida);
    }

    //7.Editar uma partida
    @PutMapping("/{id}")
    public ResponseEntity<PartidaDTO> atualizarPartida(@PathVariable Long id, @Valid @RequestBody PartidaDTO partidaDTO) {
        PartidaDTO partidaAtualizada = partidaService.atualizarPartida(id, partidaDTO);
        return ResponseEntity.ok(partidaAtualizada);
    }

    //8.Remover uma partida
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removerPartida(@PathVariable Long id) {
        partidaService.removerPartida(id);
        return ResponseEntity.noContent().build();
    }

    //9.Buscar uma partida
    @GetMapping("/{id}")
    public ResponseEntity<PartidaDTO> buscarPartidaPorId(@PathVariable Long id) {
        PartidaDTO partida = partidaService.buscarPartidaPorId(id);
        return ResponseEntity.ok(partida);
    }

    //10.Listar partidas
    @GetMapping
    public Page<PartidaDTO> listarPartidas(Pageable pageable) {
        return partidaService.listarPartidas(pageable);
    }

    //BUSCAS AVANÇADAS
    //3.Confrontos diretos
    @GetMapping("/confrontos/{clube1Id}/{clube2Id}")
    public ResponseEntity<ConfrontoDiretoDTO> getConfrontosdiretos(
            @PathVariable Long clube1Id, @PathVariable Long clube2Id,
            @RequestParam(required = false) Boolean goleadas) {

        try {
            ConfrontoDiretoDTO confrontoDireto = partidaService.getConfrontosDiretos(clube1Id, clube2Id);

            if (goleadas != null && goleadas) {
                confrontoDireto.setPartidas(
                        confrontoDireto.getPartidas().stream()
                                .filter(partida -> Math.abs(partida.getGolsMandante() - partida.getGolsVisitante()) >= 3)
                                .collect(Collectors.toList()));
            }

            return ResponseEntity.ok(confrontoDireto);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
