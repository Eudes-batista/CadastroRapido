package modelo.dto;

public class FiltroProduto {

    private String empresa;
    private String dataInicial;
    private String dataFinal;
    private String produto;
    private String tipoDeMovimentacao;

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

    @Override
    public String toString() {
        return "FiltroEstoque{" + "empresa=" + empresa + ", dataInicial=" + dataInicial + ", dataFinal=" + dataFinal + ", produto=" + produto + '}';
    }

}
