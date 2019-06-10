package org.itl.desktop.app.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.WeakEventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.itl.service.ImageToLatexService;
import org.itl.service.ImageToLatexServiceImpl;

import java.awt.dnd.DragSource;
import java.io.File;
import java.nio.file.Paths;

public class MainController {
    @FXML
    public TextArea latexTextArea;
    @FXML
    public ImageView latexImageView;

    public ImageToLatexService itlService = new ImageToLatexServiceImpl();

    public HBox hbox;

    @FXML
    public void handleOnDragDropped(DragEvent dragEvent) {
        /* data dropped */
        /* if there is a string data on dragboard, read it and use it */
        Dragboard db = dragEvent.getDragboard();
        boolean success = true;
        if (db.hasContent(DataFormat.FILES)) {
            File image = db.getFiles().get(0);
            String latexEquation = itlService.compute(Paths.get(image.getPath()));
            latexTextArea.setText(latexEquation);
            success = true;
        }
        /* let the source know whether the string was successfully
         * transferred and used */
        dragEvent.setDropCompleted(success);

        dragEvent.consume();
    }

    @FXML
    public void handleOnDragOver(DragEvent dragEvent) {
        if(dragEvent.getDragboard().hasFiles()) {
            dragEvent.acceptTransferModes(TransferMode.ANY);
        }
        dragEvent.consume();
    }

    public void handleOnAction(ActionEvent actionEvent) {
        Platform.exit();
    }
}
