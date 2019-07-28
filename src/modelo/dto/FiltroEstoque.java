package modelo.dto;

public class FiltroEstoque {
    
    private String empresa;
    private String dataInicial;
    private String dataFinal;

    public FiltroEstoque() {
    }

    public FiltroEstoque(String empresa, String dataInicial, String dataFinal) {
        this.empresa = empresa;
        this.dataInicial = dataInicial;
        this.dataFinal = dataFinal;
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

    @Override
    public String toString() {
        return "FiltroEstoque{" + "empresa=" + empresa + ", dataInicial=" + dataInicial + ", dataFinal=" + dataFinal + '}';
    }
    
}
