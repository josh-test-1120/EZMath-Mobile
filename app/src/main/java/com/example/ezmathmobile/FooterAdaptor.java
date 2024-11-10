package com.example.ezmathmobile;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

/**
 * This is the FooterAdaptor that is used in the RecycleView Adaptor
 * This extends the RecycleView.Adaptor class
 */
public class FooterAdaptor extends RecyclerView.Adapter<FooterAdaptor.FooterViewHolder> {

    /**
     * This is the constructor for the Adaptor
     */
    public FooterAdaptor() {}

    /**
     * This is an override of the onCreateViewHolder method
     *
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The view type of the new View.
     * @return A new FooterViewHolder class that has its layout inflated
     */
    @NonNull
    @Override
    public FooterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FooterViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.footer, parent, false));
    }

    /**
     * This is an override of the onBindViewHolder method
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull FooterAdaptor.FooterViewHolder holder, int position) { }

    /**
     * This is an override of the getItemCount method
     *
     * @return the size of the footer list
     */
    @Override
    public int getItemCount() { return 1; }

    /**
     * This is the FooterViewHolder class that extends the RecycleView.ViewHolder
     */
    public class FooterViewHolder extends RecyclerView.ViewHolder {
        // These are the objects in the view
        ConstraintLayout layoutHeader;
        GridLayout navigationGrid;
        ImageView menuButton, homeButton, testManagerButton, messageButton;

        /**
         * This is the FooterViewHolder constructor
         *
         * @param itemView the view that is to be inflated
         */
        public FooterViewHolder(@NonNull View itemView) {
            // Run the parent class constructor
            super(itemView);
            // Bind the objects to the view IDs
            layoutHeader = itemView.findViewById(R.id.layoutHeader);
            navigationGrid = itemView.findViewById(R.id.navigationGrid);
            menuButton = itemView.findViewById(R.id.menuButton);
            homeButton = itemView.findViewById(R.id.homeButton);
            testManagerButton = itemView.findViewById(R.id.testManagerButton);
            messageButton = itemView.findViewById(R.id.messageButton);
        }
    }
}