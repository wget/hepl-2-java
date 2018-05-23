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

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 *
 * @author wget
 */
public class PlateOrder {
    protected Plate plate;
    protected int quantity;
    protected BigDecimal price;

    public PlateOrder(Plate plate, int quantity) {
        this.plate = plate;
        this.quantity = quantity;
        this.price = new BigDecimal(this.plate.getPrice())
            .multiply(new BigDecimal(quantity))
            // Round to the nearest value (2 digits).
            // Rounding seems to be needed by law (legally binding for
            // commercial pieces of software)
            // src.: https://introcs.cs.princeton.edu/java/91float/
            .setScale(2, RoundingMode.HALF_EVEN);
    }

    public void setPlate(Plate plate) {
        this.plate = plate;
    }

    public void addQuantity(int quantity) {
        this.setQuantity(this.getQuantity() + quantity);
    }

    public void removeQuantity() {
        if (this.getQuantity() > 0) {
            this.setQuantity(this.getQuantity() - 1);
        }
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
        this.price = new BigDecimal(this.plate.getPrice())
            .multiply(new BigDecimal(quantity))
            .setScale(2, RoundingMode.HALF_EVEN);
    }

    public Plate getPlate() {
        return plate;
    }

    public int getQuantity() {
        return quantity;
    }

    public BigDecimal getPrice() {
        return this.price;
    }
}
