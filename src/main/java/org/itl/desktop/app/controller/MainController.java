package org.itl.desktop.app.controller;

import com.sun.prism.paint.Color;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.WeakEventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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
import org.scilab.forge.jlatexmath.TeXIcon;

import java.awt.*;
import java.awt.dnd.DragSource;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainController implements Initializable {

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
                try {
                    latexImageView.setImage(createSerializedFxImageFromLatexCode(latexMathematicalExpression));
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
        return; // Not implemented yet.
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        latexTextArea.textProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue.equals(oldValue)) {
                return;
            } else {
                try {
                    latexImageView.setImage(createSerializedFxImageFromLatexCode(newValue));
                } catch (MalformedURLException e) {
                    // log it.
                    e.printStackTrace();
                }
            }
        });
    }

    private static Image createSerializedFxImageFromLatexCode(String latexCode) throws MalformedURLException {
        TeXFormula teXFormula = new TeXFormula(latexCode);
        if(Paths.get("src\\main\\resources\\temp\\tmp.jpg").toFile().exists()) {
            Paths.get("src\\main\\resources\\temp\\tmp.jpg").toFile().delete();
        }
        teXFormula.createJPEG(1, 100, "src\\main\\resources\\temp\\tmp.jpg", java.awt.Color.WHITE, java.awt.Color.BLACK);
        String imagePath = Paths.get("src\\main\\resources\\temp\\tmp.jpg").toUri().toURL().toString();
        return new Image(imagePath);
    }

    private static Image createBufferedFxImageFromLatexCode(String latexCode) {
        TeXFormula teXFormula = new TeXFormula(latexCode);
        java.awt.Image awtImage = teXFormula.createBufferedImage(1, 100, java.awt.Color.WHITE, java.awt.Color.BLACK);
        BufferedImage bufferedImage = new BufferedImage(awtImage.getWidth(null), awtImage.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        return SwingFXUtils.toFXImage(bufferedImage, null);
    }
}
