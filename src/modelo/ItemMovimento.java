
package modelo;

import java.util.Objects;
import javafx.scene.control.CheckBox;


public class ItemMovimento {

    private CheckBox checkBox;
    private Movimento movimento;    
    private String seguenciaItem;
    private String descricao;
    private String produto;
    private double quantidade;
    private double precoUnitario;
    private double precoTotal;

    public ItemMovimento(Movimento movimento) {
        this.movimento = movimento;
    }

    public ItemMovimento(Movimento movimento, String seguenciaItem, String produto, double quantidade, double precoUnitario) {
        this.movimento = movimento;
        this.seguenciaItem = seguenciaItem;
        this.produto = produto;
        this.quantidade = quantidade;
        this.precoUnitario = precoUnitario;
        this.checkBox = new CheckBox();
    }
    
    public ItemMovimento(Movimento movimento,String produto, double quantidade, double precoUnitario) {
        this.movimento = movimento;
        this.produto = produto;
        this.quantidade = quantidade;
        this.precoUnitario = precoUnitario;
        this.checkBox = new CheckBox();
    }

    public ItemMovimento() {
    }

    public Movimento getMovimento() {
        return movimento;
    }

    public void setMovimento(Movimento movimento) {
        this.movimento = movimento;
    }

    public String getSeguenciaItem() {
        return seguenciaItem;
    }

    public void setSeguenciaItem(String seguenciaItem) {
        this.seguenciaItem = seguenciaItem;
    }

    public String getProduto() {
        return produto;
    }

    public void setProduto(String produto) {
        this.produto = produto;
    }

    public double getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(double quantidade) {
        this.quantidade = quantidade;
    }

    public double getPrecoUnitario() {
        return precoUnitario;
    }

    public void setPrecoUnitario(double precoUnitario) {
        this.precoUnitario = precoUnitario;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
    
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 59 * hash + Objects.hashCode(this.movimento);
        hash = 59 * hash + Objects.hashCode(this.seguenciaItem);
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
        final ItemMovimento other = (ItemMovimento) obj;
        if (!Objects.equals(this.seguenciaItem, other.seguenciaItem)) {
            return false;
        }
        return Objects.equals(this.movimento, other.movimento);
    }

    @Override
    public String toString() {
        return "ItemMovimento{" + "checkBox=" + checkBox + ", movimento=" + movimento + ", seguenciaItem=" + seguenciaItem + ", descricao=" + descricao + ", produto=" + produto + ", quantidade=" + quantidade + ", precoUnitario=" + precoUnitario + ", precoTotal=" + precoTotal + '}';
    }

    public CheckBox getCheckBox() {
        return checkBox;
    }

    public void setCheckBox(CheckBox checkBox) {
        this.checkBox = checkBox;
    }

    public double getPrecoTotal() {
        return precoTotal;
    }

    public void setPrecoTotal(double precoTotal) {
        this.precoTotal = precoTotal;
    }
}
