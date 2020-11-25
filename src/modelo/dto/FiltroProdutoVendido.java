package modelo.dto;

public class FiltroProdutoVendido {
    
    private String referencia;
    private String dataInicial;
    private String dataFinal;
    private String vendedor;
    private String caixa;
    private String periodo;
    private String informacaoCaixa;

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
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

    public String getVendedor() {
        return vendedor;
    }

    public void setVendedor(String vendedor) {
        this.vendedor = vendedor;
    }

    public String getCaixa() {
        return caixa;
    }

    public void setCaixa(String caixa) {
        this.caixa = caixa;
    }

    public String getPeriodo() {
        return periodo;
    }

    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }

    public String getInformacaoCaixa() {
        return informacaoCaixa;
    }

    public void setInformacaoCaixa(String informacaoCaixa) {
        this.informacaoCaixa = informacaoCaixa;
    }
    
    
}
