package modelo;

import java.util.Objects;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;

public class Produto {

    private String referencia;
    private String descricao;
    private Double preco;
    private Double precoAtacado;
    private Double qtdAtacado;
    private String codigoBarra;
    private String tributacao;
    private String unidade;
    private String grupo;
    private String subgrupo;
    private String ncm;
    private String cest;
    private String dataCancelamento;
    private Double quantidade;
    private String confirmaPreco;
    private Button button;
    private CheckBox checkBox;

    public Produto(String referencia, String descricao, Double preco, Double precoAtacado, Double qtdAtacado, String codigoBarra, String tributacao, String unidade, String ncm, String cest) {
        this.referencia = referencia;
        this.descricao = descricao;
        this.preco = preco;
        this.precoAtacado = precoAtacado;
        this.qtdAtacado = qtdAtacado;
        this.codigoBarra = codigoBarra;
        this.tributacao = tributacao;
        this.unidade = unidade;
        this.ncm = ncm;
        this.cest = cest;
    }

    public Produto(String referencia, String descricao, Double preco, Double precoAtacado, Double qtdAtacado, String codigoBarra) {
        this.referencia = referencia;
        this.descricao = descricao;
        this.preco = preco;
        this.precoAtacado = precoAtacado;
        this.qtdAtacado = qtdAtacado;
        this.codigoBarra = codigoBarra;
        button = new Button("Apagar");
        checkBox = new CheckBox();
    }

    public Produto() {
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Double getPreco() {
        return preco;
    }

    public void setPreco(Double preco) {
        this.preco = preco;
    }

    public String getCodigoBarra() {
        return codigoBarra;
    }

    public void setCodigoBarra(String codigoBarra) {
        this.codigoBarra = codigoBarra;
    }

    public Double getPrecoAtacado() {
        return precoAtacado;
    }

    public void setPrecoAtacado(Double precoAtacado) {
        this.precoAtacado = precoAtacado;
    }

    public Double getQtdAtacado() {
        return qtdAtacado;
    }

    public void setQtdAtacado(Double qtdAtacado) {
        this.qtdAtacado = qtdAtacado;
    }

    public String getTributacao() {
        return tributacao;
    }

    public void setTributacao(String tributacao) {
        this.tributacao = tributacao;
    }

    public String getUnidade() {
        return unidade;
    }

    public void setUnidade(String unidade) {
        this.unidade = unidade;
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

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.referencia);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Produto other = (Produto) obj;
        return Objects.equals(this.referencia, other.referencia);
    }

    @Override
    public String toString() {
        return "Produto{" + "referencia=" + referencia + ", descricao=" + descricao + ", preco=" + preco + ", precoAtacado=" + precoAtacado + ", qtdAtacado=" + qtdAtacado + ", codigoBarra=" + codigoBarra + ", tributacao=" + tributacao + ", unidade=" + unidade + ", grupo=" + grupo + ", subgrupo=" + subgrupo + ", ncm=" + ncm + ", cest=" + cest + ", dataCancelamento=" + dataCancelamento + ", button=" + button + ", checkBox=" + checkBox.isSelected() + '}';
    }

    public String getDataCancelamento() {
        return dataCancelamento;
    }

    public void setDataCancelamento(String dataCancelamento) {
        this.dataCancelamento = dataCancelamento;
    }

    public Button getButton() {
        return button;
    }

    public void setButton(Button button) {
        this.button = button;
    }

    public String getGrupo() {
        return grupo;
    }

    public void setGrupo(String grupo) {
        this.grupo = grupo;
    }

    public String getSubgrupo() {
        return subgrupo;
    }

    public void setSubgrupo(String subgrupo) {
        this.subgrupo = subgrupo;
    }

    public CheckBox getCheckBox() {
        return checkBox;
    }

    public void setCheckBox(CheckBox checkBox) {
        this.checkBox = checkBox;
    }

    public Double getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Double quantidade) {
        this.quantidade = quantidade;
    }

    public String getConfirmaPreco() {
        return confirmaPreco;
    }

    public void setConfirmaPreco(String confirmaPreco) {
        this.confirmaPreco = confirmaPreco;
    }

}
