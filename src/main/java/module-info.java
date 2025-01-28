module es.juliogtrenard.proyectobiblioteca {
    requires javafx.controls;
    requires javafx.fxml;


    opens es.juliogtrenard.proyectobiblioteca to javafx.fxml;
    exports es.juliogtrenard.proyectobiblioteca;
}