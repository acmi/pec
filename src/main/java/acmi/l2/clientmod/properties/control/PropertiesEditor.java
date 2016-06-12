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
}
