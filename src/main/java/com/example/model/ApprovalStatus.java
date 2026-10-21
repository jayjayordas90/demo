package com.example.model;

/** Whether an account may log in yet. Teacher sign-ups start as PENDING. */
public enum ApprovalStatus {
    PENDING("Pending approval"), APPROVED("Approved"), REJECTED("Rejected");

    private final String label;

    ApprovalStatus(String label) { this.label = label; }

    public String getLabel() { return label; }

    @Override
    public String toString() { return label; }
}
