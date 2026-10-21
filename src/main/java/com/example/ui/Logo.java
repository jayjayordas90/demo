package com.example.ui;

import javafx.scene.Node;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * The PaperKite logo: the paper-plane picture in src/main/resources/paperkite.png.
 * Each size is loaded once and reused. If the picture is missing, the old drawn
 * kite (ArtData "KITE") is shown instead, so the app never starts without a logo.
 */
public final class Logo {

    private static final String RESOURCE = "/paperkite.png";
    private static final Map<Integer, Image> CACHE = new HashMap<>();

    private Logo() { }

    /** The logo scaled to the given width; the height follows the picture's shape. */
    public static Node of(double width) {
        Image image = load((int) Math.round(width));
        if (image == null) {
            return Emoji.named("KITE", width * 0.7);
        }
        ImageView view = new ImageView(image);
        view.setFitWidth(width);
        view.setPreserveRatio(true);
        view.setSmooth(true);
        view.setMouseTransparent(true);
        // the plane is white, so a soft shadow keeps it visible on light backgrounds
        view.setEffect(new DropShadow(8, 0, 2, Color.web("#1B5662", 0.35)));
        return view;
    }

    private static Image load(int width) {
        Image cached = CACHE.get(width);
        if (cached != null) {
            return cached;
        }
        try (InputStream in = Logo.class.getResourceAsStream(RESOURCE)) {
            if (in == null) {
                System.err.println("paperkite.png not found - using the drawn kite instead.");
                return null;
            }
            // decode at twice the size so it stays sharp on high-DPI screens
            Image image = new Image(in, width * 2.0, 0, true, true);
            CACHE.put(width, image);
            return image;
        } catch (java.io.IOException e) {
            System.err.println("Could not load paperkite.png: " + e.getMessage());
            return null;
        }
    }
}
