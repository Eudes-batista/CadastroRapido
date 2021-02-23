package modelo.dto;

public class FiltroProduto {

    private String empresa;
    private String dataInicial;
    private String dataFinal;
    private String produto;
    private String tipoDeMovimentacao;
    private String grupo;
    private String subGrupo;

    public FiltroProduto() {
    }

    public FiltroProduto(String empresa, String dataInicial, String dataFinal, String produto) {
        this.empresa = empresa;
        this.dataInicial = dataInicial;
        this.dataFinal = dataFinal;
        this.produto = produto;
    }

    public String getEmpresa() {
        return empresa;
    }

    public void setEmpresa(String empresa) {
        this.empresa = empresa;
    }

    public String getDataInicial() {
        return dataInicial;
    }

    public void setDataInicial(String dataInicial) {
        this.dataInicial = dataInicial;
    }

    public String getDataFinal() {
        return dataFinal;
    }

    public void setDataFinal(String dataFinal) {
        this.dataFinal = dataFinal;
    }

    public String getProduto() {
        return produto;
    }

    public void setProduto(String produto) {
        this.produto = produto;
    }

    public String getTipoDeMovimentacao() {
        return tipoDeMovimentacao;
    }

    public void setTipoDeMovimentacao(String tipoDeMovimentacao) {
        this.tipoDeMovimentacao = tipoDeMovimentacao;
    }

    public String getGrupo() {
        return grupo;
    }

    public void setGrupo(String grupo) {
        this.grupo = grupo;
    }

    public String getSubGrupo() {
        return subGrupo;
    }

    public void setSubGrupo(String subGrupo) {
        this.subGrupo = subGrupo;
    }

    @Override
    public String toString() {
        return "FiltroProduto{" + "empresa=" + empresa + ", dataInicial=" + dataInicial + ", dataFinal=" + dataFinal + ", produto=" + produto + ", tipoDeMovimentacao=" + tipoDeMovimentacao + ", grupo=" + grupo + ", subGrupo=" + subGrupo + '}';
    }
}
