package com.example.ui;

import com.example.model.Report;
import com.example.service.Session;
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

    /** Dashboard pages need a session (logged in or guest); otherwise go to the start screen. */
    private void showSecured(DashboardPage page) {
        if (Session.hasSession()) {
            show(page);
        } else {
            showWelcome();
        }
    }

    // ---- public screens ----
    public void showWelcome()  { show(new WelcomePage(this)); }
    public void showLogin()    { show(new LoginPage(this)); }
    public void showRegister() { show(new RegistrationPage(this)); }

    // ---- dashboard screens ----
    public void showHome()      { showSecured(new HomePage(this)); }
    public void showMyReports() { showSecured(new MyReportsPage(this)); }
    public void showProfile()   { showSecured(new ProfilePage(this)); }
    public void showSettings()  { showSecured(new SettingsPage(this)); }
    public void showSafety()    { showSecured(new SafetyPage(this)); }
    public void showAbout()     { showSecured(new AboutPage(this)); }
    public void showReportForm(String incidentType) { showSecured(new ReportFormPage(this, incidentType)); }

    public void showConfirmation(Report report) { show(new ConfirmationPage(this, report)); }

    // ---- actions ----
    public void logout() {
        Session.end();
        showWelcome();
    }

    public void exit() {
        stage.close();
    }
}
