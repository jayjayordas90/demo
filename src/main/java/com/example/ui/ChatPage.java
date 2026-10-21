package com.example.ui;

import com.example.model.ChatMessage;
import com.example.model.Reaction;
import com.example.model.User;
import com.example.service.ChatService;
import com.example.service.Session;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/** Group chat for teachers and administrators ONLY (Navigator and ChatService both enforce this). */
public class ChatPage extends DashboardPage {

    private final VBox messageList = new VBox(8);

    public ChatPage(Navigator nav) {
        super(nav);
    }

    @Override protected String navKey() { return "chat"; }

    @Override
    protected Node buildContent() {
        User me = Session.getCurrentUser();

        VBox content = new VBox(12);
        content.setPadding(new Insets(30));
        content.getChildren().addAll(
                UI.heading("Staff Chat", 28),
                UI.muted("A private room for teachers and administrators. Students cannot see it."));

        messageList.setPadding(new Insets(12));
        ScrollPane scroll = new ScrollPane(messageList);
        scroll.setFitToWidth(true);
        scroll.setPrefHeight(380);
        scroll.setStyle("-fx-background-color: white; -fx-border-color: #dddddd;");
        // keep the newest message in view whenever the list grows
        messageList.heightProperty().addListener((obs, oldValue, newValue) -> scroll.setVvalue(1.0));
        refreshMessages(me);

        TextField input = new TextField();
        input.setPromptText("Type a message...");
        input.setPrefHeight(38);
        HBox.setHgrow(input, Priority.ALWAYS);
        Button send = UI.button("Send", true);
        send.setPrefWidth(90);
        Label error = UI.errorLabel();

        Runnable sendMessage = () -> {
            try {
                ChatService.post(me, input.getText());
                input.clear();
                UI.hide(error);
                refreshMessages(me);
            } catch (IllegalArgumentException ex) {
                UI.showMessage(error, ex.getMessage(), true);
            }
        };
        send.setOnAction(e -> sendMessage.run());
        input.setOnAction(e -> sendMessage.run());   // Enter sends

        // quick emoji buttons
        HBox emojis = new HBox(6);
        for (Reaction reaction : Reaction.values()) {
            Button emoji = new Button(reaction.getSymbol());
            emoji.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
            emoji.setOnAction(e -> {
                input.appendText(reaction.getSymbol());
                input.requestFocus();
                input.end();
            });
            emojis.getChildren().add(emoji);
        }

        content.getChildren().addAll(scroll, emojis, new HBox(10, input, send), error);
        return content;
    }

    private void refreshMessages(User me) {
        messageList.getChildren().clear();
        var messages = ChatService.getAll(me);
        if (messages.isEmpty()) {
            messageList.getChildren().add(UI.muted("No messages yet. Say hello to your colleagues!"));
            return;
        }
        for (ChatMessage message : messages) {
            messageList.getChildren().add(bubble(message, message.isFrom(me)));
        }
    }

    private HBox bubble(ChatMessage message, boolean mine) {
        Label header = UI.muted(message.getSenderName() + " (" + message.getSenderRole() + ") · "
                + message.getSentFormatted());
        header.setStyle("-fx-font-size: 11;");

        Label text = new Label(message.getText());
        text.setWrapText(true);
        text.setMaxWidth(380);
        text.setPadding(new Insets(8, 12, 8, 12));
        text.setStyle(mine
                ? "-fx-background-color: #222222; -fx-text-fill: white; -fx-background-radius: 12;"
                : "-fx-background-color: #eeeeee; -fx-background-radius: 12;");

        VBox box = new VBox(3, header, text);
        box.setAlignment(mine ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);

        HBox row = new HBox(box);
        row.setAlignment(mine ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
        return row;
    }
}
