package net.originmobi.pdv.model;

import java.io.Serializable;
import java.util.List;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "grupousuario")
public class GrupoUsuario implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long codigo;

	@NotBlank(message = "Nome não pode ser em branco")
	@Size(min = 4, max = 45, message = "Tamanho minio de quatro caracteres")
	private String nome;

	@NotBlank(message = "Descrição não pode ser em branco")
	@Size(min = 4, max = 45, message = "Tamanho minio de quatro caracteres")
	private String descricao;

	@ManyToMany(cascade = CascadeType.MERGE)
	@JoinTable(name = "usuario_grupousuario")
	private List<Usuario> usuario;

	@ManyToMany
	@JoinTable(name = "permissoes_grupo_usuario")
	private List<Permissoes> permissoes;

	public GrupoUsuario() {
	}

	public GrupoUsuario(String nome, String descricao, List<Usuario> usuario, List<Permissoes> permissoes) {
		super();
		this.nome = nome;
		this.descricao = descricao;
		this.usuario = usuario;
		this.permissoes = permissoes;
	}

	public Long getCodigo() {
		return codigo;
	}

	public void setCodigo(Long codigo) {
		this.codigo = codigo;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public List<Usuario> getUsuario() {
		return usuario;
	}

	public void setUsuario(List<Usuario> usuario) {
		this.usuario = usuario;
	}

	public List<Permissoes> getPermissoes() {
		return permissoes;
	}

	public void setPermissoes(List<Permissoes> permissoes) {
		this.permissoes = permissoes;
	}

}
