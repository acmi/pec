package acmi.l2.clientmod.properties.control;

import acmi.l2.clientmod.unreal.core.Property;
import javafx.beans.property.ObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.TreeTableRow;

/**
 * @author PointerRage
 *
 */
public class EditorContext {
	private final PropertiesEditor propertiesEditor;
	private final TreeTableRow<ObjectProperty<Object>> treeTableRow;
	private final ObjectProperty<Object> property;
	private final Property template;
	private Node editorNode;
	
	public EditorContext(PropertiesEditor propertiesEditor, TreeTableRow<ObjectProperty<Object>> treeTableRow, ObjectProperty<Object> property, Property template) {
		this.propertiesEditor = propertiesEditor;
		this.treeTableRow = treeTableRow;
		this.property = property;
		this.template = template;
	}

	public PropertiesEditor getPropertiesEditor() {
		return propertiesEditor;
	}

	public TreeTableRow<ObjectProperty<Object>> getTreeTableRow() {
		return treeTableRow;
	}

	public ObjectProperty<Object> getProperty() {
		return property;
	}

	public Property getTemplate() {
		return template;
	}

	public Node getEditorNode() {
		return editorNode;
	}

	public void setEditorNode(Node editorNode) {
		this.editorNode = editorNode;
	}
}
