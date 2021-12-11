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

    opens me.t3sl4.textfileencoder to javafx.fxml;
    exports me.t3sl4.textfileencoder;
    exports me.t3sl4.textfileencoder.utils;
    opens me.t3sl4.textfileencoder.utils to javafx.fxml;
}