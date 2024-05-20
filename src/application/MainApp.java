package application;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Pair;
import javafx.util.StringConverter;

public class MainApp extends Application {
    private List<Vetor> vetores = new ArrayList<>();
    private ListView<String> listView = new ListView<>();

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Gerenciador de Vetores");
        BorderPane root = new BorderPane();
        Scene scene = new Scene(root, 600, 400);

        MenuBar menuBar = new MenuBar();
        Menu menuVetor = new Menu("Vetor");
        Menu menuCalcular = new Menu("Calcular");
        menuVetor.getItems().addAll(criarMenuItemAdicionar(), criarMenuItemVisualizarRemover());
        menuCalcular.getItems().addAll(criarMenuCalculos("Calcular 2D", 2), criarMenuCalculos("Calcular 3D", 3));
        menuBar.getMenus().addAll(menuVetor, menuCalcular);
        root.setTop(menuBar);

        listView.setEditable(false);
        root.setCenter(listView);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private MenuItem criarMenuItemAdicionar() {
        MenuItem addItem = new MenuItem("Adicionar Vetor");
        addItem.setOnAction(e -> adicionarVetor());
        return addItem;
    }

    private MenuItem criarMenuItemVisualizarRemover() {
        MenuItem viewRemoveItem = new MenuItem("Visualizar/Remover Vetores");
        viewRemoveItem.setOnAction(e -> visualizarRemoverVetores());
        return viewRemoveItem;
    }

    private Menu criarMenuCalculos(String title, int dimension) {
        Menu menu = new Menu(title);
        menu.getItems().addAll(
            criarMenuItem("Magnitude", () -> calcularUnicoVetor(dimension, this::calcularMagnitude)),
            criarMenuItem("Produto Escalar", () -> calcularDoisVetores(dimension, this::calcularProdutoEscalar)),
            criarMenuItem("Ângulo", () -> calcularDoisVetores(dimension, this::calcularAngulo)),
            criarMenuItem("Produto Vetorial", () -> calcularDoisVetores(dimension, this::calcularProdutoVetorial))
        );
        return menu;
    }

    private MenuItem criarMenuItem(String text, Runnable action) {
        MenuItem menuItem = new MenuItem(text);
        menuItem.setOnAction(e -> action.run());
        return menuItem;
    }

    private void adicionarVetor() {
        Dialog<Vetor> dialog = criarDialogoVetor();
        Optional<Vetor> result = dialog.showAndWait();
        result.ifPresent(vetor -> {
            vetores.add(vetor);
            updateListView();
        });
    }

    private Dialog<Vetor> criarDialogoVetor() {
        Dialog<Vetor> dialog = new Dialog<>();
        dialog.setTitle("Adicionar Vetor");
        dialog.setHeaderText("Insira as coordenadas do vetor:");

        VBox vbox = new VBox();
        TextField xField = new TextField();
        xField.setPromptText("X");
        TextField yField = new TextField();
        yField.setPromptText("Y");
        TextField zField = new TextField();
        zField.setPromptText("Z (opcional)");

        vbox.getChildren().addAll(new Label("X:"), xField, new Label("Y:"), yField, new Label("Z:"), zField);
        dialog.getDialogPane().setContent(vbox);

        ButtonType buttonTypeOk = new ButtonType("Adicionar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(buttonTypeOk, ButtonType.CANCEL);

        dialog.setResultConverter(b -> {
            if (b == buttonTypeOk) {
                Double x = Double.parseDouble(xField.getText());
                Double y = Double.parseDouble(yField.getText());
                Double z = zField.getText().isEmpty() ? 0.0 : Double.parseDouble(zField.getText());
                return new Vetor(x, y, z);
            }
            return null;
        });
        return dialog;
    }

    private void visualizarRemoverVetores() {
        ChoiceDialog<Vetor> dialog = new ChoiceDialog<>(null, vetores);
        dialog.setTitle("Visualizar/Remover Vetores");
        dialog.setHeaderText("Selecione um vetor para remover:");
        dialog.setContentText("Vetores:");
        Optional<Vetor> result = dialog.showAndWait();
        result.ifPresent(vetor -> {
            vetores.remove(vetor);
            updateListView();
        });
    }

    private void calcularUnicoVetor(int dimension, Consumer<Vetor> operation) {
        ChoiceDialog<Vetor> dialog = new ChoiceDialog<>(null, vetores);
        dialog.setTitle("Seleção de Vetor");
        dialog.setHeaderText("Selecione um vetor para calcular:");
        dialog.setContentText("Vetores:");
        Optional<Vetor> result = dialog.showAndWait();
        result.ifPresent(operation::accept);
    }

    private void calcularDoisVetores(int dimension, BiConsumer<Vetor, Vetor> operation) {
        Dialog<Pair<Vetor, Vetor>> dialog = new Dialog<>();
        dialog.setTitle("Seleção de Vetores para Cálculos");

        ComboBox<Vetor> comboBoxVetores1 = new ComboBox<>(FXCollections.observableArrayList(vetores));
        ComboBox<Vetor> comboBoxVetores2 = new ComboBox<>(FXCollections.observableArrayList(vetores));
        comboBoxVetores1.setConverter(new StringConverter<Vetor>() {
            @Override
            public String toString(Vetor vetor) {
                return vetor == null ? "Nenhum" : vetor.toString();
            }

            @Override
            public Vetor fromString(String string) {
                return null; // Não necessário
            }
        });

        comboBoxVetores2.setConverter(new StringConverter<Vetor>() {
            @Override
            public String toString(Vetor vetor) {
                return vetor == null ? "Nenhum" : vetor.toString();
            }

            @Override
            public Vetor fromString(String string) {
                return null; // Não necessário
            }
        });

        VBox vBox = new VBox(10, new Label("Selecione o primeiro vetor:"), comboBoxVetores1, new Label("Selecione o segundo vetor:"), comboBoxVetores2);
        dialog.getDialogPane().setContent(vBox);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                return new Pair<>(comboBoxVetores1.getValue(), comboBoxVetores2.getValue());
            }
            return null;
        });

        Optional<Pair<Vetor, Vetor>> result = dialog.showAndWait();
        result.ifPresent(pares -> {
            Vetor v1 = pares.getKey();
            Vetor v2 = pares.getValue();
            if (v1 != null && v2 != null) {
                operation.accept(v1, v2);
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erro de Seleção");
                alert.setHeaderText(null);
                alert.setContentText("Por favor, selecione dois vetores válidos para o cálculo.");
                alert.showAndWait();
            }
        });
    }

    private void calcularMagnitude(Vetor v) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Resultado do Cálculo");
        alert.setHeaderText(null);
        alert.setContentText("Magnitude do Vetor: " + v.calcularMagnitude());
        alert.showAndWait();
    }

    private void calcularProdutoEscalar(Vetor v1, Vetor v2) {
        double resultado = Vetor.calcularProdutoEscalar(v1, v2);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Resultado do Cálculo");
        alert.setHeaderText(null);
        alert.setContentText("Produto Escalar: " + resultado);
        alert.showAndWait();
    }

    private void calcularAngulo(Vetor v1, Vetor v2) {
        double angulo = Vetor.calcularAngulo(v1, v2);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Resultado do Cálculo");
        alert.setHeaderText(null);
        alert.setContentText("Ângulo entre Vetores: " + angulo);
        alert.showAndWait();
    }

    private void calcularProdutoVetorial(Vetor v1, Vetor v2) {
        Vetor resultado = Vetor.calcularProdutoVetorial(v1, v2);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Resultado do Cálculo");
        alert.setHeaderText(null);
        alert.setContentText("Produto Vetorial: " + resultado);
        alert.showAndWait();
    }

    private void updateListView() {
        ObservableList<String> items = FXCollections.observableArrayList();
        vetores.forEach(v -> items.add(v.toString()));
        listView.setItems(items);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
