module lk.ijse.musicplayerjava {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;


    opens lk.ijse.musicplayerjava to javafx.fxml;
    exports lk.ijse.musicplayerjava;
}