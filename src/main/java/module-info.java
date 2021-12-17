module me.t3sl4.textfileencoderdemo.tfencoderdemo {
    requires javafx.controls;
    requires javafx.fxml;


    opens me.t3sl4.textfileencoderdemo to javafx.fxml;
    exports me.t3sl4.textfileencoderdemo;
}