package com.example.model;

import java.time.LocalDate;
import java.time.Year;
import java.time.format.DateTimeFormatter;

/** A single anonymous incident report. */
public class Report {

    /** Nested enum: the lifecycle of a report. */
    public enum Status {
        SUBMITTED("Submitted"), UNDER_REVIEW("Under Review"), RESOLVED("Resolved");

        private final String label;

        Status(String label) { this.label = label; }

        public String getLabel() { return label; }
    }

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("MMM d, yyyy");
    private static int sequence = 0;       // shared counter -> unique reference numbers

    private final String referenceNo;
    private final String type;
    private final String description;
    private final String location;
    private final LocalDate incidentDate;  // may be null (optional)
    private final String incidentTime;
    private final String peopleInvolved;
    private final LocalDate dateSubmitted;
    private final String ownerUsername;    // private, NO getter: the UI can never reveal who filed it
    private Status status;

    public Report(String type, String description, String location, LocalDate incidentDate,
                  String incidentTime, String peopleInvolved, User owner) {
        if (type == null || type.isBlank() || description == null || description.isBlank()) {
            throw new IllegalArgumentException("Type and description are required.");
        }
        this.referenceNo = nextReference();
        this.type = type;
        this.description = description.trim();
        this.location = location;
        this.incidentDate = incidentDate;
        this.incidentTime = incidentTime;
        this.peopleInvolved = peopleInvolved;
        this.dateSubmitted = LocalDate.now();
        this.ownerUsername = owner.isAnonymous() ? null : owner.getUsername();
        this.status = Status.SUBMITTED;
    }

    private static String nextReference() {
        return String.format("PK-%d-%04d", Year.now().getValue(), ++sequence);
    }

    /** True only if this report was filed by the given (non-anonymous) user. */
    public boolean isOwnedBy(User user) {
        return ownerUsername != null && ownerUsername.equals(user.getUsername());
    }

    public String getReferenceNo() { return referenceNo; }
    public String getType() { return type; }
    public String getDescription() { return description; }
    public String getLocation() { return location; }
    public LocalDate getIncidentDate() { return incidentDate; }
    public String getIncidentTime() { return incidentTime; }
    public String getPeopleInvolved() { return peopleInvolved; }
    public String getDateSubmittedFormatted() { return dateSubmitted.format(DATE_FORMAT); }
    public Status getStatus() { return status; }

    public void setStatus(Status status) { this.status = status; }
}
