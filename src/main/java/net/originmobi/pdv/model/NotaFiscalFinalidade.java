package net.originmobi.pdv.model;

import java.io.Serializable;

import jakarta.persistence.*;


@Entity
@Table(name = "nota_fiscal_finalidade")
public class NotaFiscalFinalidade implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long codigo;
	private int tipo;
	private String descricao;

	public NotaFiscalFinalidade() {
		super();
	}

	public Long getCodigo() {
		return codigo;
	}

	public void setCodigo(Long codigo) {
		this.codigo = codigo;
	}

	public int getTipo() {
		return tipo;
	}

	public void setTipo(int tipo) {
		this.tipo = tipo;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

}
