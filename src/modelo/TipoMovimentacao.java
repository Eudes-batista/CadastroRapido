/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modelo;

/**
 *
 * @author ZOOM
 */
public enum TipoMovimentacao {
    CREDITO("PAGAMENTO"),DEBITO("DEBITO");
    
    private final String valor;

    private TipoMovimentacao(String valor) {
        this.valor = valor;
    }

    public String getValor() {
        return valor;
    }
}
