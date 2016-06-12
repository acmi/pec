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

import acmi.l2.clientmod.io.UnrealPackage;
import acmi.l2.clientmod.properties.control.skin.PropertiesEditorDefaultSkin;
import acmi.l2.clientmod.unreal.UnrealSerializerFactory;
import acmi.l2.clientmod.unreal.core.*;
import acmi.l2.clientmod.unreal.core.Class;
import acmi.l2.clientmod.unreal.core.FloatProperty;
import acmi.l2.clientmod.unreal.core.Property;
import acmi.l2.clientmod.unreal.properties.L2Property;
import acmi.l2.clientmod.unreal.properties.PropertiesUtil;
import javafx.beans.property.*;
import javafx.beans.property.ObjectProperty;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;

import java.lang.Object;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PropertiesEditor extends Control {
    private ObjectProperty<UnrealSerializerFactory> serializer = new SimpleObjectProperty<>();
    private ObjectProperty<UnrealPackage> unrealPackage = new SimpleObjectProperty<>();
    private StringProperty structName = new SimpleStringProperty();
    private ObjectProperty<List<L2Property>> propertyList = new SimpleObjectProperty<>();
    private BooleanProperty editableOnly = new SimpleBooleanProperty();
    private BooleanProperty hideCategories = new SimpleBooleanProperty();

    public UnrealSerializerFactory getSerializer() {
        return serializer.get();
    }

    public ObjectProperty<UnrealSerializerFactory> serializerProperty() {
        return serializer;
    }

    public void setSerializer(UnrealSerializerFactory serializer) {
        this.serializer.set(serializer);
    }

    public UnrealPackage getUnrealPackage() {
        return unrealPackage.get();
    }

    public ObjectProperty<UnrealPackage> unrealPackageProperty() {
        return unrealPackage;
    }

    public void setUnrealPackage(UnrealPackage unrealPackage) {
        this.unrealPackage.set(unrealPackage);
    }

    public String getStructName() {
        return structName.get();
    }

    public StringProperty structNameProperty() {
        return structName;
    }

    public void setStructName(String structName) {
        this.structName.set(structName);
    }

    public List<L2Property> getPropertyList() {
        return propertyList.get();
    }

    public ObjectProperty<List<L2Property>> propertyListProperty() {
        return propertyList;
    }

    public void setPropertyList(List<L2Property> propertyList) {
        this.propertyList.set(propertyList);
    }

    public boolean getEditableOnly() {
        return editableOnly.get();
    }

    public BooleanProperty editableOnlyProperty() {
        return editableOnly;
    }

    public void setEditableOnly(boolean editableOnly) {
        this.editableOnly.set(editableOnly);
    }

    public boolean getHideCategories() {
        return hideCategories.get();
    }

    public BooleanProperty hideCategoriesProperty() {
        return hideCategories;
    }

    public void setHideCategories(boolean hideCategories) {
        this.hideCategories.set(hideCategories);
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new PropertiesEditorDefaultSkin(this);
    }

    public static L2Property create(Property property, String structName, UnrealSerializerFactory serializer, UnrealPackage up) {
        L2Property l2property = new L2Property(property);
        for (int i = 0; i < l2property.getSize(); i++)
            l2property.putAt(i, defaultValue(property, structName, serializer, up));
        return l2property;
    }

    public static Object defaultValue(Property property, String structName, UnrealSerializerFactory serializer, UnrealPackage unrealPackage) {
        Optional<Class> classOpt = serializer.getStruct(structName)
                .filter(struct -> struct instanceof Class)
                .map(struct -> (Class) struct);

        if (classOpt.isPresent()) {
            Object[] defaultValue = new Object[1];
            serializer.getStructTree(classOpt.get()).forEach(superClass -> superClass.properties
                    .parallelStream()
                    .filter(l2Property -> l2Property.getTemplate().equals(property))
                    .findAny()
                    .ifPresent(l2Property -> defaultValue[0] = l2Property.getAt(0)));
            if (defaultValue[0] != null) {
                if (PropertiesUtil.isPrimitive(property)) {
                    return defaultValue[0];
                } else if (property instanceof StructProperty) {
                    return PropertiesUtil.cloneStruct((List<L2Property>) defaultValue[0]);
                }
            }
        }

        return defaultValue(property, serializer, unrealPackage);
    }

    public static Object defaultValue(Property property, UnrealSerializerFactory serializer, UnrealPackage unrealPackage) {
        if (property instanceof ByteProperty) {
            return 0;
        } else if (property instanceof IntProperty) {
            return 0;
        } else if (property instanceof BoolProperty) {
            return false;
        } else if (property instanceof FloatProperty) {
            return 0f;
        } else if (property instanceof acmi.l2.clientmod.unreal.core.ObjectProperty) {
            return 0;
        } else if (property instanceof NameProperty) {
            return unrealPackage.nameReference("None");
        } else if (property instanceof StructProperty) {
            String structName = ((StructProperty) property).struct.getFullName();
            return getProperties(structName, serializer, true, true)
                    .map(p -> create(p, structName, serializer, unrealPackage))
                    .collect(Collectors.toList());
        } else if (property instanceof ArrayProperty) {
            return new ArrayList();
        } else if (property instanceof StrProperty) {
            return "";
        }
        throw new IllegalStateException(String.valueOf(property));
    }

    public static Stream<Property> getProperties(String structName, UnrealSerializerFactory serializer, boolean editOnly, boolean hideCategories) {
        Struct struct = serializer.getStruct(structName).orElse(new Struct());

        return PropertiesUtil.getPropertyFields(serializer, structName)
                .filter(p -> !editOnly || (p.propertyFlags & Property.CPF.Edit.getMask()) != 0)
                .filter(p -> !(struct instanceof Class) || (!hideCategories || !Arrays.asList(((Class) struct).hideCategories).contains(p.category)));
    }

    public static void removeDefaults(List<L2Property> properties, String structName, UnrealSerializerFactory serializer, UnrealPackage unrealPackage) {
        if (properties == null)
            return;

        for (Iterator<L2Property> it = properties.iterator(); it.hasNext(); ) {
            L2Property property = it.next();

            java.lang.Object def = PropertiesEditor.defaultValue(property.getTemplate(), structName, serializer, unrealPackage);

            boolean del = true;
            for (int i = 0; i < property.getSize(); i++) {
                java.lang.Object obj = property.getAt(i);

                if (property.getTemplate() instanceof StructProperty) {
                    List<L2Property> struct = (List<L2Property>) obj;
                    removeDefaults(struct, ((StructProperty) property.getTemplate()).struct.getFullName(), serializer, unrealPackage);
                }

                if (def.equals(obj)) {
                    if (property.getTemplate() instanceof StructProperty)
                        property.putAt(i, null);
                } else {
                    del = false;
                }
            }
            if (del) {
                it.remove();
            }
        }
    }
}
