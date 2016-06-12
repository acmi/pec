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

import acmi.l2.clientmod.io.UnrealPackage;
import acmi.l2.clientmod.properties.control.IntSliderEditor;
import acmi.l2.clientmod.properties.control.PropertiesEditor;
import acmi.l2.clientmod.unreal.UnrealSerializerFactory;
import acmi.l2.clientmod.unreal.core.*;
import acmi.l2.clientmod.unreal.core.Enum;
import acmi.l2.clientmod.unreal.properties.L2Property;
import acmi.l2.clientmod.unreal.properties.PropertiesUtil;
import acmi.util.AutoCompleteComboBox;
import javafx.beans.InvalidationListener;
import javafx.beans.property.ObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;

import java.lang.Object;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static acmi.l2.clientmod.properties.control.skin.PropertiesEditorDefaultSkin.fillArrayTree;

class PropertyValueCell extends TreeTableCell<ObjectProperty<Object>, Object> {
    private static final Logger log = Logger.getLogger(PropertyValueCell.class.getName());

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

        if (template instanceof ByteProperty) {
            ByteProperty byteProperty = (ByteProperty) template;
            if (byteProperty.enumType != null) {
                ComboBox<String> cb = new ComboBox<>();
                cb.getItems().addAll(byteProperty.enumType.values);
                cb.getSelectionModel().select((Integer) property.get());
                cb.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
                    property.setValue(newValue);
                });
                return cb;
            } else {
                IntSliderEditor editor = new IntSliderEditor(0, 255, (Integer) property.get());
                editor.valueProperty().bindBidirectional((javafx.beans.property.Property) property);
                return editor;
            }
        } else if (template instanceof IntProperty) {
            TextField tf = new TextField(String.valueOf(property.get()));
            tf.textProperty().addListener((observable, oldValue, newValue) -> {
                try {
                    property.setValue(Integer.valueOf(newValue));
                } catch (NumberFormatException ignore) {
                }
            });
            return tf;
        } else if (template instanceof BoolProperty) {
            CheckBox cb = new CheckBox();
            cb.setSelected((Boolean) property.getValue());
            cb.selectedProperty().addListener((observable, oldValue, newValue) -> {
                property.setValue(newValue);
            });
            return cb;
        } else if (template instanceof FloatProperty) {
            TextField tf = new TextField(String.valueOf(property.get()));
            tf.textProperty().addListener((observable, oldValue, newValue) -> {
                try {
                    property.setValue(Float.parseFloat(newValue));
                } catch (NumberFormatException ignore) {
                }
            });
            return tf;
        } else if (template instanceof ClassProperty) {
            String type = ((ClassProperty) template).clazz.getFullName();
            ObservableList<UnrealPackage.Entry> entries = FXCollections.observableArrayList();
            Predicate<UnrealPackage.Entry> filter = entry -> entry.getFullClassName().equalsIgnoreCase("Core.Class") && getPropertiesEditor().getSerializer().isSubclass(type, entry.getObjectFullName());
            entries.addAll(getPropertiesEditor().getUnrealPackage().getImportTable().parallelStream().filter(filter).collect(Collectors.toList()));
            entries.addAll(getPropertiesEditor().getUnrealPackage().getExportTable().parallelStream().filter(filter).collect(Collectors.toList()));
            Collections.sort(entries, (e1, e2) -> e1.getObjectFullName().compareToIgnoreCase(e2.getObjectFullName()));
            if (entries.isEmpty()) {
                int val = (Integer) property.get();
                if (val != 0) {
                    log.warning(() -> "No entries found for " + template);

                    entries.add(getPropertiesEditor().getUnrealPackage().objectReference(val));
                }
            }
            entries.add(0, new UnrealPackage.Entry(null, 0, 0, 0) {
                @Override
                public String getObjectInnerFullName() {
                    return "None";
                }

                @Override
                public String getFullClassName() {
                    return type;
                }

                @Override
                public int getObjectReference() {
                    return 0;
                }

                @Override
                public List getTable() {
                    return null;
                }
            });
            ComboBox<UnrealPackage.Entry> cb = new ComboBox<>(entries);
            AutoCompleteComboBox.autoCompleteComboBox(cb, AutoCompleteComboBox.AutoCompleteMode.CONTAINING);
            UnrealPackage.Entry v = cb.getItems()
                    .parallelStream()
                    .filter(entry -> entry.getObjectReference() == (Integer) property.get())
                    .findAny()
                    .orElseThrow(() -> new IllegalStateException("Entry not found: " + property.get() + "(" + getPropertiesEditor().getUnrealPackage().objectReference((Integer) property.get()) + ")"));
            cb.getSelectionModel().select(v);
            cb.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                property.setValue(newValue == null ? 0 : newValue.getObjectReference());
            });
            return cb;
        } else if (template instanceof acmi.l2.clientmod.unreal.core.ObjectProperty) {
            String type = ((acmi.l2.clientmod.unreal.core.ObjectProperty) template).type.getFullName();
            ObservableList<UnrealPackage.Entry> entries = FXCollections.observableArrayList();
            Predicate<UnrealPackage.Entry> filter = entry -> getPropertiesEditor().getSerializer().isSubclass(type, entry.getFullClassName());
            entries.addAll(getPropertiesEditor().getUnrealPackage().getImportTable().parallelStream().filter(filter).collect(Collectors.toList()));
            entries.addAll(getPropertiesEditor().getUnrealPackage().getExportTable().parallelStream().filter(filter).collect(Collectors.toList()));
            Collections.sort(entries, (e1, e2) -> e1.getObjectFullName().compareToIgnoreCase(e2.getObjectFullName()));
            if (entries.isEmpty()) {
                int val = (Integer) property.get();
                if (val != 0) {
                    log.warning(() -> "No entries found for " + template);

                    entries.add(getPropertiesEditor().getUnrealPackage().objectReference(val));
                }
            }
            entries.add(0, new UnrealPackage.Entry(null, 0, 0, 0) {
                @Override
                public String getObjectInnerFullName() {
                    return "None";
                }

                @Override
                public String getFullClassName() {
                    return type;
                }

                @Override
                public int getObjectReference() {
                    return 0;
                }

                @Override
                public List getTable() {
                    return null;
                }
            });
            ComboBox<UnrealPackage.Entry> cb = new ComboBox<>(entries);
            AutoCompleteComboBox.autoCompleteComboBox(cb, AutoCompleteComboBox.AutoCompleteMode.CONTAINING);
            UnrealPackage.Entry v = cb.getItems()
                    .parallelStream()
                    .filter(entry -> entry.getObjectReference() == (Integer) property.get())
                    .findAny()
                    .orElseThrow(() -> new IllegalStateException("Entry not found: " + property.get() + "(" + getPropertiesEditor().getUnrealPackage().objectReference((Integer) property.get()) + ")"));
            cb.getSelectionModel().select(v);
            cb.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                property.setValue(newValue == null ? 0 : newValue.getObjectReference());
            });
            return cb;
        } else if (template instanceof NameProperty) {
            UnrealPackage.NameEntry noneEntry = getPropertiesEditor().getUnrealPackage().getNameTable()
                    .parallelStream()
                    .filter(nameEntry -> nameEntry.getName().equalsIgnoreCase("None"))
                    .findAny()
                    .orElseThrow(() -> new IllegalStateException("Name entry not found"));
            ObservableList<UnrealPackage.NameEntry> names = FXCollections.observableList(getPropertiesEditor().getUnrealPackage().getNameTable()
                    .parallelStream()
                    .sorted((e1, e2) -> e1 == noneEntry ? -1 : e2 == noneEntry ? 1 :
                            e1.getName().compareToIgnoreCase(e2.getName()))
                    .collect(Collectors.toList()));
            ComboBox<UnrealPackage.NameEntry> cb = new ComboBox<>(names);
            AutoCompleteComboBox.autoCompleteComboBox(cb, AutoCompleteComboBox.AutoCompleteMode.CONTAINING);
            cb.getSelectionModel().select(names
                    .parallelStream()
                    .filter(nameEntry -> nameEntry.getIndex() == (Integer) property.get())
                    .findAny()
                    .orElseThrow(() -> new IllegalStateException("Name entry not found")));
            cb.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue == null)
                    newValue = noneEntry;
                property.setValue(newValue.getIndex());
            });
            return cb;
        } else if (template instanceof ArrayProperty) {
            TreeItem<ObjectProperty<Object>> item = getTreeTableRow().getTreeItem();
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
                Object value = PropertiesUtil.defaultValue(((ArrayProperty) template).inner, null, getPropertiesEditor().getSerializer(), getPropertiesEditor().getUnrealPackage());
                list.add(value);
                item.getChildren().clear();
                item.getChildren().addAll(fillArrayTree(null, (ArrayProperty) template, template.entry.getObjectName().getName(), list, getPropertiesEditor().getUnrealPackage(), getPropertiesEditor().getSerializer(), getPropertiesEditor().getEditableOnly(), getPropertiesEditor().getHideCategories()));
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
        } else if (template instanceof StructProperty) {
            List<L2Property> struct = (List<L2Property>) property.get();
            if (((StructProperty) template).struct.getFullName().equalsIgnoreCase("Core.Object.Color")) {
                java.util.function.Function<String, ObjectProperty<Object>> f = name -> getTreeTableRow()
                        .getTreeItem()
                        .getChildren()
                        .stream()
                        .filter(ti -> ti.getValue().getName().equalsIgnoreCase(name))
                        .findAny()
                        .map(TreeItem::getValue)
                        .orElseThrow(() -> new IllegalThreadStateException("Color component not found: " + name));
                ObjectProperty<Object> a = f.apply("A");
                ObjectProperty<Object> r = f.apply("R");
                ObjectProperty<Object> g = f.apply("G");
                ObjectProperty<Object> b = f.apply("B");
                Supplier<Color> colorSupplier = () -> Color.rgb((Integer) r.get(), (Integer) g.get(), (Integer) b.get(), ((Integer) a.get()) / 255.0);
                ColorPicker cp = new ColorPicker(colorSupplier.get());
                InvalidationListener il = observable -> cp.setValue(colorSupplier.get());
                a.addListener(il);
                r.addListener(il);
                g.addListener(il);
                b.addListener(il);
                cp.valueProperty().addListener((observable, oldValue, newValue) -> {
                    a.set((int) Math.round(255 * newValue.getOpacity()));
                    r.set((int) Math.round(255 * newValue.getRed()));
                    g.set((int) Math.round(255 * newValue.getGreen()));
                    b.set((int) Math.round(255 * newValue.getBlue()));
                });
                return cp;
            } else {
                String text = struct == null ? "" : inlineStruct(struct, getPropertiesEditor().getUnrealPackage(), getPropertiesEditor().getSerializer()).toString();
                return new Label(text);
            }
        } else if (template instanceof StrProperty) {
            TextField tf = new TextField(String.valueOf(property.get()));
            tf.textProperty().addListener((observable, oldValue, newValue) -> {
                property.setValue(newValue);
            });
            return tf;
        }

        return null;
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
