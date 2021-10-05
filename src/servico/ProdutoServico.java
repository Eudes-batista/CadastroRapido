package servico;

import controle.ConectaBanco;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import javafx.scene.control.Alert;
import modelo.Grupo;
import modelo.ItemMovimento;
import modelo.Movimento;
import modelo.Produto;
import modelo.SubGrupo;

public class ProdutoServico {

    public final ConectaBanco conecta = new ConectaBanco();
    private MovimentoService movimentoService;
    private ItemMovimentoService itemMovimentoService;
    private final List<Produto> produtos;

    public ProdutoServico() {
        this.produtos = new ArrayList<>();
    }

    public void salvar(Produto produto) {
        Produto produtoEncontrado = buscarProduto(produto.getReferencia());
        if (produtoEncontrado == null) {
            persistir(produto);
            return;
        }
        alterar(produto);
    }

    private void persistir(Produto produto) {
        if (conecta.conexao()) {
            try {
                String sql = "INSERT INTO SCEA01 (PRREFERE,PRDESCRI,PRCODBAR,PRREFLIM,PRCGRUPO,PRSUBGRP,PRUNDCPR,PRUNIDAD,PRPOSTRI,PRSPOTRI,PRCLASSI,PRCDCEST,PRIDENTI,PRIVESEF,PRQTDATA,PRULTALT,PRCONFPR,PRAPLICA)";
                sql += " VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
                PreparedStatement pst = conecta.getConnection().prepareStatement(sql);
                pst.setString(1, produto.getReferencia());
                if (produto.getDescricao().length() >= 36) {
                    pst.setString(2, produto.getDescricao().substring(0, 35));
                } else {
                    pst.setString(2, produto.getDescricao());
                }
                pst.setString(3, produto.getCodigoBarra());
                pst.setString(4, produto.getReferencia());
                pst.setString(5, produto.getGrupo());
                pst.setString(6, produto.getSubgrupo());
                pst.setString(7, produto.getUnidade());
                pst.setString(8, produto.getUnidade());
                pst.setString(9, produto.getTributacao());
                pst.setString(10, produto.getTributacao());
                pst.setString(11, produto.getNcm());
                pst.setString(12, produto.getCest());
                pst.setString(13, "ADM");
                pst.setString(14, "1");
                pst.setDouble(15, produto.getQtdAtacado());
                pst.setString(16, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                pst.setString(17, produto.getConfirmaPreco());
                pst.setString(18, produto.getAplicacao());
                pst.execute();
                conecta.getConnection().commit();
                pst.close();
                String data = LocalDate.now().toString();
                for (String empresa : buscarEmpresaScea07()) {
                    sql = "INSERT INTO SCEA07 (EECODEMP,EEREFERE,EEDTTAB1,EEPBRTB1,EEPLQTB1,EEDTTAB2,EEPBRTB2,EEPLQTB2,EEDTTAB3,EEPBRTB3,EEPLQTB3,EET2PVD1,EET3VIG1,EET3PVD1,EET3VIG2,EET3PVD2,EET3VIG3,EET3PVD3)";
                    sql += " VALUES ('" + empresa + "','" + produto.getReferencia() + "','" + data + "'," + produto.getPreco() + "," + produto.getPreco()
                            + ",'" + data + "','" + produto.getPreco() + "'," + produto.getPreco()
                            + ",'" + data + "'," + produto.getPreco() + "," + produto.getPreco() + "," + produto.getPrecoAtacado() + ",'" + data + "'," + produto.getPrecoEspecial() + ",'" + data + "'," + produto.getPrecoEspecial() + ",'" + data + "'," + produto.getPrecoEspecial() + ")";
                    PreparedStatement pst2 = conecta.getConnection().prepareStatement(sql);
                    pst2.execute();
                    conecta.getConnection().commit();
                    pst2.close();
                }
                this.registrarEstoque(produto);
                pst = conecta.getConnection().prepareStatement("insert into scea09 values(?,?,?)");
                pst.setString(1, produto.getReferencia());
                pst.setString(2, produto.getReferencia());
                pst.setString(3, produto.getReferencia());
                pst.execute();
                conecta.getConnection().commit();
                pst.close();
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("SUCESSO!!");
                alert.setContentText("Produto salvo com sucesso.");
                alert.showAndWait();
            } catch (SQLException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                try {
                    conecta.getConnection().rollback();
                } catch (SQLException ex1) {
                    alert.setTitle("Erro");
                    alert.setContentText("Erro ao Cadastrar Produto " + ex1.getMessage());
                    alert.show();
                }
                alert.setTitle("Erro");
                alert.setContentText("Erro ao Cadastrar o Produto\n detalhe do erro: " + ex.getMessage());
                alert.show();
            }
            conecta.desconecta();
        }
    }

    private void alterar(Produto produto) {
        if (conecta.conexao()) {
            try {
                PreparedStatement pst = conecta.getConnection().prepareStatement("update scea07 set EEPBRTB1=?,EEPLQTB1=?,EEPBRTB2=?,EEPLQTB2=?,EET2PVD1=?,EET3PVD1=?,EET3PVD2=?,EET3PVD3=?  where EEREFERE=?");
                pst.setDouble(1, produto.getPreco());
                pst.setDouble(2, produto.getPreco());
                pst.setDouble(3, produto.getPreco());
                pst.setDouble(4, produto.getPreco());
                pst.setDouble(5, produto.getPrecoAtacado());
                pst.setDouble(6, produto.getPrecoEspecial());
                pst.setDouble(7, produto.getPrecoEspecial());
                pst.setDouble(8, produto.getPrecoEspecial());
                pst.setString(9, produto.getReferencia());
                pst.execute();
                conecta.getConnection().commit();
                pst.close();
                pst = conecta.getConnection().prepareStatement("update scea01 set PRQTDATA=?,PRCLASSI=?,PRCDCEST=?,prpostri=?,prspotri=?,prdescri=?,PRUNDCPR=?,PRUNIDAD=?,prcodbar=?,PRCGRUPO=?,PRSUBGRP=?,PRULTALT=?,PRDATCAN=?,PRCONFPR=?,PRAPLICA=? where prrefere=?");
                pst.setDouble(1, produto.getQtdAtacado());
                pst.setString(2, produto.getNcm());
                pst.setString(3, produto.getCest());
                pst.setString(4, produto.getTributacao());
                pst.setString(5, produto.getTributacao());
                pst.setString(6, produto.getDescricao());
                pst.setString(7, produto.getUnidade());
                pst.setString(8, produto.getUnidade());
                pst.setString(9, produto.getCodigoBarra());
                pst.setString(10, produto.getGrupo());
                pst.setString(11, produto.getSubgrupo());
                pst.setDate(12, new java.sql.Date(new Date().getTime()));
                pst.setDate(13, null);
                if (produto.getDataCancelamento() != null) {
                    pst.setDate(13, new java.sql.Date(new SimpleDateFormat("yyyy-MM-dd").parse(produto.getDataCancelamento()).getTime()));
                }
                pst.setString(14, produto.getConfirmaPreco());
                pst.setString(15, produto.getAplicacao());
                pst.setString(16, produto.getReferencia());
                pst.execute();
                conecta.getConnection().commit();
                pst.close();
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("SUCESSO!!");
                alert.setContentText("Produto salvo com sucesso.");
                alert.showAndWait();
            } catch (SQLException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                try {
                    conecta.getConnection().rollback();
                } catch (SQLException ex1) {
                    alert.setTitle("Erro");
                    alert.setContentText("Erro ao Alterar o preço\n detalhe do erro: " + ex1.getMessage());
                    alert.show();
                }
                alert.setTitle("Erro");
                alert.setContentText("Erro ao Alterar o preço\n detalhe do erro: " + ex.getMessage());
                alert.show();
            } catch (ParseException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erro");
                alert.setContentText("Na conversão da Data de Cancelamento");
                alert.show();
            }
            conecta.desconecta();
        }
    }

    public boolean excluirProduto(String refencia) {
        if (!conecta.conexao()) {
            return false;
        }
        try {
            PreparedStatement preparedStatement = conecta.getConnection().prepareStatement("delete from scea09 where rarefere = ?");
            preparedStatement.setString(1, refencia);
            preparedStatement.execute();
            preparedStatement = conecta.getConnection().prepareStatement("delete from scea07 where eerefere = ?");
            preparedStatement.setString(1, refencia);
            preparedStatement.execute();
            preparedStatement = conecta.getConnection().prepareStatement("delete from scea01 where prrefere = ?");
            preparedStatement.setString(1, refencia);
            preparedStatement.execute();
            conecta.getConnection().commit();
            preparedStatement.close();
            conecta.desconecta();
            return true;
        } catch (SQLException ex) {
            this.conecta.desconecta();
            return false;
        }
    }

    public boolean atualizarGrupo(String referencia, String grupo) {
        if (!conecta.conexao()) {
            return false;
        }
        try {
            PreparedStatement preparedStatement = conecta.getConnection().prepareStatement("update scea01 set PRCGRUPO='" + grupo + "',PRSUBGRP='" + grupo + "' where PRREFERE IN(" + referencia + ")");
            preparedStatement.execute();
            conecta.getConnection().commit();
            preparedStatement.close();
            conecta.desconecta();
            return true;
        } catch (SQLException ex) {
            conecta.desconecta();
            return false;
        }
    }

    public List<Grupo> listarGrupos() {
        List<Grupo> grupos = new ArrayList<>();
        if (!this.conecta.conexao()) {
            return grupos;
        }
        String sql = "SELECT T51CDGRP as codigo,T51DSGRP as nome FROM LAPT51 ORDER BY T51DSGRP";
        if (!this.conecta.executaSQL(sql)) {
            this.conecta.desconecta();
            return grupos;
        }
        try {
            if (!this.conecta.getResultSet().first()) {
                this.conecta.desconecta();
                return Arrays.asList();
            }
            do {
                grupos.add(new Grupo(this.conecta.getResultSet().getString("codigo"), this.conecta.getResultSet().getString("nome")));
            } while (this.conecta.getResultSet().next());
        } catch (SQLException ex) {
            grupos.clear();
        }
        this.conecta.desconecta();
        return grupos;
    }

    public List<SubGrupo> listarSubGrupos() {
        List<SubGrupo> grupos = new ArrayList<>();
        if (!this.conecta.conexao()) {
            return grupos;
        }
        String sql = "SELECT T52CDSGR as codigo,T52DSSGR as nome FROM LAPT52 ORDER BY T52DSSGR";
        if (!this.conecta.executaSQL(sql)) {
            this.conecta.desconecta();
            return grupos;
        }
        try {
            if (!this.conecta.getResultSet().first()) {
                this.conecta.desconecta();
                return grupos;
            }
            do {
                grupos.add(new SubGrupo(this.conecta.getResultSet().getString("codigo"), this.conecta.getResultSet().getString("nome")));
            } while (this.conecta.getResultSet().next());
        } catch (SQLException ex) {
            grupos.clear();
        }
        this.conecta.desconecta();
        return grupos;
    }

    private List<String> buscarEmpresaScea07() throws SQLException {
        return buscarEmpresa("SELECT LDCODEMP FROM LAPA13 WHERE LDUSUARI LIKE '%SUPORTE%'");
    }

    private List<String> buscarEmpresaEstoque() throws SQLException {
        return this.buscarEmpresa("select LJCODEMP from lapa19 where LJNUMCGC <> '00.000.000/0000-00';");
    }

    private List<String> buscarEmpresa(String sql) {
        List<String> empresas = new ArrayList<>();
        if (!conecta.executaSQL(sql)) {
            this.conecta.desconecta();
            return empresas;
        }
        try {
            if (!conecta.getResultSet().first()) {
                return empresas;
            }
            do {
                empresas.add(conecta.getResultSet().getString(1));
            } while (conecta.getResultSet().next());
        } catch (SQLException ex) {
            empresas.clear();
        }
        return empresas;
    }

    public Produto buscarProduto(String referencia) {
        String sql;
        if (referencia.trim().isEmpty()) {
            return null;
        }
        if (!conecta.conexao()) {
            return null;
        }
        sql = "select first 1 PRREFERE,PRDESCRI,EEPBRTB1,EET2PVD1,PRQTDATA,PRCLASSI,PRCDCEST,PRPOSTRI,PRSPOTRI,PRUNIDAD,PRCODBAR,PRDATCAN,PRCGRUPO,PRSUBGRP,SUM(EEESTATU) as EEESTATU,PRCONFPR,EET3PVD1,PRAPLICA from scea01 left outer join scea07 on(eerefere=prrefere) "
                + " where prrefere ='" + referencia + "' or PRCODBAR='" + referencia + "' \n";
        sql += "group by\n"
                + "   PRREFERE\n"
                + "  ,PRDESCRI\n"
                + "  ,EEPBRTB1\n"
                + "  ,EET2PVD1\n"
                + "  ,PRQTDATA\n"
                + "  ,PRCLASSI\n"
                + "  ,PRCDCEST\n"
                + "  ,PRPOSTRI\n"
                + "  ,PRSPOTRI\n"
                + "  ,PRUNIDAD\n"
                + "  ,PRCODBAR\n"
                + "  ,PRDATCAN\n"
                + "  ,PRCGRUPO\n"
                + "  ,PRSUBGRP\n"
                + "  ,PRCONFPR"
                + "  ,EET3PVD1"
                + "  ,PRAPLICA";
        if (!conecta.executaSQL(sql)) {
            this.conecta.desconecta();
            return null;
        }
        try {
            if (conecta.getResultSet().first()) {
                Produto produto = buscarProdutoScea01();
                conecta.desconecta();
                return produto;
            }
            sql = "select first 1 PRREFERE,PRDESCRI,EEPBRTB1,EET2PVD1,PRQTDATA,PRCLASSI,PRCDCEST,prpostri,prspotri,PRUNIDAD,PRCODBAR,PRDATCAN,PRCGRUPO,PRSUBGRP,EEESTATU,PRCONFPR,EET3PVD1,PRAPLICA  from SCEA07 "
                    + "left outer join  SCEA09 on(RAREFERE=EEREFERE) "
                    + "left outer join SCEA01 on(PRREFERE=EEREFERE) "
                    + "where RAREFERE='" + referencia + "' "
                    + "or    RAREFAUX='" + referencia + "' "
                    + "or    RAREFLIM='" + referencia + "' ";
            if (!conecta.executaSQL(sql)) {
                this.conecta.desconecta();
                return null;
            }
            if (!conecta.getResultSet().first()) {
                this.conecta.desconecta();
                return null;
            }
            Produto produto = buscarProdutoScea01();
            conecta.desconecta();
            return produto;
        } catch (SQLException ex) {
            conecta.desconecta();
        }
        return null;
    }

    public List<Produto> pesquisarProduto(String pesquisa) {
        String sql;
        this.produtos.clear();
        if (pesquisa.trim().isEmpty()) {
            return null;
        }
        if (!conecta.conexao()) {
            return null;
        }
        sql = "select PRREFERE,PRDESCRI,PRCODBAR from scea01 "
                + " where prrefere ='" + pesquisa + "' or PRCODBAR='" + pesquisa + "' or PRDESCRI like '%" + pesquisa + "%'";
        if (!conecta.executaSQL(sql)) {
            this.conecta.desconecta();
            return this.produtos;
        }
        try {
            if (!conecta.getResultSet().first()) {
                this.conecta.desconecta();
                return this.produtos;
            }
            do {
                Produto produto = new Produto();
                produto.setReferencia(this.conecta.getResultSet().getString("PRREFERE"));
                produto.setDescricao(this.conecta.getResultSet().getString("PRDESCRI"));
                produto.setCodigoBarra(this.conecta.getResultSet().getString("PRCODBAR"));
                this.produtos.add(produto);
            } while (this.conecta.getResultSet().next());
            conecta.desconecta();
        } catch (SQLException ex) {
            conecta.desconecta();
        }
        return this.produtos;
    }

    public void atualizarDataCancelamento(Produto produto) {
        if (!conecta.conexao()) {
            return;
        }
        try {
            String data = produto.getDataCancelamento() != null ? "'" + produto.getDataCancelamento() + "'" : null;
            String sql = "update scea01 set PRDATCAN=" + data + " where prrefere='" + produto.getReferencia() + "'";
            PreparedStatement pst = conecta.getConnection().prepareStatement(sql);
            pst.execute();
            conecta.getConnection().commit();
            pst.close();
            this.conecta.desconecta();
        } catch (SQLException ex) {
            this.conecta.desconecta();
        }
    }

    public String gerarReferencia() {
        if (!conecta.conexao()) {
            return "1";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT ").append(" cast(floor((rand()*count(*))*1000) as integer) AS SO_NUMERO ").append(" FROM SCEA01");
        String sql = sb.toString();
        if (!conecta.executaSQL(sql)) {
            this.conecta.desconecta();
            return "1";
        }
        try {
            if (!conecta.getResultSet().first()) {
                this.conecta.desconecta();
                return "1";
            }
            String refencia = conecta.getResultSet().getString("SO_NUMERO");
            if (refencia == null) {
                this.conecta.desconecta();
                return "1";
            }
            while (verificarSeExisteReferncia(refencia)) {
                refencia = String.valueOf(Integer.parseInt(refencia) + 1);
            }
            this.conecta.desconecta();
            return refencia;
        } catch (SQLException ex) {
            this.conecta.desconecta();
            return "1";
        }
    }

    private boolean verificarSeExisteReferncia(String refencia) throws SQLException {
        if (!conecta.executaSQL("select count(*) as numero from scea01 where prrefere='" + refencia + "'")) {
            return false;
        }
        if (!conecta.getResultSet().first()) {
            return false;
        }
        return conecta.getResultSet().getInt("numero") != 0;
    }

    private Produto buscarProdutoScea01() throws SQLException {
        Produto produto = new Produto();
        produto.setReferencia(conecta.getResultSet().getString("PRREFERE"));
        produto.setCodigoBarra(conecta.getResultSet().getString("PRCODBAR"));
        produto.setDescricao(conecta.getResultSet().getString("PRDESCRI"));
        produto.setPreco(conecta.getResultSet().getDouble("EEPBRTB1"));
        produto.setPrecoAtacado(conecta.getResultSet().getDouble("EET2PVD1"));
        produto.setQtdAtacado(conecta.getResultSet().getDouble("PRQTDATA"));
        produto.setNcm(conecta.getResultSet().getString("PRCLASSI"));
        produto.setCest(conecta.getResultSet().getString("PRCDCEST"));
        produto.setTributacao(conecta.getResultSet().getString("PRPOSTRI"));
        produto.setUnidade(conecta.getResultSet().getString("PRUNIDAD"));
        produto.setDataCancelamento(conecta.getResultSet().getString("PRDATCAN"));
        produto.setGrupo(conecta.getResultSet().getString("PRCGRUPO"));
        produto.setSubgrupo(conecta.getResultSet().getString("PRSUBGRP"));
        produto.setConfirmaPreco(conecta.getResultSet().getString("PRCONFPR"));
        produto.setQuantidade(conecta.getResultSet().getDouble("EEESTATU"));
        produto.setPrecoEspecial(conecta.getResultSet().getDouble("EET3PVD1"));
        produto.setAplicacao(conecta.getResultSet().getString("PRAPLICA"));
        return produto;
    }

    private void registrarEstoque(Produto produto) throws SQLException {
        if (produto.getQuantidade() == 0) {
            return;
        }
        for (String empresa : buscarEmpresaEstoque()) {
            this.movimentoService = new MovimentoService();
            this.itemMovimentoService = new ItemMovimentoService();
            Movimento movimento = new Movimento(
                    empresa,
                    "ENAQ",
                    new SimpleDateFormat("ddMMyyyy").format(new Date()),
                    "Estoque Inicial", "ADM", new Date(), new Date());
            Movimento m = movimentoService.verificarMovimento(movimento);
            if (m == null) {
                movimentoService.salvar(movimento);
            }
            itemMovimentoService.salvar(new ItemMovimento(movimento, "01", produto.getReferencia(), produto.getQuantidade(), produto.getPreco()));
        }
    }
}
