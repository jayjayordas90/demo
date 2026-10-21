package com.example.ui;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;

/** Template for every screen that has the sidebar (Home, Reports, Profile, ...). */
public abstract class DashboardPage extends Page {

    protected DashboardPage(Navigator nav) {
        super(nav);
    }

    /** Which sidebar button to highlight. */
    protected abstract String navKey();

    /** The page-specific content shown on the right. */
    protected abstract Node buildContent();

    @Override
    public final Parent build() {
        BorderPane layout = new BorderPane();
        layout.getStyleClass().add("page-bg");
        layout.setLeft(new Sidebar(nav, navKey()).build());

        ScrollPane scroll = new ScrollPane(buildContent());
        scroll.setFitToWidth(true);
        layout.setCenter(scroll);
        return layout;
    }
}
