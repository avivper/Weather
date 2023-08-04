package weather.data;

import javafx.scene.control.TableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Items extends TableCell<Info, Image> {
    private final ImageView imageView = new ImageView();

    @Override
    protected void updateItem(Image item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || item == null) {
            setGraphic(null);
        } else {
            imageView.setImage(item);
            setGraphic(imageView);
        }
    }
}
