package adapters;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.contacts.contactsmap.MainActivity;
import com.contacts.contactsmap.R;

import java.util.ArrayList;
import java.util.List;

import models.ContactModel;
import network.CheckConnectivity;
import viewAddons.CustomMessage;

/**
 * Created by haseeb on 23/12/16.
 */
public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> {
    private static final int TYPE_LOAD_MORE = 0;
    private static final int TYPE_ITEM = 1;
    ArrayList<ContactModel> data;
    static Context context;

    // Creating a ViewHolder which extends the RecyclerView View Holder
    // ViewHolder are used to to store the inflated views in order to recycle them

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView name, number, email;
        ImageView loc_identifier;

        public ViewHolder(View itemView, int ViewType) {                 // Creating ViewHolder Constructor with View and viewType As a parameter
            super(itemView);
            if (ViewType == TYPE_ITEM) {
                name = (TextView) itemView.findViewById(R.id.contactname);
                number = (TextView) itemView.findViewById(R.id.contactnumber);
                email = (TextView) itemView.findViewById(R.id.contactemail);
                loc_identifier = (ImageView) itemView.findViewById(R.id.loc_identifier);
            }
            else {

            }

        }

    }


    public ContactAdapter(ArrayList<ContactModel> data, Context context) { // MyAdapter Constructor with titles and icons parameter
        this.data = data;
        this.context = context;

    }


    //Below first we ovverride the method onCreateViewHolder which is called when the ViewHolder is
    //Created, In this method we inflate the item_row.xml layout if the viewType is Type_ITEM or else we inflate header.xml
    // if the viewType is TYPE_HEADER
    // and pass it to the view holder

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        ViewHolder vhItem;
        if (viewType == TYPE_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_placeholder, parent, false); //Inflating the layout
            vhItem = new ViewHolder(v, viewType); //Creating ViewHolder and passing the object of type view
        }
        else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading, parent, false); //Inflating the layout
            vhItem = new ViewHolder(v, viewType); //Creating ViewHolder and passing the object of type view
        }

        return vhItem; // Returning the created object


    }

    //Next we override a method which is called when the item in a row is needed to be displayed, here the int position
    // Tells us item at which position is being constructed to be displayed and the holder id of the holder object tell us
    // which view type is being created 1 for item row


    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        ContactModel item = data.get(position);
        holder.name.setText(item.getName());
        if (item.getPhone() != null) {
            holder.number.setVisibility(View.VISIBLE);
            holder.number.setText(item.getPhone());
        }
        else {
            holder.number.setVisibility(View.GONE);
        }
        if (item.getEmail() != null) {
            holder.email.setVisibility(View.VISIBLE);
            holder.email.setText(item.getEmail());
        }
        else {
            holder.email.setVisibility(View.GONE);
        }
        if (item.getLongitude() != null){
            holder.loc_identifier.setVisibility(View.VISIBLE);
            holder.itemView.setEnabled(true);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (CheckConnectivity.isNetworkAvailable(context)) {
                        ((MainActivity) context).selectFragment(1);
                        ((MainActivity) context).updateMap(position);
                    }
                    else {
                        CustomMessage.getInstance().CustomMessage(context, "Internet not available!");
                    }
                }
            });
        }
        else {
            holder.loc_identifier.setVisibility(View.GONE);
            holder.itemView.setEnabled(false);
        }

    }

    // This method returns the number of items present in the list
    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (data.get(position) != null) {
            return TYPE_ITEM;
        }
        else {
            return TYPE_LOAD_MORE;
        }

    }

}
