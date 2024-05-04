package net.originmobi.pdv.service;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import net.originmobi.pdv.exceptions.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import net.originmobi.pdv.enumerado.caixa.CaixaTipo;
import net.originmobi.pdv.enumerado.caixa.EstiloLancamento;
import net.originmobi.pdv.enumerado.caixa.TipoLancamento;
import net.originmobi.pdv.filter.BancoFilter;
import net.originmobi.pdv.filter.CaixaFilter;
import net.originmobi.pdv.model.Caixa;
import net.originmobi.pdv.model.CaixaLancamento;
import net.originmobi.pdv.model.Usuario;
import net.originmobi.pdv.repository.CaixaRepository;
import net.originmobi.pdv.singleton.Aplicacao;

@Service
public class CaixaService {

	private final CaixaRepository caixas;

	private final UsuarioService usuarios;

	private final CaixaLancamentoService lancamentos;

	private static final Logger LOGGER = LoggerFactory.getLogger(CaixaService.class);

	public CaixaService(CaixaLancamentoService lancamentos, CaixaRepository caixas, UsuarioService usuarios) {
		this.lancamentos = lancamentos;
		this.caixas = caixas;
		this.usuarios = usuarios;
	}
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public Long cadastro(Caixa caixa) {
		validarCaixa(caixa);
		setDescricao(caixa);
		setInformacoesAdicionais(caixa);

		try {
			salvarCaixa(caixa);
			if (caixa.getValor_abertura() > 0) {
				realizarLancamento(caixa);
			} else {
				caixa.setValor_total(0.0);
			}
		} catch (Exception e) {
			e.getStackTrace();
			throw new ProcessoException("Erro no processo, chame o suporte");
		}

		return caixa.getCodigo();
	}

	private void validarCaixa(Caixa caixa) {
		if (caixa.getTipo().equals(CaixaTipo.CAIXA) && caixaIsAberto()) {
			throw new CaixaJaAbertoException("Existe caixa de dias anteriores em aberto, favor verificar");
		}
		if (caixa.getValor_abertura() == null || caixa.getValor_abertura() < 0) {
			throw new ValorInvalidoException("Valor informado é inválido");
		}
	}

	private void setDescricao(Caixa caixa) {
		String descricao = "";
		if (caixa.getTipo().equals(CaixaTipo.CAIXA)) {
			descricao = caixa.getDescricao().isEmpty() ? "Caixa diário" : caixa.getDescricao();
		} else if (caixa.getTipo().equals(CaixaTipo.COFRE)) {
			descricao = caixa.getDescricao().isEmpty() ? "Cofre" : caixa.getDescricao();
		} else if (caixa.getTipo().equals(CaixaTipo.BANCO)) {
			descricao = caixa.getDescricao().isEmpty() ? "Banco" : caixa.getDescricao();
			if (caixa.getTipo().equals(CaixaTipo.BANCO)) {
				LOGGER.info("agencia " + caixa.getAgencia());
				LOGGER.info("conta " + caixa.getConta());
				caixa.setAgencia(caixa.getAgencia().replaceAll("\\D", ""));
				caixa.setConta(caixa.getConta().replaceAll("\\D", ""));
			}
		}
		caixa.setDescricao(descricao);
	}

	private void setInformacoesAdicionais(Caixa caixa) {
		Aplicacao aplicacao = Aplicacao.getInstancia();
		Usuario usuario = usuarios.buscaUsuario(aplicacao.getUsuarioAtual());
		LocalDate dataAtual = LocalDate.now();
		caixa.setUsuario(usuario);
		caixa.setData_cadastro(java.sql.Date.valueOf(dataAtual));
	}

	private void salvarCaixa(Caixa caixa) {
		try {
			caixas.save(caixa);
		} catch (Exception e) {
			e.getStackTrace();
			throw new AberturaException("Erro no processo de abertura, chame o suporte técnico");
		}
	}

	private void realizarLancamento(Caixa caixa) {
		String observacao;
		if (caixa.getTipo().equals(CaixaTipo.CAIXA)) {
			observacao = "Abertura de caixa";
		} else if (caixa.getTipo().equals(CaixaTipo.COFRE)) {
			observacao = "Abertura de cofre";
		} else {
			observacao = "Abertura de banco";
		}

		try {
			CaixaLancamento lancamento = new CaixaLancamento(observacao, caixa.getValor_abertura(),
					TipoLancamento.SALDOINICIAL, EstiloLancamento.ENTRADA, caixa, caixa.getUsuario());
			lancamentos.lancamento(lancamento);
		} catch (Exception e) {
			e.getStackTrace();
			throw new ProcessoException("Erro no processo, chame o suporte");
		}
	}

	public String fechaCaixa(Long caixa, String senha) {

		Aplicacao aplicacao = Aplicacao.getInstancia();
		Usuario usuario = usuarios.buscaUsuario(aplicacao.getUsuarioAtual());

		BCryptPasswordEncoder decode = new BCryptPasswordEncoder();

		if (senha.equals(""))
			return "Favor, informe a senha";

		if (decode.matches(senha, usuario.getSenha())) {

			// busca caixa atual
			Optional<Caixa> caixaAtual = caixas.findById(caixa);

			if (caixaAtual.map(Caixa::getData_fechamento).isPresent())
				throw new CaixaJaFechadoException("Caixa já esta fechado");

			Double valorTotal = !caixaAtual.map(Caixa::getValor_total).isPresent() ? 0.0
					: caixaAtual.map(Caixa::getValor_total).get();

			Timestamp dataHoraAtual = new Timestamp(System.currentTimeMillis());
			caixaAtual.get().setData_fechamento(dataHoraAtual);
			caixaAtual.get().setValor_fechamento(valorTotal);

			try {
				caixas.save(caixaAtual.get());
			} catch (Exception e) {
				throw new FechaCaixaException("Ocorreu um erro ao fechar o caixa, chame o suporte");
			}

			return "Caixa fechado com sucesso";

		} else {
			return "Senha incorreta, favor verifique";
		}
	}

	public boolean caixaIsAberto() {
		return caixas.caixaAberto().isPresent();
	}

	public List<Caixa> listaTodos() {
		return caixas.findByCodigoOrdenado();
	}

	public List<Caixa> listarCaixas(CaixaFilter filter) {
		if (filter.getData_cadastro() != null && (!filter.getData_cadastro().equals(""))) {
				filter.setData_cadastro(filter.getData_cadastro().replace("/", "-"));
				return caixas.buscaCaixasPorDataAbertura(Date.valueOf(filter.getData_cadastro()));

		}
		
		return caixas.listaCaixasAbertos();
	}

	public Optional<Caixa> caixaAberto() {
		return caixas.caixaAberto();
	}

	public List<Caixa> caixasAbertos() {
		return caixas.caixasAbertos();
	}

	public Optional<Caixa> busca(Long codigo) {
		return caixas.findById(codigo);
	}

	// pega o caixa aberto do usuário informado
	public Optional<Caixa> buscaCaixaUsuario(String usuario) {
		Usuario usu = usuarios.buscaUsuario(usuario);
		return Optional.ofNullable(caixas.findByCaixaAbertoUsuario(usu.getCodigo()));
	}

	public List<Caixa> listaBancos() {
		return caixas.buscaBancos(CaixaTipo.BANCO);
	}

	public List<Caixa> listaCaixasAbertosTipo(CaixaTipo tipo) {
		return caixas.buscaCaixaTipo(tipo);
	}

	public List<Caixa> listaBancosAbertosTipoFilterBanco(CaixaTipo tipo, BancoFilter filter) {
		if (filter.getData_cadastro() != null && (!filter.getData_cadastro().equals(""))) {
				filter.setData_cadastro(filter.getData_cadastro().replace("/", "-"));
				return caixas.buscaCaixaTipoData(tipo, Date.valueOf(filter.getData_cadastro()));

		}

		return caixas.buscaCaixaTipo(CaixaTipo.BANCO);
	}

}
