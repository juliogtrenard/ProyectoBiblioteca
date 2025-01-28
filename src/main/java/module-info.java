module es.juliogtrenard.proyectobiblioteca {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.slf4j;
    requires net.sf.jasperreports.core;


    opens es.juliogtrenard.proyectobiblioteca to javafx.fxml;
    exports es.juliogtrenard.proyectobiblioteca;
    opens es.juliogtrenard.proyectobiblioteca.controller to javafx.fxml;
    exports es.juliogtrenard.proyectobiblioteca.controller;
    opens es.juliogtrenard.proyectobiblioteca.model to javafx.fxml;
    exports es.juliogtrenard.proyectobiblioteca.model;
}