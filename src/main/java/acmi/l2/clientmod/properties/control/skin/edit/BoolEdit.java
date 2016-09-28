package acmi.l2.clientmod.properties.control.skin.edit;

import acmi.l2.clientmod.properties.control.EditorContext;
import acmi.l2.clientmod.properties.control.IEdit;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.Region;

/**
 * @author PointerRage
 *
 */
public class BoolEdit implements IEdit {
	private final static BoolEdit instance = new BoolEdit();
	public static BoolEdit getInstance() {
		return instance;
	}
	
	private BoolEdit() {
	}

	@Override
	public Region create(EditorContext context) {
		CheckBox cb = new CheckBox();
        cb.setSelected((Boolean) context.getProperty().getValue());
        cb.selectedProperty().addListener((observable, oldValue, newValue) -> {
        	context.getProperty().setValue(newValue);
        });
        return cb;
	}

}
