module org.test.concurrenciacaso1 {
    requires javafx.controls;
    requires javafx.fxml;

    opens org.test.concurrenciacaso1 to javafx.fxml;
    exports org.test.concurrenciacaso1;

    opens org.test.concurrenciacaso1.controller to javafx.fxml;
    exports org.test.concurrenciacaso1.controller;

    exports org.test.concurrenciacaso1.model;
}