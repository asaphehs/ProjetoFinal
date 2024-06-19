package com.asaphe.partidasfutebol.controller;

import com.asaphe.partidasfutebol.dto.ClubeDTO;
import com.asaphe.partidasfutebol.dto.RankingDTO;
import com.asaphe.partidasfutebol.dto.RetrospectoContraAdversarioDTO;
import com.asaphe.partidasfutebol.dto.RetrospectoDTO;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.asaphe.partidasfutebol.service.ClubeService;

import java.util.List;

@RestController
@RequestMapping("/clubes")
@Validated
public class ClubeController {

    @Autowired
    private ClubeService clubeService;

    @Autowired
    private PagedResourcesAssembler<ClubeDTO> pagedResourcesAssembler;

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
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    //4.Buscar Clube
    @GetMapping("/{id}")
    public ResponseEntity<ClubeDTO> buscarClubePorId(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(clubeService.buscarClubePorId(id));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    //5. Listar Clubes
    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<ClubeDTO>>> listarClubes(Pageable pageable) {
        Page<ClubeDTO> clubesPage = clubeService.listarClubes(pageable);
        org.springframework.hateoas.PagedModel<EntityModel<ClubeDTO>> pagedModel = pagedResourcesAssembler.toModel(clubesPage,
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ClubeController.class).listarClubes(pageable)).withSelfRel());
        return ResponseEntity.ok(pagedModel);
    }

    //BUSCAS AVANÇADAS

    //1.Retrospecto geral
    @GetMapping("/{id}/retrospecto")
    public ResponseEntity<RetrospectoDTO> getRetrospecto(@PathVariable Long id) {
        try {
            RetrospectoDTO retrospecto = clubeService.getRetrospecto(id);
            return ResponseEntity.ok(retrospecto);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    //2.Retrospecto clube contra adversarios
    @GetMapping("/{id}/retrospecto/adversarios")
    public ResponseEntity<RetrospectoContraAdversarioDTO> getRetrospectoContraAdversarios(@PathVariable Long id) {
        try {
            RetrospectoContraAdversarioDTO retrospecto = clubeService.getRetrospectoContraAdversarios(id);
            return ResponseEntity.ok(retrospecto);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    //4.Ranking a
    @GetMapping("/ranking/jogos")
    public ResponseEntity<List<RankingDTO>> rankearClubesPorJogos() {
        List<RankingDTO> ranking = clubeService.rankearClubesPorJogos();
        return ResponseEntity.ok(ranking);
    }

    //4.Ranking b
    @GetMapping("/ranking/vitorias")
    public ResponseEntity<List<RankingDTO>> rankearClubesPorVitorias() {
        List<RankingDTO> ranking = clubeService.rankearClubesPorVitorias();
        return ResponseEntity.ok(ranking);
    }

    //4.Ranking c
    @GetMapping("/ranking/gols")
    public ResponseEntity<List<RankingDTO>> rankearClubesPorGols() {
        List<RankingDTO> ranking = clubeService.rankearClubesPorGols();
        return ResponseEntity.ok(ranking);
    }

    //4.Ranking d
    @GetMapping("/ranking/pontos")
    public ResponseEntity<List<RankingDTO>> rankearClubesPorPontos() {
        List<RankingDTO> ranking = clubeService.rankearClubesPorPontos();
        return ResponseEntity.ok(ranking);
    }

}
