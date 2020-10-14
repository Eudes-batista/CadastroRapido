
package modelo;

import java.util.Objects;


public class TipoMovimento {

    private String codigo;
    private String descricao;

    public TipoMovimento(String codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    public TipoMovimento() {
    }

    public TipoMovimento(String codigo) {
        this.codigo = codigo;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 31 * hash + Objects.hashCode(this.codigo);
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
        final TipoMovimento other = (TipoMovimento) obj;
        return Objects.equals(this.codigo, other.codigo);
    }

    @Override
    public String toString() {
        return codigo + " - "+ descricao;
    }
    
}
