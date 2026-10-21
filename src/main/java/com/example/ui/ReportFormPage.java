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

    private final String preselectedType;

    public ReportFormPage(Navigator nav, String preselectedType) {
        super(nav);
        this.preselectedType = preselectedType;
    }

    @Override protected String navKey() { return "home"; }

    @Override
    protected Node buildContent() {
        VBox content = new VBox(12);
        content.setPadding(new Insets(30));
        content.setMaxWidth(650);

        ComboBox<String> typeBox = new ComboBox<>();
        typeBox.getItems().addAll("Bullying", "Harassment", "Other");
        typeBox.setPromptText("Select type");
        typeBox.setPrefWidth(400);
        if (typeBox.getItems().contains(preselectedType)) {
            typeBox.setValue(preselectedType);
        }

        TextArea what = new TextArea();
        what.setPromptText("Describe what happened...");
        what.setPrefRowCount(4);
        what.setWrapText(true);

        TextField where = new TextField();
        where.setPromptText("e.g. Classroom, hallway, online...");

        DatePicker date = new DatePicker();
        date.setPromptText("Select date");
        TextField time = new TextField();
        time.setPromptText("Time (e.g. 2:30 PM)");
        HBox when = new HBox(10, date, time);

        TextField who = new TextField();
        who.setPromptText("You may leave this blank.");

        Label error = UI.errorLabel();
        Button clear = UI.button("Clear", false);
        clear.setPrefWidth(100);
        Button submit = UI.button("➤  Submit Anonymously", true);

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

        content.getChildren().addAll(
                UI.heading("Report an Incident", 26),
                UI.muted("Please fill in the form below. Fields marked * are required."),
                UI.labeled("1. Type of incident *", typeBox),
                UI.labeled("2. What happened? *", what),
                UI.labeled("3. Where did it happen?", where),
                new Label("4. When did it happen?"), when,
                UI.labeled("5. Who was involved? (Optional)", who),
                error, buttons,
                UI.muted("🛡 Reviewers never see your name. "
                        + "If you are logged in, the report is linked to your account only so you can track it."));
        return content;
    }
}
