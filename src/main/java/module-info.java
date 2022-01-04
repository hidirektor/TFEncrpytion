module me.t3sl4.textfileencoder {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires java.desktop;

    opens me.t3sl4.tfencryption to javafx.fxml;
    exports me.t3sl4.tfencryption;
    exports me.t3sl4.tfencryption.Utils;
    opens me.t3sl4.tfencryption.Utils to javafx.fxml;
    exports me.t3sl4.tfencryption.Controllers;
    opens me.t3sl4.tfencryption.Controllers to javafx.fxml;
    exports me.t3sl4.tfencryption.Main;
    opens me.t3sl4.tfencryption.Main to javafx.fxml;
}