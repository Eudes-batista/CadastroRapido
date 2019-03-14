package modelo;

import java.util.Date;
import java.util.Objects;

public class Movimento {

    private String empresa;
    private String tipo;
    private String documento;
    private String observacao;
    private String usuario;
    private Date dataMovimento;
    private Date dataAtualizacao;

    public Movimento() {
    }

    public Movimento(String empresa, String tipo, String documento, String observacao, String usuario, Date dataMovimento, Date dataAtualizacao) {
        this.empresa = empresa;
        this.tipo = tipo;
        this.documento = documento;
        this.observacao = observacao;
        this.usuario = usuario;
        this.dataMovimento = dataMovimento;
        this.dataAtualizacao = dataAtualizacao;
    }
    
    public Movimento(String empresa, String tipo, String documento,Date dataMovimento,String observacao) {
        this.empresa = empresa;
        this.tipo = tipo;
        this.documento = documento;
        this.observacao = observacao;
        this.dataMovimento = dataMovimento;
    }

    public Movimento(String documento) {
        this.documento = documento;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + Objects.hashCode(this.documento);
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
        return Objects.equals(this.documento, other.documento);
    }

    
    @Override
    public String toString() {
        return "Movimento{" + "empresa=" + empresa + ", tipo=" + tipo + ", documento=" + documento + ", observacao=" + observacao + ", usuario=" + usuario + ", dataMovimento=" + dataMovimento + ", dataAtualizacao=" + dataAtualizacao + '}';
    }

    public String getEmpresa() {
        return empresa;
    }

    public void setEmpresa(String empresa) {
        this.empresa = empresa;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public Date getDataMovimento() {
        return dataMovimento;
    }

    public void setDataMovimento(Date dataMovimento) {
        this.dataMovimento = dataMovimento;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public Date getDataAtualizacao() {
        return dataAtualizacao;
    }

    public void setDataAtualizacao(Date dataAtualizacao) {
        this.dataAtualizacao = dataAtualizacao;
    }

}
