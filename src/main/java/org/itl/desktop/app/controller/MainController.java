package org.itl.desktop.app.controller;

import com.sun.prism.paint.Color;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.WeakEventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.itl.service.ImageToLatexService;
import org.itl.service.ImageToLatexServiceImpl;
import org.scilab.forge.jlatexmath.TeXFormula;

import java.awt.dnd.DragSource;
import java.io.File;
import java.net.MalformedURLException;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainController {

    ExecutorService executorService = Executors.newFixedThreadPool(5);

    private ImageToLatexService itlService = new ImageToLatexServiceImpl();

    @FXML
    private TextArea latexTextArea;
    @FXML
    private ImageView latexImageView;
    @FXML
    private Pane imagePane;

    private HBox hbox;

    @FXML
    public void handleOnDragDropped(DragEvent dragEvent) {
        /* data dropped */
        /* if there is a string data on dragboard, read it and use it */
        Dragboard db = dragEvent.getDragboard();
        if (db.hasContent(DataFormat.FILES)) {
            File image = db.getFiles().get(0);
            Runnable runnable = () -> {
                String latexMathematicalExpression = itlService.compute(Paths.get(image.getPath()));
                latexTextArea.setText(latexMathematicalExpression);
                TeXFormula teXFormula = new TeXFormula(latexMathematicalExpression);
                teXFormula.createJPEG(1, 100, "src\\main\\resources\\temp\\tmp.jpg", java.awt.Color.WHITE, java.awt.Color.BLACK);
                try {
                    String imagePath = Paths.get("src\\main\\resources\\temp\\tmp.jpg").toUri().toURL().toString();
                    latexImageView.setImage(new Image(imagePath));
                    centerImage(imagePane, latexImageView);
                } catch (MalformedURLException e) {
                    // log it
                    e.printStackTrace();
                }
            };
            executorService.execute(runnable);
        }
        /* let the source know whether the string was successfully
         * transferred and used */
        dragEvent.setDropCompleted(true);

        dragEvent.consume();
    }

    public static void centerImage(Pane parent, ImageView imageView) {
        imageView.getFitWidth();

//        imageView.setX(parent.getPrefWidth()/2 - imageView.getImage().getWidth()/2);
//        imageView.setY(parent.getPrefHeight()/2 - imageView.getImage().getHeight()/2);
//
//        Image img = imageView.getImage();
//        if (img != null) {
//            double w = 0;
//            double h = 0;
//
//            double ratioX = imageView.getFitWidth() / img.getWidth();
//            double ratioY = imageView.getFitHeight() / img.getHeight();
//
//            double reducCoeff = 0;
//            if(ratioX >= ratioY) {
//                reducCoeff = ratioY;
//            } else {
//                reducCoeff = ratioX;
//            }
//
//            w = img.getWidth() * reducCoeff;
//            h = img.getHeight() * reducCoeff;
//
//            imageView.setX((imageView.getFitWidth() - w) / 2);
//            imageView.setY((imageView.getFitHeight() - h) / 2);
//
//        }
    }

    @FXML
    public void handleOnDragOver(DragEvent dragEvent) {
        if(dragEvent.getDragboard().hasFiles()) {
            dragEvent.acceptTransferModes(TransferMode.ANY);
        }
        dragEvent.consume();
    }

    public void handleOnAction(ActionEvent actionEvent) {
        executorService.shutdown();
        Platform.exit();
    }
}
