package com.example.service;

import com.example.model.Reaction;
import com.example.model.Report;
import com.example.model.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Static store of submitted reports. */
public final class ReportService {

    private static final List<Report> REPORTS = new ArrayList<>();

    private ReportService() { }

    public static void submit(Report report) {
        REPORTS.add(report);
        DataStore.save();
    }

    // ---------- student view ----------

    public static List<Report> getReportsFor(User user) {
        List<Report> mine = new ArrayList<>();
        for (Report report : REPORTS) {
            if (report.isOwnedBy(user)) {
                mine.add(report);
            }
        }
        return mine;
    }

    public static int countFor(User user) {
        return getReportsFor(user).size();
    }

    // ---------- staff view ----------

    /** All reports, newest first. */
    public static List<Report> getAll() {
        List<Report> copy = new ArrayList<>(REPORTS);
        Collections.reverse(copy);
        return copy;
    }

    public static List<Report> recent(int limit) {
        List<Report> all = getAll();
        return all.subList(0, Math.min(limit, all.size()));
    }

    public static List<Report> getByStatus(Report.Status status) {
        List<Report> result = new ArrayList<>();
        for (Report report : getAll()) {
            if (report.getStatus() == status) {
                result.add(report);
            }
        }
        return result;
    }

    public static int countByStatus(Report.Status status) {
        return getByStatus(status).size();
    }

    public static int count() { return REPORTS.size(); }

    public static void updateStatus(User actor, Report report, Report.Status status) {
        if (!actor.getRole().allowedStatuses().contains(status)) {
            throw new IllegalArgumentException("Your role cannot set that status.");
        }
        report.setStatus(status);
        ActivityLog.record(actor, report.getReferenceNo() + " set to " + status);
    }

    public static void addNote(User actor, Report report, String text) {
        if (!actor.getRole().canMonitorReports()) {
            throw new IllegalArgumentException("Only staff can add notes.");
        }
        report.addNote(actor, text);
        ActivityLog.record(actor, "Added a note to " + report.getReferenceNo());
    }

    public static void reply(User actor, Report report, Reaction reaction) {
        if (!actor.getRole().canReplyToReports()) {
            throw new IllegalArgumentException("Only teachers and administrators can reply.");
        }
        report.addReply(actor, reaction);
        ActivityLog.record(actor, "Replied " + reaction.getSymbol() + " to " + report.getReferenceNo());
    }

    public static void delete(User actor, Report report) {
        if (!actor.getRole().canDeleteReports()) {
            throw new IllegalArgumentException("Only administrators can delete reports.");
        }
        REPORTS.remove(report);
        ActivityLog.record(actor, "Deleted report " + report.getReferenceNo());
    }

    // package-private: only DataStore uses these
    static ArrayList<Report> snapshot() { return new ArrayList<>(REPORTS); }

    static void restore(List<Report> loaded) {
        REPORTS.clear();
        REPORTS.addAll(loaded);
        Report.restoreSequence(REPORTS);
    }
}
