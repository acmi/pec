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

import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import acmi.l2.clientmod.io.UnrealPackage;
import acmi.l2.clientmod.properties.control.EditorContext;
import acmi.l2.clientmod.properties.control.PropertiesEditor;
import acmi.l2.clientmod.unreal.core.ClassProperty;
import acmi.l2.clientmod.unreal.core.Property;
import acmi.util.AutoCompleteComboBox;
import javafx.beans.property.ObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;

/**
 * @author PointerRage
 *
 */
public class ClassEdit extends AbstractCustomizeEdit {
	private final static Logger log = Logger.getLogger(ClassEdit.class.getName());
	private final static ClassEdit instance = new ClassEdit();
	public static ClassEdit getInstance() {
		return instance;
	}

	@Override
	public Region create(EditorContext context) {
		final Property template = context.getTemplate();
		final PropertiesEditor propertiesEditor = context.getPropertiesEditor();
		final ObjectProperty<Object> property = context.getProperty();
		
		String type = ((ClassProperty) template).clazz.getFullName();
        ObservableList<UnrealPackage.Entry> entries = FXCollections.observableArrayList();
        Predicate<UnrealPackage.Entry> filter = entry -> entry.getFullClassName().equalsIgnoreCase("Core.Class") && propertiesEditor.getSerializer().isSubclass(type, entry.getObjectFullName());
        entries.addAll(propertiesEditor.getUnrealPackage().getImportTable().parallelStream().filter(filter).collect(Collectors.toList()));
        entries.addAll(propertiesEditor.getUnrealPackage().getExportTable().parallelStream().filter(filter).collect(Collectors.toList()));
        Collections.sort(entries, (e1, e2) -> e1.getObjectFullName().compareToIgnoreCase(e2.getObjectFullName()));
        if (entries.isEmpty()) {
            int val = (Integer) property.get();
            if (val != 0) {
                log.warning(() -> "No entries found for " + template);

                entries.add(propertiesEditor.getUnrealPackage().objectReference(val));
            }
        }
        entries.add(0, new UnrealPackage.Entry(null, 0, 0, 0) {
            @Override
            public String getObjectInnerFullName() {
                return "None";
            }

            @Override
            public String getFullClassName() {
                return type;
            }

            @Override
            public int getObjectReference() {
                return 0;
            }

            @Override
            public List getTable() {
                return null;
            }
        });
        
        final GridPane pane = new GridPane();
        
        ComboBox<UnrealPackage.Entry> cb = new ComboBox<>(entries);
        AutoCompleteComboBox.autoCompleteComboBox(cb, AutoCompleteComboBox.AutoCompleteMode.CONTAINING);
        UnrealPackage.Entry v = cb.getItems()
                .parallelStream()
                .filter(entry -> entry.getObjectReference() == (Integer) property.get())
                .findAny()
                .orElseThrow(() -> new IllegalStateException("Entry not found: " + property.get() + "(" + propertiesEditor.getUnrealPackage().objectReference((Integer) property.get()) + ")"));
        cb.getSelectionModel().select(v);
        cb.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            property.setValue(newValue == null ? 0 : newValue.getObjectReference());
        });
        pane.add(cb, 0, 0);
        
        context.setEditorNode(cb);
        for(int i = 0, paneCounter = 1; i < customElements.size(); i++) {
        	final Function<EditorContext, Node> function = customElements.get(i);
        	Node node = function.apply(context);
        	if(node != null) {
        		pane.add(node, paneCounter++, 0);
        	}
        }
        
        return pane;
	}

}
