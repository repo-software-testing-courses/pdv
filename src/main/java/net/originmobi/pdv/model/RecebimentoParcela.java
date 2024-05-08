package net.originmobi.pdv.model;

import java.io.Serializable;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "recebimento_parcelas")
public class RecebimentoParcela implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long codigo;

	@NotNull
	@Column(name = "recebimento_cod")
	private Long recebimento;

	@NotNull
	@Column(name = "parcela_cod")
	private Long parcela;

	public RecebimentoParcela() {
		super();
	}

	public RecebimentoParcela(@NotNull Long recebimento, @NotNull Long parcela) {
		super();
		this.recebimento = recebimento;
		this.parcela = parcela;
	}

	public Long getCodigo() {
		return codigo;
	}

	public void setCodigo(Long codigo) {
		this.codigo = codigo;
	}

	public Long getRecebimento() {
		return recebimento;
	}

	public void setRecebimento(Long recebimento) {
		this.recebimento = recebimento;
	}

	public Long getParcela() {
		return parcela;
	}

	public void setParcela(Long parcela) {
		this.parcela = parcela;
	}

}
