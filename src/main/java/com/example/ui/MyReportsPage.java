package com.example.ui;

import com.example.model.Report;
import com.example.model.User;
import com.example.service.ReportService;
import com.example.service.Session;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;

import java.util.function.Function;

public class MyReportsPage extends DashboardPage {

    public MyReportsPage(Navigator nav) {
        super(nav);
    }

    @Override protected String navKey() { return "reports"; }

    @Override
    protected Node buildContent() {
        User user = Session.getCurrentUser();

        VBox content = new VBox(20);
        content.setPadding(new Insets(30));
        content.getChildren().add(UI.heading("My Reports", 28));

        if (user.isAnonymous()) {
            content.getChildren().add(UI.panel(
                    "Anonymous guests cannot track reports. Log in or create an account to see "
                    + "the status of the reports you submit. Keep your reference number safe!"));
            return content;
        }

        TableView<Report> table = new TableView<>();
        table.getColumns().add(column("Reference No.", 150, Report::getReferenceNo));
        table.getColumns().add(column("Type", 120, Report::getType));
        table.getColumns().add(column("Date Submitted", 150, Report::getDateSubmittedFormatted));
        table.getColumns().add(column("Status", 120, r -> r.getStatus().getLabel()));
        table.getItems().addAll(ReportService.getReportsFor(user));
        table.setPlaceholder(new Label("You haven't submitted any reports yet."));
        table.setPrefHeight(400);

        content.getChildren().addAll(UI.muted("View the status of your submitted reports."), table);
        return content;
    }

    /** Small generic helper so each column is one line instead of six. */
    private TableColumn<Report, String> column(String title, double width, Function<Report, String> getter) {
        TableColumn<Report, String> col = new TableColumn<>(title);
        col.setPrefWidth(width);
        col.setCellValueFactory(data -> new SimpleStringProperty(getter.apply(data.getValue())));
        return col;
    }
}
