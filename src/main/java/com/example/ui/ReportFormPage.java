package com.example.ui;

import com.example.model.Report;
import com.example.service.ReportService;
import com.example.service.Session;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class ReportFormPage extends DashboardPage {

    private static final int MAX_LENGTH = 1000;

    private final String preselectedType;

    public ReportFormPage(Navigator nav, String preselectedType) {
        super(nav);
        this.preselectedType = preselectedType;
    }

    @Override protected String navKey() { return "home"; }

    @Override
    protected Node buildContent() {
        ComboBox<String> typeBox = new ComboBox<>();
        typeBox.getItems().addAll("Bullying", "Harassment", "Other");
        typeBox.setPromptText("Select type");
        typeBox.setPrefWidth(300);
        if (typeBox.getItems().contains(preselectedType)) {
            typeBox.setValue(preselectedType);
        }

        TextArea what = new TextArea();
        what.setPromptText("Describe what happened, in your own words...");
        what.setPrefRowCount(5);
        what.setWrapText(true);
        Label counter = UI.muted("0 / " + MAX_LENGTH);
        what.textProperty().addListener((obs, oldText, newText) -> {
            if (newText.length() > MAX_LENGTH) {
                what.setText(oldText);
                return;
            }
            counter.setText(newText.length() + " / " + MAX_LENGTH);
        });

        TextField where = new TextField();
        where.setPromptText("e.g. Classroom, hallway, online...");

        DatePicker date = new DatePicker();
        date.setPromptText("Select date");
        date.setPrefWidth(200);
        TextField time = new TextField();
        time.setPromptText("Time (e.g. 2:30 PM)");
        HBox when = new HBox(10, date, time);

        TextField who = new TextField();
        who.setPromptText("You may leave this blank.");

        Label error = UI.errorLabel();
        Button clear = UI.button("Clear", "close", false);
        clear.setPrefWidth(120);
        Button submit = UI.button("Submit Anonymously", "send", true);

        clear.setOnAction(e -> {
            typeBox.setValue(null);
            what.clear();
            where.clear();
            date.setValue(null);
            time.clear();
            who.clear();
            UI.hide(error);
        });

        submit.setOnAction(e -> {
            if (typeBox.getValue() == null || what.getText().isBlank()) {
                UI.showMessage(error, "Please choose a type and describe what happened (marked *).", true);
                return;
            }
            Report report = new Report(typeBox.getValue(), what.getText(), where.getText().trim(),
                    date.getValue(), time.getText().trim(), who.getText().trim(),
                    Session.getCurrentUser());
            ReportService.submit(report);
            nav.showConfirmation(report);
        });

        HBox buttons = new HBox(10, clear, submit);
        buttons.setAlignment(Pos.CENTER_RIGHT);

        Label whenLabel = new Label("4. When did it happen?");
        whenLabel.getStyleClass().add("field-label");

        VBox form = new VBox(14,
                UI.labeled("1. Type of incident *", typeBox),
                UI.labeled("2. What happened? *", what), counter,
                UI.labeled("3. Where did it happen?", where),
                new VBox(6, whenLabel, when),
                UI.labeled("5. Who was involved? (optional)", who),
                error, buttons);
        form.setPadding(new Insets(26));
        form.setMaxWidth(720);
        form.getStyleClass().add("card");

        HBox safe = UI.safeBanner("Reviewers never see your name. If you are logged in, the report is linked to "
                + "your account only so you can follow its progress and read replies.");
        safe.setMaxWidth(720);

        VBox page = new VBox(18, UI.heading("Report an Incident", 28),
                UI.muted("Take your time. Fields marked * are required."), safe, form);
        page.setPadding(new Insets(32));
        return page;
    }
}
