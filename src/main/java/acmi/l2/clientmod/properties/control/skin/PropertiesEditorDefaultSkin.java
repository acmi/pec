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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import acmi.l2.clientmod.io.UnrealPackage;
import acmi.l2.clientmod.properties.control.PropertiesEditor;
import acmi.l2.clientmod.unreal.UnrealSerializerFactory;
import acmi.l2.clientmod.unreal.core.ArrayProperty;
import acmi.l2.clientmod.unreal.core.Property;
import acmi.l2.clientmod.unreal.core.StructProperty;
import acmi.l2.clientmod.unreal.properties.L2Property;
import acmi.l2.clientmod.unreal.properties.PropertiesUtil;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.TreeItem;

public class PropertiesEditorDefaultSkin extends TreeBasedPropertiesEditorSkin {
    public PropertiesEditorDefaultSkin(PropertiesEditor editor) {
        super(editor);
    }

    @Override
    protected void buildTree(TreeItem<ObjectProperty<Object>> root) {
        List<TreeItem<ObjectProperty<Object>>> tree = buildTree(getSkinnable().getPropertyList(), getSkinnable().getStructName(), getSkinnable().getUnrealPackage(), getSkinnable().getSerializer(), getSkinnable().getEditableOnly(), getSkinnable().getHideCategories());
        Platform.runLater(() -> root.getChildren().addAll(tree));
    }

    static List<TreeItem<ObjectProperty<Object>>> buildTree(List<L2Property> object, String structName, UnrealPackage up, UnrealSerializerFactory serializer, boolean editableOnly, boolean hideCategories) {
        Set<Property> properties = PropertiesUtil.getProperties(structName, serializer, editableOnly, hideCategories)
                .collect(Collectors.toSet());

        properties.addAll(object
                .stream()
                .map(L2Property::getTemplate)
                .collect(Collectors.toList()));

        Map<String, List<Property>> byCategory = properties
                .stream()
                .collect(Collectors.groupingBy(property -> property.category));

        if (byCategory.size() == 1) {
            return add(byCategory.entrySet().iterator().next().getValue(), object, structName, up, serializer, editableOnly, hideCategories);
        } else {
            return byCategory.entrySet()
                    .stream()
                    .sorted((e1, e2) -> e1.getKey().compareToIgnoreCase(e2.getKey()))
                    .map(entry -> {
                        String cat = entry.getKey();
                        List<Property> list = entry.getValue();
                        TreeItem<ObjectProperty<Object>> categoryItem = new TreeItem<>(new SimpleObjectProperty<>(null, "(" + cat + ")", ""));

                        categoryItem.getChildren().addAll(add(list, object, structName, up, serializer, editableOnly, hideCategories));

                        return categoryItem;
                    })
                    .collect(Collectors.toList());
        }
    }

    static List<TreeItem<ObjectProperty<Object>>> add(List<Property> list, List<L2Property> object, String structName, UnrealPackage up, UnrealSerializerFactory serializer, boolean editableOnly, boolean hideCategories) {
        return list
                .stream()
                .sorted((p1, p2) -> p1.entry.getObjectName().getName().compareToIgnoreCase(p2.entry.getObjectName().getName()))
                .map(property -> buildItem(object, structName, up, serializer, property, editableOnly, hideCategories))
                .collect(Collectors.toList());
    }

    static TreeItem<ObjectProperty<Object>> buildItem(List<L2Property> object, String structName, UnrealPackage up, UnrealSerializerFactory serializer, Property property, boolean editableOnly, boolean hideCategories) {
        L2Property l2Property = object.stream()
                .filter(l2p -> l2p.getTemplate() == property)
                .findAny()
                .orElseGet(() -> {
                    L2Property l2p = PropertiesUtil.create(property, structName, serializer, up);
                    object.add(l2p);
                    return l2p;
                });

        String name = property.entry.getObjectName().getName();
        if (property instanceof ArrayProperty) {
            List<Object> list = (List<Object>) l2Property.getAt(0);
            ObjectProperty<Object> op = new SimpleObjectProperty<>(property, l2Property.getName(), list);
            TreeItem<ObjectProperty<Object>> item = new TreeItem<>(op);
            item.getChildren().addAll(fillArrayTree(structName, (ArrayProperty) property, name, list, up, serializer, editableOnly, hideCategories));
            return item;
        } else if (property.arrayDimension > 1) {
            TreeItem<ObjectProperty<Object>> item = new TreeItem<>(new SimpleObjectProperty<>(property, l2Property.getName(), null));
            for (int i = 0; i < property.arrayDimension; i++) {
                int ind = i;
                item.getChildren().add(buildNonArrayItem(property, structName, l2Property.getAt(i), newValue -> l2Property.putAt(ind, newValue), name + "[" + ind + "]", up, serializer, editableOnly, hideCategories));
            }
            return item;
        } else {
            return buildNonArrayItem(property, structName, l2Property.getAt(0), newValue -> l2Property.putAt(0, newValue), name, up, serializer, editableOnly, hideCategories);
        }
    }

    public static List<TreeItem<ObjectProperty<Object>>> fillArrayTree(String structName, ArrayProperty property, String name, List<Object> list, UnrealPackage up, UnrealSerializerFactory serializer, boolean editableOnly, boolean hideCategories) {
        List<TreeItem<ObjectProperty<Object>>> children = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            int ind = i;
            children.add(buildNonArrayItem(property.inner, structName, list.get(i), newValue -> {
                list.remove(ind);
                list.add(ind, newValue);
            }, name + "[" + ind + "]", up, serializer, editableOnly, hideCategories));
        }
        return children;
    }

    static TreeItem<ObjectProperty<Object>> buildNonArrayItem(Property property, String structName, Object object, Consumer<Object> setter, String name, UnrealPackage up, UnrealSerializerFactory serializer, boolean editableOnly, boolean hideCategories) {
        if (property instanceof StructProperty) {
            List<L2Property> struct = (List<L2Property>) object;
            if (struct == null)
                struct = (List<L2Property>) PropertiesUtil.defaultValue(property, structName, serializer, up);
            TreeItem<ObjectProperty<Object>> item = new TreeItem<>(new SimpleObjectProperty<>(property, name, struct));
            item.getChildren().addAll(buildTree(struct, ((StructProperty) property).struct.entry.getObjectFullName(), up, serializer, editableOnly, hideCategories));
            return item;
        } else {
            ObjectProperty<Object> value = new SimpleObjectProperty<>(property, name, object);
            value.addListener((observable, oldValue, newValue) -> {
                setter.accept(newValue);
            });
            return new TreeItem<>(value);
        }
    }

}
