package me.lucasgusmao.financeai.controller;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.BoxBlur;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import me.lucasgusmao.financeai.FinanceApplication;
import me.lucasgusmao.financeai.model.entity.Category;
import me.lucasgusmao.financeai.model.entity.Transaction;
import me.lucasgusmao.financeai.model.enums.CategoryType;
import me.lucasgusmao.financeai.service.AuthService;
import me.lucasgusmao.financeai.service.CategoryService;
import me.lucasgusmao.financeai.service.TransactionService;
import me.lucasgusmao.financeai.style.animation.AnimationFX;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

@Component
public class MainController {

    @FXML
    private VBox sidebar;

    @FXML
    private StackPane contentArea;

    @FXML
    private Label userName;

    @FXML
    private Label userInitials;

    @ FXML
    private StackPane modalOverlay;

    @Autowired
    private AuthService authService;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ApplicationContext springContext;

    private String currentView = "dashboard";

            //TODO add logica para voltar pro home, refatorar pra componentes pq isso ta ficando muito grande, add sistemas de metas, perfil e integrar IA
    @FXML
    public void initialize() {
        loadUserData();
        startAnimations();
    }

    private void loadUserData() {
        if (authService.getCurrentUser() != null) {
            String name = authService.getCurrentUser().getName();
            userName.setText(name);
            String[] nameParts = name.split(" ");
            String initials = nameParts.length > 1
                    ? nameParts[0].substring(0, 1) + nameParts[1].substring(0, 1)
                    : nameParts[0].substring(0, 2);
            userInitials.setText(initials.toUpperCase());
        }
    }


    private void startAnimations() {
        AnimationFX.fadeInUp(contentArea, 0.2);
    }
    //TODO fazer sistema de logout + profile
    @FXML
    private void handleLogout() {
        authService.logout();

        try {
            FXMLLoader loader = new FXMLLoader(
                    FinanceApplication.class.getResource("login-view.fxml")
            );
            loader.setControllerFactory(springContext::getBean);

            Scene scene = new Scene(loader.load(), 1400, 900);
            Stage stage = (Stage) sidebar.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("FinanceAI - Login");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleOpenAddModal() {
        System.out.println("funcionou a janela de seleção");
        BoxBlur blur = new BoxBlur(10, 10, 3);
        contentArea.setEffect(blur);

        Stage modalStage = new Stage();
        modalStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
        modalStage.initStyle(javafx.stage.StageStyle.TRANSPARENT);
        modalStage.initOwner(sidebar.getScene().getWindow());

        VBox modalContent = new VBox(24);
        modalContent.setAlignment(Pos.CENTER);
        modalContent.setStyle("-fx-background-color: #27272A; -fx-background-radius: 20; -fx-padding: 40; -fx-border-color: #3F3F46; -fx-border-width: 1; -fx-border-radius: 20; -fx-max-width: 500; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 30, 0, 0, 10);");
        VBox titleBox = new VBox(8);
        titleBox.setAlignment(Pos.CENTER);
        Label title = new Label("O que deseja criar?");
        title.setStyle("-fx-text-fill: #F4F4F5; -fx-font-size: 28px; -fx-font-weight: 700;");
        Label subtitle = new Label("Escolha uma opção abaixo");
        subtitle.setStyle("-fx-text-fill: #71717A; -fx-font-size: 14px;");
        titleBox.getChildren().addAll(title, subtitle);

        VBox buttonsBox = new VBox(16);

        Button categoryBtn = new Button("Categoria");
        categoryBtn.setMaxWidth(Double.MAX_VALUE);
        categoryBtn.setStyle("-fx-background-color: #18181B; -fx-background-radius: 16; -fx-text-fill: #F4F4F5; -fx-font-size: 18px; -fx-font-weight: 600; -fx-padding: 24; -fx-border-color: #3F3F46; -fx-border-width: 1; -fx-border-radius: 16; -fx-cursor: hand;");
        categoryBtn.setOnAction(e -> {
            modalStage.close();
            Platform.runLater(() -> openCategoryForm());
        });

        Button transactionBtn = new Button("Transação");
        transactionBtn.setMaxWidth(Double.MAX_VALUE);
        transactionBtn.setStyle("-fx-background-color: #18181B; -fx-background-radius: 16; -fx-text-fill: #F4F4F5; -fx-font-size: 18px; -fx-font-weight: 600; -fx-padding: 24; -fx-border-color: #3F3F46; -fx-border-width: 1; -fx-border-radius: 16; -fx-cursor: hand;");
        transactionBtn.setOnAction(e -> {
            modalStage.close();
            Platform.runLater(() -> openTransactionForm());
        });

        buttonsBox.getChildren().addAll(categoryBtn, transactionBtn);

        Button cancelBtn = new Button("Cancelar");
        cancelBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #71717A; -fx-font-size: 14px; -fx-font-weight: 600; -fx-cursor: hand; -fx-padding: 12 24;");
        cancelBtn.setOnAction(e -> {
            modalStage.close();
            contentArea.setEffect(null);
        });

        modalContent.getChildren().addAll(titleBox, buttonsBox, cancelBtn);

        StackPane root = new StackPane(modalContent);
        root.setStyle("-fx-background-color: transparent;");
        root.setPadding(new javafx.geometry.Insets(50));

        Scene scene = new Scene(root, 1280, 720);
        scene.setFill(javafx.scene.paint.Color.TRANSPARENT);

        modalStage.setScene(scene);
        modalStage.setOnHidden(e -> contentArea.setEffect(null));

        modalStage.showAndWait();
    }

    @FXML
    private void handleNavigateToTransactions() {
        currentView = "transactions";
        loadTransactionsView();
    }

    private void openTransactionForm() {
        System.out.println("[formulario nova transacao]: abrindo");

        Stage formStage = new Stage();
        formStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
        formStage.initStyle(javafx.stage.StageStyle.TRANSPARENT);
        formStage.initOwner(sidebar.getScene().getWindow());

        javafx.scene.effect.BoxBlur blur = new javafx.scene.effect.BoxBlur(10, 10, 3);
        contentArea.setEffect(blur);

        VBox formContent = new VBox(28);
        formContent.setAlignment(javafx.geometry.Pos.TOP_CENTER);
        formContent.setStyle("-fx-background-color: #27272A; -fx-background-radius: 16; -fx-padding: 48 40; -fx-border-color: #3F3F46; fx-border-width: 1; -fx-border-radius: 16; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 40, 0, 0, 10);-fx-max-width: 480;");

        VBox header = new VBox(8);
        header.setAlignment(javafx.geometry.Pos.CENTER);

        Label title = new Label("Nova Transação");
        title.setStyle("-fx-text-fill: #F4F4F5; -fx-font-size: 28px; -fx-font-weight: 600; -fx-letter-spacing: -0.5px;");

        Label subtitle = new Label("Preencha os campos abaixo para registrar uma nova transação");
        subtitle.setStyle("-fx-text-fill: #A1A1AA; -fx-font-size: 13px;");
        subtitle.setWrapText(true);
        subtitle.setMaxWidth(380);
        subtitle.setAlignment(javafx.geometry.Pos.CENTER);

        header.getChildren().addAll(title, subtitle);

        VBox fieldsContainer = new VBox(20);
        fieldsContainer.setStyle("-fx-padding: 8 0 0 0;");

        VBox nameBox = new VBox(10);
        Label nameLabel = new Label("Nome *");
        nameLabel.setStyle("-fx-text-fill: #E4E4E7; -fx-font-size: 13px; -fx-font-weight: 500;");

        TextField nameField = new TextField();
        nameField.setPromptText("Ex: Compra no supermercado, Salário...");
        nameField.setStyle("-fx-background-color: #18181B; -fx-text-fill: #F4F4F5; -fx-prompt-text-fill: #71717A; -fx-background-radius: 10; -fx-border-color: #3F3F46; -fx-border-radius: 10; -fx-border-width: 1.5; -fx-padding: 13 16; -fx-font-size: 14px; -fx-pref-width: 400px;");
        nameField.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (isNowFocused) {
                nameField.setStyle("-fx-background-color: #18181B; -fx-text-fill: #F4F4F5; -fx-prompt-text-fill: #71717A; -fx-background-radius: 10; -fx-border-color: #7C3AED; -fx-border-radius: 10; -fx-border-width: 1.5; -fx-padding: 13 16; -fx-font-size: 14px; -fx-pref-width: 400px;");
            } else {
                nameField.setStyle("-fx-background-color: #18181B; -fx-text-fill: #F4F4F5; -fx-prompt-text-fill: #71717A; -fx-background-radius: 10; -fx-border-color: #3F3F46; -fx-border-radius: 10; -fx-border-width: 1.5; -fx-padding: 13 16; -fx-font-size: 14px; -fx-pref-width: 400px;");
            }
        });

        nameBox.getChildren().addAll(nameLabel, nameField);

        VBox amountBox = new VBox(10);
        Label amountLabel = new Label("Valor *");
        amountLabel.setStyle("-fx-text-fill: #E4E4E7; -fx-font-size: 13px; -fx-font-weight: 500;");

        TextField amountField = new TextField();
        amountField.setPromptText("R$ 0,00");
        amountField.setStyle("-fx-background-color: #18181B; -fx-text-fill: #F4F4F5; -fx-prompt-text-fill: #71717A; -fx-background-radius: 10; -fx-border-color: #3F3F46; -fx-border-radius: 10; -fx-border-width: 1.5; -fx-padding: 13 16; -fx-font-size: 14px; -fx-pref-width: 400px;");
        amountField.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (isNowFocused) {
                amountField.setStyle("-fx-background-color: #18181B; -fx-text-fill: #F4F4F5; -fx-prompt-text-fill: #71717A; -fx-background-radius: 10; -fx-border-color: #7C3AED; -fx-border-radius: 10; -fx-border-width: 1.5; -fx-padding: 13 16; -fx-font-size: 14px; -fx-pref-width: 400px;");
            } else {
                amountField.setStyle("-fx-background-color: #18181B; -fx-text-fill: #F4F4F5; -fx-prompt-text-fill: #71717A; -fx-background-radius: 10; -fx-border-color: #3F3F46; -fx-border-radius: 10; -fx-border-width: 1.5; -fx-padding: 13 16; -fx-font-size: 14px; -fx-pref-width: 400px;");
            }
        });

        amountBox.getChildren().addAll(amountLabel, amountField);

        VBox categoryBox = new VBox(10);
        Label categoryLabel = new Label("Categoria *");
        categoryLabel.setStyle("-fx-text-fill: #E4E4E7; -fx-font-size: 13px; -fx-font-weight: 500;");

        ComboBox<Category> categoryCombo = new ComboBox<>();
        categoryCombo.setPromptText("Selecione uma categoria");
        categoryCombo.setStyle("-fx-background-color: #18181B; -fx-text-fill: #F4F4F5; -fx-prompt-text-fill: #71717A; -fx-background-radius: 10; -fx-border-color: #3F3F46; -fx-border-radius: 10; -fx-border-width: 1.5; -fx-padding: 8 16; -fx-font-size: 14px; -fx-pref-width: 400px;");
        categoryCombo.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (isNowFocused) {
                categoryCombo.setStyle("-fx-background-color: #18181B; -fx-text-fill: #F4F4F5; -fx-prompt-text-fill: #71717A; -fx-background-radius: 10; -fx-border-color: #7C3AED; -fx-border-radius: 10; -fx-border-width: 1.5; -fx-padding: 8 16; -fx-font-size: 14px; -fx-pref-width: 400px;");
            } else {
                categoryCombo.setStyle("-fx-background-color: #18181B; -fx-text-fill: #F4F4F5; -fx-prompt-text-fill: #71717A; -fx-background-radius: 10; -fx-border-color: #3F3F46; -fx-border-radius: 10; -fx-border-width: 1.5; -fx-padding: 8 16; -fx-font-size: 14px; -fx-pref-width: 400px;");
            }
        });

        try {
            List<Category> categories = categoryService.getAll();
            categoryCombo.getItems().addAll(categories);
            categoryCombo.setButtonCell(new ListCell<Category>() {
                @Override
                protected void updateItem(Category item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.getName());
                        setStyle("-fx-text-fill: #F4F4F5; -fx-background-color: #18181B;");
                    }
                }
            });
            categoryCombo.setCellFactory(lv -> new ListCell<Category>() {
                @Override
                protected void updateItem(Category item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.getName());
                        setStyle("-fx-text-fill: #F4F4F5; -fx-background-color: #18181B; -fx-padding: 8 16;");
                    }
                }
            });
        } catch (Exception ex) {
            showToast("Erro ao carregar categorias: " + ex.getMessage(), false);
        }

        categoryBox.getChildren().addAll(categoryLabel, categoryCombo);

        VBox notesBox = new VBox(10);
        Label notesLabel = new Label("Notas");
        notesLabel.setStyle("-fx-text-fill: #E4E4E7; -fx-font-size: 13px; -fx-font-weight: 500;");

        TextArea notesField = new TextArea();
        notesField.setPromptText("Adicione observações sobre a transação (opcional)");
        notesField.setPrefRowCount(3);
        notesField.setWrapText(true);
        notesField.setStyle("-fx-control-inner-background: #18181B; -fx-background-color: #18181B; -fx-text-fill: #F4F4F5; -fx-prompt-text-fill: #71717A; -fx-background-radius: 10; -fx-border-color: #3F3F46; -fx-border-radius: 10; -fx-border-width: 1.5; -fx-padding: 13 16; -fx-font-size: 14px; -fx-pref-width: 400px; -fx-font-family: 'Segoe UI', 'Arial', sans-serif;");
        notesField.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (isNowFocused) {
                notesField.setStyle("-fx-control-inner-background: #18181B; -fx-background-color: #18181B; -fx-text-fill: #F4F4F5; -fx-prompt-text-fill: #71717A; -fx-background-radius: 10; -fx-border-color: #7C3AED; -fx-border-radius: 10; -fx-border-width: 1.5; -fx-padding: 13 16; -fx-font-size: 14px; -fx-pref-width: 400px; -fx-font-family: 'Segoe UI', 'Arial', sans-serif;");
            } else {
                notesField.setStyle("-fx-control-inner-background: #18181B; -fx-background-color: #18181B; -fx-text-fill: #F4F4F5; -fx-prompt-text-fill: #71717A; -fx-background-radius: 10; -fx-border-color: #3F3F46; -fx-border-radius: 10; -fx-border-width: 1.5; -fx-padding: 13 16; -fx-font-size: 14px; -fx-pref-width: 400px; -fx-font-family: 'Segoe UI', 'Arial', sans-serif;");
            }
        });

        notesBox.getChildren().addAll(notesLabel, notesField);

        fieldsContainer.getChildren().addAll(nameBox, amountBox, categoryBox, notesBox);

        HBox actionButtons = new HBox(12);
        actionButtons.setAlignment(javafx.geometry.Pos.CENTER);
        actionButtons.setStyle("-fx-padding: 8 0 0 0;");

        Button cancelBtn = new Button("Cancelar");
        cancelBtn.setPrefHeight(44);
        cancelBtn.setStyle("-fx-background-color: #18181B; -fx-text-fill: #F4F4F5; -fx-font-size: 14px; -fx-font-weight: 600; -fx-background-radius: 10; -fx-border-color: #3F3F46; -fx-border-width: 1.5; -fx-border-radius: 10; -fx-cursor: hand; -fx-pref-width: 194px;");
        cancelBtn.setOnMouseEntered(e -> cancelBtn.setStyle("-fx-background-color: #1F1F23; -fx-text-fill: #F4F4F5; -fx-font-size: 14px; -fx-font-weight: 600; -fx-background-radius: 10; -fx-border-color: #52525B; -fx-border-width: 1.5; -fx-border-radius: 10; -fx-cursor: hand; -fx-pref-width: 194px;"));
        cancelBtn.setOnMouseExited(e -> cancelBtn.setStyle("-fx-background-color: #18181B; -fx-text-fill: #F4F4F5; -fx-font-size: 14px; -fx-font-weight: 600; -fx-background-radius: 10; -fx-border-color: #3F3F46; -fx-border-width: 1.5; -fx-border-radius: 10; -fx-cursor: hand; -fx-pref-width: 194px;"));
        cancelBtn.setOnAction(e -> {
            formStage.close();
            contentArea.setEffect(null);
        });

        Button saveBtn = new Button("Criar Transação");
        saveBtn.setPrefHeight(44);
        saveBtn.setStyle("-fx-background-color: #7C3AED; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: 600; -fx-background-radius: 10; -fx-cursor: hand; -fx-pref-width: 194px;");
        saveBtn.setOnMouseEntered(e -> saveBtn.setStyle("-fx-background-color: #6D28D9; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: 600; -fx-background-radius: 10; -fx-cursor: hand; -fx-pref-width: 194px;"));
        saveBtn.setOnMouseExited(e -> saveBtn.setStyle("-fx-background-color: #7C3AED; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: 600; -fx-background-radius: 10; -fx-cursor: hand; -fx-pref-width: 194px;"));
        saveBtn.setOnAction(e -> {
            String name = nameField.getText().trim();
            String amountText = amountField.getText().trim();
            Category selectedCategory = categoryCombo.getValue();
            String notes = notesField.getText().trim();

            if (name.isEmpty()) {
                showToast("O nome da transação é obrigatório!", false);
                nameField.setStyle("-fx-background-color: #18181B; -fx-text-fill: #F4F4F5; -fx-prompt-text-fill: #71717A; -fx-background-radius: 10; -fx-border-color: #EF4444; -fx-border-radius: 10; -fx-border-width: 1.5; -fx-padding: 13 16; -fx-font-size: 14px; -fx-pref-width: 400px;");
                return;
            }

            if (amountText.isEmpty()) {
                showToast("O valor da transação é obrigatório!", false);
                amountField.setStyle("-fx-background-color: #18181B; -fx-text-fill: #F4F4F5; -fx-prompt-text-fill: #71717A; -fx-background-radius: 10; -fx-border-color: #EF4444; -fx-border-radius: 10; -fx-border-width: 1.5; -fx-padding: 13 16; -fx-font-size: 14px; -fx-pref-width: 400px;");
                return;
            }

            if (selectedCategory == null) {
                showToast("Selecione uma categoria!", false);
                categoryCombo.setStyle("-fx-background-color: #18181B; -fx-text-fill: #F4F4F5; -fx-prompt-text-fill: #71717A; -fx-background-radius: 10; -fx-border-color: #EF4444; -fx-border-radius: 10; -fx-border-width: 1.5; -fx-padding: 8 16; -fx-font-size: 14px; -fx-pref-width: 400px;");
                return;
            }

            try {
                String cleanAmount = amountText.replace("R$", "").replace(".", "").replace(",", ".").trim();
                BigDecimal amount = new BigDecimal(cleanAmount);

                Transaction transaction = new Transaction();
                transaction.setName(name);
                transaction.setAmount(amount);
                transaction.setNotes(notes.isEmpty() ? null : notes);
                transaction.setUser(authService.getCurrentUser());
                transaction.setCategory(selectedCategory);

                Transaction savedTransaction = transactionService.create(transaction);
                System.out.println("Transação salva com categoria: " +
                        (savedTransaction.getCategory() != null ? savedTransaction.getCategory().getName() : "NULL"));

                showToast("Transação criada com sucesso!", true);
                formStage.close();
                contentArea.setEffect(null);

                if ("transactions".equals(currentView)) {
                    loadTransactionsView();
                }

            } catch (NumberFormatException ex) {
                showToast("Valor inválido! Use apenas números.", false);
                amountField.setStyle("-fx-background-color: #18181B; -fx-text-fill: #F4F4F5; -fx-prompt-text-fill: #71717A; -fx-background-radius: 10; -fx-border-color: #EF4444; -fx-border-radius: 10; -fx-border-width: 1.5; -fx-padding: 13 16; -fx-font-size: 14px; -fx-pref-width: 400px;");
            } catch (Exception ex) {
                showToast("Erro ao criar transação: " + ex.getMessage(), false);
                ex.printStackTrace();
            }
        });

        actionButtons.getChildren().addAll(cancelBtn, saveBtn);

        formContent.getChildren().addAll(header, fieldsContainer, actionButtons);

        StackPane root = new StackPane(formContent);
        root.setStyle("-fx-background-color: transparent;");
        root.setPadding(new Insets(40));

        Scene scene = new Scene(root, 700, 800);
        scene.setFill(Color.TRANSPARENT);

        formStage.setScene(scene);
        formStage.setOnHidden(e -> contentArea.setEffect(null));

        System.out.println("funcionou janela pra criar transacao");
        formStage.showAndWait();
        System.out.println("janela fechou");
    }

    private void openCategoryForm() {
        System.out.println("[formulario nova categoria]: abrindo");

        Stage formStage = new Stage();
        formStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
        formStage.initStyle(javafx.stage.StageStyle.TRANSPARENT);
        formStage.initOwner(sidebar.getScene().getWindow());

        javafx.scene.effect.BoxBlur blur = new javafx.scene.effect.BoxBlur(10, 10, 3);
        contentArea.setEffect(blur);

        VBox formContent = new VBox(28);
        formContent.setAlignment(javafx.geometry.Pos.TOP_CENTER);
        formContent.setStyle("-fx-background-color: #27272A; -fx-background-radius: 16; -fx-padding: 48 40; -fx-border-color: #3F3F46; fx-border-width: 1; -fx-border-radius: 16; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 40, 0, 0, 10);-fx-max-width: 480;");

        VBox header = new VBox(8);
        header.setAlignment(javafx.geometry.Pos.CENTER);

        Label title = new Label("Nova Categoria");
        title.setStyle("-fx-text-fill: #F4F4F5; -fx-font-size: 28px; -fx-font-weight: 600; -fx-letter-spacing: -0.5px;");

        Label subtitle = new Label("Preencha os campos abaixo para criar uma nova categoria");
        subtitle.setStyle("-fx-text-fill: #A1A1AA; -fx-font-size: 13px;");
        subtitle.setWrapText(true);
        subtitle.setMaxWidth(380);
        subtitle.setAlignment(javafx.geometry.Pos.CENTER);

        header.getChildren().addAll(title, subtitle);

        VBox fieldsContainer = new VBox(20);
        fieldsContainer.setStyle("-fx-padding: 8 0 0 0;");

        VBox nameBox = new VBox(10);
        Label nameLabel = new Label("Nome *");
        nameLabel.setStyle("-fx-text-fill: #E4E4E7; -fx-font-size: 13px; -fx-font-weight: 500;");

        TextField nameField = new TextField();
        nameField.setPromptText("Ex: Alimentação, Transporte, Salário...");
        nameField.setStyle("-fx-background-color: #18181B; -fx-text-fill: #F4F4F5; -fx-prompt-text-fill: #71717A; -fx-background-radius: 10; -fx-border-color: #3F3F46; -fx-border-radius: 10; -fx-border-width: 1.5; -fx-padding: 13 16; -fx-font-size: 14px; -fx-pref-width: 400px;");
        nameField.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (isNowFocused) {
                nameField.setStyle("-fx-background-color: #18181B; -fx-text-fill: #F4F4F5; -fx-prompt-text-fill: #71717A; -fx-background-radius: 10; -fx-border-color: #7C3AED; -fx-border-radius: 10; -fx-border-width: 1.5; -fx-padding: 13 16; -fx-font-size: 14px; -fx-pref-width: 400px;");
            } else {
                nameField.setStyle("-fx-background-color: #18181B; -fx-text-fill: #F4F4F5; -fx-prompt-text-fill: #71717A; -fx-background-radius: 10; -fx-border-color: #3F3F46; -fx-border-radius: 10; -fx-border-width: 1.5; -fx-padding: 13 16; -fx-font-size: 14px; -fx-pref-width: 400px;");
            }
        });

        nameBox.getChildren().addAll(nameLabel, nameField);

        VBox descBox = new VBox(10);
        Label descLabel = new Label("Descrição");
        descLabel.setStyle("-fx-text-fill: #E4E4E7; -fx-font-size: 13px; -fx-font-weight: 500;");

        TextArea descField = new TextArea();
        descField.setPromptText("Descreva a categoria (opcional)");
        descField.setPrefRowCount(3);
        descField.setWrapText(true);
        descField.setStyle("-fx-control-inner-background: #18181B; -fx-background-color: #18181B; -fx-text-fill: #F4F4F5; -fx-prompt-text-fill: #71717A; -fx-background-radius: 10; -fx-border-color: #3F3F46; -fx-border-radius: 10; -fx-border-width: 1.5; -fx-padding: 13 16; -fx-font-size: 14px; -fx-pref-width: 400px; -fx-font-family: 'Segoe UI', 'Arial', sans-serif;");
        descField.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (isNowFocused) {
                descField.setStyle("-fx-control-inner-background: #18181B; -fx-background-color: #18181B; -fx-text-fill: #F4F4F5; -fx-prompt-text-fill: #71717A; -fx-background-radius: 10; -fx-border-color: #7C3AED; -fx-border-radius: 10; -fx-border-width: 1.5; -fx-padding: 13 16; -fx-font-size: 14px; -fx-pref-width: 400px; -fx-font-family: 'Segoe UI', 'Arial', sans-serif;");
            } else {
                descField.setStyle("-fx-control-inner-background: #18181B; -fx-background-color: #18181B; -fx-text-fill: #F4F4F5; -fx-prompt-text-fill: #71717A; -fx-background-radius: 10; -fx-border-color: #3F3F46; -fx-border-radius: 10; -fx-border-width: 1.5; -fx-padding: 13 16; -fx-font-size: 14px; -fx-pref-width: 400px; -fx-font-family: 'Segoe UI', 'Arial', sans-serif;");
            }
        });
        descBox.getChildren().addAll(descLabel, descField);

        VBox typeBox = new VBox(12);
        Label typeLabel = new Label("Tipo *");
        typeLabel.setStyle("-fx-text-fill: #E4E4E7; -fx-font-size: 13px; -fx-font-weight: 500;");

        ToggleGroup typeGroup = new ToggleGroup();

        HBox typeButtons = new HBox(12);
        typeButtons.setAlignment(javafx.geometry.Pos.CENTER);

        VBox incomeBox = new VBox(8);
        incomeBox.setAlignment(javafx.geometry.Pos.CENTER);
        incomeBox.setStyle("-fx-background-color: #18181B; -fx-background-radius: 12; -fx-border-color: #3F3F46; -fx-border-width: 1.5; -fx-border-radius: 12; -fx-padding: 16 24; -fx-cursor: hand; -fx-pref-width: 194;");

        RadioButton incomeRadio = new RadioButton();
        incomeRadio.setToggleGroup(typeGroup);
        incomeRadio.setUserData("INCOME");
        incomeRadio.setStyle("-fx-text-fill: transparent;");
        incomeRadio.setVisible(false);
        Label incomeIcon = new Label("💰");
        incomeIcon.setStyle("-fx-font-size: 28px;");
        Label incomeLabel = new Label("Receita");
        incomeLabel.setStyle("-fx-text-fill: #F4F4F5; -fx-font-size: 15px; -fx-font-weight: 600;");
        Label incomeDesc = new Label("Entrada de dinheiro");
        incomeDesc.setStyle("-fx-text-fill: #71717A; -fx-font-size: 12px;");
        incomeBox.getChildren().addAll(incomeIcon, incomeLabel, incomeDesc);

        VBox expenseBox = new VBox(8);
        expenseBox.setAlignment(javafx.geometry.Pos.CENTER);
        expenseBox.setStyle("-fx-background-color: #18181B; -fx-background-radius: 12; -fx-border-color: #3F3F46; -fx-border-width: 1.5; -fx-border-radius: 12; -fx-padding: 16 24; -fx-cursor: hand; -fx-pref-width: 194;");
        RadioButton expenseRadio = new RadioButton();
        expenseRadio.setToggleGroup(typeGroup);
        expenseRadio.setUserData("EXPENSE");
        expenseRadio.setStyle("-fx-text-fill: transparent;");
        expenseRadio.setVisible(false);
        Label expenseIcon = new Label("💸");
        expenseIcon.setStyle("-fx-font-size: 28px;");
        Label expenseLabel = new Label("Despesa");
        expenseLabel.setStyle("-fx-text-fill: #F4F4F5; -fx-font-size: 15px; -fx-font-weight: 600;");
        Label expenseDesc = new Label("Saída de dinheiro");
        expenseDesc.setStyle("-fx-text-fill: #71717A; -fx-font-size: 12px;");
        expenseBox.getChildren().addAll(expenseIcon, expenseLabel, expenseDesc);

        incomeBox.setOnMouseClicked(e -> {
            incomeRadio.setSelected(true);
            updateTypeSelection(incomeBox, expenseBox, true);
        });
        expenseBox.setOnMouseClicked(e -> {
            expenseRadio.setSelected(true);
            updateTypeSelection(incomeBox, expenseBox, false);
        });
        incomeBox.setOnMouseEntered(e -> {
            if (!incomeRadio.isSelected()) {
                incomeBox.setStyle("-fx-background-color: #1F1F23; -fx-background-radius: 12; -fx-border-color: #52525B; -fx-border-width: 1.5; -fx-border-radius: 12; -fx-padding: 16 24; -fx-cursor: hand; -fx-pref-width: 194;");
            }
        });

        incomeBox.setOnMouseExited(e -> {
            if (!incomeRadio.isSelected()) {
                incomeBox.setStyle("-fx-background-color: #18181B; -fx-background-radius: 12; -fx-border-color: #3F3F46; -fx-border-width: 1.5; -fx-border-radius: 12; -fx-padding: 16 24; -fx-cursor: hand; -fx-pref-width: 194;");
            }
        });
        expenseBox.setOnMouseEntered(e -> {
            if (!expenseRadio.isSelected()) {
                expenseBox.setStyle("-fx-background-color: #1F1F23; -fx-background-radius: 12; -fx-border-color: #52525B; -fx-border-width: 1.5; -fx-border-radius: 12; -fx-padding: 16 24; -fx-cursor: hand; -fx-pref-width: 194;");
            }
        });
        expenseBox.setOnMouseExited(e -> {
            if (!expenseRadio.isSelected()) {
                expenseBox.setStyle("-fx-background-color: #18181B; -fx-background-radius: 12; -fx-border-color: #3F3F46; -fx-border-width: 1.5; -fx-border-radius: 12; -fx-padding: 16 24; -fx-cursor: hand; -fx-pref-width: 194;");
            }
        });

        typeButtons.getChildren().addAll(incomeBox, expenseBox);
        typeBox.getChildren().addAll(typeLabel, typeButtons);

        fieldsContainer.getChildren().addAll(nameBox, descBox, typeBox);

        HBox actionButtons = new HBox(12);
        actionButtons.setAlignment(javafx.geometry.Pos.CENTER);
        actionButtons.setStyle("-fx-padding: 8 0 0 0;");

        Button cancelBtn = new Button("Cancelar");
        cancelBtn.setPrefHeight(44);
        cancelBtn.setStyle("-fx-background-color: #18181B; -fx-text-fill: #F4F4F5; -fx-font-size: 14px; -fx-font-weight: 600; -fx-background-radius: 10; -fx-border-color: #3F3F46; -fx-border-width: 1.5; -fx-border-radius: 10; -fx-cursor: hand; -fx-pref-width: 194px;");
        cancelBtn.setOnMouseEntered(e -> cancelBtn.setStyle("-fx-background-color: #1F1F23; -fx-text-fill: #F4F4F5; -fx-font-size: 14px; -fx-font-weight: 600; -fx-background-radius: 10; -fx-border-color: #52525B; -fx-border-width: 1.5; -fx-border-radius: 10; -fx-cursor: hand; -fx-pref-width: 194px;"));
        cancelBtn.setOnMouseExited(e -> cancelBtn.setStyle("-fx-background-color: #18181B; -fx-text-fill: #F4F4F5; -fx-font-size: 14px; -fx-font-weight: 600; -fx-background-radius: 10; -fx-border-color: #3F3F46; -fx-border-width: 1.5; -fx-border-radius: 10; -fx-cursor: hand; -fx-pref-width: 194px;"));
        cancelBtn.setOnAction(e -> {
            formStage.close();
            contentArea.setEffect(null);
        });

        Button saveBtn = new Button("Criar Categoria");
        saveBtn.setPrefHeight(44);
        saveBtn.setStyle("-fx-background-color: #7C3AED; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: 600; -fx-background-radius: 10; -fx-cursor: hand; -fx-pref-width: 194px;");
        saveBtn.setOnMouseEntered(e -> saveBtn.setStyle("-fx-background-color: #6D28D9; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: 600; -fx-background-radius: 10; -fx-cursor: hand; -fx-pref-width: 194px;"));
        saveBtn.setOnMouseExited(e -> saveBtn.setStyle("-fx-background-color: #7C3AED; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: 600; -fx-background-radius: 10; -fx-cursor: hand; -fx-pref-width: 194px;"));
        saveBtn.setOnAction(e -> {
            String name = nameField.getText().trim();
            String description = descField.getText().trim();
            javafx.scene.control.Toggle selectedType = typeGroup.getSelectedToggle();

            if (name.isEmpty()) {
                showToast("O nome da categoria é obrigatório!", false);
                nameField.setStyle("-fx-background-color: #18181B; -fx-text-fill: #F4F4F5; -fx-prompt-text-fill: #71717A; -fx-background-radius: 10; -fx-border-color: #EF4444; -fx-border-radius: 10; -fx-border-width: 1.5; -fx-padding: 13 16; -fx-font-size: 14px; -fx-pref-width: 400px;");
                return;
            }

            if (selectedType == null) {
                showToast("Selecione o tipo da categoria!", false);
                return;
            }

            try {
                Category category = new Category();
                category.setName(name);
                category.setDescription(description.isEmpty() ? null : description);
                category.setType(CategoryType.valueOf((String) selectedType.getUserData()));

                categoryService.create(category);
                showToast("Categoria criada com sucesso!", true);
                formStage.close();
                contentArea.setEffect(null);

            } catch (Exception ex) {
                showToast("Erro ao criar categoria: " + ex.getMessage(), false);
            }
        });

        actionButtons.getChildren().addAll(cancelBtn, saveBtn);

        formContent.getChildren().addAll(header, fieldsContainer, actionButtons);

        StackPane root = new StackPane(formContent);
        root.setStyle("-fx-background-color: transparent;");
        root.setPadding(new Insets(40));

        Scene scene = new Scene(root, 700, 720);
        scene.setFill(Color.TRANSPARENT);

        formStage.setScene(scene);
        formStage.setOnHidden(e -> contentArea.setEffect(null));

        System.out.println("funcionou janela pra criar categoria");
        formStage.showAndWait();
        System.out.println("janela fechou");
    }

    private void loadTransactionsView() {
        contentArea.getChildren().clear();

        VBox transactionsView = new VBox(28);
        transactionsView.setStyle("-fx-padding: 40 48; -fx-background-color: #18181B;");

        VBox header = new VBox(8);
        Label title = new Label("Transações");
        title.setStyle("-fx-text-fill: #F4F4F5; -fx-font-size: 28px; -fx-font-weight: 700;");
        Label subtitle = new Label("Histórico completo de movimentações financeiras");
        subtitle.setStyle("-fx-text-fill: #71717A; -fx-font-size: 14px;");
        header.getChildren().addAll(title, subtitle);

        VBox transactionsContainer = new VBox(16);
        transactionsContainer.setStyle("-fx-background-color: #27272A; -fx-background-radius: 18; -fx-padding: 32 28; -fx-border-color: #3F3F46; -fx-border-width: 1; -fx-border-radius: 18;");

        try {
            List<Transaction> transactions = transactionService.getAll();
            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

            if (transactions.isEmpty()) {
                VBox emptyState = new VBox(20);
                emptyState.setAlignment(Pos.CENTER);
                emptyState.setStyle("-fx-padding: 60;");

                Label emptyIcon = new Label("📝");
                emptyIcon.setStyle("-fx-font-size: 64px;");

                Label emptyLabel = new Label("Nenhuma transação encontrada");
                emptyLabel.setStyle("-fx-text-fill: #A1A1AA; -fx-font-size: 18px; -fx-font-weight: 600;");

                Label emptyHint = new Label("Comece criando sua primeira transação");
                emptyHint.setStyle("-fx-text-fill: #71717A; -fx-font-size: 14px;");

                emptyState.getChildren().addAll(emptyIcon, emptyLabel, emptyHint);
                transactionsContainer.getChildren().add(emptyState);
            } else {
                for (Transaction transaction : transactions) {
                    HBox transactionRow = createTransactionRow(transaction, currencyFormat);
                    transactionsContainer.getChildren().add(transactionRow);
                }
            }
        } catch (Exception e) {
            showError("Erro ao carregar transações: " + e.getMessage());
            e.printStackTrace();
        }

        transactionsView.getChildren().addAll(header, transactionsContainer);

        ScrollPane scrollPane = new ScrollPane(transactionsView);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        contentArea.getChildren().add(scrollPane);
    }

    private HBox createTransactionRow(Transaction transaction, NumberFormat currencyFormat) {
        HBox row = new HBox(20);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setStyle("-fx-background-color: #18181B; -fx-background-radius: 14; -fx-padding: 20 24; -fx-border-color: #3F3F46; -fx-border-width: 1; -fx-border-radius: 14;");

        Category category = transaction.getCategory();
        CategoryType type = category != null ? category.getType() : CategoryType.EXPENSE;
        String categoryName = category != null ? category.getName() : "Sem categoria";

        String emoji = type == CategoryType.INCOME ? "💰" : "💸";
        StackPane iconContainer = new StackPane();
        iconContainer.setStyle("-fx-background-color: " +
                (type == CategoryType.INCOME ? "rgba(34, 197, 94, 0.15)" : "rgba(239, 68, 68, 0.15)") +
                "; -fx-background-radius: 12; -fx-min-width: 50; -fx-min-height: 50; -fx-max-width: 50; -fx-max-height: 50;");
        Label icon = new Label(emoji);
        icon.setStyle("-fx-font-size: 28px;");
        iconContainer.getChildren().add(icon);

        VBox info = new VBox(6);
        HBox.setHgrow(info, Priority.ALWAYS);

        Label name = new Label(transaction.getName());
        name.setStyle("-fx-text-fill: #F4F4F5; -fx-font-size: 16px; -fx-font-weight: 600;");

        HBox categoryBox = new HBox(8);
        categoryBox.setAlignment(Pos.CENTER_LEFT);
        Label categoryLabel = new Label(categoryName);
        categoryLabel.setStyle("-fx-text-fill: #71717A; -fx-font-size: 13px;");

        if (transaction.getNotes() != null && !transaction.getNotes().isEmpty()) {
            Label separator = new Label("•");
            separator.setStyle("-fx-text-fill: #52525B; -fx-font-size: 13px;");
            Label notes = new Label(transaction.getNotes());
            notes.setStyle("-fx-text-fill: #71717A; -fx-font-size: 13px;");
            notes.setMaxWidth(300);
            categoryBox.getChildren().addAll(categoryLabel, separator, notes);
        } else {
            categoryBox.getChildren().add(categoryLabel);
        }

        info.getChildren().addAll(name, categoryBox);

        String prefix = type == CategoryType.INCOME ? "+ " : "- ";
        Label amount = new Label(prefix + currencyFormat.format(transaction.getAmount()));
        String amountColor = type == CategoryType.INCOME ? "#22C55E" : "#EF4444";
        amount.setStyle("-fx-text-fill: " + amountColor + "; -fx-font-size: 22px; -fx-font-weight: 700; -fx-min-width: 150; -fx-alignment: center-right;");

        row.getChildren().addAll(iconContainer, info, amount);

        return row;
    }

    private void updateTypeSelection(VBox incomeBox, VBox expenseBox, boolean isIncome) {
        if (isIncome) {
            incomeBox.setStyle("-fx-background-color: #7C3AED; -fx-background-radius: 12; -fx-border-color: #7C3AED;-fx-border-width: 1.5; -fx-border-radius: 12; -fx-padding: 16 24; -fx-cursor: hand; -fx-pref-width: 194;");
            expenseBox.setStyle("-fx-background-color: #18181B; -fx-background-radius: 12; -fx-border-color: #3F3F46;-fx-border-width: 1.5; -fx-border-radius: 12; -fx-padding: 16 24; -fx-cursor: hand; -fx-pref-width: 194;");
        } else {
            expenseBox.setStyle("-fx-background-color: #7C3AED; -fx-background-radius: 12; -fx-border-color: #7C3AED; -fx-border-width: 1.5;   -fx-border-radius: 12;  -fx-padding: 16 24;  -fx-cursor: hand; -fx-pref-width: 194;");
            incomeBox.setStyle("-fx-background-color: #18181B; -fx-background-radius: 12; -fx-border-color: #3F3F46; -fx-border-width: 1.5;-fx-border-radius: 12; -fx-padding: 16 24; -fx-cursor: hand; -fx-pref-width: 194");
        }
    }

    private void showToast(String message, boolean isSuccess) {
        Stage toastStage = new Stage();
        toastStage.initOwner(sidebar.getScene().getWindow());
        toastStage.initStyle(StageStyle.TRANSPARENT);
        toastStage.setAlwaysOnTop(true);

        HBox toastBox = new HBox(12);
        toastBox.setAlignment(Pos.CENTER_LEFT);
        toastBox.setPadding(new Insets(10, 15, 10, 15));
        toastBox.setPrefWidth(320);
        toastBox.setMaxWidth(320);
        toastBox.setMinWidth(320);

        String backgroundColor = isSuccess ? "#10B981" : "#EF4444";
        String iconColor = isSuccess ? "#ECFDF5" : "#FEF2F2";
        String icon = isSuccess ? "✓" : "⚠";

        toastBox.setStyle("-fx-background-color: " + backgroundColor + "; -fx-background-radius: 12; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 20, 0, 0, 4);");

        Label iconLabel = new Label(icon);
        iconLabel.setStyle("-fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold; -fx-background-color: " + iconColor + "15; -fx-background-radius: 8; -fx-padding: 4 8; -fx-min-width: 32; -fx-alignment: center;");

        Label messageLabel = new Label(message);
        messageLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: 500;");
        messageLabel.setWrapText(true);
        messageLabel.setMaxWidth(240);

        HBox.setHgrow(messageLabel, Priority.ALWAYS);

        toastBox.getChildren().addAll(iconLabel, messageLabel);

        StackPane root = new StackPane(toastBox);
        root.setStyle("-fx-background-color: transparent;");
        root.setPadding(new Insets(20));

        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        toastStage.setScene(scene);

        Stage ownerStage = (Stage) sidebar.getScene().getWindow();
        toastStage.setX(ownerStage.getX() + ownerStage.getWidth() - 380);
        toastStage.setY(ownerStage.getY() + 80);
        toastStage.show();

        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), toastBox);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        PauseTransition pause = new PauseTransition(Duration.seconds(3));

        FadeTransition fadeOut = new FadeTransition(Duration.millis(300), toastBox);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(e -> toastStage.close());

        SequentialTransition sequence = new SequentialTransition(new ParallelTransition(fadeIn), pause, fadeOut);
        sequence.play();

        toastBox.setOnMouseClicked(e -> fadeOut.playFromStart());
    }

    private void showError(String message) {
        showToast(message, false);
    }

    private void showSuccess(String message) {
        showToast(message, true);
    }
}