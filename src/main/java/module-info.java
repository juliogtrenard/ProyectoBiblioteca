module es.juliogtrenard.proyectobiblioteca {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.slf4j;


    opens es.juliogtrenard.proyectobiblioteca to javafx.fxml;
    exports es.juliogtrenard.proyectobiblioteca;
}