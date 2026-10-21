package com.example.service;

import com.example.model.Report;
import com.example.model.User;

import java.util.ArrayList;
import java.util.List;

/** Static, in-memory store of submitted reports. */
public final class ReportService {

    private static final List<Report> REPORTS = new ArrayList<>();

    private ReportService() { }

    public static void submit(Report report) {
        REPORTS.add(report);
    }

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
}
