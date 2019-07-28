/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modelo;

import java.io.Serializable;
import java.util.Objects;

/**
 *
 * @author administrador
 */
public class Ncm implements Serializable{
    private static final long serialVersionUID = -5469339989691149108L;
    private String code;
    private String description;
    private String full_description;

    public Ncm() {
    }

    public Ncm(String code, String description, String full_description) {
        this.code = code;
        this.description = description;
        this.full_description = full_description;
    }

    
    
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFull_description() {
        return full_description;
    }

    public void setFull_description(String full_description) {
        this.full_description = full_description;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 59 * hash + Objects.hashCode(this.code);
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
        final Ncm other = (Ncm) obj;
        return Objects.equals(this.code, other.code);
    }

    @Override
    public String toString() {
        return "Ncm{" + "code=" + code + ", description=" + description + ", full_description=" + full_description + '}';
    }        
    
}
