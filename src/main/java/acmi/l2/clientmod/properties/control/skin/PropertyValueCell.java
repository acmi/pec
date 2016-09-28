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
package acmi.l2.clientmod.properties.control.skin;

import static acmi.l2.clientmod.properties.control.skin.PropertiesEditorDefaultSkin.fillArrayTree;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

import acmi.l2.clientmod.io.UnrealPackage;
import acmi.l2.clientmod.properties.control.EditorContext;
import acmi.l2.clientmod.properties.control.IEdit;
import acmi.l2.clientmod.properties.control.PropertiesEditor;
import acmi.l2.clientmod.properties.control.skin.edit.ArrayEdit;
import acmi.l2.clientmod.properties.control.skin.edit.BoolEdit;
import acmi.l2.clientmod.properties.control.skin.edit.ByteEdit;
import acmi.l2.clientmod.properties.control.skin.edit.ClassEdit;
import acmi.l2.clientmod.properties.control.skin.edit.FloatEdit;
import acmi.l2.clientmod.properties.control.skin.edit.IntEdit;
import acmi.l2.clientmod.properties.control.skin.edit.NameEdit;
import acmi.l2.clientmod.properties.control.skin.edit.ObjectEdit;
import acmi.l2.clientmod.properties.control.skin.edit.StrEdit;
import acmi.l2.clientmod.properties.control.skin.edit.StructEdit;
import acmi.l2.clientmod.unreal.UnrealSerializerFactory;
import acmi.l2.clientmod.unreal.core.ArrayProperty;
import acmi.l2.clientmod.unreal.core.BoolProperty;
import acmi.l2.clientmod.unreal.core.ByteProperty;
import acmi.l2.clientmod.unreal.core.ClassProperty;
import acmi.l2.clientmod.unreal.core.Enum;
import acmi.l2.clientmod.unreal.core.FloatProperty;
import acmi.l2.clientmod.unreal.core.IntProperty;
import acmi.l2.clientmod.unreal.core.NameProperty;
import acmi.l2.clientmod.unreal.core.Property;
import acmi.l2.clientmod.unreal.core.StrProperty;
import acmi.l2.clientmod.unreal.core.StructProperty;
import acmi.l2.clientmod.unreal.properties.L2Property;
import acmi.l2.clientmod.unreal.properties.PropertiesUtil;
import javafx.beans.property.ObjectProperty;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

public class PropertyValueCell extends TreeTableCell<ObjectProperty<Object>, Object> {
    private final PropertiesEditor properties;
    {
        setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
    }

    public PropertyValueCell(PropertiesEditor properties) {
        this.properties = properties;
    }

    public PropertiesEditor getPropertiesEditor() {
        return properties;
    }

    @Override
    protected void updateItem(Object item, boolean empty) {
        super.updateItem(item, empty);

        ObjectProperty<Object> property = getTreeTableRow().getItem();

        if (empty || property == null) {
            setGraphic(null);
            return;
        }

        Region editor = createEditor(property);
        if (editor != null) {
            editor.setMaxWidth(Double.MAX_VALUE);

            TreeItem<ObjectProperty<Object>> parent = getTreeTableRow().getTreeItem().getParent();
            if (parent != null &&
                    parent.getValue() != null &&
                    parent.getValue().getBean() instanceof ArrayProperty) {

                ArrayProperty parentProperty = (ArrayProperty) parent.getValue().getBean();
                int index = Integer.parseInt(property.getName().substring(
                        property.getName().indexOf('[') + 1,
                        property.getName().indexOf(']')
                ));
                List<Object> list = (List<Object>) parent.getValue().get();
                Button delete = new Button("Delete");
                delete.setMinWidth(Region.USE_PREF_SIZE);
                delete.setOnAction(event -> {
                    list.remove(index);
                    parent.getChildren().clear();
                    parent.getChildren().addAll(fillArrayTree(null, parentProperty, parentProperty.entry.getObjectName().getName(), list, getPropertiesEditor().getUnrealPackage(), getPropertiesEditor().getSerializer(), getPropertiesEditor().getEditableOnly(), getPropertiesEditor().getHideCategories()));
                });
                Button insert = new Button("Insert");
                insert.setMinWidth(Region.USE_PREF_SIZE);
                insert.setOnAction(event -> {
                    Object value = PropertiesUtil.defaultValue(parentProperty.inner, null, getPropertiesEditor().getSerializer(), getPropertiesEditor().getUnrealPackage());
                    list.add(index, value);
                    parent.getChildren().clear();
                    parent.getChildren().addAll(fillArrayTree(null, parentProperty, parentProperty.entry.getObjectName().getName(), list, getPropertiesEditor().getUnrealPackage(), getPropertiesEditor().getSerializer(), getPropertiesEditor().getEditableOnly(), getPropertiesEditor().getHideCategories()));
                });
                GridPane pane = new GridPane();
                pane.add(editor, 0, 0);
                pane.add(delete, 1, 0);
                pane.add(insert, 2, 0);
                pane.getColumnConstraints().addAll(
                        new ColumnConstraints() {{
                            setMinWidth(0);
                            setHgrow(Priority.ALWAYS);
                        }},
                        new ColumnConstraints(),
                        new ColumnConstraints());
                editor = pane;
            }
            editor.prefWidthProperty().bind(widthProperty());
        }
        setGraphic(editor);
    }

    protected Region createEditor(ObjectProperty<Object> property) {
        Property template = (Property) property.getBean();
        if (template == null)
            return null;

        if (template.arrayDimension > 1 && property.get() == null)
            return new Label("...");
        
        IEdit editor = null;
        if (template instanceof ByteProperty) {
        	editor = ByteEdit.getInstance();
        } else if (template instanceof IntProperty) {
        	editor = IntEdit.getInstance();
        } else if (template instanceof BoolProperty) {
        	editor = BoolEdit.getInstance();
        } else if (template instanceof FloatProperty) {
        	editor = FloatEdit.getInstance();
        } else if (template instanceof ClassProperty) {
        	editor = ClassEdit.getInstance();
        } else if (template instanceof acmi.l2.clientmod.unreal.core.ObjectProperty) {
        	editor = ObjectEdit.getInstance();
        } else if (template instanceof NameProperty) {
        	editor = NameEdit.getInstance();
        } else if (template instanceof ArrayProperty) {
        	editor = ArrayEdit.getInstance();
        } else if (template instanceof StructProperty) {
        	editor = StructEdit.getInstance();
        } else if (template instanceof StrProperty) {
        	editor = StrEdit.getInstance();
        }
        
        return editor == null ? 
        		null : 
        		editor.create(new EditorContext(getPropertiesEditor(), getTreeTableRow(), property, template));
    }

    public static CharSequence inlineProperty(L2Property property, UnrealPackage up, UnrealSerializerFactory objectFactory, boolean valueOnly) {
        StringBuilder sb = new StringBuilder();

        Property template = property.getTemplate();

        for (int i = 0; i < template.arrayDimension; i++) {
            if (!valueOnly) {
                sb.append(property.getName());

                if (template.arrayDimension > 1) {
                    sb.append("[").append(i).append("]");
                }

                sb.append("=");
            }

            Object object = property.getAt(i);

            if (template instanceof ByteProperty) {
                if (((ByteProperty) template).enumType != null) {
                    Enum en = ((ByteProperty) template).enumType;
                    sb.append(en.values[(Integer) object]);
                } else {
                    sb.append(object);
                }
            } else if (template instanceof IntProperty ||
                    template instanceof BoolProperty) {
                sb.append(object);
            } else if (template instanceof FloatProperty) {
                sb.append(String.format(Locale.US, "%f", (Float) object));
            } else if (template instanceof acmi.l2.clientmod.unreal.core.ObjectProperty) {
                UnrealPackage.Entry entry = up.objectReference((Integer) object);
                if (entry == null) {
                    sb.append("None");
                } else if (entry instanceof UnrealPackage.ImportEntry) {
                    sb.append(((UnrealPackage.ImportEntry) entry).getClassName().getName())
                            .append("'")
                            .append(entry.getObjectFullName())
                            .append("'");
                } else if (entry instanceof UnrealPackage.ExportEntry) {
                    if (Property.CPF.getFlags(template.propertyFlags).contains(Property.CPF.ExportObject)) {
                        sb.append("\"").append(entry.getObjectName().getName()).append("\"");
                    } else {
                        String clazz = "Class";
                        if (((UnrealPackage.ExportEntry) entry).getObjectClass() != null)
                            clazz = ((UnrealPackage.ExportEntry) entry).getObjectClass().getObjectName().getName();
                        sb.append(clazz)
                                .append("'")
                                .append(entry.getObjectName().getName())
                                .append("'");
                    }
                } else {
                    throw new IllegalStateException("wtf");
                }
            } else if (template instanceof NameProperty) {
                sb.append("'").append(Objects.toString(object)).append("'");
            } else if (template instanceof ArrayProperty) {
                ArrayProperty arrayProperty = (ArrayProperty) property.getTemplate();
                Property innerProperty = arrayProperty.inner;
                L2Property fakeProperty = new L2Property(innerProperty);
                List<Object> list = (List<Object>) object;

                sb.append(list.stream()
                        .map(o -> {
                            fakeProperty.putAt(0, o);
                            return inlineProperty(fakeProperty, up, objectFactory, true);
                        }).collect(Collectors.joining(",", "(", ")")));
            } else if (template instanceof StructProperty) {
                if (object == null) {
                    sb.append("None");
                } else {
                    sb.append(inlineStruct((List<L2Property>) object, up, objectFactory));
                }
            } else if (template instanceof StrProperty) {
                sb.append("\"").append(Objects.toString(object)).append("\"");
            }

            if (i != template.arrayDimension - 1)
                sb.append(",");
        }

        return sb;
    }

    public static CharSequence inlineStruct(List<L2Property> struct, UnrealPackage up, UnrealSerializerFactory objectFactory) {
        return struct.stream().map(p -> inlineProperty(p, up, objectFactory, false)).collect(Collectors.joining(",", "(", ")"));
    }
}
