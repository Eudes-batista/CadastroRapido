package modelo;

import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

public class Cosmos implements Serializable {

    private String description;
    private String gtin;
    private Double avg_price;
    private Double max_price;
    private Ncm ncm;
    private Cest cest;
    private String price;

    public Double getAvg_price() {
        return avg_price;
    }

    public void setAvg_price(Double avg_price) {
        this.avg_price = avg_price;
    }

    public Double getMax_price() {
        return max_price;
    }

    public void setMax_price(Double max_price) {
        this.max_price = max_price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getGtin() {
        return gtin;
    }

    public void setGtin(String gtin) {
        this.gtin = gtin;
    }

    public Ncm getNcm() {
        return ncm;
    }

    public void setNcm(Ncm ncm) {
        this.ncm = ncm;
    }

    public Cest getCest() {
        return cest;
    }

    public void setCest(Cest cest) {
        this.cest = cest;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + Objects.hashCode(this.gtin);
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
        final Cosmos other = (Cosmos) obj;
        return Objects.equals(this.gtin, other.gtin);
    }

    @Override
    public String toString() {
        return "Cosmos{" + "avg_price=" + avg_price + ", max_price=" + max_price + ", description=" + description + ", gtin=" + gtin + ", ncm=" + ncm + ", cest=" + cest + ", price=" + price + '}';
    }

    public static Produto buscarProduto(String referencia) {
        try {
            String url ="https://api.cosmos.bluesoft.com.br/gtins/" + referencia + ".json";
            URL site = new URL(url);
            HttpURLConnection httpURLConnection = (HttpURLConnection) site.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setRequestProperty("Content-Type", "application/json");
            httpURLConnection.setRequestProperty("X-Cosmos-Token", "1PBHjGipdOpCAUE4V912TQ");
            httpURLConnection.setConnectTimeout(2000);
            String conteudo = "";
            try (BufferedReader br = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()))) {
                while (br.ready()) {
                    conteudo += br.readLine();
                }
            }
            Cosmos gson = new Gson().fromJson(conteudo, Cosmos.class);
            if (gson != null) {
                String descricao;
                if(gson.description.length()>=35){
                    descricao = gson.description.substring(0, 35);
                }else{
                    descricao=gson.description;
                }
                Produto produto = new Produto(gson.gtin, descricao, gson.avg_price, gson.avg_price, 0.0, gson.gtin);
                if(gson.getNcm() != null)
                    produto.setNcm(gson.getNcm().getCode().replaceAll("\\D",""));
                if(gson.getCest() != null)
                    produto.setCest(gson.getCest().getCode().replaceAll("\\D",""));
                produto.setUnidade("UN");
                produto.setTributacao(produto.getCest() == null ? "0001" : "0600");
                return produto;
            }
        } catch (MalformedURLException ex) {
        } catch (IOException ex) {
        }
        return null;
    }
}
