package com.example.model;

import java.util.List;

/** What a user is allowed to do. Permissions live here so every screen asks the same question. */
public enum Role {
    STUDENT("Student"), TEACHER("Teacher"), ADMIN("Administrator");

    private final String label;

    Role(String label) { this.label = label; }

    public String getLabel() { return label; }

    public boolean canMonitorReports() { return this != STUDENT; }

    public boolean canManageUsers() { return this == ADMIN; }

    public boolean canDeleteReports() { return this == ADMIN; }

    /** Teachers and administrators can answer a report with an emoji. */
    public boolean canReplyToReports() { return this != STUDENT; }

    /** The staff chat is closed to students. */
    public boolean canUseStaffChat() { return this != STUDENT; }

    /** Statuses this role may assign. Teachers can only mark a report "Under Review". */
    public List<Report.Status> allowedStatuses() {
        return switch (this) {
            case ADMIN -> List.of(Report.Status.values());
            case TEACHER -> List.of(Report.Status.UNDER_REVIEW);
            default -> List.of();
        };
    }

    @Override
    public String toString() { return label; }
}
