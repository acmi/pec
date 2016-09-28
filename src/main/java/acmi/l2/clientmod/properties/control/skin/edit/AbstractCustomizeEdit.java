package acmi.l2.clientmod.properties.control.skin.edit;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import acmi.l2.clientmod.properties.control.EditorContext;
import acmi.l2.clientmod.properties.control.IEdit;
import javafx.scene.Node;

/**
 * @author PointerRage
 *
 */
abstract class AbstractCustomizeEdit implements IEdit {
	protected final List<Function<EditorContext, Node>> customElements = new ArrayList<>(0);
	
	public void addElement(Function<EditorContext, Node> function) {
		customElements.add(function);
	}
}
