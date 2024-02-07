package lk.ijse.musicplayerjava;


import javafx.animation.RotateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Stream;

public class PlayerViewController implements Initializable {

    @FXML
    private Slider slider;

    @FXML
    private Label currentTimeLbl;

    @FXML
    private Label totDurationLbl;

    @FXML
    private ImageView playPauseImg;

    @FXML
    private ScrollPane listScrollPane;

    @FXML
    private VBox listVBox;

    @FXML
    private Label songNameLabel;

    @FXML
    private ImageView discImg;

    private Media media;
    private MediaPlayer mediaPlayer;
    private String lastChooserLocation = "/E:/Downloads";
    private FileChooser fileChooser;

    private boolean isPlayed = false;
    private int index = -1;
    private List<String> audioPaths = new ArrayList<>();
    private List<Button> audioListBtns = new ArrayList<>();
    private Duration totDuration;
    private RotateTransition rotateTransition;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        fileChooser = new FileChooser();
        fileChooser.setTitle("Select Media");
        fileChooser.setInitialDirectory(new File(lastChooserLocation));
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Audio Files",  "*.mp3", "*.wav", "*.ogg");
        fileChooser.getExtensionFilters().add(filter);

        rotateTransition = new RotateTransition(Duration.seconds(30), discImg);
        rotateTransition.setByAngle(360);
        rotateTransition.setCycleCount(RotateTransition.INDEFINITE);
    }

    @FXML
    void nextBtnOnAction(ActionEvent event) {
        if(audioPaths.size() < 1){
            new Alert(Alert.AlertType.WARNING, "No Playlist to be found").show();
            return;
        }
        if(++index  >= audioPaths.size()){
            index = 0;
        }
        setAudioToPlayer(audioPaths.get(index));
    }

    @FXML
    void playBtnOnAction(ActionEvent event) {
        if(mediaPlayer == null){
            new Alert(Alert.AlertType.WARNING, "No Audio to be found").show();
            return;
        }
        if(isPlayed){
            mediaPlayer.pause();
            playPauseImg.setImage(new Image(getClass().getResource("/img/play.png").toExternalForm()));
            rotateTransition.pause();
            isPlayed = false;
        }else{
            mediaPlayer.play();
            playPauseImg.setImage(new Image(getClass().getResource("/img/pause.png").toExternalForm()));
            rotateTransition.play();
            isPlayed = true;
        }
    }

    @FXML
    void prevBtnOnAction(ActionEvent event) {
        if(audioPaths.size() < 1){
            new Alert(Alert.AlertType.WARNING, "No Playlist to be found").show();
            return;
        }
        if(--index  <= -1){
            index = audioPaths.size() - 1;
        }
        setAudioToPlayer(audioPaths.get(index));
    }

    @FXML
    void selectMediaBtnOnAction(ActionEvent event) {
        try {
            File selectedFile = fileChooser.showOpenDialog(null);

            if (selectedFile != null) {
                String url = selectedFile.toURI().toString();

                setAudioToPlayer(url);

                setLastChooserLocation(url);
                readAllItemsInFolder(lastChooserLocation);
                setList();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @FXML
    void sliderOnMousePressed(MouseEvent event) {
        mediaPlayer.seek(Duration.seconds(slider.getValue()));
    }

    @FXML
    void sliderOnMouseDragged(MouseEvent event) {
        mediaPlayer.seek(Duration.seconds(slider.getValue()));
    }

    @FXML
    void listBtnOnAction(ActionEvent event) {
        listScrollPane.setVisible(!listScrollPane.isVisible());
    }

    private void setAudioToPlayer(String url){
        if(isPlayed){
            mediaPlayer.stop();
            isPlayed = false;
        }
        setAudioName(url);

        media = new Media(url);
        mediaPlayer = new MediaPlayer(media);

        mediaPlayer.setOnEndOfMedia(() -> {
            nextBtnOnAction(new ActionEvent());
        });

        mediaPlayer.setOnReady(() -> {
            totDuration = media.getDuration();
            slider.setMax(totDuration.toSeconds());
            int minutes = (int) totDuration.toSeconds() / 60;
            int seconds = (int) totDuration.toSeconds() % 60;
            totDurationLbl.setText(String.format("%02d:%02d", minutes, seconds));
        });

        mediaPlayer.currentTimeProperty().addListener((observableValue, oldValue, newValue) -> {
            slider.setValue(newValue.toSeconds());
            int minutes = (int) newValue.toSeconds() / 60;
            int seconds = (int) newValue.toSeconds() % 60;
            currentTimeLbl.setText(String.format("%02d:%02d", minutes, seconds));
        });


        playBtnOnAction(new ActionEvent());
    }

    private void setAudioName(String url){
        String[] split = url.split("/");
        songNameLabel.setText(split[split.length - 1].replace("%20", " "));
    }

    private void setList(){
        for(int i = 0; i < audioPaths.size(); i++){
            String[] split = audioPaths.get(i).split("/");
            Button btn = new Button(split[split.length - 1].replace("%20", " "));
            btn.setPrefWidth(354);
            btn.setAlignment(Pos.CENTER_LEFT);

            final int indx = i;
            btn.setOnAction(e -> {
                setAudioToPlayer(audioPaths.get(indx));
                listBtnOnAction(new ActionEvent());
            });

            listVBox.getChildren().add(btn);

        }
    }

    private void readAllItemsInFolder(String dir) throws IOException {
        try (Stream<Path> paths = Files.walk(Paths.get(dir), 1)) {
            paths
                    .filter(Files::isRegularFile)
                    .forEach(path -> {
                        String[] dotSplit = path.toString().split("\\.");
                        String type = dotSplit[dotSplit.length - 1];
                        if(type.equals("mp3") || type.equals("wav") || type.equals("ogg")){
                            audioPaths.add(path.toUri().toString());
                        }
                    });
        }
    }

    private void setLastChooserLocation(String url){
        url = url.replaceAll("%20", " ");
        String[] urlSplit = url.split("/");
        urlSplit[0] = "";
        urlSplit[urlSplit.length - 1] = "";
        String newLocation = String.join("/",new ArrayList<>(Arrays.asList(urlSplit))).substring(1);

        audioPaths.clear();
        audioListBtns.clear();
        listVBox.getChildren().clear();
        index = 0;

        lastChooserLocation = String.join("/",new ArrayList<>(Arrays.asList(urlSplit))).substring(1);
        fileChooser.setInitialDirectory(new File(lastChooserLocation));
    }
}
