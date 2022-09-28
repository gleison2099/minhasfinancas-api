package com.gef.minhasFinancas.api.resource;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gef.minhasFinancas.api.dto.AtualizaStatusDTO;
import com.gef.minhasFinancas.api.dto.LancamentoDTO;
import com.gef.minhasFinancas.exception.RegraNegocioException;
import com.gef.minhasFinancas.model.entity.Lancamento;
import com.gef.minhasFinancas.model.entity.Usuario;
import com.gef.minhasFinancas.model.enuns.StatusLancamento;
import com.gef.minhasFinancas.model.enuns.TipoLancamento;
import com.gef.minhasFinancas.service.LancamentoService;
import com.gef.minhasFinancas.service.UsuarioService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/lancamentos")
@RequiredArgsConstructor
public class LancamentoResource {
	
	private final LancamentoService service;
	private final UsuarioService usuarioService;

	@PostMapping
	public ResponseEntity salvar (@RequestBody LancamentoDTO dto) {
		
		try {
			Lancamento entidade =  converter(dto);
			entidade = service.salva(entidade);
			return new ResponseEntity(entidade, HttpStatus.CREATED);
		} catch (RegraNegocioException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
	@PutMapping("{id}")
	public ResponseEntity atualizar(@PathVariable("id") Long id, @RequestBody LancamentoDTO dto) {
		
		return service.obterPorId(id).map(entity ->{
			try {
				Lancamento lancamento = converter(dto);
				lancamento.setId(entity.getId());
				service.atualizar(lancamento);
				
				return ResponseEntity.ok(lancamento);
			} catch (RegraNegocioException e) {
				return ResponseEntity.badRequest().body(e.getMessage());
			}
			
		}).orElseGet( () -> new ResponseEntity("Lançamento não encontrado na base de dados", HttpStatus.BAD_REQUEST ));
		
	}
	
	@DeleteMapping("{id}")
	public ResponseEntity deletar( @PathVariable("id") Long id) {
		
		return service.obterPorId(id).map( entidade ->{
			service.deletar(entidade);
			return new ResponseEntity(HttpStatus.NO_CONTENT);
			}).orElseGet(() ->
			    new ResponseEntity("Lançamento não encontrado na base de dados", HttpStatus.BAD_REQUEST ));
	}
	
	@GetMapping
	public ResponseEntity buscar(
			   @RequestParam(value="descricao", required = false) String descricao,
			   @RequestParam(value = "mes", required = false) Integer mes,
			   @RequestParam(value = "ano", required = false) Integer ano,
			   @RequestParam(value= "tipo", required= false)  TipoLancamento tipo,
			   @RequestParam(value= "status", required= false)  StatusLancamento status,
	           @RequestParam(value = "usuario") Long idUsuario){
		
		Lancamento lancamentoFiltro =  Lancamento
				.builder()
				.descricao(descricao)
				.mes(mes)
				.ano(ano)
				.tipo(tipo)
				.status(status)
				.build();
		
		Optional<Usuario> usuario = usuarioService.obterPorId(idUsuario);
		if (usuario.isPresent()) {
			lancamentoFiltro.setUsuario(usuario.get());
		} else {
			return ResponseEntity.badRequest().body("Usuario não encontrado para o id informado");
			
		}
		List<Lancamento> lancamentos =service.buscar(lancamentoFiltro);
		return ResponseEntity.ok(lancamentos);
	}
	
	@PutMapping("{id}/atualiza-status")
	public ResponseEntity atualizarStatus(@PathVariable("id") Long id, @RequestBody AtualizaStatusDTO dto) {
		return service.obterPorId(id).map(entity ->{
			StatusLancamento statusSelecionado = StatusLancamento.valueOf(dto.getStatus());
			
			if (statusSelecionado == null) {
				return ResponseEntity
						.badRequest()
						.body("Não foi possível atualizar o status de lançamento. Envie um status válido");
			}
			
			try {
				entity.setStatus(statusSelecionado);
				service.atualizar(entity);
				return ResponseEntity.ok(entity);
			} catch (RegraNegocioException e) {
				return ResponseEntity.badRequest().body(e.getMessage());
			}
			      
		}).orElseGet(() ->
	    new ResponseEntity("Lançamento não encontrado na base de dados", HttpStatus.BAD_REQUEST ));
	}
	
    private Lancamento converter(LancamentoDTO dto) {
    	Lancamento lancamento = new Lancamento();
    	
    	lancamento.setId(dto.getId());
     	lancamento.setDescricao(dto.getDescricao());
    	lancamento.setAno(dto.getAno());
    	lancamento.setMes(dto.getMes());
    	lancamento.setValor(dto.getValor());
    	
    	Usuario usuario = usuarioService
    	       .obterPorId(dto.getUsuario())
    	       .orElseThrow(() -> new RegraNegocioException("Usuário não encontrado para o id informado"));
    	
    	lancamento.setUsuario(usuario);
    	
    	if(dto.getTipo() != null) {
    	   lancamento.setTipo(TipoLancamento.valueOf(dto.getTipo()));
    	}
    	
    	if(dto.getStatus() != null) {
    	   lancamento.setStatus(StatusLancamento.valueOf(dto.getStatus()));
    	}
    	
     	return lancamento;
    }
}
