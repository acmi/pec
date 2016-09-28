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
