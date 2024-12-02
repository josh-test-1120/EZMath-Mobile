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

    /**
     * Getter for the button name
     * @return string of the button name
     */
    public String getButtonName() {
        return buttonName;
    }

    /**
     * Setter for the button name
     * @param buttonName string of the button name
     */
    public void setButtonName(String buttonName) {
        this.buttonName = buttonName;
    }

    /**
     * Getter for the drawableID
     * @return integer of the drawable layout
     */
    public int getDrawableID() {
        return drawableID;
    }

    /**
     * Setter for the drawableID
     * @param drawableID integer of the drawable layout
     */
    public void setDrawableID(int drawableID) {
        this.drawableID = drawableID;
    }
}