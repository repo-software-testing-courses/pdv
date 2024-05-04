package net.originmobi.pdv.dtos;

import net.originmobi.pdv.enumerado.produto.ProdutoSubstTributaria;

import java.util.Date;

public class ProdutoDTO {
    private Long codprod;
    private Long codforne;
    private Long codcategoria;
    private Long codgrupo;
    private int balanca;
    private String descricao;
    private Double valorCusto;
    private Double valorVenda;
    private java.util.Date dataValidade;
    private String controleEstoque;
    private String situacao;
    private String unitario;
    private ProdutoSubstTributaria subtribu;
    private String ncm;
    private String cest;
    private Long tributacao;

    public Long getCodprod() {
        return codprod;
    }

    public void setCodprod(Long codprod) {
        this.codprod = codprod;
    }

    public Long getCodforne() {
        return codforne;
    }

    public void setCodforne(Long codforne) {
        this.codforne = codforne;
    }

    public Long getCodcategoria() {
        return codcategoria;
    }

    public void setCodcategoria(Long codcategoria) {
        this.codcategoria = codcategoria;
    }

    public Long getCodgrupo() {
        return codgrupo;
    }

    public void setCodgrupo(Long codgrupo) {
        this.codgrupo = codgrupo;
    }

    public int getBalanca() {
        return balanca;
    }

    public void setBalanca(int balanca) {
        this.balanca = balanca;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Double getValorCusto() {
        return valorCusto;
    }

    public void setValorCusto(Double valorCusto) {
        this.valorCusto = valorCusto;
    }

    public Double getValorVenda() {
        return valorVenda;
    }

    public void setValorVenda(Double valorVenda) {
        this.valorVenda = valorVenda;
    }

    public Date getDataValidade() {
        return dataValidade;
    }

    public void setDataValidade(Date dataValidade) {
        this.dataValidade = dataValidade;
    }

    public String getControleEstoque() {
        return controleEstoque;
    }

    public void setControleEstoque(String controleEstoque) {
        this.controleEstoque = controleEstoque;
    }

    public String getSituacao() {
        return situacao;
    }

    public void setSituacao(String situacao) {
        this.situacao = situacao;
    }

    public String getUnitario() {
        return unitario;
    }

    public void setUnitario(String unitario) {
        this.unitario = unitario;
    }

    public ProdutoSubstTributaria getSubtribu() {
        return subtribu;
    }

    public void setSubtribu(ProdutoSubstTributaria subtribu) {
        this.subtribu = subtribu;
    }

    public String getNcm() {
        return ncm;
    }

    public void setNcm(String ncm) {
        this.ncm = ncm;
    }

    public String getCest() {
        return cest;
    }

    public void setCest(String cest) {
        this.cest = cest;
    }

    public Long getTributacao() {
        return tributacao;
    }

    public void setTributacao(Long tributacao) {
        this.tributacao = tributacao;
    }

    public Long getModbc() {
        return modbc;
    }

    public void setModbc(Long modbc) {
        this.modbc = modbc;
    }

    public String getVendavel() {
        return vendavel;
    }

    public void setVendavel(String vendavel) {
        this.vendavel = vendavel;
    }

    private Long modbc;
    private String vendavel;

    // Add getters and setters for all fields
}