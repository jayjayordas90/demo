package com.example.ui;

import com.example.model.Report;
import com.example.model.Role;
import com.example.model.User;
import com.example.service.ReportService;
import com.example.service.Session;
import com.example.service.UserService;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
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
        content.setPadding(new Insets(35));
        content.getChildren().addAll(
                UI.heading((admin ? "Administrator" : "Teacher") + " Dashboard", 28),
                UI.muted("Welcome, " + user.getFullName() + ". Monitor reports and respond with care."));

        content.getChildren().add(new FlowPane(15, 15,
                UI.stat(String.valueOf(ReportService.count()), "Total reports"),
                UI.stat(String.valueOf(ReportService.countByStatus(Report.Status.SUBMITTED)), "Awaiting review"),
                UI.stat(String.valueOf(ReportService.countByStatus(Report.Status.UNDER_REVIEW)), "Under review"),
                UI.stat(String.valueOf(ReportService.countByStatus(Report.Status.RESOLVED)), "Resolved")));

        if (admin) {
            int pending = UserService.countPending();
            content.getChildren().add(new FlowPane(15, 15,
                    UI.stat(String.valueOf(UserService.countByRole(Role.STUDENT)), "Students"),
                    UI.stat(String.valueOf(UserService.countByRole(Role.TEACHER)), "Teachers"),
                    UI.stat(String.valueOf(pending), "Teachers awaiting approval")));
            if (pending > 0) {
                Button review = UI.button("Review " + pending + " pending teacher(s)", true);
                review.setOnAction(e -> nav.showUserManagement());
                content.getChildren().add(review);
            }
        }

        content.getChildren().add(UI.heading("Recent reports", 18));
        for (Report report : ReportService.recent(5)) {
            content.getChildren().add(reportRow(report));
        }
        if (ReportService.count() == 0) {
            content.getChildren().add(UI.muted("No reports have been submitted yet."));
        }

        Button viewAll = UI.button("View All Reports", true);
        viewAll.setOnAction(e -> nav.showAllReports());
        Button chat = UI.button("💬  Open Staff Chat", false);
        chat.setOnAction(e -> nav.showStaffChat());
        content.getChildren().add(new HBox(12, viewAll, chat));
        return content;
    }

    private HBox reportRow(Report report) {
        VBox text = new VBox(2,
                UI.heading(report.getReferenceNo() + " — " + report.getType(), 14),
                UI.muted("Submitted " + report.getDateSubmittedFormatted() + " · "
                        + report.getStatus().getLabel() + " · " + report.getReplies().size() + " repl"
                        + (report.getReplies().size() == 1 ? "y" : "ies")));
        Button view = UI.button("View", false);
        view.setPrefSize(90, 32);
        view.setOnAction(e -> nav.showReportDetail(report));

        HBox row = new HBox(15, text, view);
        row.setPadding(new Insets(12));
        row.setStyle(UI.CARD_STYLE + "-fx-alignment: center-left;");
        HBox.setHgrow(text, Priority.ALWAYS);
        return row;
    }
}
