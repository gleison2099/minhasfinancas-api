package com.gef.minhasFinancas.service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.Example;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.gef.minhasFinancas.exception.RegraNegocioException;
import com.gef.minhasFinancas.model.entity.Lancamento;
import com.gef.minhasFinancas.model.entity.Usuario;
import com.gef.minhasFinancas.model.enuns.StatusLancamento;
import com.gef.minhasFinancas.model.repository.LancamentoRepository;
import com.gef.minhasFinancas.model.repository.LancamentoRepositoryTest;
import com.gef.minhasFinancas.service.impl.LancamentoServiceImpl;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")

public class LancamentoServiceTest {
	
	@SpyBean
	LancamentoServiceImpl service;
	
	@MockBean
	LancamentoRepository repository;
	
	@Test
	public void deveSalvarUmLancamento() {
		
		// Cenário
		Usuario usuario = LancamentoRepositoryTest.criarUsuario();
		Lancamento lancamentoASalvar = LancamentoRepositoryTest.criarLancamento();
		lancamentoASalvar.setUsuario(usuario);
		
		// *** Moca o método validar ***
		Mockito.doNothing().when(service).validar(lancamentoASalvar);
		
    	// *** Simula o lançamento salvo ***
		Lancamento lancamentoSalvo = LancamentoRepositoryTest.criarLancamento();
		lancamentoSalvo.setUsuario(usuario);
		lancamentoSalvo.setId(1l);
		lancamentoSalvo.setStatus(StatusLancamento.PENDENTE);
		Mockito.when(repository.save(lancamentoASalvar)).thenReturn(lancamentoSalvo);
		
		// Ação
		Lancamento lancamento = service.salva(lancamentoASalvar);
		
		// Verifição
		Assertions.assertThat(lancamento.getId().equals(lancamentoSalvo.getId()));
		Assertions.assertThat(lancamento.getStatus().equals(StatusLancamento.PENDENTE));
		
	}
	
	@Test
	public void naoDeveSalvarUmLancamentoQuandoHouverUmErroDeValidacao() {
		
		// Cenário
		Usuario usuario = LancamentoRepositoryTest.criarUsuario();
		Lancamento lancamentoASalvar = LancamentoRepositoryTest.criarLancamento();
		lancamentoASalvar.setUsuario(usuario);
		// *** Força uma exceção ***
		Mockito.doThrow(RegraNegocioException.class).when(service).validar(lancamentoASalvar);
		
		// Ação/Verificação
		// *** Verifica se a execção é do tipo esperado ***
		Assertions.catchThrowableOfType(() -> service.salva(lancamentoASalvar), RegraNegocioException.class);
		// *** Verifica que o método save nunca foi chamado ***
		Mockito.verify(repository, Mockito.never()).save(lancamentoASalvar);
		
	}
	
	@Test
	public void deveAtualizarUmLancamento() {
		
		// Cenário
		Usuario usuario = LancamentoRepositoryTest.criarUsuario();
		Lancamento lancamentoSalvo = LancamentoRepositoryTest.criarLancamento();
		lancamentoSalvo.setUsuario(usuario);
		lancamentoSalvo.setStatus(StatusLancamento.PENDENTE);
		lancamentoSalvo.setId(1l);
					
		// *** Moca o método validar ***
		Mockito.doNothing().when(service).validar(lancamentoSalvo);
		Mockito.when(repository.save(lancamentoSalvo)).thenReturn(lancamentoSalvo);
		
		// Ação
		service.atualizar(lancamentoSalvo);
		
		// Verifição
		// *** Verifica se o repository chamou pelo menos uma vez o método save passando o lancamentoSalvo ***
	    Mockito.verify(repository, Mockito.times(1)).save(lancamentoSalvo);
		
	}
	
	@Test
	public void DeveLancarUmErroAoTentarAtualizarUmLancamentoQueAindaNaoFoiSalvo() {
		
		// Cenário
		// *** Cria um usuário sem ID ***
		Usuario usuario = LancamentoRepositoryTest.criarUsuario();
		Lancamento lancamentoASalvar = LancamentoRepositoryTest.criarLancamento();
		lancamentoASalvar.setUsuario(usuario);
				
		// Ação/Verificação
		// *** Verifica se a execção é do tipo esperado ***
		Assertions.catchThrowableOfType(() -> service.atualizar(lancamentoASalvar), NullPointerException.class);
		// *** Verifica que o método save nunca foi chamado ***
		Mockito.verify(repository, Mockito.never()).save(lancamentoASalvar);
		
	}
	
	@Test
	public void deveDeletarUmLancamento() {
		
		// Cenário
		Usuario usuario = LancamentoRepositoryTest.criarUsuario();
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setUsuario(usuario);
		lancamento.setId(1l);
		
		// Execução
		service.deletar(lancamento);
		
		// Verificação
		Mockito.verify(repository).delete(lancamento);
	}
	
	@Test
	public void DeveLancarUmErroAoTentarDeletarUmLancamentoQueAindaNaoFoiSalvo() {
		
		// Cenário
		Usuario usuario = LancamentoRepositoryTest.criarUsuario();
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setUsuario(usuario);
		
		
		// Execução
		Assertions.catchThrowableOfType(() -> service.deletar(lancamento), NullPointerException.class);;
		
		// Verificação
		Mockito.verify(repository, Mockito.never()).delete(lancamento);
		
	}
	
	@Test
	public void deveFiltarLancamentos() {
		
		// Cenário
		Usuario usuario = LancamentoRepositoryTest.criarUsuario();
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setUsuario(usuario);
		lancamento.setId(1l);
		
		List<Lancamento> lista =  Arrays.asList(lancamento);
		
		Mockito.when(repository.findAll(Mockito.any(Example.class))).thenReturn(lista);
		
		// Execução
		List<Lancamento> resultado = service.buscar(lancamento);
		
		// Verificação
		Assertions
		      .assertThat(resultado)
		      .isNotEmpty()
		      .hasSize(1)
		      .contains(lancamento);
	}
	
	@Test
	public void deveAtualizarOStatusDeUmLancamento() {
		
		// Cenário
		Usuario usuario = LancamentoRepositoryTest.criarUsuario();
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setUsuario(usuario);
		lancamento.setId(1l);
		lancamento.setStatus(StatusLancamento.PENDENTE);
		
		StatusLancamento novoStatus = StatusLancamento.EFETIVADO;
		Mockito.doReturn(lancamento).when(service).atualizar(lancamento);
		
		// Execução
		service.atualizarStatus(lancamento, novoStatus);
		
		// Verificacoes
		Assertions.assertThat(lancamento.getStatus()).isEqualTo(novoStatus);
		Mockito.verify(service).atualizar(lancamento);
	}
	
	@Test
	public void deveObterUmLancamentoPorId() {
		
		// Cenário
		Long id = 1l;
		Usuario usuario = LancamentoRepositoryTest.criarUsuario();
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setUsuario(usuario);
		lancamento.setId(id);
		
		Mockito.when(repository.findById(id)).thenReturn(Optional.of(lancamento));
		
		// Execução
		Optional<Lancamento> resultado = service.obterPorId(id);
		
		// Verificacao
		Assertions.assertThat(resultado.isPresent()).isTrue();
		
		
	}
	
	@Test
	public void deveRetornarVazioQuandoOLancamentoNaoExiste() {
		
		// Cenário
		Long id = 1l;
		Usuario usuario = LancamentoRepositoryTest.criarUsuario();
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setUsuario(usuario);
		lancamento.setId(id);
		
		Mockito.when(repository.findById(id)).thenReturn(Optional.empty());
		
		// Execução
		Optional<Lancamento> resultado = service.obterPorId(id);
		
		// Verificacao
		Assertions.assertThat(resultado.isPresent()).isFalse();
		
	}
	
	@Test
	public void deveLancarErrosAoValidarUmLancamento() {
		
		Lancamento lancamento = new Lancamento();
		
		// *** Testa descrição ***
		Throwable erro = Assertions.catchThrowable(()-> service.validar(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe uma Descrição válida");
		
		lancamento.setDescricao("Salário");
		
		// *** Testa mês ***
		erro = Assertions.catchThrowable(()-> service.validar(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Mês válido");
		
        lancamento.setMes(0);
		
		erro = Assertions.catchThrowable(()-> service.validar(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Mês válido");
		
        lancamento.setMes(13);
		
		erro = Assertions.catchThrowable(()-> service.validar(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Mês válido");
		
		lancamento.setMes(1);
		
		// *** Testa ano ***
		erro = Assertions.catchThrowable(()-> service.validar(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Ano válido");
		
		lancamento.setAno(202);
		erro = Assertions.catchThrowable(()-> service.validar(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Ano válido");
			
		lancamento.setAno(2020);
		
		// *** Testa usuário ***
		erro = Assertions.catchThrowable(()-> service.validar(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Usuário.");
		
		lancamento.setUsuario(new Usuario());
		erro = Assertions.catchThrowable(()-> service.validar(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Usuário.");
		
		lancamento.getUsuario().setId(1l);
		
		// *** Testa valor ***
		erro = Assertions.catchThrowable(()-> service.validar(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Valor válido.");
		
		lancamento.setValor(BigDecimal.ZERO);
		erro = Assertions.catchThrowable(()-> service.validar(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Valor válido.");
		
		lancamento.setValor(BigDecimal.TEN);
		erro = Assertions.catchThrowable(()-> service.validar(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um tipo de lançamento.");
		
		// *** Testa tipo lançamento ***
		erro = Assertions.catchThrowable(()-> service.validar(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um tipo de lançamento.");
		
	}
		
	
}
