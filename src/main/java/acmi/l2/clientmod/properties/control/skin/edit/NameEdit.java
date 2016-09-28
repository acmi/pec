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

import java.util.stream.Collectors;

import acmi.l2.clientmod.io.UnrealPackage;
import acmi.l2.clientmod.properties.control.EditorContext;
import acmi.l2.clientmod.properties.control.IEdit;
import acmi.util.AutoCompleteComboBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.Region;

/**
 * @author PointerRage
 *
 */
public class NameEdit implements IEdit {
	private final static NameEdit instance = new NameEdit();
	public static NameEdit getInstance() {
		return instance;
	}
	
	private NameEdit() {
	}

	@Override
	public Region create(EditorContext context) {
		UnrealPackage.NameEntry noneEntry = context.getPropertiesEditor().getUnrealPackage().getNameTable()
                .parallelStream()
                .filter(nameEntry -> nameEntry.getName().equalsIgnoreCase("None"))
                .findAny()
                .orElseThrow(() -> new IllegalStateException("Name entry not found"));
        ObservableList<UnrealPackage.NameEntry> names = FXCollections.observableList(context.getPropertiesEditor().getUnrealPackage().getNameTable()
                .parallelStream()
                .sorted((e1, e2) -> e1 == noneEntry ? -1 : e2 == noneEntry ? 1 :
                        e1.getName().compareToIgnoreCase(e2.getName()))
                .collect(Collectors.toList()));
        ComboBox<UnrealPackage.NameEntry> cb = new ComboBox<>(names);
        AutoCompleteComboBox.autoCompleteComboBox(cb, AutoCompleteComboBox.AutoCompleteMode.CONTAINING);
        cb.getSelectionModel().select(names
                .parallelStream()
                .filter(nameEntry -> nameEntry.getIndex() == (Integer) context.getProperty().get())
                .findAny()
                .orElseThrow(() -> new IllegalStateException("Name entry not found")));
        cb.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null)
                newValue = noneEntry;
            context.getProperty().setValue(newValue.getIndex());
        });
        return cb;
	}

}
