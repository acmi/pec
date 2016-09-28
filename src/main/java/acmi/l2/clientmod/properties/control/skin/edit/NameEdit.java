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
