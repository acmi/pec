package acmi.l2.clientmod.properties.control.skin.edit;

import acmi.l2.clientmod.properties.control.EditorContext;
import acmi.l2.clientmod.properties.control.IEdit;
import javafx.scene.control.TextField;
import javafx.scene.layout.Region;

/**
 * @author PointerRage
 *
 */
public class StrEdit implements IEdit {
	private final static StrEdit instance = new StrEdit();
	public static StrEdit getInstance() {
		return instance;
	}
	
	private StrEdit() {
	}

	@Override
	public Region create(EditorContext context) {
		TextField tf = new TextField(String.valueOf(context.getProperty().get()));
        tf.textProperty().addListener((observable, oldValue, newValue) -> {
        	context.getProperty().setValue(newValue);
        });
        return tf;
	}

}
