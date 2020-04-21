package modelo;

import java.util.Date;
import java.util.Objects;


public class Correntista {

    private String codigoCliente;
    private Date dataLancamento;
    private Date dataDeProcesso;
    private String descricao;
    private String tipoMovimento;
    private String usuario;
    private double credito;
    private double debito;
    private double saldoAnterior;
    private double valor;

    public Correntista() {
    }

    public Correntista(String codigoCliente, Date dataLancamento, Date dataDeProcesso) {
        this.codigoCliente = codigoCliente;
        this.dataLancamento = dataLancamento;
        this.dataDeProcesso = dataDeProcesso;
    }
    
    public String getCodigoCliente() {
        return codigoCliente;
    }

    public void setCodigoCliente(String codigoCliente) {
        this.codigoCliente = codigoCliente;
    }

    public Date getDataLancamento() {
        return dataLancamento;
    }

    public void setDataLancamento(Date dataLancamento) {
        this.dataLancamento = dataLancamento;
    }

    public Date getDataDeProcesso() {
        return dataDeProcesso;
    }

    public void setDataDeProcesso(Date dataDeProcesso) {
        this.dataDeProcesso = dataDeProcesso;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getTipoMovimento() {
        return tipoMovimento;
    }

    public void setTipoMovimento(String tipoMovimento) {
        this.tipoMovimento = tipoMovimento;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public double getCredito() {
        return credito;
    }

    public void setCredito(double credito) {
        this.credito = credito;
    }

    public double getDebito() {
        return debito;
    }

    public void setDebito(double debito) {
        this.debito = debito;
    }

    public double getSaldoAnterior() {
        return saldoAnterior;
    }

    public void setSaldoAnterior(double saldoAnterior) {
        this.saldoAnterior = saldoAnterior;
    }

    public double getValor() {
        this.valor = this.credito+this.debito;
        return this.valor;
    }
    
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + Objects.hashCode(this.codigoCliente);
        hash = 97 * hash + Objects.hashCode(this.dataLancamento);
        hash = 97 * hash + Objects.hashCode(this.dataDeProcesso);
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
        final Correntista other = (Correntista) obj;
        if (!Objects.equals(this.codigoCliente, other.codigoCliente)) {
            return false;
        }
        if (!Objects.equals(this.dataLancamento, other.dataLancamento)) {
            return false;
        }
        return Objects.equals(this.dataDeProcesso, other.dataDeProcesso);
    }

    @Override
    public String toString() {
        return "Correntista{" + "codigoCliente=" + codigoCliente + ", dataLancamento=" + dataLancamento + ", dataDeProcesso=" + dataDeProcesso + ", descricao=" + descricao + ", tipoMovimento=" + tipoMovimento + ", usuario=" + usuario + ", credito=" + credito + ", debito=" + debito + ", saldoAnterior=" + saldoAnterior + '}';
    }
    
    
}
