package com.example.ui;

import com.example.model.Report;
import com.example.model.User;
import com.example.service.ReportService;
import com.example.service.Session;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

/** The student / guest dashboard. */
public class HomePage extends DashboardPage {

    public HomePage(Navigator nav) {
        super(nav);
    }

    @Override protected String navKey() { return "home"; }

    @Override
    protected Node buildContent() {
        User user = Session.getCurrentUser();

        VBox content = new VBox(22);
        content.setPadding(new Insets(32));

        String greeting = user.isAnonymous() ? "Welcome to PaperKite" : "Hi, " + user.getFullName() + "!";
        VBox header = new VBox(4, UI.heading(greeting, 30), UI.muted("What would you like to share today?"));
        HBox safe = UI.safeBanner("Your identity is protected. All reports are reviewed securely and kept anonymous.");
        safe.setMaxWidth(720);
        content.getChildren().addAll(header, safe);

        if (!user.isAnonymous()) {
            content.getChildren().add(new FlowPane(16, 16,
                    UI.stat(String.valueOf(ReportService.countFor(user)), "Reports submitted"),
                    UI.stat(user.getMemberSinceFormatted(), "Member since")));
            HBox banner = replyBanner(user);
            if (banner != null) {
                content.getChildren().add(banner);
            }
        }

        VBox bullying = UI.card("bully", "rose", "Report Bullying", "Share incidents of bullying.");
        VBox harassment = UI.card("warning", "orange", "Report Harassment", "Share incidents of harassment.");
        VBox experience = UI.card("pencil", "violet", "Share My Experience", "Share your thoughts or feelings.");
        VBox reports = UI.card("reports", "teal", "My Reports", "See your reports and replies.");
        VBox profile = UI.card("person", "teal", "My Profile", "View and edit your account.");
        VBox safety = UI.card("heart", "green", "Safety & Support", "Helpful steps and resources.");

        bullying.setOnMouseClicked(e -> nav.showReportForm("Bullying"));
        harassment.setOnMouseClicked(e -> nav.showReportForm("Harassment"));
        experience.setOnMouseClicked(e -> nav.showReportForm("Other"));
        reports.setOnMouseClicked(e -> nav.showMyReports());
        profile.setOnMouseClicked(e -> nav.showProfile());
        safety.setOnMouseClicked(e -> nav.showSafety());

        FlowPane cards = new FlowPane(18, 18, bullying, harassment, experience, reports, profile, safety);
        cards.setPadding(new Insets(6, 0, 0, 0));
        content.getChildren().add(cards);
        return content;
    }

    /** A warm banner showing the latest emoji replies from staff. Null when there are none. */
    private HBox replyBanner(User user) {
        List<Report.Reply> all = new ArrayList<>();
        for (Report report : ReportService.getReportsFor(user)) {
            all.addAll(report.getReplies());
        }
        if (all.isEmpty()) {
            return null;
        }
        HBox emojis = new HBox(6);
        emojis.setAlignment(Pos.CENTER_LEFT);
        for (Report.Reply reply : all.subList(Math.max(0, all.size() - 5), all.size())) {
            emojis.getChildren().add(Emoji.of(reply.getReaction(), 36));
        }
        VBox text = new VBox(3,
                UI.heading("Staff replied to your reports", 16),
                UI.muted(all.size() + (all.size() == 1 ? " caring reply" : " caring replies") + " waiting. Click to read."));
        HBox banner = new HBox(18, emojis, text);
        banner.setAlignment(Pos.CENTER_LEFT);
        banner.getStyleClass().add("reply-banner");
        banner.setMaxWidth(720);
        banner.setOnMouseClicked(e -> nav.showMyReports());
        UI.lift(banner);
        return banner;
    }
}
