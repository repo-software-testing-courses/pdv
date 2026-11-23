package net.originmobi.pdv.service;

/**
 * Exceção de regra de negócio do módulo Venda.
 *
 * Substitui o uso de {@link RuntimeException} genérica em {@link VendaService}
 * (regra Sonar java:S112). Por ser subclasse de {@code RuntimeException}, o
 * contrato com os chamadores (controllers), o rollback da transação e as
 * mensagens exibidas ao usuário permanecem os mesmos.
 */
public class VendaException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public VendaException() {
		super();
	}

	public VendaException(String mensagem) {
		super(mensagem);
	}
}
