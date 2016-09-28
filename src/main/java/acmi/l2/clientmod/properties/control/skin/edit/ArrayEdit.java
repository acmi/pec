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
package acmi.l2.clientmod.properties.control.skin.edit;

import java.util.List;

import acmi.l2.clientmod.properties.control.EditorContext;
import acmi.l2.clientmod.properties.control.IEdit;
import acmi.l2.clientmod.properties.control.skin.PropertiesEditorDefaultSkin;
import acmi.l2.clientmod.unreal.core.ArrayProperty;
import acmi.l2.clientmod.unreal.properties.PropertiesUtil;
import javafx.beans.property.ObjectProperty;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

/**
 * @author PointerRage
 *
 */
public class ArrayEdit implements IEdit {
	private final static ArrayEdit instance = new ArrayEdit();
	public static ArrayEdit getInstance() {
		return instance;
	}
	
	private ArrayEdit() {
	}

	@Override
	public Region create(EditorContext context) {
		TreeItem<ObjectProperty<Object>> item = context.getTreeTableRow().getTreeItem();
        List<Object> list = (List<Object>) item.getValue().get();
        Button empty = new Button("Empty");
        empty.setMinWidth(Region.USE_PREF_SIZE);
        empty.setOnAction(event -> {
            list.clear();
            item.getChildren().clear();
        });
        Button add = new Button("Add");
        add.setMinWidth(Region.USE_PREF_SIZE);
        add.setOnAction(event -> {
            Object value = PropertiesUtil.defaultValue(
            		((ArrayProperty) context.getTemplate()).inner, 
            		null, 
            		context.getPropertiesEditor().getSerializer(), 
            		context.getPropertiesEditor().getUnrealPackage()
            );
            list.add(value);
            item.getChildren().clear();
            item.getChildren().addAll(PropertiesEditorDefaultSkin.fillArrayTree(
            		null, 
            		(ArrayProperty) context.getTemplate(), 
            		context.getTemplate().entry.getObjectName().getName(), 
            		list, 
            		context.getPropertiesEditor().getUnrealPackage(), 
            		context.getPropertiesEditor().getSerializer(), 
            		context.getPropertiesEditor().getEditableOnly(), 
            		context.getPropertiesEditor().getHideCategories())
            );
        });
        GridPane pane = new GridPane();
        pane.add(new Label("..."), 0, 0);
        pane.add(empty, 2, 0);
        pane.add(add, 3, 0);
        pane.getColumnConstraints().addAll(
                new ColumnConstraints() {{
                    setMinWidth(0);
                }},
                new ColumnConstraints() {{
                    setHgrow(Priority.ALWAYS);
                }},
                new ColumnConstraints(),
                new ColumnConstraints());
        return pane;
	}

}
