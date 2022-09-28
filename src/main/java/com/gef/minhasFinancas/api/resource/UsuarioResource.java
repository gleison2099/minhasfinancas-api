package com.gef.minhasFinancas.api.resource;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gef.minhasFinancas.api.dto.UsuarioDTO;
import com.gef.minhasFinancas.exception.ErroAutenticacao;
import com.gef.minhasFinancas.exception.RegraNegocioException;
import com.gef.minhasFinancas.model.entity.Usuario;
import com.gef.minhasFinancas.service.LancamentoService;
import com.gef.minhasFinancas.service.UsuarioService;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioResource {
	
	private UsuarioService service;
	private LancamentoService lancamentoService ;
	
	public UsuarioResource(UsuarioService service, LancamentoService lancamentoService) {
		this.service = service;
		this.lancamentoService = lancamentoService;
	}
	
	@PostMapping("/autenticar")
	public ResponseEntity autenticar(@RequestBody UsuarioDTO dto) {
		
		try {
			Usuario usuarioAutenticado = service.autenticar(dto.getEmail(),  dto.getSenha());
			return ResponseEntity.ok(usuarioAutenticado);
		} catch (ErroAutenticacao e) {
			
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
	@PostMapping
	public ResponseEntity salvar(@RequestBody UsuarioDTO dto) {
		
		Usuario usuario = Usuario.builder()
				.nome(dto.getNome())
				.email(dto.getEmail())
				.senha(dto.getSenha())
				.build();
		try {
			Usuario usuarioSalvo = service.salvarUsuario(usuario);
			return new ResponseEntity(usuarioSalvo, HttpStatus.CREATED);
			
		} catch (RegraNegocioException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
			
		}
		
	}
	
	@GetMapping("{id}/saldo")
	public ResponseEntity obterSaldo(@PathVariable("id") Long id) {
		
		Optional<Usuario> usuario = service.obterPorId(id);
		
		if (!usuario.isPresent()) {
			return new ResponseEntity(HttpStatus.NOT_FOUND);
		}
		
		BigDecimal saldo = lancamentoService.ObterSaldoPorUsuario(id);
		
		return ResponseEntity.ok(saldo);
	}
	

}
