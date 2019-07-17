package controle;

import com.jfoenix.controls.JFXButton;
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
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.StringConverter;
import modelo.Cosmos;
import modelo.Grupo;
import modelo.Produto;
import modelo.SubGrupo;
import servico.ProdutoServico;

public class FrmAlteracaoPrecoController implements Initializable {

    @FXML
    private AnchorPane ancoraPrincipal;
    @FXML
    private AnchorPane ancoraAjuda;
    @FXML
    private Pane paneModal;

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
    private JFXToggleButton inativar;

    @FXML
    private JFXButton btSubGrupo;

    @FXML
    private JFXButton btGrupo;

    @FXML
    private JFXToggleButton confirmaPreco;

    private String referencia, codigoBarra;
    private final ObservableList<String> tributacoes = FXCollections.observableArrayList("0001 TRIBUTADO", "0400 ISENTO", "0600 TRIBUTADO ST");
    private ObservableList<Grupo> grupos = FXCollections.observableArrayList();
    private ObservableList<SubGrupo> subGrupos = FXCollections.observableArrayList();
    private final ObservableList<String> unidades = FXCollections.observableArrayList("UN", "KG", "CX", "FD", "M2", "PC", "ML", "PA");
    private Produto produto = new Produto();
    private final ProdutoServico produtoServico = new ProdutoServico();
    private final DecimalFormat df = new DecimalFormat("#,###0.00");
    private Thread thread;

    public void sair() {
        Platform.exit();
    }

    @FXML
    private void referencia() {
        referencia = editReferencia.getText();
        codigoBarra = "";
        Produto p, p2;
        p = produtoServico.buscarProduto(referencia);
        p2 = Cosmos.buscarProduto(referencia);
        if (p != null && p2 != null) {
            p.setNcm(p2.getNcm() == null || p2.getNcm().isEmpty() ? p.getNcm() : p2.getNcm());
            p.setCest(p2.getCest() == null || p2.getCest().isEmpty() ? p.getCest() : p2.getCest());
            p.setTributacao(p2.getTributacao() == null || p2.getTributacao().isEmpty() ? p.getTributacao() : p2.getTributacao());
            referencia = p.getReferencia();
            codigoBarra = p.getCodigoBarra();
            Platform.runLater(() -> {
                pesquisarProduto(p);
                labelReferencia.setText(p.getDataCancelamento() == null ? "Referencia" : "Referencia - Cancelada");
                editReferencia.setStyle(p.getDataCancelamento() == null ? "-fx-text-fill:#757575" : "-fx-text-fill:red");
                editEstoqueInicial.setDisable(true);
            });
        } else if (p == null && p2 != null) {
            referencia = p2.getReferencia();
            codigoBarra = p2.getReferencia();
            Platform.runLater(() -> {
                pesquisarProduto(p2);
                editEstoqueInicial.setDisable(true);
            });
        } else if (p2 == null && p != null) {
            referencia = p.getReferencia();
            codigoBarra = p.getCodigoBarra();
            Platform.runLater(() -> {
                pesquisarProduto(p);
                labelReferencia.setText(p.getDataCancelamento() == null ? "Referencia" : "Referencia - Cancelada");
                editReferencia.setStyle(p.getDataCancelamento() == null ? "-fx-text-fill:#757575" : "-fx-text-fill:red");
                editEstoqueInicial.setDisable(true);
            });
        } else {
            Platform.runLater(() -> {
                if (editReferencia.getText().isEmpty()) {
                    long gerarReferencia = produtoServico.gerarReferencia();
                    editReferencia.setText(String.valueOf(gerarReferencia));
                }
                paneModal.setVisible(false);
                editDescricao.requestFocus();
                unidade.getSelectionModel().select(0);
                codigoBarra = "";
                editEstoqueInicial.setText("1");
                editEstoqueInicial.setDisable(false);
            });
        }
        paneModal.setVisible(false);
        if (thread != null) {
            thread.stop();
        }
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
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro");
            alert.setHeaderText(ex.getMessage());
            alert.setContentText("Erro ao acessar o site do cosmos");
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
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro");
            alert.setContentText("Erro ao abrir navegador");
            alert.show();
        } catch (URISyntaxException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro");
            alert.setContentText("Erro ao abrir o site cosmos");
            alert.show();
        }
    }

    @FXML
    private void abrirEstoque() {
        try {
            FXMLLoader fXMLLoader = new FXMLLoader(getClass().getResource("/visao/FrmMovimentoEstoque.fxml"));
            Parent parent = fXMLLoader.load();
            Scene scene = new Scene(parent);
            Stage stage = new Stage(StageStyle.UNDECORATED);
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setMaximized(false);
            stage.showAndWait();
        } catch (IOException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro");
            alert.setContentText("Erro ao tela estoque");
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
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro");
            alert.setContentText("Erro ao acessar o site do cosmos");
            alert.show();
        } catch (IOException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro");
            alert.setContentText("Erro ao abrir navegador");
            alert.show();
        }
    }

    private void ativarProduto() throws SQLException {
        if (produto.getReferencia() != null) {
            if (opcoes()) {
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
        }
    }

    private boolean validarPreco() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        if (editReferencia.getText().isEmpty()) {
            alert.setTitle("AVISO");
            alert.setContentText("Campo Referencia não pode ser Vazio");
            alert.showAndWait();
            editReferencia.requestFocus();
            return false;
        }
        if (editDescricao.getText().length() > 35) {
            alert.setTitle("AVISO");
            alert.setContentText("Campo descrição deve ser no minimo 35 caracteres.");
            alert.showAndWait();
            editDescricao.requestFocus();
            return false;
        }
        if (editDescricao.getText() == null) {
            alert.setTitle("AVISO");
            alert.setContentText("Campo descrição deve ser informado.");
            alert.showAndWait();
            editDescricao.requestFocus();
            return false;
        }
        if (editNcm.getText() == null || editNcm.getText().isEmpty()) {
            alert.setTitle("AVISO");
            alert.setContentText("Campo Ncm deve ser informado.");
            alert.showAndWait();
            editNcm.requestFocus();
            return false;
        }
        if (!editNcm.getText().isEmpty() && editNcm.getText().length() < 8) {
            alert.setTitle("AVISO");
            alert.setContentText("Campo Ncm não pode ter menos que 8 digitos");
            alert.showAndWait();
            alert.showAndWait();
            editNcm.requestFocus();
            return false;
        }
        if (unidade.getSelectionModel().getSelectedItem() == null) {
            alert.setTitle("AVISO");
            alert.setContentText("Selecione alguma unidade.");
            alert.showAndWait();
            alert.showAndWait();
            return false;
        }
        if (tributacao.getSelectionModel().getSelectedIndex() == 2) {
            if (editCest.getText() != null) {
                if (tributacao.getSelectionModel().getSelectedIndex() != 2 && !editCest.getText().isEmpty()) {
                    alert.setTitle("AVISO");
                    alert.setContentText("Tributação " + tributacao.getSelectionModel().getSelectedItem() + " não pode ter cest");
                    alert.showAndWait();
                    tributacao.getSelectionModel().select(2);
                    editCest.requestFocus();
                    return false;
                }
            }
            if (editCest.getText() == null || editCest.getText().isEmpty()) {
                alert.setTitle("AVISO");
                alert.setContentText("Campo Cest deve ser informado");
                alert.showAndWait();
                editCest.requestFocus();
                return false;
            }
            if (!editCest.getText().isEmpty() && editCest.getText().length() < 7) {
                alert.setTitle("AVISO");
                alert.setContentText("Campo Cest não pode ter menos que 7 digitos");
                alert.showAndWait();
                editCest.requestFocus();
                return false;
            }
        }
        if (editQtdAtacado.getText() == null || editQtdAtacado.getText().isEmpty()) {
            alert.setTitle("AVISO");
            alert.setContentText("Campo Quantidade em atacado não pode ser Vazio");
            alert.showAndWait();
            editQtdAtacado.requestFocus();
            return false;
        }
        if (Double.parseDouble(editQtdAtacado.getText().replaceAll(",", ".")) >= 10000) {
            alert.setTitle("AVISO");
            alert.setContentText("Campo Quantidade em atacado não pode ser maior que 10 mil");
            alert.showAndWait();
            editQtdAtacado.requestFocus();
            return false;
        }
        if (editPreco.getText().trim().isEmpty()) {
            alert.setTitle("AVISO");
            alert.setContentText("Campo Preço não pode ser Vazio");
            alert.showAndWait();
            editPreco.requestFocus();
            return false;
        } else if (editPreco.getText().trim().length() >= 12) {
            alert.setTitle("AVISO");
            alert.setContentText("Valor muito grade para o preço");
            alert.showAndWait();
            editPreco.requestFocus();
            editPreco.selectAll();
            return false;
        }
        Pattern pattern = Pattern.compile("[aA-zZ]");
        Matcher matcher = pattern.matcher(editPreco.getText().trim());
        while (matcher.find()) {
            alert.setTitle("AVISO");
            alert.setContentText("Campo preço só pode receber números");
            alert.showAndWait();
            editPreco.requestFocus();
            editPreco.selectAll();
            return false;
        }
        if (Double.parseDouble(editPreco.getText().replace(",", ".")) < 0) {
            alert.setTitle("AVISO");
            alert.setContentText("Valor do Produto não pode ser negativo");
            alert.showAndWait();
            editPreco.requestFocus();
            editPreco.selectAll();
            return false;
        }

        return true;
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
            if (validarPreco()) {
                Double preco, precoAtacado, qtdAtacado;
                preco = formatarPreco(editPreco.getText());
                precoAtacado = formatarPreco(editPrecoAtacado.getText());
                qtdAtacado = formatarPreco(editQtdAtacado.getText());
                referencia = editReferencia.getText();
                produto.setReferencia(referencia);
                if (codigoBarra != null) {
                    if (!codigoBarra.isEmpty()) {
                        produto.setCodigoBarra(codigoBarra);
                    } else {
                        produto.setCodigoBarra(referencia);
                    }
                } else {
                    produto.setCodigoBarra(referencia);
                }
                produto.setDescricao(editDescricao.getText());
                produto.setPreco(preco);
                produto.setPrecoAtacado(precoAtacado);
                produto.setQtdAtacado(qtdAtacado);
                produto.setNcm(editNcm.getText());
                produto.setCest(editCest.getText());
                produto.setTributacao(tributacao.getSelectionModel().getSelectedItem().replaceAll("\\D", ""));
                produto.setUnidade(unidade.getSelectionModel().getSelectedItem());
                produto.setGrupo(this.grupo.getSelectionModel().getSelectedItem().getCodigo());
                produto.setSubgrupo(this.subGrupo.getSelectionModel().getSelectedItem().getCodigo());
                String estoque = editEstoqueInicial.getText().replace(",", ".");
                produto.setQuantidade(Double.parseDouble(estoque));
                String dataCancelamento = null;
                if (inativar.isSelected()) {
                    dataCancelamento = LocalDate.now().toString();
                }
                produto.setDataCancelamento(dataCancelamento);
                String confirmaPreco = "N";
                if (this.confirmaPreco.isSelected()) {
                    confirmaPreco = "S";
                }
                produto.setConfirmaPreco(confirmaPreco);
                produtoServico.salvar(produto);
                editReferencia.requestFocus();
                editReferencia.selectAll();
                codigoBarra = "";
                editEstoqueInicial.setDisable(false);
            }
        });
        paneModal.setVisible(false);
        if (thread != null) {
            thread.stop();
        }
    }
    private String cest = "";
    private double x, y;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        listarGrupos();
        listarSubGrupos();
        this.btGrupo.setOnAction(evt -> this.abrirCadastroDeGrupo());
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
        editDescricao.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                editPreco.requestFocus();
            } else if (e.getCode() == KeyCode.F2) {
                abrirPesquisa();
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
                editEstoqueInicial.setText(newValue.replaceAll("\\D", ""));
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
        editPreco.setOnAction(e -> editQtdAtacado.requestFocus());
        editQtdAtacado.setOnAction(e -> editPrecoAtacado.requestFocus());
        editPrecoAtacado.setOnAction(e -> editNcm.requestFocus());
        editNcm.setOnAction(e -> {
            if (!editCest.isDisable()) {
                editCest.requestFocus();
            } else {
                tributacao.requestFocus();
            }
        });
        editCest.setOnAction(e -> tributacao.requestFocus());
        editCest.setOnAction(e -> tributacao.requestFocus());
        tributacao.setOnAction(e -> unidade.requestFocus());
        labelNcm.setOnMouseClicked(e -> abrirCosmos());
        labelCest.setOnMouseClicked(e -> {
            if (this.tributacao.getSelectionModel().getSelectedIndex() == 2) {
                abrirCest();
            }
        });
        labelReferencia.setOnMouseClicked(e -> {
            try {
                ativarProduto();
            } catch (SQLException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erro");
                alert.setContentText("Erro ao Cancelar/Ativar Produto.");
                alert.show();
            }
        });

        tributacao.setItems(tributacoes);

        editCest.focusedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            if (!newValue) {
                cest = editCest.getText();
            }
        });
        editReferencia.focusedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            if (!newValue) {
                thread = new Thread() {
                    @Override
                    public void run() {
                        paneModal.setVisible(true);
                        referencia();
                    }
                };
                thread.start();
            }
        });
        editReferencia.setOnAction(e -> editDescricao.requestFocus());
        tributacao.setOnAction(e -> {
            if (tributacao.getSelectionModel().getSelectedIndex() != 2) {
                editCest.setText("");
                editCest.setDisable(true);
            } else {
                editCest.setText(cest);
                editCest.setDisable(false);
                editCest.requestFocus();
            }
        });
        if (tributacao.getSelectionModel().getSelectedIndex() != 2) {
            editCest.setText("");
            editCest.setDisable(true);
        } else {
            editCest.setText(cest);
            editCest.setDisable(false);
        }
        ancoraPrincipal.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            if (e.getCode().equals(KeyCode.F1)) {
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
            }
        });
        labelMinimizar.setOnMouseClicked(e -> {
            ((Stage) ancoraPrincipal.getScene().getWindow()).setIconified(true);
        });
        tributacao.getSelectionModel().select(0);
        grupo.getSelectionModel().select(0);
        subGrupo.getSelectionModel().select(0);
        unidade.setItems(unidades);
        unidade.getSelectionModel().select(0);
        editPreco.setText("0,00");
        editPrecoAtacado.setText("0,00");
        editQtdAtacado.setText("0,00");
        btSalvar.setOnAction(evt -> {
            paneModal.setVisible(true);
            thread = new Thread() {
                @Override
                public void run() {
                    paneModal.setVisible(true);
                    salvar();
                }
            };
            thread.start();
        });
        ancoraPrincipal.setOnMousePressed(evt -> {
            x = evt.getSceneX();
            y = evt.getSceneY();
        });

        ancoraPrincipal.setOnMouseDragged(evt -> {
            FrmBancoController.stageFrmAlteracao.setX(evt.getScreenX() - x);
            FrmBancoController.stageFrmAlteracao.setY(evt.getScreenY() - y);
        });
    }

    private boolean opcoes() {
        Alert alert = new Alert(javafx.scene.control.Alert.AlertType.CONFIRMATION);
        String mensagem = labelReferencia.getText().length() == 10 ? "Deseja cancelar o Produto?" : "Deseja ativar o Produto?";
        alert.setContentText(mensagem);
        Optional optional = alert.showAndWait();
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
            this.grupos.addAll(listarGrupos);
            this.grupo.setItems(grupos);
        } catch (SQLException ex) {
        }
    }

    private void listarSubGrupos() {
        try {
            subGrupos.clear();
            List<SubGrupo> listarSubGrupos = produtoServico.listarSubGrupos();
            this.subGrupos.addAll(listarSubGrupos);
            this.subGrupo.setItems(subGrupos);
        } catch (SQLException ex) {
        }
    }

    private void abrirPesquisa() {
        try {
            FXMLLoader fXMLLoader = new FXMLLoader(getClass().getResource("/visao/FrmPesquisa.fxml"));
            Parent parent = fXMLLoader.load();
            FrmPesquisaController controller = fXMLLoader.getController();
            controller.editPesquisa.setText(editDescricao.getText());
            Scene scene = new Scene(parent);
            Stage stage = new Stage(StageStyle.UNDECORATED);
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            if (controller.tabela.getSelectionModel().getSelectedItem() != null) {
                if (controller.isSelecionouRegistro()) {
                    referencia = controller.tabela.getSelectionModel().getSelectedItem().getReferencia();
                    Produto buscarProduto = produtoServico.buscarProduto(referencia);
                    Produto cosmosProduto = null;
                    if (buscarProduto.getCodigoBarra() != null) {
                        if (!buscarProduto.getCodigoBarra().isEmpty()) {
                            cosmosProduto = Cosmos.buscarProduto(buscarProduto.getCodigoBarra());
                        }
                    }
                    String descricao = buscarProduto.getDescricao();
                    Double preco = buscarProduto.getPreco();
                    Double precoAtacado = buscarProduto.getPrecoAtacado();
                    Double qtdAtacado = buscarProduto.getQtdAtacado();
                    codigoBarra = buscarProduto.getCodigoBarra();
                    editDescricao.setText(descricao);
                    editReferencia.setText(buscarProduto.getCodigoBarra() == null || buscarProduto.getCodigoBarra().isEmpty() ? referencia : buscarProduto.getCodigoBarra());
                    editPreco.setText(df.format(preco));
                    editPrecoAtacado.setText(df.format(precoAtacado));
                    editQtdAtacado.setText(df.format(qtdAtacado));
                    if (cosmosProduto != null) {
                        tributacao.getSelectionModel().select(0);
                        if (cosmosProduto.getTributacao().replaceAll("\\D", "").equals("0600")) {
                            tributacao.getSelectionModel().select(2);
                        }
                        editNcm.setText(cosmosProduto.getNcm());
                        editCest.setText(cosmosProduto.getCest());
                    } else {
                        tributacao.getSelectionModel().select(0);
                        if (buscarProduto.getTributacao().replaceAll("\\D", "").equals("0600")) {
                            tributacao.getSelectionModel().select(2);
                        } else if (buscarProduto.getTributacao().replaceAll("\\D", "").equals("0400")) {
                            tributacao.getSelectionModel().select(1);
                        }
                        editNcm.setText(buscarProduto.getNcm());
                        editCest.setText(buscarProduto.getCest());
                    }
                    editEstoqueInicial.setText(String.valueOf(buscarProduto.getQuantidade().intValue()));
                    grupo.getSelectionModel().select(new Grupo(buscarProduto.getGrupo()));
                    subGrupo.getSelectionModel().select(new SubGrupo(buscarProduto.getSubgrupo()));
                    unidade.getSelectionModel().select(buscarProduto.getUnidade());
                    inativar.setSelected(buscarProduto.getDataCancelamento() != null);
                    confirmaPreco.setSelected("S".equals(buscarProduto.getConfirmaPreco()));
                    editPreco.requestFocus();
                    editPreco.selectAll();
                    editEstoqueInicial.setDisable(true);
                }
            }
        } catch (IOException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro");
            alert.setContentText("Erro ao carregar o arquivo FrmPesquisa.fxml " + ex.getMessage());
            alert.show();
        }
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
            stage.showAndWait();
            this.ancoraPrincipal.setEffect(null);
        } catch (IOException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro");
            alert.setContentText("Erro ao carregar o arquivo FrmGrupo.fxml " + ex.getMessage());
            alert.show();
        }

    }

}
