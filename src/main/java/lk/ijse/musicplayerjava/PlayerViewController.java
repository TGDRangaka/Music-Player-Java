package lk.ijse.musicplayerjava;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Stream;

public class PlayerViewController implements Initializable {

    @FXML
    private ScrollPane listScrollPane;

    @FXML
    private VBox listVBox;
    @FXML
    private ImageView playPauseImg;

    private Media media;
    private MediaPlayer mediaPlayer;
    private String lastChooserLocation = "/E:/Downloads";
    private FileChooser fileChooser;

    private boolean isPlayed = false;
    private int index = -1;
    private List<String> audioPaths = new ArrayList<>();
    private List<Button> audioListBtns = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        fileChooser = new FileChooser();
        fileChooser.setTitle("Select Media");
        fileChooser.setInitialDirectory(new File(lastChooserLocation));
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Audio Files",  "*.mp3", "*.wav", "*.ogg");
        fileChooser.getExtensionFilters().add(filter);
    }

    @FXML
    void nextBtnOnAction(ActionEvent event) {
        if(++index  >= audioPaths.size()){
            index = 0;
        }
        if(isPlayed){
            mediaPlayer.stop();
            isPlayed = false;
        }
        media = new Media(audioPaths.get(index));
        mediaPlayer = new MediaPlayer(media);
        playBtnOnAction(new ActionEvent());
    }

    @FXML
    void playBtnOnAction(ActionEvent event) {
        if(isPlayed){
            mediaPlayer.pause();
            playPauseImg.setImage(new Image(getClass().getResource("/img/play.png").toExternalForm()));
            isPlayed = false;
        }else{
            mediaPlayer.play();
            playPauseImg.setImage(new Image(getClass().getResource("/img/pause.png").toExternalForm()));
            isPlayed = true;
        }
    }

    @FXML
    void prevBtnOnAction(ActionEvent event) {
        if(--index  <= -1){
            index = audioPaths.size() - 1;
        }
        if(isPlayed){
            mediaPlayer.stop();
            isPlayed = false;
        }
        media = new Media(audioPaths.get(index));
        mediaPlayer = new MediaPlayer(media);
        playBtnOnAction(new ActionEvent());
    }

    @FXML
    void selectMediaBtnOnAction(ActionEvent event) {
        try {
            File selectedFile = fileChooser.showOpenDialog(null);

            if (selectedFile != null) {
                if(isPlayed){
                    mediaPlayer.stop();
                    isPlayed = false;
                }
                String url = selectedFile.toURI().toString();

                setLastChooserLocation(url);

                media = new Media(url);
                mediaPlayer = new MediaPlayer(media);

                playBtnOnAction(new ActionEvent());

                readAllItemsInFolder(lastChooserLocation);

                setList();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @FXML
    void listBtnOnAction(ActionEvent event) {
        listScrollPane.setVisible(!listScrollPane.isVisible());
    }

    private void setList(){
        for(int i = 0; i < audioPaths.size(); i++){
            String[] split = audioPaths.get(i).split("/");
            Button btn = new Button(split[split.length - 1].replace("%20", " "));
            btn.setPrefWidth(354);
            btn.setAlignment(Pos.CENTER_LEFT);

            final int indx = i;
            btn.setOnAction(e -> {
                if(isPlayed){
                    mediaPlayer.stop();
                    isPlayed = false;
                }
                media = new Media(audioPaths.get(indx));
                mediaPlayer = new MediaPlayer(media);
                playBtnOnAction(new ActionEvent());
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
        if(!newLocation.equals(lastChooserLocation)){
            audioPaths.clear();
            audioListBtns.clear();
            listVBox.getChildren().clear();
            index = 0;
        }
        lastChooserLocation = String.join("/",new ArrayList<>(Arrays.asList(urlSplit))).substring(1);
        fileChooser.setInitialDirectory(new File(lastChooserLocation));
    }
}
