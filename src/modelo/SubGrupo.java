
package modelo;

import java.util.Objects;


public class SubGrupo {

    private String codigo;
    private String nome;

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public SubGrupo() {
    }

    public SubGrupo(String codigo) {
        this.codigo = codigo;
    }

    public SubGrupo(String codigo, String nome) {
        this.codigo = codigo;
        this.nome = nome;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 67 * hash + Objects.hashCode(this.codigo);
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
        final SubGrupo other = (SubGrupo) obj;
        return Objects.equals(this.codigo, other.codigo);
    }

    @Override
    public String toString() {
        return this.codigo+" - "+this.nome;
    }    
}
