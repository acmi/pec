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
