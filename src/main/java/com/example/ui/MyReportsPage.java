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
                    + "the status of the reports you submit and any replies from staff."));
            return content;
        }

        TableView<Report> table = new TableView<>();
        table.getColumns().add(column("Reference No.", 140, Report::getReferenceNo));
        table.getColumns().add(column("Type", 100, Report::getType));
        table.getColumns().add(column("Date Submitted", 130, Report::getDateSubmittedFormatted));
        table.getColumns().add(column("Status", 110, r -> r.getStatus().getLabel()));
        table.getColumns().add(column("Staff replies", 110, MyReportsPage::replySummary));
        table.getItems().addAll(ReportService.getReportsFor(user));
        table.setPlaceholder(new Label("You haven't submitted any reports yet."));
        table.setPrefHeight(300);

        // Selecting a report shows the staff replies underneath.
        VBox replies = new VBox(6);
        replies.getChildren().add(UI.muted("Select a report to see replies from staff."));
        table.getSelectionModel().selectedItemProperty().addListener((obs, old, selected) -> {
            replies.getChildren().clear();
            if (selected == null) {
                return;
            }
            replies.getChildren().add(UI.heading("Replies for " + selected.getReferenceNo(), 15));
            if (selected.getReplies().isEmpty()) {
                replies.getChildren().add(UI.muted("No replies yet. Staff will respond after reviewing."));
            }
            for (Report.Reply reply : selected.getReplies()) {
                replies.getChildren().add(UI.panel(reply.getReaction().getSymbol() + "  "
                        + reply.getReaction().getMeaning()
                        + "\nFrom a staff member · " + reply.getSentFormatted()));
            }
        });

        content.getChildren().addAll(UI.muted("View the status of your submitted reports."), table, replies);
        return content;
    }

    private static String replySummary(Report report) {
        StringBuilder text = new StringBuilder();
        for (Report.Reply reply : report.getReplies()) {
            text.append(reply.getReaction().getSymbol()).append(' ');
        }
        return text.length() == 0 ? "—" : text.toString().trim();
    }

    /** Small generic helper so each column is one line instead of six. */
    private TableColumn<Report, String> column(String title, double width, Function<Report, String> getter) {
        TableColumn<Report, String> col = new TableColumn<>(title);
        col.setPrefWidth(width);
        col.setCellValueFactory(data -> new SimpleStringProperty(getter.apply(data.getValue())));
        return col;
    }
}
