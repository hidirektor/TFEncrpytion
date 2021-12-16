module me.t3sl4.textfileencoderdemo.tfencoderdemo {
    requires javafx.controls;
    requires javafx.fxml;


    opens me.t3sl4.textfileencoderdemo.tfencoderdemo to javafx.fxml;
    exports me.t3sl4.textfileencoderdemo.tfencoderdemo;
}