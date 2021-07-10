package modelo.dto;

public class FiltroDevolucao {
    
    private String dataInicial;
    private String dataFinal;
    private String vendedora;

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

    public String getVendedora() {
        return vendedora;
    }

    public void setVendedora(String vendedora) {
        this.vendedora = vendedora;
    }

    @Override
    public String toString() {
        return "FiltroDevolucao{" + "dataInicial=" + dataInicial + ", dataFinal=" + dataFinal + ", vendedora=" + vendedora + '}';
    }
   
   
    
}
