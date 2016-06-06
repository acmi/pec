/*
 * Copyright (c) 2016 acmi
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package acmi.l2.clientmod.properties.control.skin;

import acmi.l2.clientmod.properties.control.IntSliderEditor;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Skin;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.util.StringConverter;

public class IntSliderEditorSkin implements Skin<IntSliderEditor> {
    private IntSliderEditor control;
    private Node node;
    private final StringConverter<Number> conv = new StringConverter<Number>() {
        @Override
        public String toString(Number object) {
            return object.toString();
        }

        @Override
        public Number fromString(String string) {
           return Integer.parseInt(string);
        }
    };

    public IntSliderEditorSkin(IntSliderEditor control) {
        this.control = control;
        this.node = build();
    }

    private Node build() {
        Slider slider = new Slider();
        slider.minProperty().bind(control.minProperty());
        slider.maxProperty().bind(control.maxProperty());
        slider.valueProperty().bindBidirectional(control.valueProperty());
        slider.setMajorTickUnit(Math.round((control.getMax() - control.getMin()) / 8f));
        slider.setMinorTickCount(0);
        slider.setShowTickMarks(true);

        TextField tf = new TextField();
        tf.setAlignment(Pos.CENTER_RIGHT);
        tf.setPrefColumnCount(Math.max(
                String.valueOf(control.getMin()).length(),
                String.valueOf(control.getMax()).length()
        ));
        tf.textProperty().bindBidirectional(control.valueProperty(), conv);
        tf.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue)
                tf.textProperty().bindBidirectional(control.valueProperty(), conv);
        });

        GridPane pane = new GridPane();
        pane.add(tf, 0, 0);
        pane.add(slider, 1, 0);
        pane.getColumnConstraints().addAll(
                new ColumnConstraints() {{
                    setMinWidth(Region.USE_PREF_SIZE);
                    setHgrow(Priority.NEVER);
                }},
                new ColumnConstraints() {{
                    setHgrow(Priority.ALWAYS);
                }}
        );
        pane.getRowConstraints().addAll(
                new RowConstraints(){{
                    setVgrow(Priority.ALWAYS);
                }}
        );

        return pane;
    }

    @Override
    public IntSliderEditor getSkinnable() {
        return control;
    }

    @Override
    public Node getNode() {
        return node;
    }

    @Override
    public void dispose() {
        this.control = null;
        this.node = null;
    }
}
