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

import acmi.l2.clientmod.properties.control.PropertiesEditor;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.MapChangeListener;
import javafx.scene.Node;
import javafx.scene.control.Skin;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;

public abstract class TreeBasedPropertiesEditorSkin implements Skin<PropertiesEditor> {
    private PropertiesEditor editor;
    private Node node;

    public TreeBasedPropertiesEditorSkin(PropertiesEditor editor) {
        this.editor = editor;
        this.node = build();
    }

    private Node build() {
        TreeTableColumn<ObjectProperty<Object>, String> propertyCol = new TreeTableColumn<>("Property");
        propertyCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("name"));
        TreeTableColumn<ObjectProperty<Object>, Object> valueCol = new TreeTableColumn<>("Value");
        valueCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("value"));

        valueCol.setCellFactory(param -> new PropertyValueCell(this.getSkinnable()));

        DoubleProperty tableContentWidth = new SimpleDoubleProperty();
        propertyCol.setPrefWidth(200);
        valueCol.prefWidthProperty().bind(Bindings.subtract(tableContentWidth, propertyCol.widthProperty()));

        TreeTableView<ObjectProperty<Object>> table = new TreeTableView<>();
        table.getColumns().add(propertyCol);
        table.getColumns().add(valueCol);
        table.getProperties().addListener((MapChangeListener<Object, Object>) c -> {
            if (c.wasAdded() && "TableView.contentWidth".equals(c.getKey())) {
                if (c.getValueAdded() instanceof Number) {
                    tableContentWidth.setValue((Number) c.getValueAdded());
                }
            }
        });

        table.setRoot(new TreeItem<>(null));
        table.setShowRoot(false);

        editor.propertyListProperty().addListener((observable, oldValue, newValue) -> {
            table.getRoot().getChildren().clear();

            if (newValue == null)
                return;

            buildTree(table.getRoot());
        });

        return table;
    }

    protected abstract void buildTree(TreeItem<ObjectProperty<Object>> root);

    @Override
    public PropertiesEditor getSkinnable() {
        return editor;
    }

    @Override
    public Node getNode() {
        return node;
    }

    @Override
    public void dispose() {
        editor = null;
        node = null;
    }
}
