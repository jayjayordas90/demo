package com.example.ui;

import com.example.model.Reaction;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.transform.Scale;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

/**
 * Emoji drawn as vector shapes. They look identical on every computer because
 * nothing depends on the operating system's emoji font.
 */
public final class Emoji {

    private Emoji() { }

    /** The emoji for a reaction, sized to size x size pixels. */
    public static Node of(Reaction reaction, double size) {
        return named(reaction.name(), size);
    }

    /** Any emoji from ArtData by name (e.g. "KITE" is the fallback logo). */
    public static Node named(String name, double size) {
        Group art = new Group();
        Rectangle frame = new Rectangle(100, 100);   // fixes the drawing's bounds to a 100x100 box
        frame.setFill(Color.TRANSPARENT);
        art.getChildren().add(frame);
        for (String[] layer : ArtData.EMOJI.get(name)) {
            art.getChildren().add(buildLayer(layer));
        }
        art.getTransforms().add(new Scale(size / 100.0, size / 100.0, 0, 0));
        Group holder = new Group(art);   // outer group reports the scaled size to layouts
        holder.setMouseTransparent(true);
        return holder;
    }

    /** Replaces the six reaction symbols inside normal text with the drawn emoji. */
    public static TextFlow flow(String text, double emojiSize, String textStyle) {
        TextFlow flow = new TextFlow();
        StringBuilder plain = new StringBuilder();
        int i = 0;
        while (i < text.length()) {
            Reaction found = null;
            for (Reaction reaction : Reaction.values()) {
                if (text.startsWith(reaction.getSymbol(), i)) {
                    found = reaction;
                    break;
                }
            }
            if (found == null) {
                plain.append(text.charAt(i));
                i++;
            } else {
                addText(flow, plain, textStyle);
                flow.getChildren().add(of(found, emojiSize));
                i += found.getSymbol().length();
            }
        }
        addText(flow, plain, textStyle);
        return flow;
    }

    private static void addText(TextFlow flow, StringBuilder plain, String style) {
        if (plain.length() > 0) {
            Text part = new Text(plain.toString());
            part.setStyle(style);
            flow.getChildren().add(part);
            plain.setLength(0);
        }
    }

    private static Node buildLayer(String[] l) {
        switch (l[0]) {
            case "circle": {
                Circle c = new Circle(Double.parseDouble(l[1]), Double.parseDouble(l[2]), Double.parseDouble(l[3]));
                paint(c, l[4], l[5], l[6]);
                return c;
            }
            case "ellipse": {
                Ellipse e = new Ellipse(Double.parseDouble(l[1]), Double.parseDouble(l[2]),
                        Double.parseDouble(l[3]), Double.parseDouble(l[4]));
                paint(e, l[5], l[6], l[7]);
                return e;
            }
            default: {
                SVGPath p = new SVGPath();
                p.setContent(l[1]);
                paint(p, l[2], l[3], l[4]);
                return p;
            }
        }
    }

    private static void paint(Shape shape, String fill, String stroke, String width) {
        shape.setFill(color(fill));
        shape.setStroke(color(stroke));
        shape.setStrokeWidth(Double.parseDouble(width));
        shape.setStrokeLineCap(StrokeLineCap.ROUND);
        shape.setStrokeLineJoin(StrokeLineJoin.ROUND);
    }

    private static Color color(String value) {
        return "none".equals(value) ? null : Color.web(value);
    }
}
