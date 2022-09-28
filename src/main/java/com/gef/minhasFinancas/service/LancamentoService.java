package com.gef.minhasFinancas.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import com.gef.minhasFinancas.model.entity.Lancamento;
import com.gef.minhasFinancas.model.enuns.StatusLancamento;

public interface LancamentoService {
	
	
	Lancamento salva(Lancamento lancamento);
	
	Lancamento atualizar(Lancamento lancamento);
	
	void deletar(Lancamento lancamento);
	
	List<Lancamento> buscar(Lancamento lancamentoFiltro);
	
	void atualizarStatus(Lancamento lancamento, StatusLancamento status);
	
	void validar(Lancamento lancamento);
	
	Optional<Lancamento> obterPorId(Long id);
	
	BigDecimal ObterSaldoPorUsuario(Long id);
	
}
