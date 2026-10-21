package com.example.ui;

import com.example.model.Reaction;
import com.example.model.Report;
import com.example.model.User;
import com.example.service.ReportService;
import com.example.service.Session;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

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

    private static VBox card(Node... children) {
        VBox box = new VBox(12, children);
        box.setPadding(new Insets(22));
        box.setMaxWidth(720);
        box.getStyleClass().add("card");
        return box;
    }

    private HBox detail(String label, String value) {
        Label name = new Label(label);
        name.getStyleClass().add("field-label");
        name.setMinWidth(130);
        Label text = new Label(value);
        text.setWrapText(true);
        HBox row = new HBox(10, name, text);
        row.setAlignment(Pos.TOP_LEFT);
        return row;
    }

    @Override
    protected Node buildContent() {
        User actor = Session.getCurrentUser();

        VBox content = new VBox(18);
        content.setPadding(new Insets(32));

        Button back = UI.flat("Back to All Reports", "back", false);
        back.setOnAction(e -> nav.showAllReports());

        HBox title = new HBox(14, UI.heading(report.getReferenceNo(), 28), UI.statusBadge(report.getStatus()));
        title.setAlignment(Pos.CENTER_LEFT);

        String when = report.getIncidentDateFormatted()
                + (report.getIncidentTime() == null || report.getIncidentTime().isBlank() ? ""
                        : " at " + report.getIncidentTime());
        VBox details = card(
                UI.heading("Details", 17),
                detail("Type", report.getType()),
                detail("Location", orNotProvided(report.getLocation())),
                detail("When", when),
                detail("People involved", orNotProvided(report.getPeopleInvolved())),
                detail("Submitted", report.getDateSubmittedFormatted()),
                UI.heading("What happened", 15),
                UI.panel(report.getDescription()));

        content.getChildren().addAll(back, title, details, replySection(actor), statusSection(actor), notesSection(actor));

        if (actor.getRole().canDeleteReports()) {
            Button delete = UI.flat("Delete this report", "delete", true);
            delete.setOnAction(e -> {
                ReportService.delete(actor, report);
                nav.showAllReports();
            });
            content.getChildren().add(delete);
        }
        return content;
    }

    // ---------- emoji replies ----------
    private VBox replySection(User actor) {
        VBox sent = new VBox(8);
        fillReplies(sent);
        Label message = UI.errorLabel();

        HBox buttons = new HBox(10);
        buttons.setAlignment(Pos.CENTER_LEFT);
        for (Reaction reaction : Reaction.values()) {
            Button button = UI.emojiButton(reaction, 40);
            button.setOnAction(e -> {
                try {
                    ReportService.reply(actor, report, reaction);
                    UI.bounce(button);
                    fillReplies(sent);
                    UI.showMessage(message, "Sent: \"" + reaction.getMeaning() + "\"", false);
                } catch (IllegalArgumentException ex) {
                    UI.showMessage(message, ex.getMessage(), true);
                }
            });
            buttons.getChildren().add(button);
        }

        return card(UI.heading("Reply with an emoji", 17),
                UI.muted("Send a caring reply. The student sees it in My Reports as coming from \"a staff member\"."),
                buttons, message, sent);
    }

    private void fillReplies(VBox list) {
        list.getChildren().clear();
        List<Report.Reply> replies = report.getReplies();
        if (replies.isEmpty()) {
            list.getChildren().add(UI.muted("No replies sent yet."));
            return;
        }
        for (Report.Reply reply : replies) {
            HBox row = new HBox(12, Emoji.of(reply.getReaction(), 30),
                    UI.muted(reply.getStaffName() + " · " + reply.getSentFormatted()));
            row.setAlignment(Pos.CENTER_LEFT);
            list.getChildren().add(row);
        }
    }

    // ---------- status ----------
    private VBox statusSection(User actor) {
        List<Report.Status> allowed = actor.getRole().allowedStatuses();
        ComboBox<Report.Status> statusBox = new ComboBox<>();
        statusBox.getItems().addAll(allowed);
        statusBox.setValue(allowed.contains(report.getStatus()) ? report.getStatus() : null);
        statusBox.setPromptText(report.getStatus().getLabel());
        statusBox.setDisable(allowed.isEmpty());

        Button update = UI.button("Update Status", "check", true);
        update.setPrefWidth(190);
        Label message = UI.errorLabel();
        update.setOnAction(e -> {
            if (statusBox.getValue() == null) {
                UI.showMessage(message, "Choose a status first.", true);
                return;
            }
            try {
                ReportService.updateStatus(actor, report, statusBox.getValue());
                nav.showReportDetail(report);   // refresh
            } catch (IllegalArgumentException ex) {
                UI.showMessage(message, ex.getMessage(), true);
            }
        });
        HBox row = new HBox(12, statusBox, update);
        row.setAlignment(Pos.CENTER_LEFT);
        return card(UI.heading("Status", 17), row, message);
    }

    // ---------- internal notes ----------
    private VBox notesSection(User actor) {
        VBox notes = new VBox(8);
        if (report.getNotes().isEmpty()) {
            notes.getChildren().add(UI.muted("No notes yet."));
        }
        for (String note : report.getNotes()) {
            notes.getChildren().add(UI.panel(note));
        }
        TextArea noteText = new TextArea();
        noteText.setPromptText("Add an internal note (staff only, never shown to the reporter)...");
        noteText.setPrefRowCount(2);
        noteText.setWrapText(true);
        Button add = UI.button("Add Note", "add", false);
        add.setPrefWidth(160);
        Label message = UI.errorLabel();
        add.setOnAction(e -> {
            try {
                ReportService.addNote(actor, report, noteText.getText());
                nav.showReportDetail(report);   // refresh
            } catch (IllegalArgumentException ex) {
                UI.showMessage(message, ex.getMessage(), true);
            }
        });
        return card(UI.heading("Staff notes", 17), notes, noteText, add, message);
    }
}
