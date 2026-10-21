package com.example.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** A single anonymous incident report. */
public class Report implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Nested enum: the lifecycle of a report. */
    public enum Status {
        SUBMITTED("Submitted"), UNDER_REVIEW("Under Review"), RESOLVED("Resolved");

        private final String label;

        Status(String label) { this.label = label; }

        public String getLabel() { return label; }

        @Override
        public String toString() { return label; }
    }

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("MMM d, yyyy");
    private static final DateTimeFormatter NOTE_FORMAT = DateTimeFormatter.ofPattern("MMM d, h:mm a");
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
    private final List<String> notes = new ArrayList<>();   // staff notes
    private final List<Reply> replies = new ArrayList<>();  // emoji replies

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

    /** After loading saved reports, continue numbering from the highest existing number. */
    public static void restoreSequence(List<Report> loaded) {
        int max = 0;
        for (Report report : loaded) {
            String ref = report.referenceNo;
            try {
                max = Math.max(max, Integer.parseInt(ref.substring(ref.lastIndexOf('-') + 1)));
            } catch (NumberFormatException ignored) {
                // skip unexpected formats
            }
        }
        sequence = max;
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

    public String getIncidentDateFormatted() {
        return incidentDate == null ? "Not provided" : incidentDate.format(DATE_FORMAT);
    }

    public void setStatus(Status status) { this.status = status; }

    public void addNote(User author, String text) {
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("Please write a note first.");
        }
        notes.add(String.format("%s — %s (%s): %s", LocalDateTime.now().format(NOTE_FORMAT),
                author.getFullName(), author.getRole(), text.trim()));
    }

    public List<String> getNotes() { return Collections.unmodifiableList(notes); }

    // ---- emoji replies ----
    public void addReply(User staff, Reaction reaction) {
        replies.add(new Reply(reaction, staff.getFullName(), LocalDateTime.now()));
    }

    public List<Reply> getReplies() { return Collections.unmodifiableList(replies); }

    /** Static nested class: one emoji reply from staff. */
    public static class Reply implements Serializable {

        private static final long serialVersionUID = 1L;

        private final Reaction reaction;
        private final String staffName;
        private final LocalDateTime sentAt;

        private Reply(Reaction reaction, String staffName, LocalDateTime sentAt) {
            this.reaction = reaction;
            this.staffName = staffName;
            this.sentAt = sentAt;
        }

        public Reaction getReaction() { return reaction; }
        public String getStaffName() { return staffName; }
        public String getSentFormatted() { return sentAt.format(NOTE_FORMAT); }
    }
}
