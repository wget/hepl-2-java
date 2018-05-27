/*
 * Copyright (C) 2018 wget
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package be.wget.inpres.java.restaurant.dataobjects;

import java.io.Serializable;

/**
 *
 * @author wget
 */
public abstract class Plate implements Service, Serializable {
    protected double price;
    protected String label;
    protected PlateCategory category;

    public Plate(String label, PlateCategory category, double price) {
        this.price = price;
        this.label = label;
        this.category = category;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setCategory(PlateCategory category) {
        this.category = category;
    }

    @Override
    public double getPrice() {
        return this.price;
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public String getCategory() {
        return category.getName();
    }
}
