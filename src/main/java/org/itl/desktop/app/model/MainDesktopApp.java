package org.itl.desktop.app.model;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.event.WeakEventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import sun.net.util.URLUtil;

import java.nio.file.Paths;

public class MainDesktopApp extends Application {

    public static void main(String[] args) {
        launch(args);
    }
    private Parent root;

    @Override
    public void start(Stage primaryStage) throws Exception {
        System.setProperty("configurationFactory","org.apache.logging.log4j.core.config.json.JsonConfigurationFactory");
//        String sources = "C:\\ComputerScience\\source\\";
//        System.setProperty("log4j.configurationFile", sources + "itl-service\\src\\main\\resources\\log4j2.json");
        String rootp = "C:/ComputerScience/source/itl-desktop-app/target/classes/";
        String path = rootp + "fxml/sample.fxml";
        System.out.println(Paths.get(path).toUri());
        Parent root = FXMLLoader.load(Paths.get(path).toUri().toURL());
//        root.setOnDragOver(new EventHandler<DragEvent>() {
//            @Override
//            public void handle(final DragEvent event) {
//                mouseDragOver(event);
//            }
//        });
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setTitle("Hello World");
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        allowDrag(scene.lookup("#hbox"), primaryStage);

//        primaryStage.getScene().getRoot().setEffect(new DropShadow());
//        primaryStage.getScene().setFill(Color.TRANSPARENT);

        primaryStage.show();
    }

    private double mouseDragDeltaX = 0;
    private double mouseDragDeltaY = 0;
    private EventHandler<MouseEvent> mousePressedHandler;
    private EventHandler<MouseEvent> mouseDraggedHandler;
    private WeakEventHandler<MouseEvent> weakMousePressedHandler;
    private WeakEventHandler<MouseEvent> weakMouseDraggedHandler;

    protected void allowDrag(Node node, Stage stage) {
        mousePressedHandler = (MouseEvent event) -> {
            mouseDragDeltaX = node.getLayoutX() - event.getSceneX();
            mouseDragDeltaY = node.getLayoutY() - event.getSceneY();
        };
        weakMousePressedHandler = new WeakEventHandler<>(mousePressedHandler);
        node.setOnMousePressed(weakMousePressedHandler);

        mouseDraggedHandler = (MouseEvent event) -> {
            stage.setX(event.getScreenX() + mouseDragDeltaX);
            stage.setY(event.getScreenY() + mouseDragDeltaY);
        };
        weakMouseDraggedHandler = new WeakEventHandler<>(mouseDraggedHandler);
        node.setOnMouseDragged(weakMouseDraggedHandler);
    }
}