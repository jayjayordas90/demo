package com.example.ui;

import com.example.model.Reaction;
import com.example.model.Report;
import com.example.model.User;
import com.example.service.ReportService;
import com.example.service.Session;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import java.util.List;

/** Staff-only view of one report: details, emoji replies, status, and notes. */
public class ReportDetailPage extends DashboardPage {

    private final Report report;

    public ReportDetailPage(Navigator nav, Report report) {
        super(nav);
        this.report = report;
    }

    @Override protected String navKey() { return "reports"; }

    private static String orNotProvided(String value) {
        return value == null || value.isBlank() ? "Not provided" : value;
    }

    @Override
    protected Node buildContent() {
        User actor = Session.getCurrentUser();

        VBox content = new VBox(16);
        content.setPadding(new Insets(30));
        content.setMaxWidth(650);

        Button back = new Button("← Back to All Reports");
        back.setStyle("-fx-background-color: transparent; -fx-text-fill: gray;");
        back.setOnAction(e -> nav.showAllReports());

        content.getChildren().addAll(back,
                UI.heading(report.getReferenceNo(), 26),
                UI.panel("Type:  " + report.getType()
                        + "\nLocation:  " + orNotProvided(report.getLocation())
                        + "\nIncident date:  " + report.getIncidentDateFormatted()
                        + (report.getIncidentTime() == null || report.getIncidentTime().isBlank() ? ""
                                : "  at  " + report.getIncidentTime())
                        + "\nPeople involved:  " + orNotProvided(report.getPeopleInvolved())
                        + "\nSubmitted:  " + report.getDateSubmittedFormatted()
                        + "\nStatus:  " + report.getStatus().getLabel()),
                UI.heading("Description", 15),
                UI.muted(report.getDescription()));

        // ---- emoji replies ----
        content.getChildren().add(UI.heading("Reply to this report", 15));
        content.getChildren().add(UI.muted(
                "Send a caring emoji. The student sees it in My Reports as coming from \"a staff member\"."));
        Label replyMsg = UI.errorLabel();
        HBox reactions = new HBox(8);
        for (Reaction reaction : Reaction.values()) {
            Button button = new Button(reaction.getSymbol());
            button.setFont(Font.font(24));
            button.setTooltip(new Tooltip(reaction.getMeaning()));
            button.setStyle("-fx-background-color: white; -fx-border-color: #cccccc; -fx-border-radius: 6;"
                    + "-fx-background-radius: 6; -fx-cursor: hand;");
            button.setOnAction(e -> {
                try {
                    ReportService.reply(actor, report, reaction);
                    nav.showReportDetail(report);   // refresh
                } catch (IllegalArgumentException ex) {
                    UI.showMessage(replyMsg, ex.getMessage(), true);
                }
            });
            reactions.getChildren().add(button);
        }
        content.getChildren().addAll(reactions, replyMsg);

        List<Report.Reply> replies = report.getReplies();
        if (!replies.isEmpty()) {
            VBox sent = new VBox(4);
            for (Report.Reply reply : replies) {
                sent.getChildren().add(UI.muted(reply.getReaction().getSymbol() + "   "
                        + reply.getStaffName() + " · " + reply.getSentFormatted()));
            }
            content.getChildren().add(sent);
        }

        // ---- status control ----
        List<Report.Status> allowed = actor.getRole().allowedStatuses();
        ComboBox<Report.Status> statusBox = new ComboBox<>();
        statusBox.getItems().addAll(allowed);
        statusBox.setValue(allowed.contains(report.getStatus()) ? report.getStatus() : null);
        statusBox.setPromptText(report.getStatus().getLabel());
        statusBox.setDisable(allowed.isEmpty());
        Button updateStatus = UI.button("Update Status", true);
        updateStatus.setPrefWidth(180);
        Label statusMsg = UI.errorLabel();
        updateStatus.setOnAction(e -> {
            if (statusBox.getValue() == null) {
                UI.showMessage(statusMsg, "Choose a status first.", true);
                return;
            }
            try {
                ReportService.updateStatus(actor, report, statusBox.getValue());
                nav.showReportDetail(report);   // refresh
            } catch (IllegalArgumentException ex) {
                UI.showMessage(statusMsg, ex.getMessage(), true);
            }
        });
        HBox statusRow = new HBox(10, new Label("Set status:"), statusBox, updateStatus);
        statusRow.setStyle("-fx-alignment: center-left;");
        content.getChildren().addAll(UI.heading("Status", 15), statusRow, statusMsg);

        // ---- internal notes ----
        content.getChildren().add(UI.heading("Staff notes", 15));
        if (report.getNotes().isEmpty()) {
            content.getChildren().add(UI.muted("No notes yet."));
        } else {
            for (String note : report.getNotes()) {
                content.getChildren().add(UI.muted(note));
            }
        }
        TextArea noteText = new TextArea();
        noteText.setPromptText("Add an internal note (staff only, never shown to the reporter)...");
        noteText.setPrefRowCount(2);
        noteText.setWrapText(true);
        Button addNote = UI.button("Add Note", false);
        addNote.setPrefWidth(140);
        Label noteMsg = UI.errorLabel();
        addNote.setOnAction(e -> {
            try {
                ReportService.addNote(actor, report, noteText.getText());
                nav.showReportDetail(report);   // refresh
            } catch (IllegalArgumentException ex) {
                UI.showMessage(noteMsg, ex.getMessage(), true);
            }
        });
        content.getChildren().addAll(noteText, addNote, noteMsg);

        // ---- delete (administrator only) ----
        if (actor.getRole().canDeleteReports()) {
            Button delete = new Button("🗑  Delete Report");
            delete.setStyle("-fx-background-color: transparent; -fx-text-fill: crimson;");
            delete.setOnAction(e -> {
                ReportService.delete(actor, report);
                nav.showAllReports();
            });
            content.getChildren().add(delete);
        }
        return content;
    }
}
