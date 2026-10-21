package com.example.ui;

import com.example.model.Report;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.function.Function;

/** Staff-only list of every report, with a status filter. */
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
        content.setPadding(new Insets(30));
        content.getChildren().add(UI.heading("All Reports", 28));

        ComboBox<Report.Status> filter = new ComboBox<>();
        filter.getItems().add(null);   // "All"
        filter.getItems().addAll(Report.Status.values());
        filter.setValue(initialFilter);
        filter.setPromptText("All statuses");
        filter.setButtonCell(statusCell());
        filter.setCellFactory(list -> statusCell());

        TableView<Report> table = new TableView<>();
        table.getColumns().add(column("Reference No.", 150, Report::getReferenceNo));
        table.getColumns().add(column("Type", 110, Report::getType));
        table.getColumns().add(column("Location", 150, r -> r.getLocation() == null || r.getLocation().isBlank()
                ? "—" : r.getLocation()));
        table.getColumns().add(column("Submitted", 140, Report::getDateSubmittedFormatted));
        table.getColumns().add(column("Status", 130, r -> r.getStatus().getLabel()));
        table.setPlaceholder(new Label("No reports match this filter."));
        table.setPrefHeight(420);
        table.setRowFactory(t -> {
            javafx.scene.control.TableRow<Report> row = new javafx.scene.control.TableRow<>();
            row.setOnMouseClicked(e -> {
                if (!row.isEmpty()) {
                    nav.showReportDetail(row.getItem());
                }
            });
            row.setStyle("-fx-cursor: hand;");
            return row;
        });

        Runnable refresh = () -> {
            table.getItems().setAll(filter.getValue() == null
                    ? com.example.service.ReportService.getAll()
                    : com.example.service.ReportService.getByStatus(filter.getValue()));
        };
        filter.setOnAction(e -> refresh.run());
        refresh.run();

        HBox toolbar = new HBox(10, new Label("Filter by status:"), filter);
        toolbar.setStyle("-fx-alignment: center-left;");

        content.getChildren().addAll(toolbar, table);
        return content;
    }

    private TableColumn<Report, String> column(String title, double width, Function<Report, String> getter) {
        TableColumn<Report, String> col = new TableColumn<>(title);
        col.setPrefWidth(width);
        col.setCellValueFactory(data -> new SimpleStringProperty(getter.apply(data.getValue())));
        return col;
    }

    private javafx.scene.control.ListCell<Report.Status> statusCell() {
        return new javafx.scene.control.ListCell<>() {
            @Override
            protected void updateItem(Report.Status status, boolean empty) {
                super.updateItem(status, empty);
                setText(empty ? null : (status == null ? "All statuses" : status.getLabel()));
            }
        };
    }
}
