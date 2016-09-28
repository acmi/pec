package acmi.l2.clientmod.properties.control.skin.edit;

import java.util.List;

import acmi.l2.clientmod.properties.control.EditorContext;
import acmi.l2.clientmod.properties.control.IEdit;
import acmi.l2.clientmod.properties.control.skin.PropertiesEditorDefaultSkin;
import acmi.l2.clientmod.unreal.core.ArrayProperty;
import acmi.l2.clientmod.unreal.properties.PropertiesUtil;
import javafx.beans.property.ObjectProperty;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

/**
 * @author PointerRage
 *
 */
public class ArrayEdit implements IEdit {
	private final static ArrayEdit instance = new ArrayEdit();
	public static ArrayEdit getInstance() {
		return instance;
	}
	
	private ArrayEdit() {
	}

	@Override
	public Region create(EditorContext context) {
		TreeItem<ObjectProperty<Object>> item = context.getTreeTableRow().getTreeItem();
        List<Object> list = (List<Object>) item.getValue().get();
        Button empty = new Button("Empty");
        empty.setMinWidth(Region.USE_PREF_SIZE);
        empty.setOnAction(event -> {
            list.clear();
            item.getChildren().clear();
        });
        Button add = new Button("Add");
        add.setMinWidth(Region.USE_PREF_SIZE);
        add.setOnAction(event -> {
            Object value = PropertiesUtil.defaultValue(
            		((ArrayProperty) context.getTemplate()).inner, 
            		null, 
            		context.getPropertiesEditor().getSerializer(), 
            		context.getPropertiesEditor().getUnrealPackage()
            );
            list.add(value);
            item.getChildren().clear();
            item.getChildren().addAll(PropertiesEditorDefaultSkin.fillArrayTree(
            		null, 
            		(ArrayProperty) context.getTemplate(), 
            		context.getTemplate().entry.getObjectName().getName(), 
            		list, 
            		context.getPropertiesEditor().getUnrealPackage(), 
            		context.getPropertiesEditor().getSerializer(), 
            		context.getPropertiesEditor().getEditableOnly(), 
            		context.getPropertiesEditor().getHideCategories())
            );
        });
        GridPane pane = new GridPane();
        pane.add(new Label("..."), 0, 0);
        pane.add(empty, 2, 0);
        pane.add(add, 3, 0);
        pane.getColumnConstraints().addAll(
                new ColumnConstraints() {{
                    setMinWidth(0);
                }},
                new ColumnConstraints() {{
                    setHgrow(Priority.ALWAYS);
                }},
                new ColumnConstraints(),
                new ColumnConstraints());
        return pane;
	}

}
