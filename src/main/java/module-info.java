module com.example.security {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.security to javafx.fxml;
    exports com.example.security;
}