package weather.data;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Info {
    private Image setImage;
    private String ImageName;

    public Info(ImageView setImage, String ImageName) {
        this.setImage = setImage.getImage();
        this.ImageName = ImageName;
    }

    public Image getImage() {
        return setImage;
    }

    public String getImageName() {
        return ImageName;
    }

}
