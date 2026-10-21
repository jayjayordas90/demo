package com.example.ui;

import com.example.model.User;
import com.example.service.ReportService;
import com.example.service.Session;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

/** The dashboard landing page. */
public class HomePage extends DashboardPage {

    public HomePage(Navigator nav) {
        super(nav);
    }

    @Override protected String navKey() { return "home"; }

    @Override
    protected Node buildContent() {
        User user = Session.getCurrentUser();

        VBox content = new VBox(20);
        content.setPadding(new Insets(35));

        String greeting = user.isAnonymous() ? "Welcome to PaperKite" : "Welcome, " + user.getFullName();
        Label question = new Label("What would you like to share today?");
        question.setFont(Font.font(16));
        content.getChildren().addAll(UI.heading(greeting, 28), question);

        if (!user.isAnonymous()) {
            FlowPane stats = new FlowPane(15, 15,
                    UI.stat(String.valueOf(ReportService.countFor(user)), "Reports submitted"),
                    UI.stat(user.getMemberSinceFormatted(), "Member since"));
            content.getChildren().add(stats);
        }

        VBox bullying = UI.card("💬", "Report Bullying", "Share incidents of bullying.");
        VBox harassment = UI.card("⚠", "Report Harassment", "Share incidents of harassment.");
        VBox experience = UI.card("✎", "Share My Experience", "Share your thoughts or experiences.");
        VBox reports = UI.card("📄", "My Reports", "View your submitted reports.");
        VBox profile = UI.card("👤", "My Profile", "View and edit your account.");
        VBox safety = UI.card("🛡", "Safety & Support", "Resources and helpful information.");

        bullying.setOnMouseClicked(e -> nav.showReportForm("Bullying"));
        harassment.setOnMouseClicked(e -> nav.showReportForm("Harassment"));
        experience.setOnMouseClicked(e -> nav.showReportForm("Other"));
        reports.setOnMouseClicked(e -> nav.showMyReports());
        profile.setOnMouseClicked(e -> nav.showProfile());
        safety.setOnMouseClicked(e -> nav.showSafety());

        content.getChildren().addAll(
                new FlowPane(15, 15, bullying, harassment, experience),
                new FlowPane(15, 15, reports, profile, safety),
                UI.panel("🛡  All reports are reviewed and handled securely.\nYour identity is kept anonymous."));
        return content;
    }
}
