package com.example.ezmathmobile.models;

/**
 * This model is used for the GridView Adaptor
 */
public class NavigationCard {
    // Private variables
    private String buttonName;
    private int drawableID;

    /**
     * Default constructor for the Navigation Card
     * @param buttonName this is the name for the card
     * @param drawableID this is the icon for the card
     */
    public NavigationCard(String buttonName, int drawableID) {
        this.buttonName = buttonName;
        this.drawableID = drawableID;
    }

    public String getButtonName() {
        return buttonName;
    }

    public void setButtonName(String buttonName) {
        this.buttonName = buttonName;
    }

    public int getDrawableID() {
        return drawableID;
    }

    public void setDrawableID(int drawableID) {
        this.drawableID = drawableID;
    }
}