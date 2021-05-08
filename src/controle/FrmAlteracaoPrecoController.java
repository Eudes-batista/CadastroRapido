package controle;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXToggleButton;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.BoxBlur;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import javafx.util.StringConverter;
import modelo.Cosmos;
import modelo.Grupo;
import modelo.Produto;
import modelo.SubGrupo;
import servico.ProdutoServico;
import util.FXMLUtil;

public class FrmAlteracaoPrecoController implements Initializable {

    @FXML
    private AnchorPane ancoraPrincipal;
    @FXML
    private AnchorPane ancoraAjuda;
    @FXML
    private HBox paneModal;

    @FXML
    private Label labelReferencia;
    @FXML
    private Label labelDescricao;
    @FXML
    private Label labelPreco;
    @FXML
    private Label labelNcm;
    @FXML
    private Label labelCest;
    @FXML
    private Label labelMinimizar;

    @FXML
    private TextField editReferencia;
    @FXML
    private TextField editDescricao;
    @FXML
    private TextField editPreco;
    @FXML
    private TextField editQtdAtacado;
    @FXML
    private TextField editPrecoAtacado;
    @FXML
    private TextField editNcm;
    @FXML
    private TextField editCest;
    @FXML
    private TextField editEstoqueInicial;
    @FXML
    private JFXTextArea textAplicacaoComposicao;

    @FXML
    private ComboBox<String> tributacao;
    @FXML
    private ComboBox<String> unidade;

    @FXML
    private ComboBox<Grupo> grupo;
    @FXML
    private ComboBox<SubGrupo> subGrupo;

    @FXML
    private Button btSair;

    @FXML
    private Button btSalvar;

    @FXML
    private Button btEstoque;

    @FXML
    private JFXToggleButton inativar;

    @FXML
    private JFXButton btSubGrupo;

    @FXML
    private JFXButton btGrupo;

    @FXML
    private JFXButton btRelatorioProduto;

    @FXML
    private JFXToggleButton confirmaPreco;

    @FXML
    private TextField editPrecoEspecial;

    private String referencia, codigoBarra;
    private final ObservableList<String> tributacoes = FXCollections.observableArrayList("0001 TRIBUTADO", "0400 ISENTO", "0600 TRIBUTADO ST");
    private final ObservableList<Grupo> grupos = FXCollections.observableArrayList();
    private final ObservableList<SubGrupo> subGrupos = FXCollections.observableArrayList();
    private final ObservableList<String> unidades = FXCollections.observableArrayList("UN", "KG", "CX", "FD", "M2", "PC", "ML", "PA");
    private Produto produto = new Produto();
    private final ProdutoServico produtoServico = new ProdutoServico();
    private final DecimalFormat df = new DecimalFormat("#,###0.00");
    private Thread thread;
    private final Alert alert = new Alert(Alert.AlertType.WARNING);

    public void sair() {
        FXMLUtil.sair(this.ancoraPrincipal);
    }

    private void referencia() {
        referencia = editReferencia.getText().trim();
        codigoBarra = "";
        Produto produtoNaBaseLocal, produtoNaBaseDaInternet = null;
        produtoNaBaseLocal = this.buscarProdutoBaseLocal();
        if (produtoNaBaseLocal != null) {
            this.pararProgressoPanelModal();
            return;
        }
        if (this.referencia.length() >= 8) {
            produtoNaBaseDaInternet = this.buscarProdutoNaInternet();
        }
        if (produtoNaBaseDaInternet == null) {
            Platform.runLater(() -> {
                if (editReferencia.getText().isEmpty()) {
                    String gerarReferencia = produtoServico.gerarReferencia();
                    editReferencia.setText(String.valueOf(gerarReferencia));
                }
                paneModal.setVisible(false);
                editDescricao.requestFocus();
                unidade.getSelectionModel().select(0);
                codigoBarra = "";
                editEstoqueInicial.setText("0");
                editEstoqueInicial.setDisable(false);
            });
        }
        this.pararProgressoPanelModal();
    }

    private void pararProgressoPanelModal() {
        Platform.runLater(() -> this.paneModal.setVisible(false));
        if (this.thread != null) {
            this.thread.interrupt();
        }
    }

    private Produto buscarProdutoNaInternet() {
        Produto produtoNaBaseDaInternet = Cosmos.buscarProduto(referencia);
        if (produtoNaBaseDaInternet == null) {
            return null;
        }
        referencia = produtoNaBaseDaInternet.getReferencia();
        codigoBarra = produtoNaBaseDaInternet.getReferencia();
        Platform.runLater(() -> {
            pesquisarProduto(produtoNaBaseDaInternet);
            editEstoqueInicial.setDisable(true);
        });
        return produtoNaBaseDaInternet;
    }

    private Produto buscarProdutoBaseLocal() {
        Produto produtoNaBaseLocal = produtoServico.buscarProduto(this.referencia);
        if (produtoNaBaseLocal == null) {
            return null;
        }
        this.referencia = produtoNaBaseLocal.getReferencia();
        this.codigoBarra = produtoNaBaseLocal.getCodigoBarra();
        Platform.runLater(() -> {
            pesquisarProduto(produtoNaBaseLocal);
            this.labelReferencia.setText(produtoNaBaseLocal.getDataCancelamento() == null ? "Referencia" : "Referencia - Cancelada");
            this.editReferencia.setStyle(produtoNaBaseLocal.getDataCancelamento() == null ? "-fx-text-fill:#757575" : "-fx-text-fill:red");
            this.editEstoqueInicial.setDisable(true);
        });
        return produtoNaBaseLocal;
    }

    @FXML
    private void abrirAjuda() {
        ancoraAjuda.setVisible(true);
    }

    @FXML
    private void voltar() {
        ancoraAjuda.setVisible(false);
    }

    @FXML
    private void abrirSuporte() {
        try {
            Desktop.getDesktop().open(new File("Anydesk.exe"));
        } catch (IOException ex) {
            alert.setAlertType(Alert.AlertType.ERROR);
            alert.setTitle("Erro");
            alert.setHeaderText(ex.getMessage());
            alert.setContentText("Erro ao acessar o site do cosmos");
            alert.initOwner(this.pegarTelaCadastroProduto());
            alert.show();
        }
    }

    @FXML
    private void abrirCosmos() {
        try {
            String sistema = System.getProperty("os.name");
            if (sistema.equals("Linux")) {
                String[] browsers = {"x-www-browser", "google-chrome",
                    "firefox", "opera", "epiphany", "konqueror", "conkeror", "midori",
                    "kazehakase", "mozilla"};
                String browser = null;
                for (String b : browsers) {
                    if (browser == null && Runtime.getRuntime().exec(new String[]{"which", b}).getInputStream().read() != -1) {
                        Runtime.getRuntime().exec(new String[]{browser = b, "https://cosmos.bluesoft.com.br/pesquisar?utf8=✓&q=" + editDescricao.getText()});
                        voltar();
                        editNcm.requestFocus();
                    }
                }
                return;
            }
            Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
            String url = "https://cosmos.bluesoft.com.br/pesquisar?utf8=✓&q=" + editDescricao.getText().replace(" ", "%20");
            if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
                if (!editDescricao.getText().trim().isEmpty()) {
                    desktop.browse(new URI(url));
                    voltar();
                    editNcm.requestFocus();
                } else {
                    url = "https://cosmos.bluesoft.com.br";
                    desktop.browse(new URI(url));
                }
            }
        } catch (IOException ex) {
            alert.setAlertType(Alert.AlertType.ERROR);
            alert.setTitle("Erro");
            alert.setContentText("Erro ao abrir navegador");
            alert.initOwner(this.pegarTelaCadastroProduto());
            alert.show();
        } catch (URISyntaxException ex) {
            alert.setAlertType(Alert.AlertType.ERROR);
            alert.setTitle("Erro");
            alert.setContentText("Erro ao abrir o site cosmos");
            alert.initOwner(this.pegarTelaCadastroProduto());
            alert.show();
        }
    }

    @FXML
    private void abrirCest() {
        try {
            String sistema = System.getProperty("os.name");
            if (sistema.equals("Linux")) {
                String[] browsers = {"x-www-browser", "google-chrome",
                    "firefox", "opera", "epiphany", "konqueror", "conkeror", "midori",
                    "kazehakase", "mozilla"};
                String browser = null;
                for (String b : browsers) {
                    if (browser == null && Runtime.getRuntime().exec(new String[]{"which", b}).getInputStream().read() != -1) {
                        Runtime.getRuntime().exec(new String[]{browser = b, "http://www.buscacest.com.br/?utf8=✓&ncm=" + editNcm.getText()});
                        voltar();
                        editCest.requestFocus();
                    }
                }
                return;
            }
            Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
            if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
                if (!editNcm.getText().isEmpty()) {
                    desktop.browse(new URI("http://www.buscacest.com.br/?utf8=✓&ncm=" + editNcm.getText()));
                    voltar();
                    editCest.requestFocus();
                } else {
                    desktop.browse(new URI("http://www.buscacest.com.br"));
                }
            }
        } catch (URISyntaxException ex) {
            alert.setAlertType(Alert.AlertType.ERROR);
            alert.setTitle("Erro");
            alert.setContentText("Erro ao acessar o site do cosmos");
            alert.initOwner(this.pegarTelaCadastroProduto());
            alert.show();
        } catch (IOException ex) {
            alert.setAlertType(Alert.AlertType.ERROR);
            alert.setTitle("Erro");
            alert.setContentText("Erro ao abrir navegador");
            alert.initOwner(this.pegarTelaCadastroProduto());
            alert.show();
        }
    }

    private void ativarProduto() throws SQLException {
        if (produto.getReferencia() == null) {
            return;
        }
        if (!opcoes()) {
            return;
        }
        if (produto.getDataCancelamento() == null) {
            produto.setReferencia(referencia);
            produto.setDataCancelamento(LocalDate.now().toString());
            produtoServico.atualizarDataCancelamento(produto);
        } else {
            produto.setDataCancelamento(null);
            produtoServico.atualizarDataCancelamento(produto);
        }
        referencia();
    }

    private boolean validarCampos() {
        if (editReferencia.getText().isEmpty()) {
            alert.setTitle("AVISO");
            alert.setContentText("Campo Referencia não pode ser Vazio");
            alert.initOwner(this.pegarTelaCadastroProduto());
            alert.showAndWait();
            editReferencia.requestFocus();
            return false;
        }
        if (editDescricao.getText().length() > 35) {
            alert.setTitle("AVISO");
            alert.setContentText("Campo descrição deve ser no minimo 35 caracteres.");
            alert.initOwner(this.pegarTelaCadastroProduto());
            alert.showAndWait();
            editDescricao.requestFocus();
            return false;
        }
        if (this.validarCampoVazio(editDescricao.getText())) {
            alert.setTitle("AVISO");
            alert.setContentText("Campo descrição deve ser informado.");
            alert.initOwner(this.pegarTelaCadastroProduto());
            alert.showAndWait();
            editDescricao.requestFocus();
            return false;
        }
        if (this.validarCampoVazio(editNcm.getText())) {
            alert.setTitle("AVISO");
            alert.setContentText("Campo Ncm deve ser informado.");
            alert.initOwner(this.pegarTelaCadastroProduto());
            alert.showAndWait();
            editNcm.requestFocus();
            return false;
        }
        if (editNcm.getText().length() < 8) {
            alert.setTitle("AVISO");
            alert.setContentText("Campo Ncm não pode ter menos que 8 digitos");
            alert.initOwner(this.pegarTelaCadastroProduto());
            alert.showAndWait();
            editNcm.requestFocus();
            return false;
        }
        if (unidade.getSelectionModel().getSelectedItem() == null) {
            alert.setTitle("AVISO");
            alert.setContentText("Selecione alguma unidade.");
            alert.initOwner(this.pegarTelaCadastroProduto());
            alert.showAndWait();
            return false;
        }
        if (tributacao.getSelectionModel().getSelectedIndex() == 2) {
            if (editCest.getText() != null) {
                if (tributacao.getSelectionModel().getSelectedIndex() != 2 && !editCest.getText().isEmpty()) {
                    alert.setTitle("AVISO");
                    alert.setContentText("Tributação " + tributacao.getSelectionModel().getSelectedItem() + " não pode ter cest");
                    alert.initOwner(this.pegarTelaCadastroProduto());
                    alert.showAndWait();
                    tributacao.getSelectionModel().select(2);
                    editCest.requestFocus();
                    return false;
                }
            }
            if (editCest.getText() == null || editCest.getText().isEmpty()) {
                alert.setTitle("AVISO");
                alert.setContentText("Campo Cest deve ser informado");
                alert.initOwner(this.pegarTelaCadastroProduto());
                alert.showAndWait();
                editCest.requestFocus();
                return false;
            }
            if (!editCest.getText().isEmpty() && editCest.getText().length() < 7) {
                alert.setTitle("AVISO");
                alert.setContentText("Campo Cest não pode ter menos que 7 digitos");
                alert.initOwner(this.pegarTelaCadastroProduto());
                alert.showAndWait();
                editCest.requestFocus();
                return false;
            }
        }
        if (this.validarCampoDePreco(editQtdAtacado)) {
            return false;
        }
        if (this.validarCampoDePreco(editPrecoAtacado)) {
            return false;
        }
        return !this.validarCampoDePreco(editPreco);
    }

    private boolean validarCampoVazio(String valor) {
        return valor.trim().isEmpty();
    }

    private boolean validarCampoMonetario(String valor) {
        Pattern pattern = Pattern.compile("[aA-zZ]");
        Matcher matcher = pattern.matcher(valor.trim());
        return matcher.find();
    }

    private boolean validarCampoMonetarioMaiorQueDozeDigitos(String valor) {
        int tamanhoMaximoDoCampo = 12;
        return valor.trim().length() >= tamanhoMaximoDoCampo;
    }

    private boolean validarCampoMonetarioComValorNegativo(String valor) {
        return Double.parseDouble(valor.replace(",", ".")) < 0;
    }

    private boolean validarCampoDePreco(TextField textField) {
        String valor = textField.getText();
        if (this.validarCampoVazio(valor)) {
            alert.setTitle("AVISO");
            alert.setContentText("Campo não pode ser Vazio");
            alert.initOwner(this.pegarTelaCadastroProduto());
            alert.showAndWait();
            textField.requestFocus();
            textField.setText("0,00");
            textField.selectAll();
            return true;
        }
        if (validarCampoMonetarioMaiorQueDozeDigitos(valor)) {
            alert.setTitle("AVISO");
            alert.setContentText("Valor muito grade");
            alert.initOwner(this.pegarTelaCadastroProduto());
            alert.showAndWait();
            textField.requestFocus();
            textField.selectAll();
            return true;
        }
        if (this.validarCampoMonetario(valor)) {
            alert.setTitle("AVISO");
            alert.setContentText("Campo só pode receber números");
            alert.initOwner(this.pegarTelaCadastroProduto());
            alert.showAndWait();
            textField.requestFocus();
            textField.selectAll();
            return true;
        }
        if (this.validarCampoMonetarioComValorNegativo(valor)) {
            alert.setTitle("AVISO");
            alert.setContentText("Produto não pode ter números negativo");
            alert.initOwner(this.pegarTelaCadastroProduto());
            alert.showAndWait();
            textField.requestFocus();
            textField.selectAll();
            return true;
        }
        return false;
    }

    private Double formatarPreco(String preco) {
        String precoSemformatacao = preco.replaceAll(",", ".");
        if (precoSemformatacao.equals(".")) {
            return 0.0;
        }
        int count = 0;
        Pattern pattern = Pattern.compile(Pattern.quote("."));
        Matcher matcher = pattern.matcher(precoSemformatacao);
        while (matcher.find()) {
            count++;
        }
        if (count > 1) {
            precoSemformatacao = precoSemformatacao.replaceFirst(Pattern.quote("."), "");
        }
        return Double.parseDouble(precoSemformatacao);
    }

    @FXML
    private void salvar() {
        Platform.runLater(() -> {
            if (!validarCampos()) {
                return;
            }
            Double preco = formatarPreco(this.editPreco.getText()), precoAtacado = formatarPreco(this.editPrecoAtacado.getText()), qtdAtacado = formatarPreco(this.editQtdAtacado.getText());
            Double precoEspecial = formatarPreco(this.editPrecoEspecial.getText());
            this.referencia = this.editReferencia.getText().trim();
            produto.setReferencia(referencia);
            produto.setCodigoBarra(referencia);
            if (verificarTemValorCodigoBarra()) {
                produto.setCodigoBarra(codigoBarra.trim());
            }
            this.produto.setDescricao(editDescricao.getText());
            this.produto.setAplicacao(textAplicacaoComposicao.getText());
            this.produto.setPreco(preco);
            this.produto.setPrecoAtacado(precoAtacado);
            this.produto.setQtdAtacado(qtdAtacado);
            this.produto.setNcm(editNcm.getText());
            this.produto.setCest(editCest.getText());
            this.produto.setTributacao(tributacao.getSelectionModel().getSelectedItem().replaceAll("\\D", ""));
            this.produto.setUnidade(unidade.getSelectionModel().getSelectedItem());
            this.produto.setGrupo(this.grupo.getSelectionModel().getSelectedItem().getCodigo());
            this.produto.setSubgrupo(this.subGrupo.getSelectionModel().getSelectedItem().getCodigo());
            this.produto.setPrecoEspecial(precoEspecial);
            String estoque = editEstoqueInicial.getText().replace(",", ".");
            this.produto.setQuantidade(Double.parseDouble(estoque));
            String dataCancelamento = null;
            if (this.inativar.isSelected()) {
                dataCancelamento = LocalDate.now().toString();
            }
            this.produto.setDataCancelamento(dataCancelamento);
            String seConfirmaPreco = "N";
            if (this.confirmaPreco.isSelected()) {
                seConfirmaPreco = "S";
            }
            this.produto.setConfirmaPreco(seConfirmaPreco);
            this.produtoServico.salvar(this.produto);
            this.editReferencia.requestFocus();
            this.editReferencia.selectAll();
            this.codigoBarra = "";
            this.editEstoqueInicial.setDisable(false);
        });
        this.paneModal.setVisible(false);
        if (this.thread != null) {
            this.thread.interrupt();
        }
    }

    private boolean verificarTemValorCodigoBarra() {
        return codigoBarra != null && !codigoBarra.isEmpty();
    }

    private String cest = "";
    private double x, y;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.listarGrupos();
        this.listarSubGrupos();
        this.adicionarEventos();
        this.converterComboBox();
        editDescricao.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                editPreco.requestFocus();
                return;
            }
            if (e.getCode() == KeyCode.F2) {
                this.abrirPesquisa();
            }
        });
        editDescricao.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            if (newValue.length() <= 35) {
                editDescricao.setText(newValue.toUpperCase());
            } else {
                editDescricao.setText("");
                editDescricao.setText(oldValue.toUpperCase());
            }
        });
        editReferencia.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            if (newValue.length() <= 20) {
                editReferencia.setText(newValue.toUpperCase());
            } else {
                editReferencia.setText("");
                editReferencia.setText(oldValue.toUpperCase());
            }
        });

        editNcm.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            if (newValue != null) {
                editNcm.setText(newValue.replaceAll("\\D", ""));
                if (editNcm.getText().length() <= 8) {
                    editNcm.setText(newValue);
                } else {
                    editNcm.setText("");
                    editNcm.setText(oldValue);
                }
            }
        });
        editEstoqueInicial.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            if (newValue != null) {
                editEstoqueInicial.setText(newValue.replaceAll("^[aA-zZ]", ""));
            }
        });
        editCest.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            if (newValue != null) {
                editCest.setText(newValue.replaceAll("\\D", ""));
                if (editCest.getText().length() <= 7) {
                    editCest.setText(newValue);
                } else {
                    editCest.setText("");
                    editCest.setText(oldValue);
                }
            }
        });

        editNcm.setOnAction(e -> {
            if (!editCest.isDisable()) {
                editCest.requestFocus();
                return;
            }
            tributacao.requestFocus();
        });
        labelCest.setOnMouseClicked(e -> {
            if (this.tributacao.getSelectionModel().getSelectedIndex() == 2) {
                abrirCest();
            }
        });
        labelReferencia.setOnMouseClicked(e -> {
            try {
                ativarProduto();
            } catch (SQLException ex) {
                alert.setAlertType(Alert.AlertType.ERROR);
                alert.setTitle("Erro");
                alert.setContentText("Erro ao Cancelar/Ativar Produto.");
                alert.initOwner(this.pegarTelaCadastroProduto());
                alert.show();
            }
        });
        editCest.focusedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            if (!newValue) {
                cest = editCest.getText();
            }
        });
        tributacao.setOnAction(e -> {
            if (tributacao.getSelectionModel().getSelectedIndex() != 2) {
                editCest.setText("");
                editCest.setDisable(true);
                return;
            }
            editCest.setText(cest);
            editCest.setDisable(false);
            editCest.requestFocus();
        });
        if (tributacao.getSelectionModel().getSelectedIndex() != 2) {
            editCest.setText("");
            editCest.setDisable(true);
        }
        this.iniciarValores();
        ancoraPrincipal.setOnMousePressed(evt -> {
            x = evt.getSceneX();
            y = evt.getSceneY();
        });

        ancoraPrincipal.setOnMouseDragged(evt -> {
            FrmBancoController.stageFrmAlteracao.setX(evt.getScreenX() - x);
            FrmBancoController.stageFrmAlteracao.setY(evt.getScreenY() - y);
        });

    }

    private void adicionandoEventosComThread() {
        editReferencia.focusedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            if (!newValue) {
                thread = null;
                thread = new Thread() {
                    @Override
                    public void run() {
                        Platform.runLater(() -> paneModal.setVisible(true));
                        referencia();
                    }
                };
                thread.start();
            }
        });
        ancoraPrincipal.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            if (e.getCode().equals(KeyCode.F1)) {
                thread = null;
                thread = new Thread() {
                    @Override
                    public void run() {
                        paneModal.setVisible(true);
                        salvar();
                    }
                };
                thread.start();
            } else if (e.getCode().equals(KeyCode.ESCAPE)) {
                sair();
            } else if (e.getCode().equals(KeyCode.F2)) {
                this.abrirMovimentacaoDeEstoque();
            }
        });
        btSalvar.setOnAction(evt -> {
            thread = null;
            thread = new Thread() {
                @Override
                public void run() {
                    paneModal.setVisible(true);
                    salvar();
                }
            };
            thread.start();
        });
    }

    private void iniciarValores() {
        editCest.setText(cest);
        editCest.setDisable(true);
        unidade.setItems(unidades);
        tributacao.setItems(tributacoes);
        editPreco.setText("0,00");
        editPrecoAtacado.setText("0,00");
        editQtdAtacado.setText("0,00");
        grupo.getSelectionModel().select(0);
        subGrupo.getSelectionModel().select(0);
        tributacao.getSelectionModel().select(0);
        unidade.getSelectionModel().select(0);
    }

    private void converterComboBox() {
        grupo.setConverter(new StringConverter<Grupo>() {
            @Override
            public String toString(Grupo object) {
                return object.getCodigo() + " - " + object.getNome();
            }

            @Override
            public Grupo fromString(String string) {
                String codigo = string.split(Pattern.quote("-"))[0].trim();
                return new Grupo(codigo);
            }
        });
        subGrupo.setConverter(new StringConverter<SubGrupo>() {
            @Override
            public String toString(SubGrupo object) {
                return object.getCodigo() + " - " + object.getNome();
            }

            @Override
            public SubGrupo fromString(String string) {
                String codigo = string.split(Pattern.quote("-"))[0].trim();
                return new SubGrupo(codigo);
            }
        });
    }

    private void minimizar() {
        ((Stage) this.pegarTelaCadastroProduto()).setIconified(true);
    }

    private void adicionarEventos() {
        this.labelMinimizar.setOnMouseClicked(e -> this.minimizar());
        this.btRelatorioProduto.setOnAction(evt -> this.abrirTelaDeRelatorio());
        this.editReferencia.setOnAction(e -> this.editDescricao.requestFocus());
        this.editPreco.setOnAction(e -> this.editPrecoAtacado.requestFocus());
        this.editPrecoAtacado.setOnAction(e -> this.editPrecoEspecial.requestFocus());
        this.editPrecoEspecial.setOnAction(e -> this.editNcm.requestFocus());
        this.editCest.setOnAction(e -> this.tributacao.requestFocus());
        this.editCest.setOnAction(e -> this.tributacao.requestFocus());
        this.tributacao.setOnAction(e -> this.unidade.requestFocus());
        this.labelNcm.setOnMouseClicked(e -> abrirCosmos());
        this.btGrupo.setOnAction(evt -> this.abrirCadastroDeGrupo());
        this.btSubGrupo.setOnAction(evt -> this.abrirCadastroDeSubGrupo());
        this.btEstoque.setOnAction(evt -> this.abrirMovimentacaoDeEstoque());
        this.adicionandoEventosComThread();
    }

    private boolean opcoes() {
        alert.setAlertType(Alert.AlertType.CONFIRMATION);
        String mensagem = labelReferencia.getText().length() == 10 ? "Deseja cancelar o Produto?" : "Deseja ativar o Produto?";
        alert.setContentText(mensagem);
        Optional<ButtonType> optional = alert.showAndWait();
        return ButtonType.OK == optional.get();
    }

    private void pesquisarProduto(Produto produto) {
        editReferencia.setText(produto.getReferencia());
        editDescricao.setText(produto.getDescricao());
        if (produto.getPreco() != null) {
            editPreco.setText(df.format(produto.getPreco()));
        }
        if (produto.getPreco() != null) {
            editPrecoAtacado.setText(df.format(produto.getPrecoAtacado()));
        }
        if (produto.getPrecoEspecial() != null) {
            editPrecoEspecial.setText(df.format(produto.getPrecoEspecial()));
        }
        editQtdAtacado.setText(String.valueOf(produto.getQtdAtacado()));
        editNcm.setText(produto.getNcm());
        switch (produto.getTributacao()) {
            case "0001":
                tributacao.getSelectionModel().select(0);
                break;
            case "0400":
                tributacao.getSelectionModel().select(1);
                break;
            default:
                tributacao.getSelectionModel().select(2);
                break;
        }
        editCest.setText(produto.getCest());
        textAplicacaoComposicao.setText(produto.getAplicacao());
        unidade.getSelectionModel().select(produto.getUnidade());
        grupo.getSelectionModel().select(0);
        subGrupo.getSelectionModel().select(0);
        if (produto.getGrupo() != null) {
            grupo.getSelectionModel().select(new Grupo(produto.getGrupo()));
            subGrupo.getSelectionModel().select(new SubGrupo(produto.getSubgrupo()));
        }
        editEstoqueInicial.setText(produto.getQuantidade() != null ? String.valueOf(produto.getQuantidade().intValue()) : "0");
        inativar.setSelected(produto.getDataCancelamento() != null);
        confirmaPreco.setSelected("S".equals(produto.getConfirmaPreco()));
        editPreco.requestFocus();
        editPreco.selectAll();
        this.produto = produto;
    }

    private void listarGrupos() {
        try {
            grupos.clear();
            List<Grupo> listarGrupos = produtoServico.listarGrupos();
            this.grupos.addAll(listarGrupos == null ? Arrays.asList() : listarGrupos);
            this.grupo.setItems(grupos);
        } catch (SQLException ex) {
        }
    }

    private void listarSubGrupos() {
        try {
            subGrupos.clear();
            List<SubGrupo> listarSubGrupos = produtoServico.listarSubGrupos();
            this.subGrupos.addAll(listarSubGrupos == null ? Arrays.asList() : listarSubGrupos);
            this.subGrupo.setItems(subGrupos);
        } catch (SQLException ex) {
        }
    }

    private void abrirPesquisa() {
        try {
            this.ancoraPrincipal.setEffect(new BoxBlur(10, 10, 10));
            FXMLLoader fXMLLoader = new FXMLLoader(getClass().getResource("/visao/FrmPesquisa.fxml"));
            Parent parent = fXMLLoader.load();
            FrmPesquisaController controller = fXMLLoader.getController();
            controller.editPesquisa.setText(editDescricao.getText());
            Scene scene = new Scene(parent);
            Stage stage = new Stage(StageStyle.UNDECORATED);
            stage.setScene(scene);
            stage.initOwner(this.pegarTelaCadastroProduto());
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            this.ancoraPrincipal.setEffect(null);
            if (controller.tabela.getSelectionModel().getSelectedItem() == null) {
                return;
            }
            if (!controller.isSelecionouRegistro()) {
                return;
            }
            referencia = controller.tabela.getSelectionModel().getSelectedItem().getReferencia();
            Produto buscarProduto = produtoServico.buscarProduto(referencia);
            String descricao = buscarProduto.getDescricao();
            Double preco = buscarProduto.getPreco();
            Double precoAtacado = buscarProduto.getPrecoAtacado();
            Double qtdAtacado = buscarProduto.getQtdAtacado();
            Double precoEspecial = buscarProduto.getPrecoEspecial();
            codigoBarra = buscarProduto.getCodigoBarra();
            editDescricao.setText(descricao);
            editReferencia.setText(buscarProduto.getCodigoBarra() == null || buscarProduto.getCodigoBarra().isEmpty() ? referencia : buscarProduto.getCodigoBarra());
            editPreco.setText(df.format(preco));
            editPrecoAtacado.setText(df.format(precoAtacado));
            editQtdAtacado.setText(df.format(qtdAtacado));
            editPrecoEspecial.setText(df.format(precoEspecial));
            textAplicacaoComposicao.setText(buscarProduto.getAplicacao());
            tributacao.getSelectionModel().select(0);
            if (buscarProduto.getTributacao().replaceAll("\\D", "").equals("0600")) {
                tributacao.getSelectionModel().select(2);
            } else if (buscarProduto.getTributacao().replaceAll("\\D", "").equals("0400")) {
                tributacao.getSelectionModel().select(1);
            }
            editNcm.setText(buscarProduto.getNcm());
            editCest.setText(buscarProduto.getCest());
            editEstoqueInicial.setText(String.valueOf(buscarProduto.getQuantidade().intValue()));
            grupo.getSelectionModel().select(new Grupo(buscarProduto.getGrupo()));
            subGrupo.getSelectionModel().select(new SubGrupo(buscarProduto.getSubgrupo()));
            unidade.getSelectionModel().select(buscarProduto.getUnidade());
            inativar.setSelected(buscarProduto.getDataCancelamento() != null);
            confirmaPreco.setSelected("S".equals(buscarProduto.getConfirmaPreco()));
            editPreco.requestFocus();
            editPreco.selectAll();
            editEstoqueInicial.setDisable(true);
        } catch (IOException ex) {
            alert.setAlertType(Alert.AlertType.ERROR);
            alert.setTitle("Erro");
            alert.setContentText("Erro ao carregar o arquivo FrmPesquisa.fxml " + ex.getMessage());
            alert.initOwner(this.pegarTelaCadastroProduto());
            alert.show();
        }
    }

    private Window pegarTelaCadastroProduto() {
        return this.ancoraPrincipal.getScene().getWindow();
    }

    private void abrirCadastroDeGrupo() {
        BoxBlur boxBlur = new BoxBlur(10, 10, 10);
        this.ancoraPrincipal.setEffect(boxBlur);
        try {
            FXMLLoader fXMLLoader = new FXMLLoader(getClass().getResource("/visao/FrmGrupo.fxml"));
            Parent root = fXMLLoader.load();
            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT);
            Stage stage = new Stage(StageStyle.TRANSPARENT);
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(this.pegarTelaCadastroProduto());
            stage.showAndWait();
            this.ancoraPrincipal.setEffect(null);
            this.listarGrupos();
            this.grupo.getSelectionModel().select(0);
        } catch (IOException ex) {
            alert.setAlertType(Alert.AlertType.ERROR);
            alert.setTitle("Erro");
            alert.setContentText("Erro ao carregar o arquivo FrmGrupo.fxml " + ex.getMessage());
            alert.initOwner(this.pegarTelaCadastroProduto());
            alert.show();
        }
    }

    private void abrirCadastroDeSubGrupo() {
        BoxBlur boxBlur = new BoxBlur(10, 10, 10);
        this.ancoraPrincipal.setEffect(boxBlur);
        try {
            FXMLLoader fXMLLoader = new FXMLLoader(getClass().getResource("/visao/FrmSubGrupo.fxml"));
            Parent root = fXMLLoader.load();
            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT);
            Stage stage = new Stage(StageStyle.TRANSPARENT);
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(this.pegarTelaCadastroProduto());
            stage.showAndWait();
            this.ancoraPrincipal.setEffect(null);
            this.listarSubGrupos();
            this.subGrupo.getSelectionModel().select(0);
        } catch (IOException ex) {
            alert.setAlertType(Alert.AlertType.ERROR);
            alert.setTitle("Cadastro Rapido");
            alert.setContentText("Erro ao carregar o arquivo FrmGrupo.fxml " + ex.getMessage());
            alert.initOwner(this.pegarTelaCadastroProduto());
            alert.show();
        }
    }

    private void abrirMovimentacaoDeEstoque() {
        try {
            FXMLLoader fXMLLoader = new FXMLLoader(getClass().getResource("/visao/FrmMovimentoEstoque.fxml"));
            Parent root = fXMLLoader.load();
            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT);
            Stage stage = new Stage(StageStyle.TRANSPARENT);
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(this.pegarTelaCadastroProduto());
            stage.showAndWait();
        } catch (IOException ex) {
            alert.setAlertType(Alert.AlertType.ERROR);
            alert.setTitle("Cadastro Rapido");
            alert.setContentText("Erro ao carregar o arquivo FrmMovimentoEstoque.fxml " + ex.getMessage());
            alert.initOwner(this.pegarTelaCadastroProduto());
            alert.show();
        }
    }

    private void abrirTelaDeRelatorio() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/visao/FrmRelatorio.fxml"));
            Stage stage = new Stage(StageStyle.TRANSPARENT);
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(this.pegarTelaCadastroProduto());
            stage.show();
        } catch (IOException ex) {
            alert.setAlertType(Alert.AlertType.ERROR);
            alert.setTitle("Cadastro Rapido");
            alert.setContentText("Erro ao carregar o arquivo FrmRelatorio.fxml " + ex.getMessage());
            alert.initOwner(this.pegarTelaCadastroProduto());
            alert.show();
        }
    }
}
