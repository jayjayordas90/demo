package com.example.ui;

import com.example.model.Report;
import com.example.service.ReportService;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/** Staff-only list of every report, with status filter and reference search. */
public class AllReportsPage extends DashboardPage {

    private final Report.Status initialFilter;

    public AllReportsPage(Navigator nav, Report.Status initialFilter) {
        super(nav);
        this.initialFilter = initialFilter;
    }

    @Override protected String navKey() { return "reports"; }

    @Override
    protected Node buildContent() {
        VBox content = new VBox(16);
        content.setPadding(new Insets(32));
        content.getChildren().addAll(UI.heading("All Reports", 28),
                UI.muted("Click a report to read it, reply with an emoji, or add notes."));

        ComboBox<Report.Status> filter = new ComboBox<>();
        filter.getItems().add(null);   // null = "All"
        filter.getItems().addAll(Report.Status.values());
        filter.setValue(initialFilter);
        filter.setButtonCell(statusCell());
        filter.setCellFactory(list -> statusCell());
        filter.setPrefWidth(190);

        TextField search = new TextField();
        search.setPromptText("Search reference or type...");
        search.setPrefWidth(240);

        TableView<Report> table = new TableView<>();
        table.getColumns().add(textColumn("Reference No.", 150, Report::getReferenceNo));
        table.getColumns().add(textColumn("Type", 110, Report::getType));
        table.getColumns().add(textColumn("Location", 150, r -> r.getLocation() == null || r.getLocation().isBlank()
                ? "—" : r.getLocation()));
        table.getColumns().add(textColumn("Submitted", 130, Report::getDateSubmittedFormatted));
        table.getColumns().add(statusColumn());
        table.getColumns().add(repliesColumn());
        table.setPlaceholder(new Label("No reports match."));
        table.setPrefHeight(440);
        table.setRowFactory(t -> {
            TableRow<Report> row = new TableRow<>();
            row.setOnMouseClicked(e -> {
                if (!row.isEmpty()) {
                    nav.showReportDetail(row.getItem());
                }
            });
            row.setStyle("-fx-cursor: hand;");
            return row;
        });

        Runnable refresh = () -> {
            String needle = search.getText() == null ? "" : search.getText().trim().toLowerCase();
            List<Report> shown = new ArrayList<>();
            for (Report report : filter.getValue() == null
                    ? ReportService.getAll() : ReportService.getByStatus(filter.getValue())) {
                if (needle.isEmpty() || report.getReferenceNo().toLowerCase().contains(needle)
                        || report.getType().toLowerCase().contains(needle)) {
                    shown.add(report);
                }
            }
            table.getItems().setAll(shown);
        };
        filter.setOnAction(e -> refresh.run());
        search.textProperty().addListener((obs, oldText, newText) -> refresh.run());   // live search
        refresh.run();

        HBox toolbar = new HBox(12, new Icon("reports", 20), filter, search);
        toolbar.setAlignment(Pos.CENTER_LEFT);
        content.getChildren().addAll(toolbar, table);
        return content;
    }

    private TableColumn<Report, String> textColumn(String title, double width, Function<Report, String> getter) {
        TableColumn<Report, String> col = new TableColumn<>(title);
        col.setPrefWidth(width);
        col.setCellValueFactory(data -> new SimpleStringProperty(getter.apply(data.getValue())));
        return col;
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

    private TableColumn<Report, Report> repliesColumn() {
        TableColumn<Report, Report> col = new TableColumn<>("Replies");
        col.setPrefWidth(150);
        col.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue()));
        col.setCellFactory(c -> new TableCell<Report, Report>() {
            @Override
            protected void updateItem(Report report, boolean empty) {
                super.updateItem(report, empty);
                if (empty || report == null || report.getReplies().isEmpty()) {
                    setGraphic(null);
                    setText(empty || report == null ? null : "—");
                    return;
                }
                HBox box = new HBox(3);
                box.setAlignment(Pos.CENTER_LEFT);
                for (Report.Reply reply : report.getReplies()) {
                    box.getChildren().add(Emoji.of(reply.getReaction(), 22));
                }
                setText(null);
                setGraphic(box);
            }
        });
        return col;
    }

    private ListCell<Report.Status> statusCell() {
        return new ListCell<Report.Status>() {
            @Override
            protected void updateItem(Report.Status status, boolean empty) {
                super.updateItem(status, empty);
                setText(empty ? null : (status == null ? "All statuses" : status.getLabel()));
            }
        };
    }
}
