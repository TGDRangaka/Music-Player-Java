package lk.ijse.musicplayerjava;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;

import java.io.File;

public class PlayerViewController {

    @FXML
    private MediaView mediaView;

    private Media media;
    private MediaPlayer mediaPlayer;

    @FXML
    void nextBtnOnAction(ActionEvent event) {

    }

    @FXML
    void playBtnOnAction(ActionEvent event) {

    }

    @FXML
    void prevBtnOnAction(ActionEvent event) {

    }

    @FXML
    void selectMediaBtnOnAction(ActionEvent event) {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select Media");
            FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Video Files", "*.mp4", "*.avi", "*.mkv", "*.mov", "*.mp3", "*.wav", "*.ogg");
            fileChooser.getExtensionFilters().add(filter);
            File selectedFile = fileChooser.showOpenDialog(null);

            if (selectedFile != null) {
                String url = selectedFile.toURI().toString();

                media = new Media(url);
                mediaPlayer = new MediaPlayer(media);

                Scene scene = mediaView.getScene();
                mediaView.fitWidthProperty().bind(scene.widthProperty());
                mediaView.fitHeightProperty().bind(scene.heightProperty());

                mediaView.setMediaPlayer(mediaPlayer);
                mediaPlayer.setAutoPlay(true);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
