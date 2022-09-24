package com.gef.minhasFinancas.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gef.minhasFinancas.model.entity.Lancamento;

public interface LancamentoRepository extends JpaRepository<Lancamento, Long> {

}
