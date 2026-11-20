package net.originmobi.pdv.service;

/**
 * Exceção de regra de negócio do módulo Caixa.
 *
 * Substitui o uso de {@link RuntimeException} genérica em {@link CaixaService}
 * (regra Sonar java:S112). Por ser subclasse de {@code RuntimeException}, o
 * contrato com os chamadores (controllers) e as mensagens exibidas ao usuário
 * permanecem os mesmos.
 */
public class CaixaException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public CaixaException(String mensagem) {
		super(mensagem);
	}

	public CaixaException(String mensagem, Throwable causa) {
		super(mensagem, causa);
	}
}
