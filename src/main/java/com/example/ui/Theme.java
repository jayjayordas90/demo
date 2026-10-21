package com.example.ui;

import javafx.scene.Parent;
import javafx.scene.Scene;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/** Loads the shared stylesheet (src/main/resources/paperkite.css). */
public final class Theme {

    private static String url;

    private Theme() { }

    public static String stylesheet() {
        if (url == null) {
            java.net.URL resource = Theme.class.getResource("/paperkite.css");
            if (resource != null) {
                url = resource.toExternalForm();
            } else {
                // fallback when run without Maven resources: read straight from the project folder
                Path file = Paths.get("src", "main", "resources", "paperkite.css");
                if (Files.exists(file)) {
                    url = file.toUri().toString();
                } else {
                    System.err.println("paperkite.css not found - the app will use the default look.");
                    url = "";
                }
            }
        }
        return url;
    }

    public static void apply(Scene scene) {
        addTo(scene.getStylesheets());
    }

    public static void apply(Parent root) {
        addTo(root.getStylesheets());
    }

    private static void addTo(List<String> sheets) {
        String sheet = stylesheet();
        if (!sheet.isEmpty() && !sheets.contains(sheet)) {
            sheets.add(sheet);
        }
    }
}
