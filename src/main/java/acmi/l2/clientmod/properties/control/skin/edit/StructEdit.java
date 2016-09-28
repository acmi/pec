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
import java.util.function.Supplier;

import acmi.l2.clientmod.properties.control.EditorContext;
import acmi.l2.clientmod.properties.control.IEdit;
import acmi.l2.clientmod.properties.control.PropertiesEditor;
import acmi.l2.clientmod.properties.control.skin.PropertyValueCell;
import acmi.l2.clientmod.unreal.core.Property;
import acmi.l2.clientmod.unreal.core.StructProperty;
import acmi.l2.clientmod.unreal.properties.L2Property;
import javafx.beans.InvalidationListener;
import javafx.beans.property.ObjectProperty;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableRow;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;

/**
 * @author PointerRage
 *
 */
public class StructEdit implements IEdit {
	private final static StructEdit instance = new StructEdit();
	public static StructEdit getInstance() {
		return instance;
	}
	
	private StructEdit() {
	}

	@Override
	public Region create(EditorContext context) {
		final Property template = context.getTemplate();
		final PropertiesEditor propertiesEditor = context.getPropertiesEditor();
		final ObjectProperty<Object> property = context.getProperty();
		final TreeTableRow<ObjectProperty<Object>> treeTableRow = context.getTreeTableRow();
		
		List<L2Property> struct = (List<L2Property>) property.get();
        if (((StructProperty) template).struct.getFullName().equalsIgnoreCase("Core.Object.Color")) {
            java.util.function.Function<String, ObjectProperty<Object>> f = name -> treeTableRow
                    .getTreeItem()
                    .getChildren()
                    .stream()
                    .filter(ti -> ti.getValue().getName().equalsIgnoreCase(name))
                    .findAny()
                    .map(TreeItem::getValue)
                    .orElseThrow(() -> new IllegalThreadStateException("Color component not found: " + name));
            ObjectProperty<Object> a = f.apply("A");
            ObjectProperty<Object> r = f.apply("R");
            ObjectProperty<Object> g = f.apply("G");
            ObjectProperty<Object> b = f.apply("B");
            Supplier<Color> colorSupplier = () -> Color.rgb((Integer) r.get(), (Integer) g.get(), (Integer) b.get(), ((Integer) a.get()) / 255.0);
            ColorPicker cp = new ColorPicker(colorSupplier.get());
            InvalidationListener il = observable -> cp.setValue(colorSupplier.get());
            a.addListener(il);
            r.addListener(il);
            g.addListener(il);
            b.addListener(il);
            cp.valueProperty().addListener((observable, oldValue, newValue) -> {
                a.set((int) Math.round(255 * newValue.getOpacity()));
                r.set((int) Math.round(255 * newValue.getRed()));
                g.set((int) Math.round(255 * newValue.getGreen()));
                b.set((int) Math.round(255 * newValue.getBlue()));
            });
            return cp;
        } else {
            String text = struct == null ? "" : PropertyValueCell.inlineStruct(struct, propertiesEditor.getUnrealPackage(), propertiesEditor.getSerializer()).toString();
            return new Label(text);
        }
	}

}
