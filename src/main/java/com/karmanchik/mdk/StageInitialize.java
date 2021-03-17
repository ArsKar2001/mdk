package com.karmanchik.mdk;

import com.karmanchik.mdk.figure.Figure;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Point3D;
import javafx.scene.*;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Transform;
import javafx.stage.Stage;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import static com.karmanchik.mdk.JavaFxApplication.StageReadyEvent;
import static com.karmanchik.mdk.figure.Figure.*;

@Log4j2
@Component
public class StageInitialize implements ApplicationListener<StageReadyEvent> {

    public static final float WIDTH = 1000;
    public static final float HEIGHT = 800;
    private static final float DEPTH = 1800;

    private final DoubleProperty angelX = new SimpleDoubleProperty(0);
    private final DoubleProperty angelY = new SimpleDoubleProperty(0);
    private final DoubleProperty angelZ = new SimpleDoubleProperty(0);
    private final String title;

    private double anchorX;
    private double anchorY;
    private double anchorZ;
    private double angelAnchorX = 0;
    private double angelAnchorY = 0;
    private double angelAnchorZ = 0;
    private Shape3D selectFigure;

    public StageInitialize(@Value("${spring.app.title}") String title) {
        this.title = title;
    }

    @Override
    public void onApplicationEvent(StageReadyEvent event) {
        try {
            Stage stage = event.getStage();

            VBox vBox = new VBox();
            ComboBox<String> cbFigure = new ComboBox<>();
            SmartGroup group = new SmartGroup();
            Transform transform = new Rotate(0, new Point3D(1, 0, 1));


            cbFigure.getItems().addAll(
                    "Параллелепипед", "Шар", "Пирамида", "Цилиндр"
            );
            cbFigure.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
                switch (newValue.intValue()) {
                    case 0:
                        selectFigure = createBox(600, 500, 300);
                        break;
                    case 1:
                        selectFigure = createSphere(500);
                        break;
                    case 2:
                        selectFigure = createPyramid(500, 600);
                        break;
                    case 3:
                        selectFigure = createCylinder(200, 500);
                        break;
                }
                group.getChildren().clear();
                group.getChildren().addAll(selectFigure);
            });
            cbFigure.getSelectionModel().selectFirst();

            vBox.getChildren().addAll(cbFigure, group);

            Scene scene = new Scene(vBox, WIDTH, HEIGHT);
            Camera camera = new PerspectiveCamera();
            scene.setCamera(camera);
            scene.setFill(Color.SILVER);

            group.translateXProperty().set(WIDTH / 2);
            group.translateYProperty().set(HEIGHT / 2);
            group.translateZProperty().set(DEPTH);
            group.getTransforms().add(transform);

            initMouseControl(group, scene, stage);

            stage.addEventHandler(KeyEvent.KEY_PRESSED, keyEvent -> {
                switch (keyEvent.getCode()) {
                    case W:
                        group.rotateByY(10);
                        break;
                    case S:
                        group.rotateByY(-10);
                        break;
                    case A:
                        group.rotateByX(10);
                        break;
                    case D:
                        group.rotateByX(-10);
                        break;
                    case Q:
                        group.rotateByZ(10);
                        break;
                    case E:
                        group.rotateByZ(-10);
                        break;
                }
            });
            stage.setScene(scene);
            stage.setTitle(title);
            stage.show();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            new Alert(Alert.AlertType.ERROR, e.toString(), ButtonType.OK).showAndWait();
        }
    }

    private void initMouseControl(SmartGroup group, Scene scene, Stage stage) {
        Rotate xRotate;
        Rotate yRotate;
        Rotate zRotate;
        group.getTransforms().addAll(
                xRotate = new Rotate(0, Rotate.X_AXIS),
                yRotate = new Rotate(0, Rotate.Y_AXIS),
                zRotate = new Rotate(0, Rotate.Z_AXIS)
        );
        xRotate.angleProperty().bind(angelX);
        yRotate.angleProperty().bind(angelY);
        zRotate.angleProperty().bind(angelZ);

        scene.setOnMousePressed(event -> {
            anchorX = event.getSceneX();
            anchorY = event.getSceneY();
            anchorZ = event.getSceneY();

            angelAnchorX = angelX.get();
            angelAnchorY = angelY.get();
            angelAnchorZ = angelY.get();
        });

        scene.setOnMouseDragged(event -> {
            angelX.set(angelAnchorX + anchorY + event.getSceneY());
            angelY.set(angelAnchorY - (anchorX + event.getSceneX()));
            angelZ.set(angelAnchorZ - (anchorX + event.getSceneY()));
        });

        stage.addEventHandler(ScrollEvent.SCROLL, event -> {
            double delta = event.getDeltaY();
            group.translateZProperty().set(group.getTranslateZ() + delta * 4);
        });
    }

    static class SmartGroup extends Group {
        Rotate rotate;
        Transform transform = new Rotate();

        public void rotateByX(int angle) {
            rotate = new Rotate(angle, Rotate.X_AXIS);
            transform = rotate.createConcatenation(transform);
            this.getTransforms().clear();
            this.getTransforms().add(transform);
        }

        public void rotateByY(int angle) {
            rotate = new Rotate(angle, Rotate.Y_AXIS);
            transform = rotate.createConcatenation(transform);
            this.getTransforms().clear();
            this.getTransforms().add(transform);
        }

        public void rotateByZ(int angle) {
            rotate = new Rotate(angle, Rotate.Z_AXIS);
            transform = rotate.createConcatenation(transform);
            this.getTransforms().clear();
            this.getTransforms().add(transform);
        }
    }
}
