package com.example.ui;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.scene.transform.Scale;

/**
 * Simple one-colour icons drawn as vector paths (no icon font needed).
 * The colour comes from the stylesheet (".icon"), so buttons can recolour them on hover.
 */
public final class Icon extends Group {

    public Icon(String name, double size) {
        String data = ArtData.ICONS.get(name);
        if (data == null) {
            throw new IllegalArgumentException("Unknown icon: " + name);
        }
        Rectangle frame = new Rectangle(24, 24);   // fixes bounds to the 24x24 design grid
        frame.setFill(Color.TRANSPARENT);

        SVGPath path = new SVGPath();
        path.setContent(data);
        path.setFill(Color.BLACK);          // default; the stylesheet overrides it
        path.getStyleClass().add("icon");

        Group art = new Group(frame, path);
        art.getTransforms().add(new Scale(size / 24.0, size / 24.0, 0, 0));
        getChildren().add(art);
        setMouseTransparent(true);
    }

    public static Icon of(String name) {
        return new Icon(name, 20);
    }
}
