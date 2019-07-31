
package modelo.dto;

import java.util.Date;
import java.util.Objects;
import modelo.Movimento;


public class MovimentoDTO extends Movimento{

    public MovimentoDTO() {
    }

    public MovimentoDTO(String empresa, String tipo, String documento, Date dataMovimento, String observacao) {
        super(empresa, tipo, documento, dataMovimento, observacao);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + Objects.hashCode(super.getEmpresa());
        hash = 89 * hash + Objects.hashCode(this.getTipo());
        hash = 89 * hash + Objects.hashCode(this.getDocumento());
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
        final Movimento other = (Movimento) obj;
        if (!Objects.equals(this.getEmpresa(), other.getEmpresa())) {
            return false;
        }
        if (!Objects.equals(this.getTipo(), other.getTipo())) {
            return false;
        }
        return Objects.equals(this.getDocumento(), other.getDocumento());
    }
    
}
