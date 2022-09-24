package com.gef.minhasFinancas.service;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.gef.minhasFinancas.exception.ErroAutenticacao;
import com.gef.minhasFinancas.exception.RegraNegocioException;
import com.gef.minhasFinancas.model.entity.Usuario;
import com.gef.minhasFinancas.model.repository.UsuarioRepository;
import com.gef.minhasFinancas.service.impl.UsuarioServiceImpl;

//Sobe o contexto de toda a aplicação mas estamos testando classe a classe
@SpringBootTest 
@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public class UsuarioServiceTest {

	@SpyBean
	UsuarioServiceImpl service;
	
	@MockBean
	UsuarioRepository repository;
	
	@Test
	public void deveSalvarUmUsuario() {
		
		// Cenário
		Mockito.doNothing().when(service).validarEmail(Mockito.anyString());
		Usuario usuario = Usuario.builder()
				.id(1l)
				.nome("nome")
				.email("email@email.com")
				.senha("senha")
				.build();
		
		Mockito.when(repository.save(Mockito.any(Usuario.class))).thenReturn(usuario);
		
		// Ação
		Usuario usuarioSalvo = service.salvarUsuario(new Usuario());
		
		// Verificação
		Assertions.assertThat(usuarioSalvo).isNotNull();
		Assertions.assertThat(usuarioSalvo.getId()).isEqualTo(1l);
		Assertions.assertThat(usuarioSalvo.getNome()).isEqualTo("nome");
		Assertions.assertThat(usuarioSalvo.getEmail()).isEqualTo("email@email.com");
		Assertions.assertThat(usuarioSalvo.getSenha()).isEqualTo("senha");
	}
	
	@Test(expected = RegraNegocioException.class)
	public void naoDeveSalvarUmUsuarioComEmailJaCadastrado() {
		
		// Cenário
		String email = "email@email.com";
		Usuario usuario =  Usuario.builder().email("email@email.com").build();
		Mockito.doThrow(RegraNegocioException.class).when(service).validarEmail(email);
		
		// Ação
		service.salvarUsuario(usuario);
		
		// Verificação
		Mockito.verify(repository, Mockito.never()).save(usuario);
	}
	
	@Test
	public void deveLancarErroQuandoSenhaNaoBater() {
		
		// Cenário
		String senha = "senha";
		Usuario usuario = Usuario.builder().email("email@email.com").senha(senha).build();
		Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(usuario));
		
		// Ação
		Throwable exception = Assertions.catchThrowable(()-> service.autenticar("email@email.com", "123"));
		
		// Verificação
		Assertions.assertThat(exception).isInstanceOf(ErroAutenticacao.class).hasMessage("Senha inválida");
	}
	
	@Test 
	public void deveLancarErroQuandoNaoEncontrarUsuarioComEmailInformado() {
		
		// Cenário
		Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.empty());
		
		// Ação
		Throwable exception = Assertions.catchThrowable(() -> service.autenticar("email@email.com", "senha"));
		
		// Verificação
		Assertions.assertThat(exception)
		         .isInstanceOf(ErroAutenticacao.class)
		         .hasMessage("Usuário não encontrado para o email informado");
	}
	
	// Espera que não seja lançada nenhuma exceção.
	@Test(expected = Test.None.class)
	public void deveAutenticarUmUsuarioComSucesso() {
		// Cenário
		String email = "email@email.com";
		String senha = "senha";
		
		Usuario usuario = Usuario.builder().email(email).senha(senha).id(1l).build();
		// Quando acessado o método findByEmail retornará um objeto usuario.
		Mockito.when(repository.findByEmail(email)).thenReturn(Optional.of(usuario));
		
		// Ação
		Usuario result = service.autenticar(email, senha);
		
		// Verificação
		Assertions.assertThat(result).isNotNull();
	}
	
	@Test(expected = Test.None.class)
	// Teste do método validarEmail.
	// Cenário 01 - Não deve lançar excessão se o email não existir
	public void deveValidarEmail() {
		
		// Cenário
		// Atraves do Mockito força resultado false na chamada do método existsByEmail.
		Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(false);
		
		// Acão
		service.validarEmail("email@email.com");
		
		// Verificação
		// Já está sendo feita na anotação
		
	}
	
	@Test(expected = RegraNegocioException.class)
	// Teste do método validarEmail.
	// Cenário 02 - Deve lançar a excessão RegraNegocioException se o email já estiver cadastrado.
	public void deveLancarErroAoValidarEmailQuandoExistirEmailCadastrado() {
		
		// Cenário
		// Atraves do Mockito força resultado true na chamada do método existsByEmail.
		// Retornando a exceção que informa que usuario já existe.
		Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(true);
		
		// Acão
		service.validarEmail("email@email.com");
	}
	
}
