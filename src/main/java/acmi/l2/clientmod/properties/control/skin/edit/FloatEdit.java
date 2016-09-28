package acmi.l2.clientmod.properties.control.skin.edit;

import acmi.l2.clientmod.properties.control.EditorContext;
import acmi.l2.clientmod.properties.control.IEdit;
import javafx.scene.control.TextField;
import javafx.scene.layout.Region;

/**
 * @author PointerRage
 *
 */
public class FloatEdit implements IEdit {
	private final static FloatEdit instance = new FloatEdit();
	public static FloatEdit getInstance() {
		return instance;
	}
	
	private FloatEdit() {
	}

	@Override
	public Region create(EditorContext context) {
		TextField tf = new TextField(String.valueOf(context.getProperty().get()));
        tf.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
            	context.getProperty().setValue(Float.parseFloat(newValue));
            } catch (NumberFormatException ignore) {
            }
        });
        return tf;
	}

}
