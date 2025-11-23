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
import me.lucasgusmao.financeai.model.entity.Goal;
import me.lucasgusmao.financeai.model.entity.Transaction;
import me.lucasgusmao.financeai.model.enums.CategoryType;
import me.lucasgusmao.financeai.service.AuthService;
import me.lucasgusmao.financeai.service.CategoryService;
import me.lucasgusmao.financeai.service.GoalService;
import me.lucasgusmao.financeai.service.TransactionService;
import me.lucasgusmao.financeai.style.animation.AnimationFX;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

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
    private GoalService goalService;

    @Autowired
    private ApplicationContext springContext;

    private String currentView = "dashboard";

    @FXML
    public void initialize() {
        loadUserData();
        startAnimations();
        loadDashboardData();
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
    private void handleNavigateHome() {
        currentView = "dashboard";
        try {
            Scene scene = sidebar.getScene();
            Stage stage = (Stage) scene.getWindow();

            FXMLLoader loader = new FXMLLoader(
                    FinanceApplication.class.getResource("main-view.fxml")
            );
            loader.setControllerFactory(springContext::getBean);

            Scene newScene = new Scene(loader.load(), scene.getWidth(), scene.getHeight());
            stage.setScene(newScene);

        } catch (Exception e) {
            e.printStackTrace();
            showError("Erro ao carregar menu principal");
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
        categoryBtn.setOnMouseEntered(e -> categoryBtn.setStyle("-fx-background-color: #1F1F23; -fx-background-radius: 16; -fx-text-fill: #F4F4F5; -fx-font-size: 18px; -fx-font-weight: 600; -fx-padding: 24; -fx-border-color: #52525B; -fx-border-width: 1; -fx-border-radius: 16; -fx-cursor: hand;"));
        categoryBtn.setOnMouseExited(e -> categoryBtn.setStyle("-fx-background-color: #18181B; -fx-background-radius: 16; -fx-text-fill: #F4F4F5; -fx-font-size: 18px; -fx-font-weight: 600; -fx-padding: 24; -fx-border-color: #3F3F46; -fx-border-width: 1; -fx-border-radius: 16; -fx-cursor: hand;"));
        categoryBtn.setOnAction(e -> {
            modalStage.close();
            Platform.runLater(() -> openCategoryForm());
        });

        Button transactionBtn = new Button("Transação");
        transactionBtn.setMaxWidth(Double.MAX_VALUE);
        transactionBtn.setStyle("-fx-background-color: #18181B; -fx-background-radius: 16; -fx-text-fill: #F4F4F5; -fx-font-size: 18px; -fx-font-weight: 600; -fx-padding: 24; -fx-border-color: #3F3F46; -fx-border-width: 1; -fx-border-radius: 16; -fx-cursor: hand;");
        transactionBtn.setOnMouseEntered(e -> transactionBtn.setStyle("-fx-background-color: #1F1F23; -fx-background-radius: 16; -fx-text-fill: #F4F4F5; -fx-font-size: 18px; -fx-font-weight: 600; -fx-padding: 24; -fx-border-color: #52525B; -fx-border-width: 1; -fx-border-radius: 16; -fx-cursor: hand;"));
        transactionBtn.setOnMouseExited(e -> transactionBtn.setStyle("-fx-background-color: #18181B; -fx-background-radius: 16; -fx-text-fill: #F4F4F5; -fx-font-size: 18px; -fx-font-weight: 600; -fx-padding: 24; -fx-border-color: #3F3F46; -fx-border-width: 1; -fx-border-radius: 16; -fx-cursor: hand;"));
        transactionBtn.setOnAction(e -> {
            modalStage.close();
            Platform.runLater(() -> openTransactionForm());
        });

        Button goalBtn = new Button("Meta");
        goalBtn.setMaxWidth(Double.MAX_VALUE);
        goalBtn.setStyle("-fx-background-color: #18181B; -fx-background-radius: 16; -fx-text-fill: #F4F4F5; -fx-font-size: 18px; -fx-font-weight: 600; -fx-padding: 24; -fx-border-color: #3F3F46; -fx-border-width: 1; -fx-border-radius: 16; -fx-cursor: hand;");
        goalBtn.setOnMouseEntered(e -> goalBtn.setStyle("-fx-background-color: #1F1F23; -fx-background-radius: 16; -fx-text-fill: #F4F4F5; -fx-font-size: 18px; -fx-font-weight: 600; -fx-padding: 24; -fx-border-color: #52525B; -fx-border-width: 1; -fx-border-radius: 16; -fx-cursor: hand;"));
        goalBtn.setOnMouseExited(e -> goalBtn.setStyle("-fx-background-color: #18181B; -fx-background-radius: 16; -fx-text-fill: #F4F4F5; -fx-font-size: 18px; -fx-font-weight: 600; -fx-padding: 24; -fx-border-color: #3F3F46; -fx-border-width: 1; -fx-border-radius: 16; -fx-cursor: hand;"));
        goalBtn.setOnAction(e -> {
            modalStage.close();
            Platform.runLater(() -> openGoalForm());
        });

        buttonsBox.getChildren().addAll(categoryBtn, transactionBtn, goalBtn);

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

    @FXML
    private void handleNavigateCategories() {
        currentView = "categories";
        loadCategoriesView();
    }

    @FXML
    private void handleNavigateGoals() {
        currentView = "goals";
        loadGoalsView();
    }

    private void handleEditTransaction(Transaction transaction) {
        openEditTransactionForm(transaction);
    }

    private void handleEditCategory(Category category) {
        openEditCategoryForm(category);
    }

    private void handleEditGoal(Goal goal) {
        openEditGoalForm(goal);
    }

    private void handleDeleteTransaction(Transaction transaction) {
        Stage confirmStage = new Stage();
        confirmStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
        confirmStage.initStyle(StageStyle.TRANSPARENT);
        confirmStage.initOwner(sidebar.getScene().getWindow());

        VBox confirmContent = new VBox(24);
        confirmContent.setAlignment(Pos.CENTER);
        confirmContent.setStyle("-fx-background-color: #27272A; -fx-background-radius: 16; -fx-padding: 40; -fx-border-color: #3F3F46; -fx-border-width: 1; -fx-border-radius: 16; -fx-max-width: 420;");

        Label icon = new Label("⚠️");
        icon.setStyle("-fx-font-size: 48px;");

        Label title = new Label("Confirmar Exclusão");
        title.setStyle("-fx-text-fill: #F4F4F5; -fx-font-size: 24px; -fx-font-weight: 700;");

        Label message = new Label("Tem certeza que deseja excluir a transação \"" + transaction.getName() + "\"?");
        message.setStyle("-fx-text-fill: #A1A1AA; -fx-font-size: 14px; -fx-text-alignment: center;");
        message.setWrapText(true);
        message.setMaxWidth(340);
        message.setAlignment(Pos.CENTER);

        HBox buttons = new HBox(12);
        buttons.setAlignment(Pos.CENTER);

        Button cancelBtn = new Button("Cancelar");
        cancelBtn.setPrefHeight(44);
        cancelBtn.setPrefWidth(160);
        cancelBtn.setStyle("-fx-background-color: #18181B; -fx-text-fill: #F4F4F5; -fx-font-size: 14px; -fx-font-weight: 600; -fx-background-radius: 10; -fx-border-color: #3F3F46; -fx-border-width: 1.5; -fx-border-radius: 10; -fx-cursor: hand;");
        cancelBtn.setOnAction(e -> confirmStage.close());

        Button deleteBtn = new Button("Excluir");
        deleteBtn.setPrefHeight(44);
        deleteBtn.setPrefWidth(160);
        deleteBtn.setStyle("-fx-background-color: #EF4444; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: 600; -fx-background-radius: 10; -fx-cursor: hand;");
        deleteBtn.setOnAction(e -> {
            try {
                transactionService.delete(transaction.getId());
                showToast("Transação excluída com sucesso!", true);
                confirmStage.close();
                loadTransactionsView();
            } catch (Exception ex) {
                showToast("Erro ao excluir transação: " + ex.getMessage(), false);
            }
        });

        buttons.getChildren().addAll(cancelBtn, deleteBtn);
        confirmContent.getChildren().addAll(icon, title, message, buttons);

        StackPane root = new StackPane(confirmContent);
        root.setStyle("-fx-background-color: transparent;");
        root.setPadding(new Insets(50));

        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        confirmStage.setScene(scene);
        confirmStage.showAndWait();
    }

    private void handleDeleteCategory(Category category) {
        Stage confirmStage = new Stage();
        confirmStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
        confirmStage.initStyle(StageStyle.TRANSPARENT);
        confirmStage.initOwner(sidebar.getScene().getWindow());

        VBox confirmContent = new VBox(24);
        confirmContent.setAlignment(Pos.CENTER);
        confirmContent.setStyle("-fx-background-color: #27272A; -fx-background-radius: 16; -fx-padding: 40; -fx-border-color: #3F3F46; -fx-border-width: 1; -fx-border-radius: 16; -fx-max-width: 420;");

        Label icon = new Label("⚠️");
        icon.setStyle("-fx-font-size: 48px;");

        Label title = new Label("Confirmar Exclusão");
        title.setStyle("-fx-text-fill: #F4F4F5; -fx-font-size: 24px; -fx-font-weight: 700;");

        Label message = new Label("Tem certeza que deseja excluir a categoria \"" + category.getName() + "\"?");
        message.setStyle("-fx-text-fill: #A1A1AA; -fx-font-size: 14px; -fx-text-alignment: center;");
        message.setWrapText(true);
        message.setMaxWidth(340);
        message.setAlignment(Pos.CENTER);

        HBox buttons = new HBox(12);
        buttons.setAlignment(Pos.CENTER);

        Button cancelBtn = new Button("Cancelar");
        cancelBtn.setPrefHeight(44);
        cancelBtn.setPrefWidth(160);
        cancelBtn.setStyle("-fx-background-color: #18181B; -fx-text-fill: #F4F4F5; -fx-font-size: 14px; -fx-font-weight: 600; -fx-background-radius: 10; -fx-border-color: #3F3F46; -fx-border-width: 1.5; -fx-border-radius: 10; -fx-cursor: hand;");
        cancelBtn.setOnAction(e -> confirmStage.close());

        Button deleteBtn = new Button("Excluir");
        deleteBtn.setPrefHeight(44);
        deleteBtn.setPrefWidth(160);
        deleteBtn.setStyle("-fx-background-color: #EF4444; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: 600; -fx-background-radius: 10; -fx-cursor: hand;");
        deleteBtn.setOnAction(e -> {
            try {
                categoryService.delete(category.getId());
                showToast("Categoria excluída com sucesso!", true);
                confirmStage.close();
                loadCategoriesView();
            } catch (Exception ex) {
                showToast("Erro ao excluir categoria: " + ex.getMessage(), false);
            }
        });

        buttons.getChildren().addAll(cancelBtn, deleteBtn);
        confirmContent.getChildren().addAll(icon, title, message, buttons);

        StackPane root = new StackPane(confirmContent);
        root.setStyle("-fx-background-color: transparent;");
        root.setPadding(new Insets(50));

        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        confirmStage.setScene(scene);
        confirmStage.showAndWait();
    }

    private void handleDeleteGoal(Goal goal) {
        Stage confirmStage = new Stage();
        confirmStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
        confirmStage.initStyle(StageStyle.TRANSPARENT);
        confirmStage.initOwner(sidebar.getScene().getWindow());

        VBox confirmContent = new VBox(24);
        confirmContent.setAlignment(Pos.CENTER);
        confirmContent.setStyle("-fx-background-color: #27272A; -fx-background-radius: 16; -fx-padding: 40; -fx-border-color: #3F3F46; -fx-border-width: 1; -fx-border-radius: 16; -fx-max-width: 420;");

        Label icon = new Label("⚠️");
        icon.setStyle("-fx-font-size: 48px;");

        Label title = new Label("Confirmar Exclusão");
        title.setStyle("-fx-text-fill: #F4F4F5; -fx-font-size: 24px; -fx-font-weight: 700;");

        Label message = new Label("Tem certeza que deseja excluir a meta \"" + goal.getName() + "\"?");
        message.setStyle("-fx-text-fill: #A1A1AA; -fx-font-size: 14px; -fx-text-alignment: center;");
        message.setWrapText(true);
        message.setMaxWidth(340);
        message.setAlignment(Pos.CENTER);

        HBox buttons = new HBox(12);
        buttons.setAlignment(Pos.CENTER);

        Button cancelBtn = new Button("Cancelar");
        cancelBtn.setPrefHeight(44);
        cancelBtn.setPrefWidth(160);
        cancelBtn.setStyle("-fx-background-color: #18181B; -fx-text-fill: #F4F4F5; -fx-font-size: 14px; -fx-font-weight: 600; -fx-background-radius: 10; -fx-border-color: #3F3F46; -fx-border-width: 1.5; -fx-border-radius: 10; -fx-cursor: hand;");
        cancelBtn.setOnAction(e -> confirmStage.close());

        Button deleteBtn = new Button("Excluir");
        deleteBtn.setPrefHeight(44);
        deleteBtn.setPrefWidth(160);
        deleteBtn.setStyle("-fx-background-color: #EF4444; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: 600; -fx-background-radius: 10; -fx-cursor: hand;");
        deleteBtn.setOnAction(e -> {
            try {
                goalService.delete(goal.getId());
                showToast("Meta excluída com sucesso!", true);
                confirmStage.close();
                loadGoalsView();
            } catch (Exception ex) {
                showToast("Erro ao excluir meta: " + ex.getMessage(), false);
            }
        });

        buttons.getChildren().addAll(cancelBtn, deleteBtn);
        confirmContent.getChildren().addAll(icon, title, message, buttons);

        StackPane root = new StackPane(confirmContent);
        root.setStyle("-fx-background-color: transparent;");
        root.setPadding(new Insets(50));

        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        confirmStage.setScene(scene);
        confirmStage.showAndWait();
    }

    private void handleAddToGoal(Goal goal) {
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

        Label title = new Label("Adicionar à Meta");
        title.setStyle("-fx-text-fill: #F4F4F5; -fx-font-size: 28px; -fx-font-weight: 600; -fx-letter-spacing: -0.5px;");

        Label subtitle = new Label("Quanto deseja adicionar?");
        subtitle.setStyle("-fx-text-fill: #A1A1AA; -fx-font-size: 13px;");
        subtitle.setWrapText(true);
        subtitle.setMaxWidth(380);
        subtitle.setAlignment(javafx.geometry.Pos.CENTER);

        header.getChildren().addAll(title, subtitle);

        VBox fieldsContainer = new VBox(20);
        fieldsContainer.setStyle("-fx-padding: 8 0 0 0;");

        VBox goalInfo = new VBox(12);
        goalInfo.setAlignment(Pos.CENTER);
        goalInfo.setStyle("-fx-background-color: #18181B; -fx-background-radius: 12; -fx-padding: 20; -fx-border-color: #3F3F46; -fx-border-width: 1; -fx-border-radius: 12;");

        Label goalName = new Label(goal.getName());
        goalName.setStyle("-fx-text-fill: #F4F4F5; -fx-font-size: 16px; -fx-font-weight: 600;");

        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        Label currentProgress = new Label(currencyFormat.format(goal.getCurrentAmount()) + " / " + currencyFormat.format(goal.getGoalAmount()));
        currentProgress.setStyle("-fx-text-fill: #7C3AED; -fx-font-size: 18px; -fx-font-weight: 700;");

        goalInfo.getChildren().addAll(goalName, currentProgress);

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

        fieldsContainer.getChildren().addAll(goalInfo, amountBox);

        HBox actionButtons = new HBox(12);
        actionButtons.setAlignment(javafx.geometry.Pos.CENTER);

        Button cancelBtn = new Button("Cancelar");
        cancelBtn.setPrefHeight(44);
        cancelBtn.setStyle("-fx-background-color: #18181B; -fx-text-fill: #F4F4F5; -fx-font-size: 14px; -fx-font-weight: 600; -fx-background-radius: 10; -fx-border-color: #3F3F46; -fx-border-width: 1.5; -fx-border-radius: 10; -fx-cursor: hand; -fx-pref-width: 194px;");
        cancelBtn.setOnAction(e -> {
            formStage.close();
            contentArea.setEffect(null);
        });

        Button saveBtn = new Button("Adicionar");
        saveBtn.setPrefHeight(44);
        saveBtn.setStyle("-fx-background-color: #7C3AED; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: 600; -fx-background-radius: 10; -fx-cursor: hand; -fx-pref-width: 194px;");
        saveBtn.setOnAction(e -> {
            String amountText = amountField.getText().trim();

            if (amountText.isEmpty()) {
                showToast("O valor é obrigatório!", false);
                return;
            }

            try {
                String cleanAmount = amountText.replace("R$", "").replace(".", "").replace(",", ".").trim();
                BigDecimal amount = new BigDecimal(cleanAmount);

                BigDecimal newAmount = goal.getCurrentAmount().add(amount);
                goal.setCurrentAmount(newAmount);

                goalService.update(goal.getId(), goal);

                if (newAmount.compareTo(goal.getGoalAmount()) >= 0 && (goal.getAchieved() == null || !goal.getAchieved())) {
                    goalService.markAsAchieved(goal.getId());
                    showToast("Parabéns! Meta concluída! 🎉", true);
                } else {
                    showToast("Valor adicionado com sucesso!", true);
                }
                formStage.close();
                contentArea.setEffect(null);
                loadGoalsView();
            } catch (NumberFormatException ex) {
                showToast("Valor inválido! Use apenas números.", false);
            } catch (Exception ex) {
                showToast("Erro ao adicionar valor: " + ex.getMessage(), false);
            }
        });

        actionButtons.getChildren().addAll(cancelBtn, saveBtn);
        formContent.getChildren().addAll(header, fieldsContainer, actionButtons);

        StackPane root = new StackPane(formContent);
        root.setStyle("-fx-background-color: transparent;");
        root.setPadding(new Insets(40));

        Scene scene = new Scene(root, 700, 600);
        scene.setFill(Color.TRANSPARENT);

        formStage.setScene(scene);
        formStage.setOnHidden(e -> contentArea.setEffect(null));
        formStage.showAndWait();
    }

    private void loadDashboardData() {
        try {
            List<Transaction> allTransactions = transactionService.getAll();
            BigDecimal totalIncome = allTransactions.stream()
                    .filter(t -> t.getCategory() != null && t.getCategory().getType() == CategoryType.INCOME)
                    .map(Transaction::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal totalExpense = allTransactions.stream()
                    .filter(t -> t.getCategory() != null && t.getCategory().getType() == CategoryType.EXPENSE)
                    .map(Transaction::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal balance = totalIncome.subtract(totalExpense);
            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

            Platform.runLater(() -> {
                Label incomeValueLabel = (Label) contentArea.lookup("#incomeValue");
                Label expenseValueLabel = (Label) contentArea.lookup("#expenseValue");
                Label balanceValueLabel = (Label) contentArea.lookup("#balanceValue");

                if (incomeValueLabel != null) incomeValueLabel.setText(currencyFormat.format(totalIncome));
                if (expenseValueLabel != null) expenseValueLabel.setText(currencyFormat.format(totalExpense));
                if (balanceValueLabel != null) balanceValueLabel.setText(currencyFormat.format(balance));

                loadFinancialChart(allTransactions);
                loadTopCategories();
            });

        } catch (Exception e) {
            e.printStackTrace();
            showError("Erro ao carregar dados do dashboard");
        }
    }

    private void loadFinancialChart(List<Transaction> allTransactions) {
        StackPane chartContainer = (StackPane) contentArea.lookup("#chartContainer");
        if (chartContainer == null) return;

        try {
            if (allTransactions.isEmpty()) {
                chartContainer.getChildren().clear();
                VBox emptyState = new VBox(16);
                emptyState.setAlignment(Pos.CENTER);

                Label icon = new Label("📊");
                icon.setStyle("-fx-font-size: 72px;");

                Label message = new Label("Sem dados para exibir");
                message.setStyle("-fx-text-fill: #71717A; -fx-font-size: 16px; -fx-font-weight: 600;");

                Label hint = new Label("Adicione transações para visualizar o gráfico");
                hint.setStyle("-fx-text-fill: #52525B; -fx-font-size: 13px;");

                emptyState.getChildren().addAll(icon, message, hint);
                chartContainer.getChildren().add(emptyState);
                return;
            }

            BigDecimal totalIncome = allTransactions.stream()
                    .filter(t -> t.getCategory() != null && t.getCategory().getType() == CategoryType.INCOME)
                    .map(Transaction::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal totalExpense = allTransactions.stream()
                    .filter(t -> t.getCategory() != null && t.getCategory().getType() == CategoryType.EXPENSE)
                    .map(Transaction::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            VBox chartContent = createChart(totalIncome, totalExpense);
            chartContainer.getChildren().clear();
            chartContainer.getChildren().add(chartContent);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadTopCategories() {
        try {
            List<Transaction> allTransactions = transactionService.getAll();
            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
            Map<Category, BigDecimal> categoryTotals = allTransactions.stream()
                    .filter(t -> t.getCategory() != null && t.getCategory().getType() == CategoryType.EXPENSE)
                    .collect(Collectors.groupingBy(
                            Transaction::getCategory,
                            Collectors.reducing(BigDecimal.ZERO, Transaction::getAmount, BigDecimal::add)
                    ));

            List<Map.Entry<Category, BigDecimal>> topCategories = categoryTotals.entrySet().stream()
                    .sorted(Map.Entry.<Category, BigDecimal>comparingByValue().reversed())
                    .limit(5)
                    .collect(Collectors.toList());

            BigDecimal totalExpenses = categoryTotals.values().stream()
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            VBox categoryContainer = (VBox) contentArea.lookup("#categoryExpensesContainer");

            if (categoryContainer != null) {
                categoryContainer.getChildren().clear();

                if (topCategories.isEmpty()) {
                    Label emptyLabel = new Label("Nenhuma despesa registrada");
                    emptyLabel.setStyle("-fx-text-fill: #71717A; -fx-font-size: 14px;");
                    categoryContainer.getChildren().add(emptyLabel);
                } else {
                    for (Map.Entry<Category, BigDecimal> entry : topCategories) {
                        Category category = entry.getKey();
                        BigDecimal amount = entry.getValue();

                        double percentage = totalExpenses.compareTo(BigDecimal.ZERO) > 0
                                ? amount.divide(totalExpenses, 4, RoundingMode.HALF_UP).multiply(new BigDecimal(100)).doubleValue()
                                : 0;

                        HBox categoryItem = createCategoryExpenseItem(category, amount, percentage, currencyFormat);
                        categoryContainer.getChildren().add(categoryItem);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadTransactionsView() {
        contentArea.getChildren().clear();

        HBox mainContainer = new HBox(24);
        mainContainer.setStyle("-fx-padding: 40 48; -fx-background-color: #18181B;");
        HBox.setHgrow(mainContainer, Priority.ALWAYS);

        VBox transactionsView = new VBox(28);
        HBox.setHgrow(transactionsView, Priority.ALWAYS);

        VBox header = new VBox(8);
        Label title = new Label("Transações");
        title.setStyle("-fx-text-fill: #F4F4F5; -fx-font-size: 28px; -fx-font-weight: 700;");
        Label subtitle = new Label("Histórico completo de movimentações financeiras");
        subtitle.setStyle("-fx-text-fill: #71717A; -fx-font-size: 14px;");
        header.getChildren().addAll(title, subtitle);

        VBox transactionsContainer = new VBox(16);
        transactionsContainer.setId("transactionsContainer");
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

        VBox filtersPanel = createTransactionFiltersPanel();

        mainContainer.getChildren().addAll(transactionsView, filtersPanel);

        ScrollPane scrollPane = new ScrollPane(mainContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        contentArea.getChildren().add(scrollPane);
    }

    private void loadCategoriesView() {
        contentArea.getChildren().clear();

        HBox mainContainer = new HBox(24);
        mainContainer.setStyle("-fx-padding: 40 48; -fx-background-color: #18181B;");
        HBox.setHgrow(mainContainer, Priority.ALWAYS);

        VBox categoriesView = new VBox(28);
        HBox.setHgrow(categoriesView, Priority.ALWAYS);

        VBox header = new VBox(8);
        Label title = new Label("Categorias");
        title.setStyle("-fx-text-fill: #F4F4F5; -fx-font-size: 28px; -fx-font-weight: 700;");
        Label subtitle = new Label("Organize suas finanças por categorias");
        subtitle.setStyle("-fx-text-fill: #71717A; -fx-font-size: 14px;");
        header.getChildren().addAll(title, subtitle);

        VBox categoriesContainer = new VBox(16);
        categoriesContainer.setId("categoriesContainer");
        categoriesContainer.setStyle("-fx-background-color: #27272A; -fx-background-radius: 18; -fx-padding: 32 28; -fx-border-color: #3F3F46; -fx-border-width: 1; -fx-border-radius: 18;");

        try {
            List<Category> categories = categoryService.getAll();
            List<Transaction> allTransactions = transactionService.getAll();
            Map<UUID, Long> transactionCountByCategory = allTransactions.stream()
                    .filter(t -> t.getCategory() != null)
                    .collect(Collectors.groupingBy(t -> t.getCategory().getId(), Collectors.counting()));

            Map<UUID, BigDecimal> totalByCategory = allTransactions.stream()
                    .filter(t -> t.getCategory() != null)
                    .collect(Collectors.groupingBy(
                            t -> t.getCategory().getId(),
                            Collectors.reducing(BigDecimal.ZERO, Transaction::getAmount, BigDecimal::add)
                    ));

            if (categories.isEmpty()) {
                VBox emptyState = new VBox(20);
                emptyState.setAlignment(Pos.CENTER);
                emptyState.setStyle("-fx-padding: 60;");

                Label emptyIcon = new Label("🏷️");
                emptyIcon.setStyle("-fx-font-size: 64px;");
                Label emptyLabel = new Label("Nenhuma categoria encontrada");
                emptyLabel.setStyle("-fx-text-fill: #A1A1AA; -fx-font-size: 18px; -fx-font-weight: 600;");
                Label emptyHint = new Label("Crie categorias para organizar suas transações");
                emptyHint.setStyle("-fx-text-fill: #71717A; -fx-font-size: 14px;");
                emptyState.getChildren().addAll(emptyIcon, emptyLabel, emptyHint);
                categoriesContainer.getChildren().add(emptyState);
            } else {
                NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
                for (Category category : categories) {
                    long transactionCount = transactionCountByCategory.getOrDefault(category.getId(), 0L);
                    BigDecimal total = totalByCategory.getOrDefault(category.getId(), BigDecimal.ZERO);
                    HBox categoryRow = createCategoryRow(category, transactionCount, total, currencyFormat);
                    categoriesContainer.getChildren().add(categoryRow);
                }
            }
        } catch (Exception e) {
            showError("Erro ao carregar categorias: " + e.getMessage());
            e.printStackTrace();
        }

        categoriesView.getChildren().addAll(header, categoriesContainer);

        VBox categoryFiltersPanel = createCategoryFiltersPanel();

        mainContainer.getChildren().addAll(categoriesView, categoryFiltersPanel);

        ScrollPane scrollPane = new ScrollPane(mainContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        contentArea.getChildren().add(scrollPane);
    }

    private void loadGoalsView() {
        contentArea.getChildren().clear();

        VBox goalsView = new VBox(28);
        goalsView.setStyle("-fx-padding: 40 48; -fx-background-color: #18181B;");

        VBox header = new VBox(8);
        Label title = new Label("Metas");
        title.setStyle("-fx-text-fill: #F4F4F5; -fx-font-size: 28px; -fx-font-weight: 700;");
        Label subtitle = new Label("Acompanhe suas metas financeiras");
        subtitle.setStyle("-fx-text-fill: #71717A; -fx-font-size: 14px;");
        header.getChildren().addAll(title, subtitle);

        VBox goalsContainer = new VBox(16);
        goalsContainer.setId("goalsContainer");
        goalsContainer.setStyle("-fx-background-color: #27272A; -fx-background-radius: 18; -fx-padding: 32 28; -fx-border-color: #3F3F46; -fx-border-width: 1; -fx-border-radius: 18;");

        try {
            List<Goal> goals = goalService.getAll();

            if (goals.isEmpty()) {
                VBox emptyState = new VBox(20);
                emptyState.setAlignment(Pos.CENTER);
                emptyState.setStyle("-fx-padding: 60;");

                Label emptyIcon = new Label("🎯");
                emptyIcon.setStyle("-fx-font-size: 64px;");
                Label emptyLabel = new Label("Nenhuma meta encontrada");
                emptyLabel.setStyle("-fx-text-fill: #A1A1AA; -fx-font-size: 18px; -fx-font-weight: 600;");
                Label emptyHint = new Label("Crie metas para organizar seus objetivos financeiros");
                emptyHint.setStyle("-fx-text-fill: #71717A; -fx-font-size: 14px;");
                emptyState.getChildren().addAll(emptyIcon, emptyLabel, emptyHint);
                goalsContainer.getChildren().add(emptyState);
            } else {
                for (Goal goal : goals) {
                    HBox goalRow = createGoalRow(goal);
                    goalsContainer.getChildren().add(goalRow);
                }
            }
        } catch (Exception e) {
            showError("Erro ao carregar metas: " + e.getMessage());
            e.printStackTrace();
        }

        goalsView.getChildren().addAll(header, goalsContainer);

        ScrollPane scrollPane = new ScrollPane(goalsView);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        contentArea.getChildren().add(scrollPane);
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

    private void openEditTransactionForm(Transaction transaction) {
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

        Label title = new Label("Editar Transação");
        title.setStyle("-fx-text-fill: #F4F4F5; -fx-font-size: 28px; -fx-font-weight: 600; -fx-letter-spacing: -0.5px;");

        Label subtitle = new Label("Atualize os dados da transação");
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

        TextField nameField = new TextField(transaction.getName());
        nameField.setStyle("-fx-background-color: #18181B; -fx-text-fill: #F4F4F5; -fx-prompt-text-fill: #71717A; -fx-background-radius: 10; -fx-border-color: #3F3F46; -fx-border-radius: 10; -fx-border-width: 1.5; -fx-padding: 13 16; -fx-font-size: 14px; -fx-pref-width: 400px;");
        nameBox.getChildren().addAll(nameLabel, nameField);

        VBox amountBox = new VBox(10);
        Label amountLabel = new Label("Valor *");
        amountLabel.setStyle("-fx-text-fill: #E4E4E7; -fx-font-size: 13px; -fx-font-weight: 500;");

        TextField amountField = new TextField(transaction.getAmount().toString().replace('.', ','));
        amountField.setStyle("-fx-background-color: #18181B; -fx-text-fill: #F4F4F5; -fx-prompt-text-fill: #71717A; -fx-background-radius: 10; -fx-border-color: #3F3F46; -fx-border-radius: 10; -fx-border-width: 1.5; -fx-padding: 13 16; -fx-font-size: 14px; -fx-pref-width: 400px;");
        amountBox.getChildren().addAll(amountLabel, amountField);

        VBox categoryBox = new VBox(10);
        Label categoryLabel = new Label("Categoria *");
        categoryLabel.setStyle("-fx-text-fill: #E4E4E7; -fx-font-size: 13px; -fx-font-weight: 500;");

        ComboBox<Category> categoryCombo = new ComboBox<>();
        categoryCombo.setStyle("-fx-background-color: #18181B; -fx-text-fill: #F4F4F5; -fx-background-radius: 10; -fx-border-color: #3F3F46; -fx-border-radius: 10; -fx-border-width: 1.5; -fx-padding: 8 16; -fx-font-size: 14px; -fx-pref-width: 400px;");

        try {
            List<Category> categories = categoryService.getAll();
            categoryCombo.getItems().addAll(categories);
            categoryCombo.setValue(transaction.getCategory());
        } catch (Exception ex) {
            showToast("Erro ao carregar categorias: " + ex.getMessage(), false);
        }

        categoryBox.getChildren().addAll(categoryLabel, categoryCombo);

        VBox notesBox = new VBox(10);
        Label notesLabel = new Label("Notas");
        notesLabel.setStyle("-fx-text-fill: #E4E4E7; -fx-font-size: 13px; -fx-font-weight: 500;");

        TextArea notesField = new TextArea(transaction.getNotes() != null ? transaction.getNotes() : "");
        notesField.setPrefRowCount(3);
        notesField.setWrapText(true);
        notesField.setStyle("-fx-control-inner-background: #18181B; -fx-background-color: #18181B; -fx-text-fill: #F4F4F5; -fx-prompt-text-fill: #71717A; -fx-background-radius: 10; -fx-border-color: #3F3F46; -fx-border-radius: 10; -fx-border-width: 1.5; -fx-padding: 13 16; -fx-font-size: 14px; -fx-pref-width: 400px; -fx-font-family: 'Segoe UI', 'Arial', sans-serif;");
        notesBox.getChildren().addAll(notesLabel, notesField);

        fieldsContainer.getChildren().addAll(nameBox, amountBox, categoryBox, notesBox);

        HBox actionButtons = new HBox(12);
        actionButtons.setAlignment(javafx.geometry.Pos.CENTER);

        Button cancelBtn = new Button("Cancelar");
        cancelBtn.setPrefHeight(44);
        cancelBtn.setStyle("-fx-background-color: #18181B; -fx-text-fill: #F4F4F5; -fx-font-size: 14px; -fx-font-weight: 600; -fx-background-radius: 10; -fx-border-color: #3F3F46; -fx-border-width: 1.5; -fx-border-radius: 10; -fx-cursor: hand; -fx-pref-width: 194px;");
        cancelBtn.setOnAction(e -> {
            formStage.close();
            contentArea.setEffect(null);
        });

        Button saveBtn = new Button("Salvar Alterações");
        saveBtn.setPrefHeight(44);
        saveBtn.setStyle("-fx-background-color: #7C3AED; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: 600; -fx-background-radius: 10; -fx-cursor: hand; -fx-pref-width: 194px;");
        saveBtn.setOnAction(e -> {
            try {
                String cleanAmount = amountField.getText().replace("R$", "").replace(".", "").replace(",", ".").trim();
                BigDecimal amount = new BigDecimal(cleanAmount);

                Transaction updatedTransaction = new Transaction();
                updatedTransaction.setName(nameField.getText().trim());
                updatedTransaction.setAmount(amount);
                updatedTransaction.setCategory(categoryCombo.getValue());
                updatedTransaction.setNotes(notesField.getText().trim().isEmpty() ? null : notesField.getText().trim());

                transactionService.update(transaction.getId(), updatedTransaction);
                showToast("Transação atualizada com sucesso!", true);
                formStage.close();
                contentArea.setEffect(null);
                loadTransactionsView();
            } catch (Exception ex) {
                showToast("Erro ao atualizar transação: " + ex.getMessage(), false);
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
        formStage.showAndWait();
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

                if ("categories".equals(currentView)) {
                    loadCategoriesView();
                }

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

    private void openEditCategoryForm(Category category) {
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

        Label title = new Label("Editar Categoria");
        title.setStyle("-fx-text-fill: #F4F4F5; -fx-font-size: 28px; -fx-font-weight: 600; -fx-letter-spacing: -0.5px;");

        Label subtitle = new Label("Atualize os dados da categoria");
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

        TextField nameField = new TextField(category.getName());
        nameField.setStyle("-fx-background-color: #18181B; -fx-text-fill: #F4F4F5; -fx-prompt-text-fill: #71717A; -fx-background-radius: 10; -fx-border-color: #3F3F46; -fx-border-radius: 10; -fx-border-width: 1.5; -fx-padding: 13 16; -fx-font-size: 14px; -fx-pref-width: 400px;");
        nameBox.getChildren().addAll(nameLabel, nameField);

        VBox descBox = new VBox(10);
        Label descLabel = new Label("Descrição");
        descLabel.setStyle("-fx-text-fill: #E4E4E7; -fx-font-size: 13px; -fx-font-weight: 500;");

        TextArea descField = new TextArea(category.getDescription() != null ? category.getDescription() : "");
        descField.setPrefRowCount(3);
        descField.setWrapText(true);
        descField.setStyle("-fx-control-inner-background: #18181B; -fx-background-color: #18181B; -fx-text-fill: #F4F4F5; -fx-prompt-text-fill: #71717A; -fx-background-radius: 10; -fx-border-color: #3F3F46; -fx-border-radius: 10; -fx-border-width: 1.5; -fx-padding: 13 16; -fx-font-size: 14px; -fx-pref-width: 400px; -fx-font-family: 'Segoe UI', 'Arial', sans-serif;");
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

        if (category.getType() == CategoryType.INCOME) {
            incomeRadio.setSelected(true);
            updateTypeSelection(incomeBox, expenseBox, true);
        } else {
            expenseRadio.setSelected(true);
            updateTypeSelection(incomeBox, expenseBox, false);
        }

        incomeBox.setOnMouseClicked(e -> {
            incomeRadio.setSelected(true);
            updateTypeSelection(incomeBox, expenseBox, true);
        });
        expenseBox.setOnMouseClicked(e -> {
            expenseRadio.setSelected(true);
            updateTypeSelection(incomeBox, expenseBox, false);
        });

        typeButtons.getChildren().addAll(incomeBox, expenseBox);
        typeBox.getChildren().addAll(typeLabel, typeButtons);

        fieldsContainer.getChildren().addAll(nameBox, descBox, typeBox);

        HBox actionButtons = new HBox(12);
        actionButtons.setAlignment(javafx.geometry.Pos.CENTER);

        Button cancelBtn = new Button("Cancelar");
        cancelBtn.setPrefHeight(44);
        cancelBtn.setStyle("-fx-background-color: #18181B; -fx-text-fill: #F4F4F5; -fx-font-size: 14px; -fx-font-weight: 600; -fx-background-radius: 10; -fx-border-color: #3F3F46; -fx-border-width: 1.5; -fx-border-radius: 10; -fx-cursor: hand; -fx-pref-width: 194px;");
        cancelBtn.setOnAction(e -> {
            formStage.close();
            contentArea.setEffect(null);
        });

        Button saveBtn = new Button("Salvar Alterações");
        saveBtn.setPrefHeight(44);
        saveBtn.setStyle("-fx-background-color: #7C3AED; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: 600; -fx-background-radius: 10; -fx-cursor: hand; -fx-pref-width: 194px;");
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
                category.setName(name);
                category.setDescription(description.isEmpty() ? null : description);
                category.setType(CategoryType.valueOf((String) selectedType.getUserData()));

                categoryService.update(category.getId(), category);
                showToast("Categoria atualizada com sucesso!", true);
                formStage.close();
                contentArea.setEffect(null);
                loadCategoriesView();
            } catch (Exception ex) {
                showToast("Erro ao atualizar categoria: " + ex.getMessage(), false);
                ex.printStackTrace();
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
        formStage.showAndWait();
    }

    private void openGoalForm() {
        System.out.println("[formulario nova meta]: abrindo");

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

        Label title = new Label("Nova Meta");
        title.setStyle("-fx-text-fill: #F4F4F5; -fx-font-size: 28px; -fx-font-weight: 600; -fx-letter-spacing: -0.5px;");

        Label subtitle = new Label("Defina sua meta financeira");
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
        nameField.setPromptText("Ex: Viagem, Carro novo, Reserva de emergência...");
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
        descField.setPromptText("Descreva sua meta (opcional)");
        descField.setPrefRowCount(2);
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

        VBox goalAmountBox = new VBox(10);
        Label goalAmountLabel = new Label("Valor da Meta *");
        goalAmountLabel.setStyle("-fx-text-fill: #E4E4E7; -fx-font-size: 13px; -fx-font-weight: 500;");

        TextField goalAmountField = new TextField();
        goalAmountField.setPromptText("R$ 0,00");
        goalAmountField.setStyle("-fx-background-color: #18181B; -fx-text-fill: #F4F4F5; -fx-prompt-text-fill: #71717A; -fx-background-radius: 10; -fx-border-color: #3F3F46; -fx-border-radius: 10; -fx-border-width: 1.5; -fx-padding: 13 16; -fx-font-size: 14px; -fx-pref-width: 400px;");
        goalAmountField.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (isNowFocused) {
                goalAmountField.setStyle("-fx-background-color: #18181B; -fx-text-fill: #F4F4F5; -fx-prompt-text-fill: #71717A; -fx-background-radius: 10; -fx-border-color: #7C3AED; -fx-border-radius: 10; -fx-border-width: 1.5; -fx-padding: 13 16; -fx-font-size: 14px; -fx-pref-width: 400px;");
            } else {
                goalAmountField.setStyle("-fx-background-color: #18181B; -fx-text-fill: #F4F4F5; -fx-prompt-text-fill: #71717A; -fx-background-radius: 10; -fx-border-color: #3F3F46; -fx-border-radius: 10; -fx-border-width: 1.5; -fx-padding: 13 16; -fx-font-size: 14px; -fx-pref-width: 400px;");
            }
        });

        goalAmountBox.getChildren().addAll(goalAmountLabel, goalAmountField);

        VBox currentAmountBox = new VBox(10);
        Label currentAmountLabel = new Label("Valor Inicial");
        currentAmountLabel.setStyle("-fx-text-fill: #E4E4E7; -fx-font-size: 13px; -fx-font-weight: 500;");

        TextField currentAmountField = new TextField();
        currentAmountField.setPromptText("R$ 0,00");
        currentAmountField.setText("0");
        currentAmountField.setStyle("-fx-background-color: #18181B; -fx-text-fill: #F4F4F5; -fx-prompt-text-fill: #71717A; -fx-background-radius: 10; -fx-border-color: #3F3F46; -fx-border-radius: 10; -fx-border-width: 1.5; -fx-padding: 13 16; -fx-font-size: 14px; -fx-pref-width: 400px;");
        currentAmountField.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (isNowFocused) {
                currentAmountField.setStyle("-fx-background-color: #18181B; -fx-text-fill: #F4F4F5; -fx-prompt-text-fill: #71717A; -fx-background-radius: 10; -fx-border-color: #7C3AED; -fx-border-radius: 10; -fx-border-width: 1.5; -fx-padding: 13 16; -fx-font-size: 14px; -fx-pref-width: 400px;");
            } else {
                currentAmountField.setStyle("-fx-background-color: #18181B; -fx-text-fill: #F4F4F5; -fx-prompt-text-fill: #71717A; -fx-background-radius: 10; -fx-border-color: #3F3F46; -fx-border-radius: 10; -fx-border-width: 1.5; -fx-padding: 13 16; -fx-font-size: 14px; -fx-pref-width: 400px;");
            }
        });

        currentAmountBox.getChildren().addAll(currentAmountLabel, currentAmountField);

        HBox datesBox = new HBox(10);

        VBox startDateBox = new VBox(10);
        Label startDateLabel = new Label("Data Início *");
        startDateLabel.setStyle("-fx-text-fill: #E4E4E7; -fx-font-size: 13px; -fx-font-weight: 500;");

        DatePicker startDatePicker = new DatePicker();
        startDatePicker.setValue(java.time.LocalDate.now());
        startDatePicker.setConverter(new javafx.util.StringConverter<java.time.LocalDate>() {
            private final java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy");

            @Override
            public String toString(java.time.LocalDate date) {
                return date != null ? formatter.format(date) : "";
            }

            @Override
            public java.time.LocalDate fromString(String string) {
                return (string != null && !string.isEmpty()) ? java.time.LocalDate.parse(string, formatter) : null;
            }
        });
        startDatePicker.setStyle("-fx-background-color: #18181B;");
        startDatePicker.getEditor().setStyle("-fx-text-fill: #F4F4F5; -fx-prompt-text-fill: #71717A; -fx-background-color: #18181B; -fx-background-radius: 10; -fx-border-color: #3F3F46; -fx-border-radius: 10; -fx-border-width: 1.5; -fx-padding: 13 16; -fx-font-size: 14px;");
        startDatePicker.setPromptText("dd/MM/yyyy");
        HBox.setHgrow(startDateBox, Priority.ALWAYS);
        startDateBox.getChildren().addAll(startDateLabel, startDatePicker);

        VBox endDateBox = new VBox(10);
        Label endDateLabel = new Label("Data Fim *");
        endDateLabel.setStyle("-fx-text-fill: #E4E4E7; -fx-font-size: 13px; -fx-font-weight: 500;");

        DatePicker endDatePicker = new DatePicker();
        endDatePicker.setConverter(new javafx.util.StringConverter<java.time.LocalDate>() {
            private final java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy");

            @Override
            public String toString(java.time.LocalDate date) {
                return date != null ? formatter.format(date) : "";
            }

            @Override
            public java.time.LocalDate fromString(String string) {
                return (string != null && !string.isEmpty()) ? java.time.LocalDate.parse(string, formatter) : null;
            }
        });
        endDatePicker.setStyle("-fx-background-color: #18181B;");
        endDatePicker.getEditor().setStyle("-fx-text-fill: #F4F4F5; -fx-prompt-text-fill: #71717A; -fx-background-color: #18181B; -fx-background-radius: 10; -fx-border-color: #3F3F46; -fx-border-radius: 10; -fx-border-width: 1.5; -fx-padding: 13 16; -fx-font-size: 14px;");
        endDatePicker.setPromptText("dd/MM/yyyy");
        HBox.setHgrow(endDateBox, Priority.ALWAYS);
        endDateBox.getChildren().addAll(endDateLabel, endDatePicker);

        datesBox.getChildren().addAll(startDateBox, endDateBox);

        fieldsContainer.getChildren().addAll(nameBox, descBox, goalAmountBox, currentAmountBox, datesBox);

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

        Button saveBtn = new Button("Criar Meta");
        saveBtn.setPrefHeight(44);
        saveBtn.setStyle("-fx-background-color: #7C3AED; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: 600; -fx-background-radius: 10; -fx-cursor: hand; -fx-pref-width: 194px;");
        saveBtn.setOnMouseEntered(e -> saveBtn.setStyle("-fx-background-color: #6D28D9; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: 600; -fx-background-radius: 10; -fx-cursor: hand; -fx-pref-width: 194px;"));
        saveBtn.setOnMouseExited(e -> saveBtn.setStyle("-fx-background-color: #7C3AED; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: 600; -fx-background-radius: 10; -fx-cursor: hand; -fx-pref-width: 194px;"));
        saveBtn.setOnAction(e -> {
            String name = nameField.getText().trim();
            String description = descField.getText().trim();
            String goalAmountText = goalAmountField.getText().trim();
            String currentAmountText = currentAmountField.getText().trim();
            java.time.LocalDate startDate = startDatePicker.getValue();
            java.time.LocalDate endDate = endDatePicker.getValue();

            if (name.isEmpty()) {
                showToast("O nome da meta é obrigatório!", false);
                nameField.setStyle("-fx-background-color: #18181B; -fx-text-fill: #F4F4F5; -fx-prompt-text-fill: #71717A; -fx-background-radius: 10; -fx-border-color: #EF4444; -fx-border-radius: 10; -fx-border-width: 1.5; -fx-padding: 13 16; -fx-font-size: 14px; -fx-pref-width: 400px;");
                return;
            }

            if (goalAmountText.isEmpty()) {
                showToast("O valor da meta é obrigatório!", false);
                goalAmountField.setStyle("-fx-background-color: #18181B; -fx-text-fill: #F4F4F5; -fx-prompt-text-fill: #71717A; -fx-background-radius: 10; -fx-border-color: #EF4444; -fx-border-radius: 10; -fx-border-width: 1.5; -fx-padding: 13 16; -fx-font-size: 14px; -fx-pref-width: 400px;");
                return;
            }

            if (startDate == null) {
                showToast("A data de início é obrigatória!", false);
                return;
            }

            if (endDate == null) {
                showToast("A data de fim é obrigatória!", false);
                return;
            }

            if (endDate.isBefore(startDate)) {
                showToast("A data de fim deve ser posterior à data de início!", false);
                return;
            }

            try {
                String cleanGoalAmount = goalAmountText.replace("R$", "").replace(".", "").replace(",", ".").trim();
                BigDecimal goalAmount = new BigDecimal(cleanGoalAmount);

                String cleanCurrentAmount = currentAmountText.replace("R$", "").replace(".", "").replace(",", ".").trim();
                BigDecimal currentAmount = new BigDecimal(cleanCurrentAmount);

                Goal goal = new Goal();
                goal.setName(name);
                goal.setDescription(description.isEmpty() ? null : description);
                goal.setGoalAmount(goalAmount);
                goal.setCurrentAmount(currentAmount);
                goal.setStartDate(startDate);
                goal.setEndDate(endDate);

                goalService.create(goal);
                showToast("Meta criada com sucesso!", true);
                formStage.close();
                contentArea.setEffect(null);

                if ("goals".equals(currentView)) {
                    loadGoalsView();
                }

            } catch (NumberFormatException ex) {
                showToast("Valor inválido! Use apenas números.", false);
            } catch (Exception ex) {
                showToast("Erro ao criar meta: " + ex.getMessage(), false);
                ex.printStackTrace();
            }
        });

        actionButtons.getChildren().addAll(cancelBtn, saveBtn);

        formContent.getChildren().addAll(header, fieldsContainer, actionButtons);

        StackPane root = new StackPane(formContent);
        root.setStyle("-fx-background-color: transparent;");
        root.setPadding(new Insets(40));

        Scene scene = new Scene(root, 700, 900);
        scene.setFill(Color.TRANSPARENT);

        formStage.setScene(scene);
        formStage.setOnHidden(e -> contentArea.setEffect(null));

        System.out.println("funcionou janela pra criar meta");
        formStage.showAndWait();
        System.out.println("janela fechou");
    }

    private void openEditGoalForm(Goal goal) {
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

        Label title = new Label("Editar Meta");
        title.setStyle("-fx-text-fill: #F4F4F5; -fx-font-size: 28px; -fx-font-weight: 600; -fx-letter-spacing: -0.5px;");

        Label subtitle = new Label("Atualize os dados da sua meta");
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

        TextField nameField = new TextField(goal.getName());
        nameField.setStyle("-fx-background-color: #18181B; -fx-text-fill: #F4F4F5; -fx-prompt-text-fill: #71717A; -fx-background-radius: 10; -fx-border-color: #3F3F46; -fx-border-radius: 10; -fx-border-width: 1.5; -fx-padding: 13 16; -fx-font-size: 14px; -fx-pref-width: 400px;");
        nameBox.getChildren().addAll(nameLabel, nameField);

        VBox descBox = new VBox(10);
        Label descLabel = new Label("Descrição");
        descLabel.setStyle("-fx-text-fill: #E4E4E7; -fx-font-size: 13px; -fx-font-weight: 500;");

        TextArea descField = new TextArea(goal.getDescription() != null ? goal.getDescription() : "");
        descField.setPrefRowCount(2);
        descField.setWrapText(true);
        descField.setStyle("-fx-control-inner-background: #18181B; -fx-background-color: #18181B; -fx-text-fill: #F4F4F5; -fx-prompt-text-fill: #71717A; -fx-background-radius: 10; -fx-border-color: #3F3F46; -fx-border-radius: 10; -fx-border-width: 1.5; -fx-padding: 13 16; -fx-font-size: 14px; -fx-pref-width: 400px; -fx-font-family: 'Segoe UI', 'Arial', sans-serif;");
        descBox.getChildren().addAll(descLabel, descField);

        VBox goalAmountBox = new VBox(10);
        Label goalAmountLabel = new Label("Valor da Meta *");
        goalAmountLabel.setStyle("-fx-text-fill: #E4E4E7; -fx-font-size: 13px; -fx-font-weight: 500;");

        TextField goalAmountField = new TextField(goal.getGoalAmount().toString());
        goalAmountField.setStyle("-fx-background-color: #18181B; -fx-text-fill: #F4F4F5; -fx-prompt-text-fill: #71717A; -fx-background-radius: 10; -fx-border-color: #3F3F46; -fx-border-radius: 10; -fx-border-width: 1.5; -fx-padding: 13 16; -fx-font-size: 14px; -fx-pref-width: 400px;");
        goalAmountBox.getChildren().addAll(goalAmountLabel, goalAmountField);

        VBox currentAmountBox = new VBox(10);
        Label currentAmountLabel = new Label("Valor Atual *");
        currentAmountLabel.setStyle("-fx-text-fill: #E4E4E7; -fx-font-size: 13px; -fx-font-weight: 500;");

        TextField currentAmountField = new TextField(goal.getCurrentAmount().toString());
        currentAmountField.setStyle("-fx-background-color: #18181B; -fx-text-fill: #F4F4F5; -fx-prompt-text-fill: #71717A; -fx-background-radius: 10; -fx-border-color: #3F3F46; -fx-border-radius: 10; -fx-border-width: 1.5; -fx-padding: 13 16; -fx-font-size: 14px; -fx-pref-width: 400px;");
        currentAmountBox.getChildren().addAll(currentAmountLabel, currentAmountField);

        HBox datesBox = new HBox(10);

        VBox startDateBox = new VBox(10);
        Label startDateLabel = new Label("Data Início *");
        startDateLabel.setStyle("-fx-text-fill: #E4E4E7; -fx-font-size: 13px; -fx-font-weight: 500;");

        DatePicker startDatePicker = new DatePicker(goal.getStartDate());
        startDatePicker.setConverter(new javafx.util.StringConverter<java.time.LocalDate>() {
            private final java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy");

            @Override
            public String toString(java.time.LocalDate date) {
                return date != null ? formatter.format(date) : "";
            }

            @Override
            public java.time.LocalDate fromString(String string) {
                return (string != null && !string.isEmpty()) ? java.time.LocalDate.parse(string, formatter) : null;
            }
        });
        startDatePicker.setStyle("-fx-background-color: #18181B;");
        startDatePicker.getEditor().setStyle("-fx-text-fill: #F4F4F5; -fx-prompt-text-fill: #71717A; -fx-background-color: #18181B; -fx-background-radius: 10; -fx-border-color: #3F3F46; -fx-border-radius: 10; -fx-border-width: 1.5; -fx-padding: 13 16; -fx-font-size: 14px;");
        startDatePicker.setPromptText("dd/MM/yyyy");
        HBox.setHgrow(startDateBox, Priority.ALWAYS);
        startDateBox.getChildren().addAll(startDateLabel, startDatePicker);

        VBox endDateBox = new VBox(10);
        Label endDateLabel = new Label("Data Fim *");
        endDateLabel.setStyle("-fx-text-fill: #E4E4E7; -fx-font-size: 13px; -fx-font-weight: 500;");

        DatePicker endDatePicker = new DatePicker(goal.getEndDate());
        endDatePicker.setConverter(new javafx.util.StringConverter<java.time.LocalDate>() {
            private final java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy");

            @Override
            public String toString(java.time.LocalDate date) {
                return date != null ? formatter.format(date) : "";
            }

            @Override
            public java.time.LocalDate fromString(String string) {
                return (string != null && !string.isEmpty()) ? java.time.LocalDate.parse(string, formatter) : null;
            }
        });
        endDatePicker.setStyle("-fx-background-color: #18181B;");
        endDatePicker.getEditor().setStyle("-fx-text-fill: #F4F4F5; -fx-prompt-text-fill: #71717A; -fx-background-color: #18181B; -fx-background-radius: 10; -fx-border-color: #3F3F46; -fx-border-radius: 10; -fx-borderwidth: 1.5; -fx-padding: 13 16; -fx-font-size: 14px;");
        endDatePicker.setPromptText("dd/MM/yyyy");
        HBox.setHgrow(endDateBox, Priority.ALWAYS);
        endDateBox.getChildren().addAll(endDateLabel, endDatePicker);
        datesBox.getChildren().addAll(startDateBox, endDateBox);

        fieldsContainer.getChildren().addAll(nameBox, descBox, goalAmountBox, currentAmountBox, datesBox);

        HBox actionButtons = new HBox(12);
        actionButtons.setAlignment(javafx.geometry.Pos.CENTER);

        Button cancelBtn = new Button("Cancelar");
        cancelBtn.setPrefHeight(44);
        cancelBtn.setStyle("-fx-background-color: #18181B; -fx-text-fill: #F4F4F5; -fx-font-size: 14px; -fx-font-weight: 600; -fx-background-radius: 10; -fx-border-color: #3F3F46; -fx-border-width: 1.5; -fx-border-radius: 10; -fx-cursor: hand; -fx-pref-width: 194px;");
        cancelBtn.setOnAction(e -> {
            formStage.close();
            contentArea.setEffect(null);
        });

        Button saveBtn = new Button("Salvar Alterações");
        saveBtn.setPrefHeight(44);
        saveBtn.setStyle("-fx-background-color: #7C3AED; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: 600; -fx-background-radius: 10; -fx-cursor: hand; -fx-pref-width: 194px;");
        saveBtn.setOnAction(e -> {
            String name = nameField.getText().trim();
            String description = descField.getText().trim();
            String goalAmountText = goalAmountField.getText().trim();
            String currentAmountText = currentAmountField.getText().trim();
            java.time.LocalDate startDate = startDatePicker.getValue();
            java.time.LocalDate endDate = endDatePicker.getValue();

            if (name.isEmpty()) {
                showToast("O nome da meta é obrigatório!", false);
                return;
            }

            if (goalAmountText.isEmpty()) {
                showToast("O valor da meta é obrigatório!", false);
                return;
            }

            if (startDate == null || endDate == null) {
                showToast("As datas são obrigatórias!", false);
                return;
            }

            if (endDate.isBefore(startDate)) {
                showToast("A data de fim deve ser posterior à data de início!", false);
                return;
            }

            try {
                String cleanGoalAmount = goalAmountText.replace("R$", "").replace(".", "").replace(",", ".").trim();
                BigDecimal goalAmount = new BigDecimal(cleanGoalAmount);

                String cleanCurrentAmount = currentAmountText.replace("R$", "").replace(".", "").replace(",", ".").trim();
                BigDecimal currentAmount = new BigDecimal(cleanCurrentAmount);

                goal.setName(name);
                goal.setDescription(description.isEmpty() ? null : description);
                goal.setGoalAmount(goalAmount);
                goal.setCurrentAmount(currentAmount);
                goal.setStartDate(startDate);
                goal.setEndDate(endDate);

                goalService.update(goal.getId(), goal);

                if (currentAmount.compareTo(goalAmount) >= 0) {
                    goalService.markAsAchieved(goal.getId());
                }

                showToast("Meta atualizada com sucesso!", true);
                formStage.close();
                contentArea.setEffect(null);
                loadGoalsView();
            } catch (NumberFormatException ex) {
                showToast("Valor inválido! Use apenas números.", false);
            } catch (Exception ex) {
                showToast("Erro ao atualizar meta: " + ex.getMessage(), false);
                ex.printStackTrace();
            }
        });

        actionButtons.getChildren().addAll(cancelBtn, saveBtn);
        formContent.getChildren().addAll(header, fieldsContainer, actionButtons);

        StackPane root = new StackPane(formContent);
        root.setStyle("-fx-background-color: transparent;");
        root.setPadding(new Insets(40));

        Scene scene = new Scene(root, 700, 900);
        scene.setFill(Color.TRANSPARENT);

        formStage.setScene(scene);
        formStage.setOnHidden(e -> contentArea.setEffect(null));
        formStage.showAndWait();
    }

    private VBox createChart(BigDecimal totalIncome, BigDecimal totalExpense) {
        VBox chart = new VBox(24);
        chart.setAlignment(Pos.CENTER);
        chart.setStyle("-fx-padding: 30 20;");

        HBox legend = new HBox(32);
        legend.setAlignment(Pos.CENTER);

        HBox incomeLegend = new HBox(8);
        incomeLegend.setAlignment(Pos.CENTER);
        StackPane incomeColor = new StackPane();
        incomeColor.setStyle("-fx-background-color: #22C55E; -fx-background-radius: 3; -fx-pref-width: 16; -fx-pref-height: 16;");
        Label incomeLabel = new Label("Receitas");
        incomeLabel.setStyle("-fx-text-fill: #A1A1AA; -fx-font-size: 13px; -fx-font-weight: 600;");
        incomeLegend.getChildren().addAll(incomeColor, incomeLabel);

        HBox expenseLegend = new HBox(8);
        expenseLegend.setAlignment(Pos.CENTER);
        StackPane expenseColor = new StackPane();
        expenseColor.setStyle("-fx-background-color: #EF4444; -fx-background-radius: 3; -fx-pref-width: 16; -fx-pref-height: 16;");
        Label expenseLabel = new Label("Despesas");
        expenseLabel.setStyle("-fx-text-fill: #A1A1AA; -fx-font-size: 13px; -fx-font-weight: 600;");
        expenseLegend.getChildren().addAll(expenseColor, expenseLabel);

        HBox profitLegend = new HBox(8);
        profitLegend.setAlignment(Pos.CENTER);
        StackPane profitLine = new StackPane();
        profitLine.setStyle("-fx-background-color: #3B82F6; -fx-background-radius: 3; -fx-pref-width: 16; -fx-pref-height: 16;");
        Label profitLabel = new Label("Lucro");
        profitLabel.setStyle("-fx-text-fill: #A1A1AA; -fx-font-size: 13px; -fx-font-weight: 600;");
        profitLegend.getChildren().addAll(profitLine, profitLabel);

        legend.getChildren().addAll(incomeLegend, expenseLegend, profitLegend);

        BigDecimal profit = totalIncome.subtract(totalExpense);
        if (profit.equals(BigDecimal.ZERO) || profit.signum() == -1) {
            profit = BigDecimal.ZERO;
        }
        BigDecimal maxValue = totalIncome.max(totalExpense).max(profit.abs());
        if (maxValue.compareTo(BigDecimal.ZERO) == 0) {
            maxValue = new BigDecimal("1000");
        }

        double chartHeight = 240;
        HBox barsContainer = new HBox(80);
        barsContainer.setAlignment(Pos.BOTTOM_CENTER);
        barsContainer.setPrefHeight(chartHeight + 60);

        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

        double incomeHeight = totalIncome.divide(maxValue, 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal(chartHeight)).doubleValue();
        double expenseHeight = totalExpense.divide(maxValue, 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal(chartHeight)).doubleValue();

        VBox incomeColumn = new VBox(12);
        incomeColumn.setAlignment(Pos.BOTTOM_CENTER);

        VBox incomeBar = new VBox();
        incomeBar.setPrefWidth(80);
        incomeBar.setPrefHeight(Math.max(incomeHeight, 4));
        incomeBar.setStyle("-fx-background-color: linear-gradient(to top, #22C55E, #4ADE80); -fx-background-radius: 8 8 0 0;");

        VBox incomeInfo = new VBox(4);
        incomeInfo.setAlignment(Pos.CENTER);
        Label incomeValue = new Label(currencyFormat.format(totalIncome));
        incomeValue.setStyle("-fx-text-fill: #22C55E; -fx-font-size: 16px; -fx-font-weight: 700;");
        Label incomeTitle = new Label("Receitas");
        incomeTitle.setStyle("-fx-text-fill: #A1A1AA; -fx-font-size: 13px; -fx-font-weight: 600;");
        incomeInfo.getChildren().addAll(incomeValue, incomeTitle);

        incomeColumn.getChildren().addAll(incomeBar, incomeInfo);

        VBox expenseColumn = new VBox(12);
        expenseColumn.setAlignment(Pos.BOTTOM_CENTER);

        VBox expenseBar = new VBox();
        expenseBar.setPrefWidth(80);
        expenseBar.setPrefHeight(Math.max(expenseHeight, 4));
        expenseBar.setStyle("-fx-background-color: linear-gradient(to top, #EF4444, #F87171); -fx-background-radius: 8 8 0 0;");

        VBox expenseInfo = new VBox(4);
        expenseInfo.setAlignment(Pos.CENTER);
        Label expenseValue = new Label(currencyFormat.format(totalExpense));
        expenseValue.setStyle("-fx-text-fill: #EF4444; -fx-font-size: 16px; -fx-font-weight: 700;");
        Label expenseTitle = new Label("Despesas");
        expenseTitle.setStyle("-fx-text-fill: #A1A1AA; -fx-font-size: 13px; -fx-font-weight: 600;");
        expenseInfo.getChildren().addAll(expenseValue, expenseTitle);

        expenseColumn.getChildren().addAll(expenseBar, expenseInfo);

        VBox profitColumn = new VBox(12);
        profitColumn.setAlignment(Pos.BOTTOM_CENTER);

        double profitHeight = profit.abs().divide(maxValue, 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal(chartHeight)).doubleValue();

        VBox profitBar = new VBox();
        profitBar.setPrefWidth(80);
        profitBar.setPrefHeight(Math.max(profitHeight, 4));
        profitBar.setStyle("-fx-background-color: linear-gradient(to top, #3B82F6, #60A5FA); -fx-background-radius: 8 8 0 0;");

        VBox profitInfo = new VBox(4);
        profitInfo.setAlignment(Pos.CENTER);
        Label profitValue = new Label(currencyFormat.format(profit));
        profitValue.setStyle("-fx-text-fill: #3B82F6; -fx-font-size: 16px; -fx-font-weight: 700;");
        Label profitTitle = new Label("Lucro");
        profitTitle.setStyle("-fx-text-fill: #A1A1AA; -fx-font-size: 13px; -fx-font-weight: 600;");
        profitInfo.getChildren().addAll(profitValue, profitTitle);

        profitColumn.getChildren().addAll(profitBar, profitInfo);

        barsContainer.getChildren().addAll(incomeColumn, expenseColumn, profitColumn);

        chart.getChildren().addAll(legend, barsContainer);

        return chart;
    }

    private HBox createCategoryExpenseItem(Category category, BigDecimal amount, double percentage, NumberFormat currencyFormat) {
        HBox item = new HBox(16);
        item.setAlignment(Pos.CENTER_LEFT);
        item.setStyle("-fx-background-color: #18181B; -fx-background-radius: 14; -fx-padding: 18 20; -fx-border-color: #3F3F46; -fx-border-width: 1; -fx-border-radius: 14;");
        StackPane iconContainer = new StackPane();
        iconContainer.setStyle("-fx-background-color: rgba(239, 68, 68, 0.15); -fx-background-radius: 12; -fx-min-width: 48; -fx-min-height: 48; -fx-max-width: 48; -fx-max-height: 48;");
        Label icon = new Label("💸");
        icon.setStyle("-fx-font-size: 24px;");
        iconContainer.getChildren().add(icon);
        VBox info = new VBox(8);
        HBox.setHgrow(info, Priority.ALWAYS);

        HBox topRow = new HBox(12);
        topRow.setAlignment(Pos.CENTER_LEFT);
        Label categoryName = new Label(category.getName());
        categoryName.setStyle("-fx-text-fill: #F4F4F5; -fx-font-size: 15px; -fx-font-weight: 600;");
        Label percentageLabel = new Label(String.format("%.1f%%", percentage));
        percentageLabel.setStyle("-fx-background-color: rgba(239, 68, 68, 0.12); -fx-text-fill: #FCA5A5; -fx-padding: 4 10; -fx-background-radius: 6; -fx-font-size: 11px; -fx-font-weight: 600;");
        topRow.getChildren().addAll(categoryName, percentageLabel);

        StackPane progressBar = new StackPane();
        progressBar.setStyle("-fx-background-color: #27272A; -fx-background-radius: 4; -fx-pref-height: 6;");

        StackPane progressFill = new StackPane();
        progressFill.setStyle("-fx-background-color: #EF4444; -fx-background-radius: 4; -fx-pref-height: 6;");
        progressFill.setMaxWidth(percentage * 3.5);
        progressFill.setAlignment(Pos.CENTER_LEFT);
        progressBar.getChildren().add(progressFill);
        StackPane.setAlignment(progressFill, Pos.CENTER_LEFT);
        info.getChildren().addAll(topRow, progressBar);
        Label amountLabel = new Label(currencyFormat.format(amount));
        amountLabel.setStyle("-fx-text-fill: #EF4444; -fx-font-size: 18px; -fx-font-weight: 700;");

        item.getChildren().addAll(iconContainer, info, amountLabel);

        return item;
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

        Locale localeBR = new Locale("pt", "BR");
        currencyFormat = NumberFormat.getNumberInstance(localeBR);
        currencyFormat.setMinimumFractionDigits(2);
        currencyFormat.setMaximumFractionDigits(2);

        String prefix = type == CategoryType.INCOME ? "+ " : "- ";
        Label amount = new Label(prefix + currencyFormat.format(transaction.getAmount()));
        String amountColor = type == CategoryType.INCOME ? "#22C55E" : "#EF4444";
        amount.setStyle("-fx-text-fill: " + amountColor + "; -fx-font-size: 22px; -fx-font-weight: 700; -fx-min-width: 120; -fx-alignment: center-right;");

        HBox actions = new HBox(8);
        actions.setAlignment(Pos.CENTER);

        Button editBtn = getButton(transaction);

        Button deleteBtn = new Button("Excluir");
        deleteBtn.setStyle("-fx-background-color: rgba(239, 68, 68, 0.1); -fx-text-fill: #EF4444; -fx-font-size: 12px; -fx-font-weight: 600; -fx-background-radius: 8; -fx-cursor: hand; -fx-padding: 8 14; -fx-border-color: rgba(239, 68, 68, 0.2); -fx-border-width: 1; -fx-border-radius: 8;");
        deleteBtn.setOnMouseEntered(e -> deleteBtn.setStyle("-fx-background-color: rgba(239, 68, 68, 0.2); -fx-text-fill: #EF4444; -fx-font-size: 12px; -fx-font-weight: 600; -fx-background-radius: 8; -fx-cursor: hand; -fx-padding: 8 14; -fx-border-color: rgba(239, 68, 68, 0.3); -fx-border-width: 1; -fx-border-radius: 8;"));
        deleteBtn.setOnMouseExited(e -> deleteBtn.setStyle("-fx-background-color: rgba(239, 68, 68, 0.1); -fx-text-fill: #EF4444; -fx-font-size: 12px; -fx-font-weight: 600; -fx-background-radius: 8; -fx-cursor: hand; -fx-padding: 8 14; -fx-border-color: rgba(239, 68, 68, 0.2); -fx-border-width: 1; -fx-border-radius: 8;"));
        deleteBtn.setOnAction(e -> handleDeleteTransaction(transaction));

        actions.getChildren().addAll(editBtn, deleteBtn);

        row.getChildren().addAll(iconContainer, info, amount, actions);

        return row;
    }

    private HBox createCategoryRow(Category category, long transactionCount, BigDecimal total, NumberFormat currencyFormat) {
        HBox row = new HBox(20);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setStyle("-fx-background-color: #18181B; -fx-background-radius: 14; -fx-padding: 20 24; -fx-border-color: #3F3F46; -fx-border-width: 1; -fx-border-radius: 14;");
        String emoji = category.getType() == CategoryType.INCOME ? "💰" : "💸";
        StackPane iconContainer = new StackPane();
        String iconBg = category.getType() == CategoryType.INCOME ? "rgba(34, 197, 94, 0.15)" : "rgba(239, 68, 68, 0.15)";
        iconContainer.setStyle("-fx-background-color: " + iconBg + "; -fx-background-radius: 12; -fx-min-width: 50; -fx-min-height: 50; -fx-max-width: 50; -fx-max-height: 50;");
        Label icon = new Label(emoji);
        icon.setStyle("-fx-font-size: 28px;");
        iconContainer.getChildren().add(icon);

        VBox info = new VBox(6);
        HBox.setHgrow(info, Priority.ALWAYS);
        Label name = new Label(category.getName());
        name.setStyle("-fx-text-fill: #F4F4F5; -fx-font-size: 16px; -fx-font-weight: 600;");
        HBox detailsBox = new HBox(8);
        detailsBox.setAlignment(Pos.CENTER_LEFT);
        Label typeBadge = new Label(category.getType() == CategoryType.INCOME ? "Receita" : "Despesa");
        String badgeColor = category.getType() == CategoryType.INCOME ? "rgba(34, 197, 94, 0.15)" : "rgba(239, 68, 68, 0.15)";
        String textColor = category.getType() == CategoryType.INCOME ? "#22C55E" : "#EF4444";
        typeBadge.setStyle("-fx-background-color: " + badgeColor + "; -fx-text-fill: " + textColor + "; -fx-padding: 3 10; -fx-background-radius: 6; -fx-font-size: 11px; -fx-font-weight: 600;");

        Label separator = new Label("•");
        separator.setStyle("-fx-text-fill: #52525B; -fx-font-size: 13px;");

        Label countLabel = new Label(transactionCount + " transaç" + (transactionCount == 1 ? "ão" : "ões"));
        countLabel.setStyle("-fx-text-fill: #71717A; -fx-font-size: 13px;");

        detailsBox.getChildren().addAll(typeBadge, separator, countLabel);

        if (category.getDescription() != null && !category.getDescription().isEmpty()) {
            Label separator2 = new Label("•");
            separator2.setStyle("-fx-text-fill: #52525B; -fx-font-size: 13px;");
            Label description = new Label(category.getDescription());
            description.setStyle("-fx-text-fill: #71717A; -fx-font-size: 13px;");
            description.setMaxWidth(300);
            detailsBox.getChildren().addAll(separator2, description);
        }

        info.getChildren().addAll(name, detailsBox);
        Label amount = new Label(currencyFormat.format(total));
        amount.setStyle("-fx-text-fill: " + textColor + "; -fx-font-size: 22px; -fx-font-weight: 700; -fx-min-width: 120; -fx-alignment: center-right;");

        HBox actions = new HBox(8);
        actions.setAlignment(Pos.CENTER);

        Button editBtn = new Button("Editar");
        editBtn.setStyle("-fx-background-color: rgba(59, 130, 246, 0.1); -fx-text-fill: #3B82F6; -fx-font-size: 12px; -fx-font-weight: 600; -fx-background-radius: 8; -fx-cursor: hand; -fx-padding: 8 14; -fx-border-color: rgba(59, 130, 246, 0.2); -fx-border-width: 1; -fx-border-radius: 8;");
        editBtn.setOnMouseEntered(e -> editBtn.setStyle("-fx-background-color: rgba(59, 130, 246, 0.2); -fx-text-fill: #3B82F6; -fx-font-size: 12px; -fx-font-weight: 600; -fx-background-radius: 8; -fx-cursor: hand; -fx-padding: 8 14; -fx-border-color: rgba(59, 130, 246, 0.3); -fx-border-width: 1; -fx-border-radius: 8;"));
        editBtn.setOnMouseExited(e -> editBtn.setStyle("-fx-background-color: rgba(59, 130, 246, 0.1); -fx-text-fill: #3B82F6; -fx-font-size: 12px; -fx-font-weight: 600; -fx-background-radius: 8; -fx-cursor: hand; -fx-padding: 8 14; -fx-border-color: rgba(59, 130, 246, 0.2); -fx-border-width: 1; -fx-border-radius: 8;"));
        editBtn.setOnAction(e -> handleEditCategory(category));

        Button deleteBtn = new Button("Excluir");
        deleteBtn.setStyle("-fx-background-color: rgba(239, 68, 68, 0.1); -fx-text-fill: #EF4444; -fx-font-size: 12px; -fx-font-weight: 600; -fx-background-radius: 8; -fx-cursor: hand; -fx-padding: 8 14; -fx-border-color: rgba(239, 68, 68, 0.2); -fx-border-width: 1; -fx-border-radius: 8;");
        deleteBtn.setOnMouseEntered(e -> deleteBtn.setStyle("-fx-background-color: rgba(239, 68, 68, 0.2); -fx-text-fill: #EF4444; -fx-font-size: 12px; -fx-font-weight: 600; -fx-background-radius: 8; -fx-cursor: hand; -fx-padding: 8 14; -fx-border-color: rgba(239, 68, 68, 0.3); -fx-border-width: 1; -fx-border-radius: 8;"));
        deleteBtn.setOnMouseExited(e -> deleteBtn.setStyle("-fx-background-color: rgba(239, 68, 68, 0.1); -fx-text-fill: #EF4444; -fx-font-size: 12px; -fx-font-weight: 600; -fx-background-radius: 8; -fx-cursor: hand; -fx-padding: 8 14; -fx-border-color: rgba(239, 68, 68, 0.2); -fx-border-width: 1; -fx-border-radius: 8;"));
        deleteBtn.setOnAction(e -> handleDeleteCategory(category));

        actions.getChildren().addAll(editBtn, deleteBtn);

        row.getChildren().addAll(iconContainer, info, amount, actions);

        return row;
    }

    private HBox createGoalRow(Goal goal) {
        HBox row = new HBox(20);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setStyle("-fx-background-color: #18181B; -fx-background-radius: 14; -fx-padding: 24; -fx-border-color: #3F3F46; -fx-border-width: 1; -fx-border-radius: 14;");

        VBox mainContent = new VBox(16);
        HBox.setHgrow(mainContent, Priority.ALWAYS);

        HBox topRow = new HBox(16);
        topRow.setAlignment(Pos.CENTER_LEFT);

        StackPane iconContainer = new StackPane();
        boolean isAchieved = goal.getAchieved() != null && goal.getAchieved();
        boolean isExpired = java.time.LocalDate.now().isAfter(goal.getEndDate()) && !isAchieved;

        String iconBg = isAchieved ? "rgba(34, 197, 94, 0.15)" : (isExpired ? "rgba(239, 68, 68, 0.15)" : "rgba(124, 58, 237, 0.15)");
        iconContainer.setStyle("-fx-background-color: " + iconBg + "; -fx-background-radius: 12; -fx-min-width: 56; -fx-min-height: 56; -fx-max-width: 56; -fx-max-height: 56;");
        Label icon = new Label(isAchieved ? "✓" : "🎯");
        icon.setStyle("-fx-font-size: " + (isAchieved ? "32px" : "28px") + "; -fx-text-fill: " + (isAchieved ? "#22C55E" : "#7C3AED") + ";");
        iconContainer.getChildren().add(icon);

        VBox info = new VBox(6);
        HBox.setHgrow(info, Priority.ALWAYS);

        HBox nameRow = new HBox(12);
        nameRow.setAlignment(Pos.CENTER_LEFT);

        Label name = new Label(goal.getName());
        name.setStyle("-fx-text-fill: #F4F4F5; -fx-font-size: 18px; -fx-font-weight: 600;");

        if (isAchieved) {
            Label achievedBadge = new Label("Concluída");
            achievedBadge.setStyle("-fx-background-color: rgba(34, 197, 94, 0.15); -fx-text-fill: #22C55E; -fx-padding: 4 12; -fx-background-radius: 6; -fx-font-size: 11px; -fx-font-weight: 600;");
            nameRow.getChildren().addAll(name, achievedBadge);
        } else if (isExpired) {
            Label expiredBadge = new Label("Expirada");
            expiredBadge.setStyle("-fx-background-color: rgba(239, 68, 68, 0.15); -fx-text-fill: #EF4444; -fx-padding: 4 12; -fx-background-radius: 6; -fx-font-size: 11px; -fx-font-weight: 600;");
            nameRow.getChildren().addAll(name, expiredBadge);
        } else {
            Label activeBadge = new Label("Em andamento");
            activeBadge.setStyle("-fx-background-color: rgba(124, 58, 237, 0.15); -fx-text-fill: #A78BFA; -fx-padding: 4 12; -fx-background-radius: 6; -fx-font-size: 11px; -fx-font-weight: 600;");
            nameRow.getChildren().addAll(name, activeBadge);
        }

        HBox detailsBox = new HBox(8);
        detailsBox.setAlignment(Pos.CENTER_LEFT);

        if (goal.getDescription() != null && !goal.getDescription().isEmpty()) {
            Label description = new Label(goal.getDescription());
            description.setStyle("-fx-text-fill: #71717A; -fx-font-size: 13px;");
            description.setMaxWidth(300);
            detailsBox.getChildren().add(description);
        }

        Label separator = new Label("•");
        separator.setStyle("-fx-text-fill: #52525B; -fx-font-size: 13px;");

        DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy");
        Label dateRange = new Label(goal.getStartDate().format(formatter) + " - " + goal.getEndDate().format(formatter));
        dateRange.setStyle("-fx-text-fill: #71717A; -fx-font-size: 13px;");

        if (goal.getDescription() != null && !goal.getDescription().isEmpty()) {
            detailsBox.getChildren().addAll(separator, dateRange);
        } else {
            detailsBox.getChildren().add(dateRange);
        }

        info.getChildren().addAll(nameRow, detailsBox);

        topRow.getChildren().addAll(iconContainer, info);

        VBox progressSection = new VBox(12);
        progressSection.setStyle("-fx-background-color: #27272A; -fx-background-radius: 12; -fx-padding: 16; -fx-border-color: #3F3F46; -fx-border-width: 1; -fx-border-radius: 12;");

        HBox amountsRow = new HBox();
        HBox.setHgrow(amountsRow, Priority.ALWAYS);
        amountsRow.setAlignment(Pos.CENTER_LEFT);

        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

        VBox currentBox = new VBox(4);
        Label currentLabel = new Label("Atual");
        currentLabel.setStyle("-fx-text-fill: #71717A; -fx-font-size: 11px; -fx-font-weight: 600;");
        Label currentValue = new Label(currencyFormat.format(goal.getCurrentAmount()));
        currentValue.setStyle("-fx-text-fill: #7C3AED; -fx-font-size: 20px; -fx-font-weight: 700;");
        currentBox.getChildren().addAll(currentLabel, currentValue);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        VBox targetBox = new VBox(4);
        targetBox.setAlignment(Pos.TOP_RIGHT);
        Label targetLabel = new Label("Meta");
        targetLabel.setStyle("-fx-text-fill: #71717A; -fx-font-size: 11px; -fx-font-weight: 600;");
        Label targetValue = new Label(currencyFormat.format(goal.getGoalAmount()));
        targetValue.setStyle("-fx-text-fill: #F4F4F5; -fx-font-size: 20px; -fx-font-weight: 700;");
        targetBox.getChildren().addAll(targetLabel, targetValue);

        amountsRow.getChildren().addAll(currentBox, spacer, targetBox);

        HBox progressBarContainer = new HBox();
        progressBarContainer.setStyle("-fx-background-color: #18181B; -fx-background-radius: 8; -fx-pref-height: 12;");
        HBox.setHgrow(progressBarContainer, Priority.ALWAYS);

        double percentage = goal.getCurrentAmount().divide(goal.getGoalAmount(), 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal(100)).doubleValue();

        double fillPercentage = Math.min(percentage, 100);

        Region progressFill = new Region();
        String progressColor = isAchieved ? "#22C55E" : (isExpired ? "#EF4444" : "linear-gradient(to right, #7C3AED, #A78BFA)");
        progressFill.setStyle("-fx-background-color: " + progressColor + "; -fx-background-radius: 8; -fx-pref-height: 12;");
        progressFill.prefWidthProperty().bind(progressBarContainer.widthProperty().multiply(fillPercentage / 100.0));

        progressBarContainer.getChildren().add(progressFill);

        Label percentageLabel = new Label(String.format("%.1f%%", percentage));
        percentageLabel.setStyle("-fx-text-fill: #A1A1AA; -fx-font-size: 12px; -fx-font-weight: 600;");

        progressSection.getChildren().addAll(amountsRow, progressBarContainer, percentageLabel);

        mainContent.getChildren().addAll(topRow, progressSection);

        HBox actions = new HBox(8);
        actions.setAlignment(Pos.CENTER);

        if (!isExpired) {
            Button addBtn = new Button("Adicionar");
            addBtn.setStyle("-fx-background-color: rgba(124, 58, 237, 0.1); -fx-text-fill: #7C3AED; -fx-font-size: 12px; -fx-font-weight: 600; -fx-background-radius: 8; -fx-cursor: hand; -fx-padding: 8 14; -fx-border-color: rgba(124, 58, 237, 0.2); -fx-border-width: 1; -fx-border-radius: 8;");
            addBtn.setOnMouseEntered(e -> addBtn.setStyle("-fx-background-color: rgba(124, 58, 237, 0.2); -fx-text-fill: #7C3AED; -fx-font-size: 12px; -fx-font-weight: 600; -fx-background-radius: 8; -fx-cursor: hand; -fx-padding: 8 14; -fx-border-color: rgba(124, 58, 237, 0.3); -fx-border-width: 1; -fx-border-radius: 8;"));
            addBtn.setOnMouseExited(e -> addBtn.setStyle("-fx-background-color: rgba(124, 58, 237, 0.1); -fx-text-fill: #7C3AED; -fx-font-size: 12px; -fx-font-weight: 600; -fx-background-radius: 8; -fx-cursor: hand; -fx-padding: 8 14; -fx-border-color: rgba(124, 58, 237, 0.2); -fx-border-width: 1; -fx-border-radius: 8;"));
            addBtn.setOnAction(e -> handleAddToGoal(goal));
            actions.getChildren().add(addBtn);
        }

        Button editBtn = new Button("Editar");
        editBtn.setStyle("-fx-background-color: rgba(59, 130, 246, 0.1); -fx-text-fill: #3B82F6; -fx-font-size: 12px; -fx-font-weight: 600; -fx-background-radius: 8; -fx-cursor: hand; -fx-padding: 8 14; -fx-border-color: rgba(59, 130, 246, 0.2); -fx-border-width: 1; -fx-border-radius: 8;");
        editBtn.setOnMouseEntered(e -> editBtn.setStyle("-fx-background-color: rgba(59, 130, 246, 0.2); -fx-text-fill: #3B82F6; -fx-font-size: 12px; -fx-font-weight: 600; -fx-background-radius: 8; -fx-cursor: hand; -fx-padding: 8 14; -fx-border-color: rgba(59, 130, 246, 0.3); -fx-border-width: 1; -fx-border-radius: 8;"));
        editBtn.setOnMouseExited(e -> editBtn.setStyle("-fx-background-color: rgba(59, 130, 246, 0.1); -fx-text-fill: #3B82F6; -fx-font-size: 12px; -fx-font-weight: 600; -fx-background-radius: 8; -fx-cursor: hand; -fx-padding: 8 14; -fx-border-color: rgba(59, 130, 246, 0.2); -fx-border-width: 1; -fx-border-radius: 8;"));
        editBtn.setOnAction(e -> handleEditGoal(goal));

        Button deleteBtn = new Button("Excluir");
        deleteBtn.setStyle("-fx-background-color: rgba(239, 68, 68, 0.1); -fx-text-fill: #EF4444; -fx-font-size: 12px; -fx-font-weight: 600; -fx-background-radius: 8; -fx-cursor: hand; -fx-padding: 8 14; -fx-border-color: rgba(239, 68, 68, 0.2); -fx-border-width: 1; -fx-border-radius: 8;");
        deleteBtn.setOnMouseEntered(e -> deleteBtn.setStyle("-fx-background-color: rgba(239, 68, 68, 0.2); -fx-text-fill: #EF4444; -fx-font-size: 12px; -fx-font-weight: 600; -fx-background-radius: 8; -fx-cursor: hand; -fx-padding: 8 14; -fx-border-color: rgba(239, 68, 68, 0.3); -fx-border-width: 1; -fx-border-radius: 8;"));
        deleteBtn.setOnMouseExited(e -> deleteBtn.setStyle("-fx-background-color: rgba(239, 68, 68, 0.1); -fx-text-fill: #EF4444; -fx-font-size: 12px; -fx-font-weight: 600; -fx-background-radius: 8; -fx-cursor: hand; -fx-padding: 8 14; -fx-border-color: rgba(239, 68, 68, 0.2); -fx-border-width: 1; -fx-border-radius: 8;"));
        deleteBtn.setOnAction(e -> handleDeleteGoal(goal));

        actions.getChildren().addAll(editBtn, deleteBtn);

        row.getChildren().addAll(mainContent, actions);

        return row;
    }

    private VBox createTransactionFiltersPanel() {
        VBox filtersPanel = new VBox(20);
        filtersPanel.setStyle("-fx-background-color: #27272A; -fx-background-radius: 18; -fx-padding: 32 24; -fx-border-color: #3F3F46; -fx-border-width: 1; -fx-border-radius: 18; -fx-pref-width: 320; -fx-min-width: 320;");

        HBox titleBox = new HBox(12);
        titleBox.setAlignment(Pos.CENTER_LEFT);

        StackPane iconContainer = new StackPane();
        iconContainer.setStyle("-fx-background-color: rgba(124, 58, 237, 0.15); -fx-background-radius: 10; -fx-min-width: 40; -fx-min-height: 40; -fx-max-width: 40; -fx-max-height: 40;");
        Label filterIcon = new Label("🔍");
        filterIcon.setStyle("-fx-font-size: 20px;");
        iconContainer.getChildren().add(filterIcon);

        VBox titleContent = new VBox(2);
        Label filtersTitle = new Label("Filtros");
        filtersTitle.setStyle("-fx-text-fill: #F4F4F5; -fx-font-size: 18px; -fx-font-weight: 700;");
        Label filtersSubtitle = new Label("Refine sua busca");
        filtersSubtitle.setStyle("-fx-text-fill: #71717A; -fx-font-size: 12px;");
        titleContent.getChildren().addAll(filtersTitle, filtersSubtitle);

        titleBox.getChildren().addAll(iconContainer, titleContent);

        Separator separator1 = new Separator();
        separator1.setStyle("-fx-background-color: #3F3F46;");

        VBox searchBox = new VBox(8);
        Label searchLabel = new Label("Buscar por nome");
        searchLabel.setStyle("-fx-text-fill: #E4E4E7; -fx-font-size: 12px; -fx-font-weight: 600; -fx-letter-spacing: 0.5px;");

        TextField searchField = new TextField();
        searchField.setPromptText("Digite o nome...");
        searchField.setStyle("-fx-background-color: #18181B; -fx-text-fill: #F4F4F5; -fx-prompt-text-fill: #71717A; -fx-background-radius: 10; -fx-border-color: #3F3F46; -fx-border-radius: 10; -fx-border-width: 1.5; -fx-padding: 12 14; -fx-font-size: 13px;");
        searchField.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (isNowFocused) {
                searchField.setStyle("-fx-background-color: #18181B; -fx-text-fill: #F4F4F5; -fx-prompt-text-fill: #71717A; -fx-background-radius: 10; -fx-border-color: #7C3AED; -fx-border-radius: 10; -fx-border-width: 1.5; -fx-padding: 12 14; -fx-font-size: 13px;");
            } else {
                searchField.setStyle("-fx-background-color: #18181B; -fx-text-fill: #F4F4F5; -fx-prompt-text-fill: #71717A; -fx-background-radius: 10; -fx-border-color: #3F3F46; -fx-border-radius: 10; -fx-border-width: 1.5; -fx-padding: 12 14; -fx-font-size: 13px;");
            }
        });

        final javafx.concurrent.Task<Void>[] searchTask = new javafx.concurrent.Task[]{null};
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (searchTask[0] != null && searchTask[0].isRunning()) {
                searchTask[0].cancel();
            }

            searchTask[0] = new javafx.concurrent.Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    Thread.sleep(500);
                    return null;
                }
            };

            searchTask[0].setOnSucceeded(e -> applyCategoryFilters(searchField, null, null, null, null));
            new Thread(searchTask[0]).start();
        });

        searchBox.getChildren().addAll(searchLabel, searchField);

        VBox categoryBox = new VBox(8);
        Label categoryLabel = new Label("Categoria");
        categoryLabel.setStyle("-fx-text-fill: #E4E4E7; -fx-font-size: 12px; -fx-font-weight: 600; -fx-letter-spacing: 0.5px;");

        ComboBox<Category> categoryCombo = new ComboBox<>();
        categoryCombo.setPromptText("Todas");
        categoryCombo.setStyle("-fx-background-color: #18181B; -fx-text-fill: #F4F4F5; -fx-background-radius: 10; -fx-border-color: #3F3F46; -fx-border-radius: 10; -fx-border-width: 1.5; -fx-padding: 8 12; -fx-font-size: 13px;");
        categoryCombo.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (isNowFocused) {
                categoryCombo.setStyle("-fx-background-color: #18181B; -fx-text-fill: #F4F4F5; -fx-background-radius: 10; -fx-border-color: #7C3AED; -fx-border-radius: 10; -fx-border-width: 1.5; -fx-padding: 8 12; -fx-font-size: 13px;");
            } else {
                categoryCombo.setStyle("-fx-background-color: #18181B; -fx-text-fill: #F4F4F5; -fx-background-radius: 10; -fx-border-color: #3F3F46; -fx-border-radius: 10; -fx-border-width: 1.5; -fx-padding: 8 12; -fx-font-size: 13px;");
            }
        });

        try {
            List<Category> categories = categoryService.getAll();
            categoryCombo.getItems().add(null);
            categoryCombo.getItems().addAll(categories);

            categoryCombo.setButtonCell(new ListCell<Category>() {
                @Override
                protected void updateItem(Category item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText("Todas");
                    } else {
                        setText(item.getName());
                    }
                    setStyle("-fx-text-fill: #F4F4F5; -fx-background-color: #18181B;");
                }
            });

            categoryCombo.setCellFactory(lv -> new ListCell<Category>() {
                @Override
                protected void updateItem(Category item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText("Todas");
                    } else {
                        setText(item.getName());
                    }
                    setStyle("-fx-text-fill: #F4F4F5; -fx-background-color: #18181B; -fx-padding: 8 12;");
                }
            });

            categoryCombo.setOnAction(e -> applyCategoryFilters(searchField, categoryCombo, null, null, null));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        categoryBox.getChildren().addAll(categoryLabel, categoryCombo);

        VBox typeBox = new VBox(8);
        Label typeLabel = new Label("Tipo");
        typeLabel.setStyle("-fx-text-fill: #E4E4E7; -fx-font-size: 12px; -fx-font-weight: 600; -fx-letter-spacing: 0.5px;");

        ComboBox<String> typeCombo = new ComboBox<>();
        typeCombo.getItems().addAll("Todos", "Receitas", "Despesas");
        typeCombo.setValue("Todos");
        typeCombo.setStyle("-fx-background-color: #18181B; -fx-text-fill: #F4F4F5; -fx-background-radius: 10; -fx-border-color: #3F3F46; -fx-border-radius: 10; -fx-border-width: 1.5; -fx-padding: 8 12; -fx-font-size: 13px;");
        typeCombo.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (isNowFocused) {
                typeCombo.setStyle("-fx-background-color: #18181B; -fx-text-fill: #F4F4F5; -fx-background-radius: 10; -fx-border-color: #7C3AED; -fx-border-radius: 10; -fx-border-width: 1.5; -fx-padding: 8 12; -fx-font-size: 13px;");
            } else {
                typeCombo.setStyle("-fx-background-color: #18181B; -fx-text-fill: #F4F4F5; -fx-background-radius: 10; -fx-border-color: #3F3F46; -fx-border-radius: 10; -fx-border-width: 1.5; -fx-padding: 8 12; -fx-font-size: 13px;");
            }
        });
        typeCombo.setOnAction(e -> applyCategoryFilters(searchField, categoryCombo, typeCombo, null, null));

        typeBox.getChildren().addAll(typeLabel, typeCombo);

        VBox amountBox = new VBox(8);
        Label amountLabel = new Label("Faixa de valor");
        amountLabel.setStyle("-fx-text-fill: #E4E4E7; -fx-font-size: 12px; -fx-font-weight: 600; -fx-letter-spacing: 0.5px;");

        HBox amountFields = new HBox(10);

        TextField minAmountField = new TextField();
        minAmountField.setPromptText("Mínimo");
        minAmountField.setStyle("-fx-background-color: #18181B; -fx-text-fill: #F4F4F5; -fx-prompt-text-fill: #71717A; -fx-background-radius: 10; -fx-border-color: #3F3F46; -fx-border-radius: 10; -fx-border-width: 1.5; -fx-padding: 12 14; -fx-font-size: 13px;");
        HBox.setHgrow(minAmountField, Priority.ALWAYS);
        minAmountField.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (isNowFocused) {
                minAmountField.setStyle("-fx-background-color: #18181B; -fx-text-fill: #F4F4F5; -fx-prompt-text-fill: #71717A; -fx-background-radius: 10; -fx-border-color: #7C3AED; -fx-border-radius: 10; -fx-border-width: 1.5; -fx-padding: 12 14; -fx-font-size: 13px;");
            } else {
                minAmountField.setStyle("-fx-background-color: #18181B; -fx-text-fill: #F4F4F5; -fx-prompt-text-fill: #71717A; -fx-background-radius: 10; -fx-border-color: #3F3F46; -fx-border-radius: 10; -fx-border-width: 1.5; -fx-padding: 12 14; -fx-font-size: 13px;");
            }
        });

        TextField maxAmountField = new TextField();
        maxAmountField.setPromptText("Máximo");
        maxAmountField.setStyle("-fx-background-color: #18181B; -fx-text-fill: #F4F4F5; -fx-prompt-text-fill: #71717A; -fx-background-radius: 10; -fx-border-color: #3F3F46; -fx-border-radius: 10; -fx-border-width: 1.5; -fx-padding: 12 14; -fx-font-size: 13px;");
        HBox.setHgrow(maxAmountField, Priority.ALWAYS);
        maxAmountField.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (isNowFocused) {
                maxAmountField.setStyle("-fx-background-color: #18181B; -fx-text-fill: #F4F4F5; -fx-prompt-text-fill: #71717A; -fx-background-radius: 10; -fx-border-color: #7C3AED; -fx-border-radius: 10; -fx-border-width: 1.5; -fx-padding: 12 14; -fx-font-size: 13px;");
            } else {
                maxAmountField.setStyle("-fx-background-color: #18181B; -fx-text-fill: #F4F4F5; -fx-prompt-text-fill: #71717A; -fx-background-radius: 10; -fx-border-color: #3F3F46; -fx-border-radius: 10; -fx-border-width: 1.5; -fx-padding: 12 14; -fx-font-size: 13px;");
            }
        });

        final javafx.concurrent.Task<Void>[] amountTask = new javafx.concurrent.Task[]{null};
        javafx.beans.value.ChangeListener<String> amountListener = (obs, oldVal, newVal) -> {
            if (amountTask[0] != null && amountTask[0].isRunning()) {
                amountTask[0].cancel();
            }

            amountTask[0] = new javafx.concurrent.Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    Thread.sleep(500);
                    return null;
                }
            };

            amountTask[0].setOnSucceeded(e -> applyCategoryFilters(searchField, categoryCombo, typeCombo, minAmountField, maxAmountField));
            new Thread(amountTask[0]).start();
        };

        minAmountField.textProperty().addListener(amountListener);
        maxAmountField.textProperty().addListener(amountListener);

        amountFields.getChildren().addAll(minAmountField, maxAmountField);
        amountBox.getChildren().addAll(amountLabel, amountFields);

        Separator separator2 = new Separator();
        separator2.setStyle("-fx-background-color: #3F3F46;");

        Button clearBtn = new Button("Limpar Filtros");
        clearBtn.setMaxWidth(Double.MAX_VALUE);
        clearBtn.setPrefHeight(44);
        clearBtn.setStyle("-fx-background-color: #18181B; -fx-text-fill: #F4F4F5; -fx-font-size: 13px; -fx-font-weight: 600; -fx-background-radius: 10; -fx-border-color: #3F3F46; -fx-border-width: 1.5; -fx-border-radius: 10; -fx-cursor: hand;");
        clearBtn.setOnMouseEntered(e -> clearBtn.setStyle("-fx-background-color: #7C3AED; -fx-text-fill: white; -fx-font-size: 13px; -fx-font-weight: 600; -fx-background-radius: 10; -fx-border-color: #7C3AED; -fx-border-width: 1.5; -fx-border-radius: 10; -fx-cursor: hand;"));
        clearBtn.setOnMouseExited(e -> clearBtn.setStyle("-fx-background-color: #18181B; -fx-text-fill: #F4F4F5; -fx-font-size: 13px; -fx-font-weight: 600; -fx-background-radius: 10; -fx-border-color: #3F3F46; -fx-border-width: 1.5; -fx-border-radius: 10; -fx-cursor: hand;"));
        clearBtn.setOnAction(e -> {
            searchField.clear();
            categoryCombo.setValue(null);
            typeCombo.setValue("Todos");
            minAmountField.clear();
            maxAmountField.clear();
            loadTransactionsView();
        });

        filtersPanel.getChildren().addAll(titleBox, separator1, searchBox, categoryBox, typeBox, amountBox, separator2, clearBtn);

        return filtersPanel;
    }

    private VBox createCategoryFiltersPanel() {
        VBox filtersPanel = new VBox(20);
        filtersPanel.setStyle("-fx-background-color: #27272A; -fx-background-radius: 18; -fx-padding: 32 24; -fx-border-color: #3F3F46; -fx-border-width: 1; -fx-border-radius: 18; -fx-pref-width: 320; -fx-min-width: 320;");

        HBox titleBox = new HBox(12);
        titleBox.setAlignment(Pos.CENTER_LEFT);

        StackPane iconContainer = new StackPane();
        iconContainer.setStyle("-fx-background-color: rgba(124, 58, 237, 0.15); -fx-background-radius: 10; -fx-min-width: 40; -fx-min-height: 40; -fx-max-width: 40; -fx-max-height: 40;");
        Label filterIcon = new Label("🔍");
        filterIcon.setStyle("-fx-font-size: 20px;");
        iconContainer.getChildren().add(filterIcon);

        VBox titleContent = new VBox(2);
        Label filtersTitle = new Label("Filtros");
        filtersTitle.setStyle("-fx-text-fill: #F4F4F5; -fx-font-size: 18px; -fx-font-weight: 700;");
        Label filtersSubtitle = new Label("Refine sua busca");
        filtersSubtitle.setStyle("-fx-text-fill: #71717A; -fx-font-size: 12px;");
        titleContent.getChildren().addAll(filtersTitle, filtersSubtitle);

        titleBox.getChildren().addAll(iconContainer, titleContent);

        Separator separator1 = new Separator();
        separator1.setStyle("-fx-background-color: #3F3F46;");

        VBox searchBox = new VBox(8);
        Label searchLabel = new Label("Buscar por nome");
        searchLabel.setStyle("-fx-text-fill: #E4E4E7; -fx-font-size: 12px; -fx-font-weight: 600; -fx-letter-spacing: 0.5px;");

        TextField searchField = new TextField();
        searchField.setPromptText("Digite o nome...");
        searchField.setStyle("-fx-background-color: #18181B; -fx-text-fill: #F4F4F5; -fx-prompt-text-fill: #71717A; -fx-background-radius: 10; -fx-border-color: #3F3F46; -fx-border-radius: 10; -fx-border-width: 1.5; -fx-padding: 12 14; -fx-font-size: 13px;");
        searchField.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (isNowFocused) {
                searchField.setStyle("-fx-background-color: #18181B; -fx-text-fill: #F4F4F5; -fx-prompt-text-fill: #71717A; -fx-background-radius: 10; -fx-border-color: #7C3AED; -fx-border-radius: 10; -fx-border-width: 1.5; -fx-padding: 12 14; -fx-font-size: 13px;");
            } else {
                searchField.setStyle("-fx-background-color: #18181B; -fx-text-fill: #F4F4F5; -fx-prompt-text-fill: #71717A; -fx-background-radius: 10; -fx-border-color: #3F3F46; -fx-border-radius: 10; -fx-border-width: 1.5; -fx-padding: 12 14; -fx-font-size: 13px;");
            }
        });

        final javafx.concurrent.Task<Void>[] searchTask = new javafx.concurrent.Task[]{null};
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (searchTask[0] != null && searchTask[0].isRunning()) {
                searchTask[0].cancel();
            }

            searchTask[0] = new javafx.concurrent.Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    Thread.sleep(500);
                    return null;
                }
            };

            searchTask[0].setOnSucceeded(e -> applyCategoryFilters(searchField, null));
            new Thread(searchTask[0]).start();
        });

        searchBox.getChildren().addAll(searchLabel, searchField);

        VBox typeBox = new VBox(8);
        Label typeLabel = new Label("Tipo");
        typeLabel.setStyle("-fx-text-fill: #E4E4E7; -fx-font-size: 12px; -fx-font-weight: 600; -fx-letter-spacing: 0.5px;");

        ComboBox<String> typeCombo = new ComboBox<>();
        typeCombo.getItems().addAll("Todos", "Receitas", "Despesas");
        typeCombo.setValue("Todos");
        typeCombo.setStyle("-fx-background-color: #18181B; -fx-text-fill: #F4F4F5; -fx-background-radius: 10; -fx-border-color: #3F3F46; -fx-border-radius: 10; -fx-border-width: 1.5; -fx-padding: 8 12; -fx-font-size: 13px;");
        typeCombo.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (isNowFocused) {
                typeCombo.setStyle("-fx-background-color: #18181B; -fx-text-fill: #F4F4F5; -fx-background-radius: 10; -fx-border-color: #7C3AED; -fx-border-radius: 10; -fx-border-width: 1.5; -fx-padding: 8 12; -fx-font-size: 13px;");
            } else {
                typeCombo.setStyle("-fx-background-color: #18181B; -fx-text-fill: #F4F4F5; -fx-background-radius: 10; -fx-border-color: #3F3F46; -fx-border-radius: 10; -fx-border-width: 1.5; -fx-padding: 8 12; -fx-font-size: 13px;");
            }
        });
        typeCombo.setOnAction(e -> applyCategoryFilters(searchField, typeCombo));

        typeBox.getChildren().addAll(typeLabel, typeCombo);

        Separator separator2 = new Separator();
        separator2.setStyle("-fx-background-color: #3F3F46;");

        Button clearBtn = new Button("Limpar Filtros");
        clearBtn.setMaxWidth(Double.MAX_VALUE);
        clearBtn.setPrefHeight(44);
        clearBtn.setStyle("-fx-background-color: #18181B; -fx-text-fill: #F4F4F5; -fx-font-size: 13px; -fx-font-weight: 600; -fx-background-radius: 10; -fx-border-color: #3F3F46; -fx-border-width: 1.5; -fx-border-radius: 10; -fx-cursor: hand;");
        clearBtn.setOnMouseEntered(e -> clearBtn.setStyle("-fx-background-color: #7C3AED; -fx-text-fill: white; -fx-font-size: 13px; -fx-font-weight: 600; -fx-background-radius: 10; -fx-border-color: #7C3AED; -fx-border-width: 1.5; -fx-border-radius: 10; -fx-cursor: hand;"));
        clearBtn.setOnMouseExited(e -> clearBtn.setStyle("-fx-background-color: #18181B; -fx-text-fill: #F4F4F5; -fx-font-size: 13px; -fx-font-weight: 600; -fx-background-radius: 10; -fx-border-color: #3F3F46; -fx-border-width: 1.5; -fx-border-radius: 10; -fx-cursor: hand;"));
        clearBtn.setOnAction(e -> {
            searchField.clear();
            typeCombo.setValue("Todos");
            loadCategoriesView();
        });

        filtersPanel.getChildren().addAll(titleBox, separator1, searchBox, typeBox, separator2, clearBtn);

        return filtersPanel;
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

    private void updateTransactionsList(List<Transaction> transactions) {
        VBox transactionsContainer = (VBox) contentArea.lookup("#transactionsContainer");
        if (transactionsContainer == null) return;

        transactionsContainer.getChildren().clear();
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

        if (transactions.isEmpty()) {
            VBox emptyState = new VBox(20);
            emptyState.setAlignment(Pos.CENTER);
            emptyState.setStyle("-fx-padding: 60;");

            Label emptyIcon = new Label("🔍");
            emptyIcon.setStyle("-fx-font-size: 64px;");

            Label emptyLabel = new Label("Nenhuma transação encontrada");
            emptyLabel.setStyle("-fx-text-fill: #A1A1AA; -fx-font-size: 18px; -fx-font-weight: 600;");

            Label emptyHint = new Label("Tente ajustar os filtros");
            emptyHint.setStyle("-fx-text-fill: #71717A; -fx-font-size: 14px;");

            emptyState.getChildren().addAll(emptyIcon, emptyLabel, emptyHint);
            transactionsContainer.getChildren().add(emptyState);
        } else {
            for (Transaction transaction : transactions) {
                HBox transactionRow = createTransactionRow(transaction, currencyFormat);
                transactionsContainer.getChildren().add(transactionRow);
            }
        }
    }

    private void updateCategoriesList(List<Category> categories, List<Transaction> allTransactions) {
        VBox categoriesContainer = (VBox) contentArea.lookup("#categoriesContainer");
        if (categoriesContainer == null) return;

        categoriesContainer.getChildren().clear();

        Map<UUID, Long> transactionCountByCategory = allTransactions.stream()
                .filter(t -> t.getCategory() != null)
                .collect(Collectors.groupingBy(t -> t.getCategory().getId(), Collectors.counting()));

        Map<UUID, BigDecimal> totalByCategory = allTransactions.stream()
                .filter(t -> t.getCategory() != null)
                .collect(Collectors.groupingBy(
                        t -> t.getCategory().getId(),
                        Collectors.reducing(BigDecimal.ZERO, Transaction::getAmount, BigDecimal::add)
                ));

        if (categories.isEmpty()) {
            VBox emptyState = new VBox(20);
            emptyState.setAlignment(Pos.CENTER);
            emptyState.setStyle("-fx-padding: 60;");

            Label emptyIcon = new Label("🔍");
            emptyIcon.setStyle("-fx-font-size: 64px;");

            Label emptyLabel = new Label("Nenhuma categoria encontrada");
            emptyLabel.setStyle("-fx-text-fill: #A1A1AA; -fx-font-size: 18px; -fx-font-weight: 600;");

            Label emptyHint = new Label("Tente ajustar os filtros");
            emptyHint.setStyle("-fx-text-fill: #71717A; -fx-font-size: 14px;");

            emptyState.getChildren().addAll(emptyIcon, emptyLabel, emptyHint);
            categoriesContainer.getChildren().add(emptyState);
        } else {
            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
            for (Category category : categories) {
                long transactionCount = transactionCountByCategory.getOrDefault(category.getId(), 0L);
                BigDecimal total = totalByCategory.getOrDefault(category.getId(), BigDecimal.ZERO);
                HBox categoryRow = createCategoryRow(category, transactionCount, total, currencyFormat);
                categoriesContainer.getChildren().add(categoryRow);
            }
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

    private Button getButton(Transaction transaction) {
        Button editBtn = new Button("Editar");
        editBtn.setStyle("-fx-background-color: rgba(59, 130, 246, 0.1); -fx-text-fill: #3B82F6; -fx-font-size: 12px; -fx-font-weight: 600; -fx-background-radius: 8; -fx-cursor: hand; -fx-padding: 8 14; -fx-border-color: rgba(59, 130, 246, 0.2); -fx-border-width: 1; -fx-border-radius: 8;");
        editBtn.setOnMouseEntered(e -> editBtn.setStyle("-fx-background-color: rgba(59, 130, 246, 0.2); -fx-text-fill: #3B82F6; -fx-font-size: 12px; -fx-font-weight: 600; -fx-background-radius: 8; -fx-cursor: hand; -fx-padding: 8 14; -fx-border-color: rgba(59, 130, 246, 0.3); -fx-border-width: 1; -fx-border-radius: 8;"));
        editBtn.setOnMouseExited(e -> editBtn.setStyle("-fx-background-color: rgba(59, 130, 246, 0.1); -fx-text-fill: #3B82F6; -fx-font-size: 12px; -fx-font-weight: 600; -fx-background-radius: 8; -fx-cursor: hand; -fx-padding: 8 14; -fx-border-color: rgba(59, 130, 246, 0.2); -fx-border-width: 1; -fx-border-radius: 8;"));
        editBtn.setOnAction(e -> handleEditTransaction(transaction));
        return editBtn;
    }

    private void applyCategoryFilters(TextField searchField, ComboBox<Category> categoryCombo, ComboBox<String> typeCombo, TextField minAmountField, TextField maxAmountField) {
        try {
            List<Transaction> transactions = transactionService.getAll();

            String searchText = searchField.getText().trim().toLowerCase();
            if (!searchText.isEmpty()) {
                transactions = transactions.stream()
                        .filter(t -> t.getName().toLowerCase().contains(searchText))
                        .collect(Collectors.toList());
            }

            if (categoryCombo != null && categoryCombo.getValue() != null) {
                UUID categoryId = categoryCombo.getValue().getId();
                transactions = transactions.stream()
                        .filter(t -> t.getCategory() != null && t.getCategory().getId().equals(categoryId))
                        .collect(Collectors.toList());
            }

            if (typeCombo != null && !typeCombo.getValue().equals("Todos")) {
                CategoryType type = typeCombo.getValue().equals("Receitas") ? CategoryType.INCOME : CategoryType.EXPENSE;
                transactions = transactions.stream()
                        .filter(t -> t.getCategory() != null && t.getCategory().getType() == type)
                        .collect(Collectors.toList());
            }

            if (minAmountField != null && !minAmountField.getText().trim().isEmpty()) {
                try {
                    BigDecimal minAmount = new BigDecimal(minAmountField.getText().trim());
                    transactions = transactions.stream()
                            .filter(t -> t.getAmount().compareTo(minAmount) >= 0)
                            .collect(Collectors.toList());
                } catch (NumberFormatException ex) {
                }
            }

            if (maxAmountField != null && !maxAmountField.getText().trim().isEmpty()) {
                try {
                    BigDecimal maxAmount = new BigDecimal(maxAmountField.getText().trim());
                    transactions = transactions.stream()
                            .filter(t -> t.getAmount().compareTo(maxAmount) <= 0)
                            .collect(Collectors.toList());
                } catch (NumberFormatException ex) {
                }
            }

            updateTransactionsList(transactions);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void applyCategoryFilters(TextField searchField, ComboBox<String> typeCombo) {
        try {
            List<Category> categories = categoryService.getAll();
            List<Transaction> allTransactions = transactionService.getAll();

            String searchText = searchField.getText().trim().toLowerCase();
            if (!searchText.isEmpty()) {
                categories = categories.stream()
                        .filter(c -> c.getName().toLowerCase().contains(searchText))
                        .collect(Collectors.toList());
            }

            if (typeCombo != null && !typeCombo.getValue().equals("Todos")) {
                CategoryType type = typeCombo.getValue().equals("Receitas") ? CategoryType.INCOME : CategoryType.EXPENSE;
                categories = categories.stream()
                        .filter(c -> c.getType() == type)
                        .collect(Collectors.toList());
            }

            updateCategoriesList(categories, allTransactions);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}