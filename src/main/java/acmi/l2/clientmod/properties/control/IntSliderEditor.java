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
import javafx.beans.property.IntegerPropertyBase;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;

public class IntSliderEditor extends Control {
    public IntSliderEditor() {
    }

    public IntSliderEditor(int min, int max, int value) {
        setMin(min);
        setMax(max);
        setValue(value);
    }

    private IntegerProperty min;

    public int getMin() {
        return min == null ? 0 : min.get();
    }

    public IntegerProperty minProperty() {
        if (min == null) {
            min = new IntegerPropertyBase(0) {
                @Override
                protected void invalidated() {
                    if (get() > getMax()) {
                        setMax(get());
                    }
                    adjustValues();
                }

                @Override
                public Object getBean() {
                    return IntSliderEditor.this;
                }

                @Override
                public String getName() {
                    return "min";
                }
            };
        }
        return min;
    }

    public void setMin(int min) {
        minProperty().set(min);
    }


    private IntegerProperty max;

    public int getMax() {
        return max == null ? 255 : max.get();
    }

    public IntegerProperty maxProperty() {
        if (max == null) {
            max = new IntegerPropertyBase(255) {
                @Override
                protected void invalidated() {
                    if (get() < getMin()) {
                        setMin(get());
                    }
                    adjustValues();
                }

                @Override
                public Object getBean() {
                    return IntSliderEditor.this;
                }

                @Override
                public String getName() {
                    return "max";
                }
            };
        }
        return max;
    }

    public void setMax(int max) {
        maxProperty().set(max);
    }


    private IntegerProperty value;

    public int getValue() {
        return value == null ? 0 : value.get();
    }

    public IntegerProperty valueProperty() {
        if (value == null) {
            value = new IntegerPropertyBase(0) {
                @Override
                protected void invalidated() {
                    adjustValues();
                }

                @Override
                public Object getBean() {
                    return IntSliderEditor.this;
                }

                @Override
                public String getName() {
                    return "value";
                }
            };
        }
        return value;
    }

    public void setValue(int value) {
        if (!valueProperty().isBound()) valueProperty().set(value);
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new IntSliderEditorSkin(this);
    }

    private void adjustValues() {
        if ((getValue() < getMin() || getValue() > getMax()))
            setValue(clamp(getMin(), getValue(), getMax()));
    }

    private static int clamp(int min, int value, int max) {
        if (value < min) return min;
        if (value > max) return max;
        return value;
    }
}
