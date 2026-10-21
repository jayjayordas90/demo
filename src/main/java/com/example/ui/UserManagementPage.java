package com.example.ui;

import com.example.model.ApprovalStatus;
import com.example.model.Role;
import com.example.model.User;
import com.example.service.Session;
import com.example.service.UserService;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

/** Administrator-only: approve teachers, and manage students and teachers. */
public class UserManagementPage extends DashboardPage {

    private final VBox sections = new VBox(14);
    private final Label error = UI.errorLabel();

    public UserManagementPage(Navigator nav) {
        super(nav);
    }

    @Override protected String navKey() { return "users"; }

    @Override
    protected Node buildContent() {
        VBox content = new VBox(16);
        content.setPadding(new Insets(30));

        Button add = UI.button("➕  Add Teacher / Student Account", true);
        add.setPrefWidth(300);
        add.setOnAction(e -> openCreateDialog());

        content.getChildren().addAll(
                UI.heading("Manage Users", 28),
                UI.muted("Approve teacher registrations and manage students and teachers."),
                add, error, sections);
        refresh();
        return content;
    }

    private void refresh() {
        sections.getChildren().clear();
        List<User> pending = new ArrayList<>();
        List<User> teachers = new ArrayList<>();
        List<User> students = new ArrayList<>();
        for (User user : UserService.getAll()) {
            if (user.getRole() == Role.ADMIN) {
                continue;
            }
            if (user.getApproval() != ApprovalStatus.APPROVED && user.getRole() == Role.TEACHER) {
                pending.add(user);
            } else if (user.getRole() == Role.TEACHER) {
                teachers.add(user);
            } else {
                students.add(user);
            }
        }
        addSection("Waiting for approval (" + pending.size() + ")", pending, "No teacher registrations waiting.");
        addSection("Teachers (" + teachers.size() + ")", teachers, "No approved teachers yet.");
        addSection("Students (" + students.size() + ")", students, "No students yet.");
    }

    private void addSection(String title, List<User> users, String emptyText) {
        sections.getChildren().add(UI.heading(title, 18));
        if (users.isEmpty()) {
            sections.getChildren().add(UI.muted(emptyText));
        }
        for (User user : users) {
            sections.getChildren().add(userRow(user));
        }
    }

    private HBox userRow(User user) {
        User admin = Session.getCurrentUser();

        String state = user.getApproval() != ApprovalStatus.APPROVED
                ? user.getApproval().getLabel()
                : (user.isActive() ? "Active" : "Deactivated");
        VBox info = new VBox(2,
                UI.heading(user.getFullName(), 14),
                UI.muted("@" + user.getUsername() + " · " + user.getEmail() + " · " + state));
        HBox.setHgrow(info, Priority.ALWAYS);

        HBox row = new HBox(10, info);
        row.setPadding(new Insets(12));
        row.setStyle(UI.CARD_STYLE + "-fx-alignment: center-left;");

        if (user.getApproval() != ApprovalStatus.APPROVED) {
            // ---- registration waiting for a decision ----
            Button approve = UI.button("Approve", true);
            approve.setPrefSize(100, 34);
            approve.setOnAction(e -> run(() -> UserService.approve(admin, user)));
            row.getChildren().add(approve);
            if (user.getApproval() == ApprovalStatus.PENDING) {
                Button reject = UI.button("Reject", false);
                reject.setPrefSize(100, 34);
                reject.setOnAction(e -> run(() -> UserService.reject(admin, user)));
                row.getChildren().add(reject);
            }
        } else {
            // ---- normal account controls ----
            ComboBox<Role> roleBox = new ComboBox<>();
            roleBox.getItems().addAll(Role.STUDENT, Role.TEACHER);
            roleBox.setValue(user.getRole());
            roleBox.setOnAction(e -> {
                if (roleBox.getValue() != user.getRole()) {
                    run(() -> UserService.changeRole(admin, user, roleBox.getValue()));
                }
            });

            Button toggle = UI.button(user.isActive() ? "Deactivate" : "Activate", false);
            toggle.setPrefSize(110, 34);
            toggle.setOnAction(e -> run(() -> UserService.setActive(admin, user, !user.isActive())));

            Button reset = UI.button("Reset Password", false);
            reset.setPrefSize(140, 34);
            reset.setOnAction(e -> openResetDialog(user));

            row.getChildren().addAll(roleBox, toggle, reset);
        }

        Button delete = new Button("🗑");
        delete.setStyle("-fx-background-color: transparent; -fx-text-fill: crimson;");
        delete.setOnAction(e -> run(() -> UserService.delete(admin, user)));
        row.getChildren().add(delete);
        return row;
    }

    /** Runs an admin action, shows any error, and redraws the lists. */
    private void run(Runnable action) {
        try {
            action.run();
            UI.hide(error);
        } catch (IllegalArgumentException ex) {
            UI.showMessage(error, ex.getMessage(), true);
        }
        refresh();
    }

    private void openCreateDialog() {
        TextField fullName = new TextField();
        fullName.setPromptText("Full name");
        TextField username = new TextField();
        username.setPromptText("Username");
        TextField email = new TextField();
        email.setPromptText("Email");
        PasswordField password = new PasswordField();
        password.setPromptText("Temporary password");
        ComboBox<Role> role = new ComboBox<>();
        role.getItems().addAll(Role.TEACHER, Role.STUDENT);
        role.setValue(Role.TEACHER);
        Label dialogError = UI.errorLabel();

        Stage dialog = new Stage();
        Button create = UI.button("Create Account", true);
        create.setOnAction(e -> {
            try {
                UserService.create(Session.getCurrentUser(), fullName.getText(), username.getText(),
                        email.getText(), password.getText(), role.getValue());
                dialog.close();
                refresh();
            } catch (IllegalArgumentException ex) {
                UI.showMessage(dialogError, ex.getMessage(), true);
            }
        });

        VBox box = new VBox(10,
                UI.heading("New Account", 18),
                UI.labeled("Full name", fullName), UI.labeled("Username", username),
                UI.labeled("Email", email), UI.labeled("Temporary password", password),
                UI.labeled("Role", role), dialogError, create);
        box.setPadding(new Insets(25));

        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setScene(new javafx.scene.Scene(box, 360, 440));
        dialog.setTitle("Add Account");
        dialog.showAndWait();
    }

    private void openResetDialog(User target) {
        PasswordField newPassword = new PasswordField();
        newPassword.setPromptText("New temporary password");
        Label dialogError = UI.errorLabel();

        Stage dialog = new Stage();
        Button confirm = UI.button("Reset Password", true);
        confirm.setOnAction(e -> {
            try {
                UserService.resetPassword(Session.getCurrentUser(), target, newPassword.getText());
                dialog.close();
            } catch (IllegalArgumentException ex) {
                UI.showMessage(dialogError, ex.getMessage(), true);
            }
        });

        VBox box = new VBox(10,
                UI.heading("Reset password for @" + target.getUsername(), 16),
                UI.labeled("New password", newPassword), dialogError, confirm);
        box.setPadding(new Insets(25));

        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setScene(new javafx.scene.Scene(box, 340, 220));
        dialog.setTitle("Reset Password");
        dialog.showAndWait();
    }
}
