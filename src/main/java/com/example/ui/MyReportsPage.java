package com.example.ui;

import com.example.model.Report;
import com.example.model.User;
import com.example.service.ReportService;
import com.example.service.Session;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
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

        VBox content = new VBox(18);
        content.setPadding(new Insets(32));
        content.getChildren().addAll(UI.heading("My Reports", 28),
                UI.muted("Follow your reports and read caring replies from staff."));

        if (user.isAnonymous()) {
            content.getChildren().add(UI.panel(
                    "Anonymous guests cannot track reports. Log in or create an account to see "
                    + "the status of the reports you submit and any replies from staff."));
            return content;
        }

        TableView<Report> table = new TableView<>();
        table.getColumns().add(textColumn("Reference No.", 150, Report::getReferenceNo));
        table.getColumns().add(textColumn("Type", 110, Report::getType));
        table.getColumns().add(textColumn("Submitted", 130, Report::getDateSubmittedFormatted));
        table.getColumns().add(statusColumn());
        table.getColumns().add(repliesColumn());
        table.getItems().addAll(ReportService.getReportsFor(user));
        table.setPlaceholder(new Label("You haven't submitted any reports yet."));
        table.setPrefHeight(300);

        // Selecting a report shows the staff replies underneath.
        VBox replies = new VBox(10);
        replies.getChildren().add(UI.muted("Select a report to see replies from staff."));
        table.getSelectionModel().selectedItemProperty().addListener((obs, old, selected) -> {
            replies.getChildren().clear();
            if (selected == null) {
                return;
            }
            replies.getChildren().add(UI.heading("Replies for " + selected.getReferenceNo(), 16));
            if (selected.getReplies().isEmpty()) {
                replies.getChildren().add(UI.muted("No replies yet. Staff will respond after reviewing."));
            }
            for (Report.Reply reply : selected.getReplies()) {
                VBox text = new VBox(2, UI.heading(reply.getReaction().getMeaning(), 14),
                        UI.muted("From a staff member · " + reply.getSentFormatted()));
                HBox row = new HBox(16, Emoji.of(reply.getReaction(), 44), text);
                row.setAlignment(Pos.CENTER_LEFT);
                row.setPadding(new Insets(12, 18, 12, 18));
                row.getStyleClass().add("card");
                replies.getChildren().add(row);
            }
        });

        content.getChildren().addAll(table, replies);
        return content;
    }

    private TableColumn<Report, Report> statusColumn() {
        TableColumn<Report, Report> col = new TableColumn<>("Status");
        col.setPrefWidth(140);
        col.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue()));
        col.setCellFactory(c -> new TableCell<Report, Report>() {
            @Override
            protected void updateItem(Report report, boolean empty) {
                super.updateItem(report, empty);
                setText(null);
                setGraphic(empty || report == null ? null : UI.statusBadge(report.getStatus()));
            }
        });
        return col;
    }

    /** Shows the drawn emoji replies right inside the table. */
    private TableColumn<Report, Report> repliesColumn() {
        TableColumn<Report, Report> col = new TableColumn<>("Staff replies");
        col.setPrefWidth(170);
        col.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue()));
        col.setCellFactory(c -> new TableCell<Report, Report>() {
            @Override
            protected void updateItem(Report report, boolean empty) {
                super.updateItem(report, empty);
                if (empty || report == null) {
                    setText(null);
                    setGraphic(null);
                } else if (report.getReplies().isEmpty()) {
                    setGraphic(null);
                    setText("—");
                } else {
                    HBox box = new HBox(3);
                    box.setAlignment(Pos.CENTER_LEFT);
                    for (Report.Reply reply : report.getReplies()) {
                        box.getChildren().add(Emoji.of(reply.getReaction(), 24));
                    }
                    setText(null);
                    setGraphic(box);
                }
            }
        });
        return col;
    }

    private TableColumn<Report, String> textColumn(String title, double width, Function<Report, String> getter) {
        TableColumn<Report, String> col = new TableColumn<>(title);
        col.setPrefWidth(width);
        col.setCellValueFactory(data -> new SimpleStringProperty(getter.apply(data.getValue())));
        return col;
    }
}
