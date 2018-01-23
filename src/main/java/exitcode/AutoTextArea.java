package exitcode;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;

public class AutoTextArea extends StackPane {
    
        private Label autoLabel;
        private TextArea autoTextArea;
    
    
        public AutoTextArea(String text) {

            this.autoLabel = new Label(text);
            this.autoTextArea = new TextArea(text);
    
            this.autoLabel.setWrapText(true);
            this.autoTextArea.setWrapText(true);
            this.autoTextArea.setEditable(false);
            this.autoLabel.setStyle("-fx-text-fill: transparent;");
    
            this.autoLabel.setPadding(new Insets(0, 10, 0, 11)); //Top, Right, Bottom, Left
            this.autoTextArea.setPadding(new Insets(2, 0, 0, 0)); //Top, Right, Bottom, Left
    
            this.autoLabel.prefWidthProperty().bind(this.widthProperty());
    
            this.autoTextArea.prefWidthProperty().bind(this.autoLabel.widthProperty().add(14));
            this.autoTextArea.prefHeightProperty().bind(this.autoLabel.heightProperty().add(16));
    
            this.getChildren().addAll(this.autoLabel, this.autoTextArea);

            this.autoLabel.textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(final ObservableValue<? extends String> observable, final String oldValue, final String newValue) {
                    updateFromLabel();
                }
            });
        }
    
        public AutoTextArea() {
            this.autoLabel = new Label();
            this.autoTextArea = new TextArea();
            
            this.autoLabel.setWrapText(true);
            this.autoTextArea.setWrapText(true);
            this.autoTextArea.setEditable(false);
            this.autoLabel.setStyle("-fx-text-fill: transparent;");
    
            this.autoLabel.setPadding(new Insets(0, 10, 0, 11)); //Top, Right, Bottom, Left
            this.autoTextArea.setPadding(new Insets(2, 0, 0, 0)); //Top, Right, Bottom, Left
    
            this.autoLabel.prefWidthProperty().bind(this.widthProperty());
    
            this.autoTextArea.prefWidthProperty().bind(this.autoLabel.widthProperty().add(14));
            this.autoTextArea.prefHeightProperty().bind(this.autoLabel.heightProperty().add(16));
    
            this.getChildren().addAll(this.autoLabel, this.autoTextArea);

            this.autoLabel.textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(final ObservableValue<? extends String> observable, final String oldValue, final String newValue) {
                    updateFromLabel();
                }
            });
        }
        
        private void updateFromLabel() {
            this.autoTextArea.setText(this.autoLabel.getText());
        }
    
        public Label getLabel() {
            return this.autoLabel;
        }

        public void setFont(Font value) {
            this.autoLabel.setFont(value);
            this.autoTextArea.setFont(value);
        }

        public Label get() {
            return getLabel();
        }
    
        public TextArea getTextArea() {
            return this.autoTextArea;
        }
    
        public void clear() {
            //this.autoTextArea.clear();
            this.autoLabel.setText("");
        }
    
        public void setText(String text) {
            //this.autoTextArea.setText(text);
            this.autoLabel.setText(text);
        }
    
        public void appendText(String text) {
            this.setText(this.getText() + text);
        }
    
        public String getText() {
            return this.autoLabel.getText();
        }
    
    }