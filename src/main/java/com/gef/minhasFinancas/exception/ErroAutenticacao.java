package com.gef.minhasFinancas.exception;

@SuppressWarnings("serial")
public class ErroAutenticacao extends RuntimeException {
	
	public ErroAutenticacao(String mensagem) {
		super(mensagem);
	}

}
