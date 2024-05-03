package net.originmobi.pdv.model;

import java.io.Serializable;
import jakarta.persistence.*;

import net.originmobi.pdv.enumerado.EntradaSaida;

@Entity
@Table(name = "cst_ipi")
public class CstIPI implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long codigo;

	private String cst;
	private String descricao;

	@Enumerated(EnumType.STRING)
	private EntradaSaida tipo;

	public CstIPI() {
		super();
	}

	public Long getCodigo() {
		return codigo;
	}

	public void setCodigo(Long codigo) {
		this.codigo = codigo;
	}

	public String getCst() {
		return cst;
	}

	public void setCst(String cst) {
		this.cst = cst;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public EntradaSaida getTipo() {
		return tipo;
	}

	public void setTipo(EntradaSaida tipo) {
		this.tipo = tipo;
	}

}
