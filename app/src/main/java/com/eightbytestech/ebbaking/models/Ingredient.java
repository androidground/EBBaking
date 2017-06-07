package com.eightbytestech.ebbaking.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by vishalsaxena on 31/05/17.
 */

public class Ingredient {

    @SerializedName("quantity")
    @Expose
    private Float quantity;

    @SerializedName("measure")
    @Expose
    private String measure;

    @SerializedName("ingredient")
    @Expose
    private String ingredient;

    public Float getQuantity() {
        return quantity;
    }

    public void setQuantity(Float quantity) {
        this.quantity = quantity;
    }

    public String getMeasure() {
        return measure;
    }

    public void setMeasure(String measure) {
        this.measure = measure;
    }

    public String getIngredient() {
        return ingredient;
    }

    public void setIngredient(String ingredient) {
        this.ingredient = ingredient;
    }
}