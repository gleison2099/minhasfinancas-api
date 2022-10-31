package com.gef.minhasFinancas.model.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.gef.minhasFinancas.model.entity.Lancamento;
import com.gef.minhasFinancas.model.entity.Usuario;
import com.gef.minhasFinancas.model.enuns.StatusLancamento;
import com.gef.minhasFinancas.model.enuns.TipoLancamento;


@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@ActiveProfiles("test")
public class LancamentoRepositoryTest {
	
	@Autowired
	LancamentoRepository repository;
	
	@Autowired
	TestEntityManager entityManager;
	
	@Test
	public void deveSalvarUmLancamento() {
		
		// Cenário
		Usuario usuario = criarUsuario();
		entityManager.persist(usuario);
		
		Lancamento lancamento = criarLancamento();
		lancamento.setUsuario(usuario);
			
		// Ação
		lancamento = repository.save(lancamento);
		
		// Verificação
		Assertions.assertThat(lancamento.getId()).isNotNull();
	}
	
	@Test
	public void deveDeletarUmLancamento() {
		
		// Cenário
		Usuario usuario = criarUsuario();
		entityManager.persist(usuario);
		
		Lancamento lancamento = criarLancamento();
		lancamento.setUsuario(usuario);
		lancamento = repository.save(lancamento);
		
		lancamento = entityManager.find(Lancamento.class, lancamento.getId());
		
		// Ação
		repository.delete(lancamento);
		
		// Verificação
		Lancamento lancamentoInexistente = entityManager.find(Lancamento.class, lancamento.getId());
		Assertions.assertThat(lancamentoInexistente).isNull();
		
	}
	
	@Test
	public void deveAtualizarUmLancamento() {
		
		// Cenário
		Usuario usuario = criarUsuario();
		entityManager.persist(usuario);
		
		Lancamento lancamento = criarLancamento();
		lancamento.setUsuario(usuario);
		lancamento = repository.save(lancamento);
		
		// Ação
		lancamento.setAno(2018);
		lancamento.setDescricao("Teste de atualizar");
		lancamento.setStatus(StatusLancamento.CANCELADO);
		repository.save(lancamento);
		
		// Verificação
		Lancamento lancamentoAtualizado = entityManager.find(Lancamento.class,lancamento.getId());
		Assertions.assertThat(lancamentoAtualizado.getAno().equals(2018));
		Assertions.assertThat(lancamentoAtualizado.getDescricao().equals("Teste de atualizar"));
		Assertions.assertThat(lancamentoAtualizado.getStatus().equals(StatusLancamento.CANCELADO));
		
	}
	
	@Test
	public void deveBuscarUmLancamentoPorId() {
	
		// Cenário
		Usuario usuario = criarUsuario();
		entityManager.persist(usuario);
		
		Lancamento lancamento = criarLancamento();
		lancamento.setUsuario(usuario);
		lancamento = repository.save(lancamento);
		
		// Ação
		Optional<Lancamento> lancamentoEncontrado = repository.findById(lancamento.getId());
		
		// Verificação
		Assertions.assertThat(lancamentoEncontrado.isPresent()).isTrue();
		
	}
	
	
	
	
	public static Lancamento criarLancamento() {

		Lancamento lancamento = Lancamento.builder()
				.ano(2022)
				.mes(1)
				.descricao("Salvando um lançamento no teste de integração")
				.valor(BigDecimal.valueOf(10))
				.tipo(TipoLancamento.RECEITA)
				.status(StatusLancamento.PENDENTE)
				.dataCadastro(LocalDate.now())
				.build();
		
		return lancamento;
	}
	
	public static Usuario criarUsuario() {
		return  Usuario 
					.builder()
					.nome("usuario")
					.email("usuario@email.com")
					.senha("senha")
					.build();	
	}

}
