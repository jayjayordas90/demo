package com.example.ui;

import javafx.scene.Parent;

/** Base class for every screen. Subclasses must implement build(). */
public abstract class Page {

    protected final Navigator nav;   // protected: visible to subclasses only

    protected Page(Navigator nav) {
        this.nav = nav;
    }

    public abstract Parent build();
}
