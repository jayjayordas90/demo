package com.example.ui;

import com.example.model.Report;
import com.example.service.Session;
import com.example.service.UserService;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/** Owns the window and switches between pages. One Navigator instance per app. */
public class Navigator {

    private final Stage stage;
    private final Scene scene;

    public Navigator(Stage stage) {
        this.stage = stage;
        this.scene = new Scene(new StackPane(), 1000, 650);
        stage.setTitle("PaperKite");
        stage.setScene(scene);
    }

    private void show(Page page) {
        scene.setRoot(page.build());
        stage.show();
    }

    /** Any logged-in user or guest may open this page. */
    private void showSecured(DashboardPage page) {
        showRestricted(page, true);
    }

    /** Only shows the page if the current user is allowed; otherwise sends them home. */
    private void showRestricted(DashboardPage page, boolean allowed) {
        if (!Session.hasSession()) {
            showWelcome();
        } else if (!allowed) {
            showHome();
        } else {
            show(page);
        }
    }

    /** Before an administrator exists, nothing else is reachable. */
    private boolean setupRequired() {
        if (!UserService.hasAdmin()) {
            showAdminSetup();
            return true;
        }
        return false;
    }

    // ---- public screens ----
    public void showAdminSetup() { show(new AdminSetupPage(this)); }

    public void showWelcome() {
        if (!setupRequired()) {
            show(new WelcomePage(this));
        }
    }

    public void showLogin() { showLogin(null); }

    public void showLogin(String notice) {
        if (!setupRequired()) {
            show(new LoginPage(this, notice));
        }
    }

    public void showRegister() {
        if (!setupRequired()) {
            show(new RegistrationPage(this));
        }
    }

    // ---- everyone (students, guests, staff) ----
    // showHome() routes to the right landing page for whoever is logged in.
    public void showHome() {
        if (Session.canMonitor()) {
            showSecured(new StaffDashboardPage(this));
        } else {
            showSecured(new HomePage(this));
        }
    }
    public void showProfile()  { showSecured(new ProfilePage(this)); }
    public void showSettings() { showSecured(new SettingsPage(this)); }

    // ---- students and guests ----
    public void showMyReports() { showSecured(new MyReportsPage(this)); }
    public void showSafety()    { showSecured(new SafetyPage(this)); }
    public void showAbout()     { showSecured(new AboutPage(this)); }
    public void showReportForm(String incidentType) { showSecured(new ReportFormPage(this, incidentType)); }
    public void showConfirmation(Report report) { show(new ConfirmationPage(this, report)); }

    // ---- teachers and administrators ----
    public void showAllReports() { showAllReports(null); }
    public void showAllReports(Report.Status filter) {
        showRestricted(new AllReportsPage(this, filter), Session.canMonitor());
    }
    public void showReportDetail(Report report) {
        showRestricted(new ReportDetailPage(this, report), Session.canMonitor());
    }
    public void showStaffChat() {
        showRestricted(new ChatPage(this), Session.canMonitor());   // never for students
    }

    // ---- administrators only ----
    public void showUserManagement() { showRestricted(new UserManagementPage(this), Session.isAdmin()); }
    public void showActivityLog()    { showRestricted(new ActivityLogPage(this), Session.isAdmin()); }

    // ---- actions ----
    public void logout() {
        Session.end();
        showWelcome();
    }

    public void exit() {
        stage.close();
    }
}
