module org.text_processor.auto_text_processor {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires java.logging;

    opens org.text_processor.auto_text_processor to javafx.fxml;
    exports org.text_processor.auto_text_processor;
    exports org.text_processor.auto_text_processor.controllers;
    opens org.text_processor.auto_text_processor.controllers to javafx.fxml;
}