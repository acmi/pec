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

import acmi.l2.clientmod.properties.control.skin.IntSliderEditorSkin;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;

public class IntSliderEditor extends Control {
    private IntegerProperty min = new SimpleIntegerProperty();
    private IntegerProperty max = new SimpleIntegerProperty();
    private IntegerProperty value = new SimpleIntegerProperty();

    public IntSliderEditor() {
        this(0, 255, 0);
    }

    public IntSliderEditor(int min, int max, int value) {
        setMin(min);
        setMax(max);
        setValue(value);
    }

    public int getMin() {
        return min.get();
    }

    public IntegerProperty minProperty() {
        return min;
    }

    public void setMin(int min) {
        this.min.set(min);
    }

    public int getMax() {
        return max.get();
    }

    public IntegerProperty maxProperty() {
        return max;
    }

    public void setMax(int max) {
        this.max.set(max);
    }

    public int getValue() {
        return value.get();
    }

    public IntegerProperty valueProperty() {
        return value;
    }

    public void setValue(int value) {
        this.value.set(value);
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new IntSliderEditorSkin(this);
    }
}
