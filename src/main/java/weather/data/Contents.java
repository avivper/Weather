package weather.data;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import weather.widgets.CloseBar;
import weather.widgets.Conditions;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Map;

public class Contents extends Stage {

    public static boolean isContentsOpen = false;

    public Contents(Stage stage) throws FileNotFoundException {
        initOwner(stage);
        setupTableOfContents();
    }

    private void setupTableOfContents() throws FileNotFoundException {
        CloseBar CloseBar = new CloseBar(this);

        BorderPane root = new BorderPane();

        root.setTop(new CloseBar(this));
        root.setCenter(TableContainer());

        Scene scene = new Scene(root);

        scene.getStylesheets().add(new File("data\\css\\search.css").toURI().toString()); // Implement CSS

        this.initStyle(StageStyle.UNDECORATED);
        this.setResizable(false);  // Prevent from window to   resize the window
        this.setTitle("Weather App");
        this.setWidth(300);
        this.setHeight(300);
        this.setScene(scene);
        this.show();

        CloseBar.closeButton.setOnAction(
                event -> {
                    isContentsOpen = false;
                    this.close();
                }
        );
    }

    private HBox TableContainer() throws FileNotFoundException {
        HBox Container = new HBox();

        TableView<Info> Table = new TableView<>();
        ObservableList<Info> data = FXCollections.observableArrayList();

        TableColumn<Info, Image> conditionColumn = new TableColumn<>("Condition");
        TableColumn<Info, String> nameColumn = new TableColumn<>("Name");

        conditionColumn.setCellValueFactory(new PropertyValueFactory<>("image"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("imageName"));

        conditionColumn.setCellFactory(
                column -> new Items()
        );

        conditionColumn.prefWidthProperty().set(200);
        nameColumn.prefWidthProperty().set(210);

        Table.getStyleClass().add("table-view");
        Table.getColumns().addAll( // todo: fix that
                conditionColumn, nameColumn
        );

        for (Map.Entry<String, String> entry : Conditions.statusToImagePath.entrySet()) {
            String status = entry.getKey();
            String imagePath = entry.getValue();
            Image image = new Image(new FileInputStream(imagePath));
            ImageView setImage = new ImageView();
            setImage.setImage(image);
            Info imageInfo = new Info(setImage, status);
            data.add(imageInfo);
        }

        Container.setPadding(new Insets(5));
        Container.setAlignment(Pos.TOP_CENTER);
        Container.getChildren().add(Table);

        Table.setItems(data);

        return Container;
    }
}
