package net.originmobi.pdv.model;

import java.io.Serializable;

import jakarta.persistence.*;

/**
 * 
 * @author joao Responsável pela base de calculos do icms
 */

@Entity
@Table(name = "mod_bc_icms")
public class ModBcIcms implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long codigo;

	private int tipo;
	private String descricao;
	private int sub_tributaria;

	public ModBcIcms() {
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

	public int getSub_tributaria() {
		return sub_tributaria;
	}

	public void setSub_tributaria(int sub_tributaria) {
		this.sub_tributaria = sub_tributaria;
	}

}
