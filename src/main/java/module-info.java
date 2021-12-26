module me.t3sl4.textfileencoder {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires commons.collections;
    requires org.apache.commons.codec;
    requires java.desktop;

    opens me.t3sl4.textfileencoder to javafx.fxml;
    exports me.t3sl4.textfileencoder;
    exports me.t3sl4.textfileencoder.Utils;
    opens me.t3sl4.textfileencoder.Utils to javafx.fxml;
    exports me.t3sl4.textfileencoder.Controllers;
    opens me.t3sl4.textfileencoder.Controllers to javafx.fxml;
    exports me.t3sl4.textfileencoder.Main;
    opens me.t3sl4.textfileencoder.Main to javafx.fxml;
}