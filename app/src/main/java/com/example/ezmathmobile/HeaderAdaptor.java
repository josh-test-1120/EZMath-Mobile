package com.example.ezmathmobile;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

/**
 * This is the HeaderAdaptor that is used in the RecycleView Adaptor
 * This extends the RecycleView.Adaptor class
 */
public class HeaderAdaptor extends RecyclerView.Adapter<HeaderAdaptor.HeaderViewHolder> {

    /**
     * This is the constructor for the Adaptor
     */
    public HeaderAdaptor() {}

    /**
     * This is an override of the onCreateViewHolder method
     *
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The view type of the new View.
     * @return A new HeaderViewHolder class that has its layout inflated
     */
    @NonNull
    @Override
    public HeaderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new HeaderViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.header, parent, false));
    }

    /**
     * This is an override of the onBindViewHolder method
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull HeaderAdaptor.HeaderViewHolder holder, int position) { }

    /**
     * This is an override of the getItemCount method
     *
     * @return the size of the header list
     */
    @Override
    public int getItemCount() { return 1; }

    /**
     * This is the HeaderViewHolder class that extends the RecycleView.ViewHolder
     */
    public class HeaderViewHolder extends RecyclerView.ViewHolder {
        // These are the objects in the view
        ConstraintLayout layoutHeader;
        ImageView logoImage;
        TextView siteTitle;

        /**
         * This is the HeaderViewHolder constructor
         *
         * @param itemView the view that is to be inflated
         */
        public HeaderViewHolder(@NonNull View itemView) {
            // Run the parent class constructor
            super(itemView);
            // Bind the objects to the view IDs
            layoutHeader = itemView.findViewById(R.id.layoutHeader);
            logoImage = itemView.findViewById(R.id.logoImage);
            siteTitle = itemView.findViewById(R.id.titleName);
        }
    }
}