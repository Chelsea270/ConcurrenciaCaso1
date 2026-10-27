module org.test.concurrenciacaso1 {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.test.concurrenciacaso1 to javafx.fxml;
    exports org.test.concurrenciacaso1;
    exports org.test.concurrenciacaso1.model;
    opens org.test.concurrenciacaso1.model to javafx.fxml;
}