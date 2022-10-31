package com.gef.minhasFinancas.api.resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gef.minhasFinancas.api.dto.UsuarioDTO;
import com.gef.minhasFinancas.exception.ErroAutenticacao;
import com.gef.minhasFinancas.exception.RegraNegocioException;
import com.gef.minhasFinancas.model.entity.Usuario;
import com.gef.minhasFinancas.service.LancamentoService;
import com.gef.minhasFinancas.service.UsuarioService;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@WebMvcTest(controllers = UsuarioResource.class)
@AutoConfigureMockMvc
public class UsuarioResourceTest {
	
	static final String API = "/api/usuarios";
	static final MediaType JSON = MediaType.APPLICATION_JSON;
	
	@Autowired
	MockMvc mvc;
	
	@MockBean
	UsuarioService service;
	
	@MockBean
	LancamentoService lancamentoService;
	
	@Test
	public void deveAutenticarUmUsuario() throws Exception {
		
		//CENÁRIO
		String email = "usuario@email.com";
		String senha = "123";
				
		// Usuário que será enviado na requisição em JSON
		UsuarioDTO dto =  UsuarioDTO.builder().email(email).senha(senha).build();
		
		// Usuário que será retorndo pelo método testado
		Usuario usuario = Usuario.builder().id(1l).email(email).senha(senha).build();
		
		// Força retorno do usuário declarado quendo o método autenticar for chamado
		Mockito.when(service.autenticar(email, senha)).thenReturn(usuario);
		
		// Transforma DTO em JSON
		String json =  new ObjectMapper().writeValueAsString(dto);
		
		// EXECUÇÃO E VERIFICAÇÃO
		// Objeto que cria uma requisição para o endereço do método testado
		// que envia e recebe um objeto JSON
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(API.concat("/autenticar"))
												                      .accept(JSON)
												                      .contentType(JSON)
												                      .content(json);
			
		// executa a requisição e as verificações
		mvc
		.perform(request)
		.andExpect(MockMvcResultMatchers.status().isOk())
		.andExpect(MockMvcResultMatchers.jsonPath("id").value(usuario.getId()))
		.andExpect(MockMvcResultMatchers.jsonPath("nome").value(usuario.getNome()))
		.andExpect(MockMvcResultMatchers.jsonPath("email").value(usuario.getEmail()));
		
		// Não é interessante retornar a senha,
		// então usa-se a notação JsonIgnore no campo senha na classe Usuario.
		

	}
	
	@Test
	public void deveRetornarBadRequestAoObterErroDeAutenticacao() throws Exception {
		
		//CENÁRIO
		String email = "usuario@email.com";
		String senha = "123";
				
		// Usuário que será enviado na requisição em JSON
		UsuarioDTO dto =  UsuarioDTO.builder().email(email).senha(senha).build();
		
		// Força retorno de erro ao tentar autenticar um usuário
		Mockito.when(service.autenticar(email, senha)).thenThrow(ErroAutenticacao.class);
		
		// Transforma DTO em JSON
		String json =  new ObjectMapper().writeValueAsString(dto);
		
		// EXECUÇÃO E VERIFICAÇÃO
		// Objeto que cria uma requisição para o endereço do método testado
		// que envia e recebe um objeto JSON
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(API.concat("/autenticar"))
												                      .accept(JSON)
												                      .contentType(JSON)
												                      .content(json);
			
		// executa a requisição e as verificações
		mvc
		.perform(request)
		.andExpect(MockMvcResultMatchers.status().isBadRequest());
		
	}
	
	@Test
	public void deveCriarUmNovoUsuario() throws Exception {
		
		//CENÁRIO
		String email = "usuario@email.com";
		String senha = "123";
				
		// Usuário que será enviado na requisição em JSON
		UsuarioDTO dto =  UsuarioDTO.builder().email(email).senha(senha).build();
		
		// Usuário que será retorndo pelo método testado
		Usuario usuario = Usuario.builder().id(1l).email(email).senha(senha).build();
		
		// Força retorno do usuário declarado quendo o método autenticar for chamado
		Mockito.when(service.salvarUsuario(Mockito.any(Usuario.class))).thenReturn(usuario);
		
		// Transforma DTO em JSON
		String json =  new ObjectMapper().writeValueAsString(dto);
		
		// EXECUÇÃO E VERIFICAÇÃO
		// Objeto que cria uma requisição para o endereço do método testado
		// que envia e recebe um objeto JSON
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(API)
												                      .accept(JSON)
												                      .contentType(JSON)
												                      .content(json);
			
		// executa a requisição e as verificações
		mvc
		.perform(request)
		.andExpect(MockMvcResultMatchers.status().isCreated())
		.andExpect(MockMvcResultMatchers.jsonPath("id").value(usuario.getId()))
		.andExpect(MockMvcResultMatchers.jsonPath("nome").value(usuario.getNome()))
		.andExpect(MockMvcResultMatchers.jsonPath("email").value(usuario.getEmail()));
	}
	
	@Test
	public void deveRetornarBadRequestAoTentarCriarUmUsuario() throws Exception {
		
		//CENÁRIO
		String email = "usuario@email.com";
		String senha = "123";
				
		// Usuário que será enviado na requisição em JSON
		UsuarioDTO dto =  UsuarioDTO.builder().email(email).senha(senha).build();
		
				
		// Força retorno do usuário declarado quendo o método autenticar for chamado
		Mockito.when(service.salvarUsuario(Mockito.any(Usuario.class))).thenThrow(RegraNegocioException.class);
		
		// Transforma DTO em JSON
		String json =  new ObjectMapper().writeValueAsString(dto);
		
		// EXECUÇÃO E VERIFICAÇÃO
		// Objeto que cria uma requisição para o endereço do método testado
		// que envia e recebe um objeto JSON
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(API)
												                      .accept(JSON)
												                      .contentType(JSON)
												                      .content(json);
			
		// executa a requisição e as verificações
		mvc
		.perform(request)
		.andExpect(MockMvcResultMatchers.status().isBadRequest());
		
	}


}
