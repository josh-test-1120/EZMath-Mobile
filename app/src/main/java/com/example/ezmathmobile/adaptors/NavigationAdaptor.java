package com.example.ezmathmobile.adaptors;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.ezmathmobile.R;
import com.example.ezmathmobile.models.NavigationCard;

import java.util.List;

/**
 * This is the Navigation adaptor for GridView
 */
public class NavigationAdaptor extends ArrayAdapter<NavigationCard> {

    /**
     * This is the default constructor
     * @param context This is the current context
     * @param cards These are the navigation cards to process
     */
    public NavigationAdaptor(@NonNull Context context, @NonNull List<NavigationCard> cards) {
        super(context,0, cards);
    }

    /**
     * This is the override for the getView method
     * @param position this is the position
     * @param convertView this is the convertView
     * @param parent this is the parent
     * @return an updated view is returned
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Assign the converted view to a default nav view
        View navCardView = convertView;
        // If empty, inflate the layout
        if (navCardView == null) {
            navCardView = LayoutInflater.from(getContext()).inflate(R.layout.navigation_card,
                    parent, false);
        }
        // Get the specific card based on position
        NavigationCard navigationCard = getItem(position);
        // Bind the objects in the view
        TextView navName = navCardView.findViewById(R.id.cardName);
        ImageView navImage = navCardView.findViewById(R.id.cardImage);
        // Update the layout fields
        navName.setText(navigationCard.getButtonName());
        navImage.setImageResource(navigationCard.getDrawableID());
        // Return the modified view
        return navCardView;
    }
}