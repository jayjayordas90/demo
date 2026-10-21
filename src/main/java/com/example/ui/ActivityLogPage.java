package com.example.ui;

import com.example.service.ActivityLog;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;

/** Staff-only audit trail: who did what, and when. */
public class ActivityLogPage extends DashboardPage {

    public ActivityLogPage(Navigator nav) {
        super(nav);
    }

    @Override protected String navKey() { return "activity"; }

    @Override
    protected Node buildContent() {
        VBox content = new VBox(16);
        content.setPadding(new Insets(30));
        content.getChildren().add(UI.heading("Activity Log", 28));
        content.getChildren().add(UI.muted("A record of staff actions: status changes, account changes, and more."));

        TableView<ActivityLog.Entry> table = new TableView<>();

        TableColumn<ActivityLog.Entry, String> time = new TableColumn<>("Time");
        time.setPrefWidth(170);
        time.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getTime()));

        TableColumn<ActivityLog.Entry, String> actor = new TableColumn<>("Staff member");
        actor.setPrefWidth(180);
        actor.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getActor()));

        TableColumn<ActivityLog.Entry, String> action = new TableColumn<>("Action");
        action.setPrefWidth(300);
        action.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getAction()));

        table.getColumns().addAll(time, actor, action);
        table.getItems().addAll(ActivityLog.getAll());
        table.setPlaceholder(new Label("No activity recorded yet."));
        table.setPrefHeight(450);

        content.getChildren().add(table);
        return content;
    }
}
