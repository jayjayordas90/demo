package com.example.ui;

import com.example.model.Report;
import com.example.model.Role;
import com.example.model.User;
import com.example.service.ReportService;
import com.example.service.Session;
import com.example.service.UserService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/** Landing page for teachers and administrators: a monitoring overview. */
public class StaffDashboardPage extends DashboardPage {

    public StaffDashboardPage(Navigator nav) {
        super(nav);
    }

    @Override protected String navKey() { return "home"; }

    @Override
    protected Node buildContent() {
        User user = Session.getCurrentUser();
        boolean admin = user.getRole() == Role.ADMIN;

        VBox content = new VBox(20);
        content.setPadding(new Insets(32));
        content.getChildren().addAll(
                UI.heading((admin ? "Administrator" : "Teacher") + " Dashboard", 30),
                UI.muted("Welcome, " + user.getFullName() + ". Monitor reports and respond with care."));

        content.getChildren().add(new FlowPane(16, 16,
                UI.stat(String.valueOf(ReportService.count()), "Total reports"),
                UI.stat(String.valueOf(ReportService.countByStatus(Report.Status.SUBMITTED)), "Awaiting review"),
                UI.stat(String.valueOf(ReportService.countByStatus(Report.Status.UNDER_REVIEW)), "Under review"),
                UI.stat(String.valueOf(ReportService.countByStatus(Report.Status.RESOLVED)), "Resolved")));

        if (admin) {
            int pending = UserService.countPending();
            content.getChildren().add(new FlowPane(16, 16,
                    UI.stat(String.valueOf(UserService.countByRole(Role.STUDENT)), "Students"),
                    UI.stat(String.valueOf(UserService.countByRole(Role.TEACHER)), "Teachers"),
                    UI.stat(String.valueOf(pending), "Awaiting approval")));
            if (pending > 0) {
                Button review = UI.button("Review " + pending + " pending teacher(s)", "people", true);
                review.setPrefWidth(320);
                review.setOnAction(e -> nav.showUserManagement());
                content.getChildren().add(review);
            }
        }

        content.getChildren().add(UI.heading("Recent reports", 20));
        for (Report report : ReportService.recent(5)) {
            content.getChildren().add(reportRow(report));
        }
        if (ReportService.count() == 0) {
            content.getChildren().add(UI.panel("No reports have been submitted yet. New reports will appear here."));
        }

        Button viewAll = UI.button("View All Reports", "reports", true);
        Button chat = UI.button("Open Staff Chat", "chat", false);
        content.getChildren().add(new HBox(12, viewAll, chat));
        viewAll.setOnAction(e -> nav.showAllReports());
        chat.setOnAction(e -> nav.showStaffChat());
        return content;
    }

    private HBox reportRow(Report report) {
        VBox text = new VBox(3,
                UI.heading(report.getReferenceNo() + "  ·  " + report.getType(), 15),
                UI.muted("Submitted " + report.getDateSubmittedFormatted()));
        HBox.setHgrow(text, Priority.ALWAYS);

        HBox replies = new HBox(2);
        for (Report.Reply reply : report.getReplies()) {
            replies.getChildren().add(Emoji.of(reply.getReaction(), 22));
        }

        Region gap = new Region();
        gap.setMinWidth(4);
        HBox row = new HBox(14, UI.iconBadge("reports", "teal", 44), text, replies,
                UI.statusBadge(report.getStatus()), gap);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(14, 18, 14, 18));
        row.setMaxWidth(820);
        row.getStyleClass().addAll("card", "card-hover");
        row.setStyle("-fx-cursor: hand;");
        row.setOnMouseClicked(e -> nav.showReportDetail(report));
        UI.lift(row);
        return row;
    }
}
