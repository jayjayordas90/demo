package com.example.ui;

import com.example.model.ApprovalStatus;
import com.example.model.Role;
import com.example.model.User;
import com.example.service.Session;
import com.example.service.UserService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
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
        content.setPadding(new Insets(32));

        Button add = UI.button("Add Teacher / Student Account", "add", true);
        add.setPrefWidth(320);
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
        sections.getChildren().add(UI.heading(title, 19));
        if (users.isEmpty()) {
            sections.getChildren().add(UI.muted(emptyText));
        }
        for (User user : users) {
            sections.getChildren().add(userRow(user));
        }
    }

    private HBox userRow(User user) {
        User admin = Session.getCurrentUser();

        HBox top = new HBox(10, UI.heading(user.getFullName(), 15), UI.roleBadge(user.getRole()));
        top.setAlignment(Pos.CENTER_LEFT);
        if (user.getApproval() != ApprovalStatus.APPROVED) {
            top.getChildren().add(UI.badge(user.getApproval().getLabel(), "badge-pending"));
        } else if (!user.isActive()) {
            top.getChildren().add(UI.badge("Deactivated", "badge-off"));
        }
        VBox info = new VBox(3, top, UI.muted("@" + user.getUsername() + " · " + user.getEmail()));
        HBox.setHgrow(info, Priority.ALWAYS);

        HBox row = new HBox(12, UI.avatar(user.getFullName(), 22), info);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(14, 18, 14, 18));
        row.setMaxWidth(900);
        row.getStyleClass().add("card");

        if (user.getApproval() != ApprovalStatus.APPROVED) {
            // ---- registration waiting for a decision ----
            Button approve = UI.button("Approve", "check", true);
            approve.setPrefSize(130, 36);
            approve.setOnAction(e -> run(() -> UserService.approve(admin, user)));
            row.getChildren().add(approve);
            if (user.getApproval() == ApprovalStatus.PENDING) {
                Button reject = UI.flat("Reject", "close", true);
                reject.setOnAction(e -> run(() -> UserService.reject(admin, user)));
                row.getChildren().add(reject);
            }
        } else {
            // ---- normal account controls ----
            ComboBox<Role> roleBox = new ComboBox<>();
            roleBox.getItems().addAll(Role.STUDENT, Role.TEACHER);
            roleBox.setValue(user.getRole());
            roleBox.setPrefWidth(120);
            roleBox.setOnAction(e -> {
                if (roleBox.getValue() != user.getRole()) {
                    run(() -> UserService.changeRole(admin, user, roleBox.getValue()));
                }
            });

            Button toggle = UI.flat(user.isActive() ? "Deactivate" : "Activate", user.isActive() ? "close" : "check", false);
            toggle.setOnAction(e -> run(() -> UserService.setActive(admin, user, !user.isActive())));

            Button reset = UI.flat("Reset password", "lock", false);
            reset.setOnAction(e -> openResetDialog(user));

            row.getChildren().addAll(roleBox, toggle, reset);
        }

        Button delete = UI.flat("", "delete", true);
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

    private void showDialog(Stage dialog, VBox box, double width, double height, String title) {
        box.setPadding(new Insets(26));
        box.getStyleClass().add("page-bg");
        Scene scene = new Scene(box, width, height);
        Theme.apply(scene);
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setScene(scene);
        dialog.setTitle(title);
        dialog.showAndWait();
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
        Button create = UI.button("Create Account", "add", true);
        create.setMaxWidth(Double.MAX_VALUE);
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
                UI.heading("New Account", 22),
                UI.labeled("Full name", fullName), UI.labeled("Username", username),
                UI.labeled("Email", email), UI.labeled("Temporary password", password),
                UI.labeled("Role", role), dialogError, create);
        showDialog(dialog, box, 380, 560, "Add Account");
    }

    private void openResetDialog(User target) {
        PasswordField newPassword = new PasswordField();
        newPassword.setPromptText("New temporary password");
        Label dialogError = UI.errorLabel();

        Stage dialog = new Stage();
        Button confirm = UI.button("Reset Password", "lock", true);
        confirm.setMaxWidth(Double.MAX_VALUE);
        confirm.setOnAction(e -> {
            try {
                UserService.resetPassword(Session.getCurrentUser(), target, newPassword.getText());
                dialog.close();
            } catch (IllegalArgumentException ex) {
                UI.showMessage(dialogError, ex.getMessage(), true);
            }
        });

        VBox box = new VBox(12,
                UI.heading("Reset password for @" + target.getUsername(), 18),
                UI.labeled("New password", newPassword), dialogError, confirm);
        showDialog(dialog, box, 360, 260, "Reset Password");
    }
}
