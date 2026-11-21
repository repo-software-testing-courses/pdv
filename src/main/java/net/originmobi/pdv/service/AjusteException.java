package net.originmobi.pdv.service;

public class AjusteException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public AjusteException(String mensagem) {
		super(mensagem);
	}

	public AjusteException(String mensagem, Throwable causa) {
		super(mensagem, causa);
	}
}