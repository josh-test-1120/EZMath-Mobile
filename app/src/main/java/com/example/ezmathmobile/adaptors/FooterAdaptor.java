package com.example.ezmathmobile.adaptors;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ezmathmobile.R;
import com.example.ezmathmobile.activities.RemindersActivity;
import com.example.ezmathmobile.activities.MainActivity;
import com.example.ezmathmobile.activities.TestManagerActivity;

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
        return new FooterViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.navigation_bar_2, parent, false));
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
        ImageView menuButton, homeButton, testManagerButton, messageButton, remindersButton;

        /**
         * This is the FooterViewHolder constructor
         *
         * @param itemView the view that is to be inflated
         */
        public FooterViewHolder(@NonNull View itemView) {
            // Run the parent class constructor
            super(itemView);

            Context mainPageLayout = itemView.getContext();

            // Bind the objects to the view IDs
            layoutHeader = itemView.findViewById(R.id.navigation_bar);
            navigationGrid = itemView.findViewById(R.id.navigationLayout);
            //menuButton = itemView.findViewById(R.id.homeBtn);
            homeButton = itemView.findViewById(R.id.homeBtn);
            testManagerButton = itemView.findViewById(R.id.testManagerBtn);
            messageButton = itemView.findViewById(R.id.chatBtn);
            remindersButton = itemView.findViewById(R.id.remindersBtn);

            // Set the listeners
            setListeners(mainPageLayout);
        }

        /**
         * This is the onClick method to change background color of the button being clicked
         *
         * @param imageView the current button that is being clicked
         */
        public void onClick(ImageView imageView) {
            // if user clicks button, change color to black
            if (imageView.isSelected()) {
                imageView.setBackgroundColor(Color.BLACK);
            }
            else // if not clicked button, change back to white
            {
                imageView.setBackgroundColor(Color.WHITE);
            }
        }

        /**
         * This is the setListeners method for the different buttons in the navigation bar
         * @param newMain Context to use for loading new Intent
         **/
        private void setListeners(Context newMain) {
            // Change to MainActivity (home screen) if homeButton clicked
            homeButton.setOnClickListener(v -> {
                        Intent intent = new Intent(newMain.getApplicationContext(), MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        newMain.startActivity(intent);
                    });
            // Change to TestManagerActivity if testManagerButton clicked
            testManagerButton.setOnClickListener(v -> {
                        Intent intent = new Intent(newMain.getApplicationContext(), TestManagerActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        newMain.startActivity(intent);
                    });
            // Change to RemindersActivity if remindersButton clicked
            remindersButton.setOnClickListener(v -> {
                        Intent intent = new Intent(newMain.getApplicationContext(), RemindersActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        newMain.startActivity(intent);
                    });
        /* This is for the chat feature if we have time:
        // Change to chat screen activity if chatButton clicked
        chatButton.setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(),MainActivity.class)));
         */
        }
    }
}