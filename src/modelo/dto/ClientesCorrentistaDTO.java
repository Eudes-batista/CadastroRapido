package modelo.dto;

public class ClientesCorrentistaDTO{
    
    private double saldoDisponivel;
    private double saldoDevedor;
    private double saldoCredito;
    private double totalCredito;
    private double totalDebito;
    private double valorReceber;

    public ClientesCorrentistaDTO() {
    }

    public ClientesCorrentistaDTO(double saldoDisponivel, double saldoDevedor, double saldoCredito, double totalCredito, double totalDebito) {
        this.saldoDisponivel = saldoDisponivel;
        this.saldoDevedor = saldoDevedor;
        this.saldoCredito = saldoCredito;
        this.totalCredito = totalCredito;
        this.totalDebito = totalDebito;
    }

    public double getSaldoDisponivel() {
        return saldoDisponivel;
    }

    public void setSaldoDisponivel(double saldoDisponivel) {
        this.saldoDisponivel = saldoDisponivel;
    }

    public double getSaldoDevedor() {
        return saldoDevedor;
    }

    public void setSaldoDevedor(double saldoDevedor) {
        this.saldoDevedor = saldoDevedor;
    }

    public double getSaldoCredito() {
        return saldoCredito;
    }

    public void setSaldoCredito(double saldoCredito) {
        this.saldoCredito = saldoCredito;
    }

    public double getTotalCredito() {
        return totalCredito;
    }

    public void setTotalCredito(double totalCredito) {
        this.totalCredito = totalCredito;
    }

    public double getTotalDebito() {
        return totalDebito;
    }

    public void setTotalDebito(double totalDebito) {
        this.totalDebito = totalDebito;
    }

    @Override
    public String toString() {
        return "ClientesCorrentistaDTO{" + "saldoDisponivel=" + saldoDisponivel + ", saldoDevedor=" + saldoDevedor + ", saldoCredito=" + saldoCredito + ", totalCredito=" + totalCredito + ", totalDebito=" + totalDebito + ", valorReceber=" + valorReceber + '}';
    }

    public double getValorReceber() {
        return valorReceber;
    }

    public void setValorReceber(double valorReceber) {
        this.valorReceber = valorReceber;
    }

    
    
    
    
}
