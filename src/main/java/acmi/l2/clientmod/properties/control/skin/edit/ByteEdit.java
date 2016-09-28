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

import acmi.l2.clientmod.properties.control.EditorContext;
import acmi.l2.clientmod.properties.control.IEdit;
import acmi.l2.clientmod.properties.control.IntSliderEditor;
import acmi.l2.clientmod.unreal.core.ByteProperty;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.Region;

/**
 * @author PointerRage
 *
 */
public class ByteEdit implements IEdit {
	private final static ByteEdit instance = new ByteEdit();
	public static ByteEdit getInstance() {
		return instance;
	}
	
	private ByteEdit() {
	}

	@Override
	public Region create(EditorContext context) {
		ByteProperty byteProperty = (ByteProperty) context.getTemplate();
		if (byteProperty.enumType != null) {
			ComboBox<String> cb = new ComboBox<>();
			cb.getItems().addAll(byteProperty.enumType.values);
			cb.getSelectionModel().select((Integer) context.getProperty().get());
			cb.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
				context.getProperty().setValue(newValue);
			});
			return cb;
		} else {
			IntSliderEditor editor = new IntSliderEditor(0, 255, (Integer) context.getProperty().get());
			editor.valueProperty().bindBidirectional((javafx.beans.property.Property) context.getProperty());
			return editor;
		}
	}
}
