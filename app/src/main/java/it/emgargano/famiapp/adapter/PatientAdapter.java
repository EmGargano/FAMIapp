package it.emgargano.famiapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import it.emgargano.famiapp.PatientDetailActivity;
import it.emgargano.famiapp.sms.prova.R;
import it.emgargano.famiapp.models.User;

public class PatientAdapter extends RecyclerView.Adapter<PatientAdapter.ViewHolder> implements Filterable {

    //Variable for Adapter
    private Context mContext;
    private List<User> mUser;
    private List<User> mUserDisplayed;

    //Adapter
    public PatientAdapter(Context mContext, List<User> mUser){
        this.mContext = mContext;
        this.mUser = mUser;
        mUserDisplayed = mUser;
    }

    //Function to set view of users row
    @NonNull
    @Override
    public PatientAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View view = LayoutInflater.from(mContext).inflate(R.layout.activity_patient_row, parent, false);
        return new PatientAdapter.ViewHolder(view);
    }

    //Function to populate ViewHolder
    @Override
    public void onBindViewHolder(@NonNull final PatientAdapter.ViewHolder holder, final int position){
        final User user = mUserDisplayed.get(position);
        String name = user.name;
        String surname = user.surname;
        String dateBirth = user.date_of_birth;
        holder.text_userNameClicked.setText(name);
        holder.text_userSurname.setText(surname);
        holder.text_userBirth.setText(dateBirth);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Get id of user clicked
                String user_clicked = user.getUserId();
                //Create new intent
                Intent patientDetailIntent = new Intent(mContext, PatientDetailActivity.class);
                //pass parameter to intent
                patientDetailIntent.putExtra("user_clicked", user_clicked);
                mContext.startActivity(patientDetailIntent);
            }
        });
    }

    //Get number of user
    @Override
    public int getItemCount(){
        return mUserDisplayed.size();
    }

    //Declaration class ViewHolder
    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView text_userNameClicked;
        public TextView text_userSurname;
        public TextView text_userBirth;

        public ViewHolder(View itemView){
            super(itemView);
            text_userNameClicked = itemView.findViewById(R.id.text_userNameClicked);
            text_userSurname = itemView.findViewById(R.id.userRating);
            text_userBirth = itemView.findViewById(R.id.userBirth);
        }
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                FilterResults filterResults = new FilterResults();
                ArrayList<User> FilteredList = new ArrayList<User>();
                if (mUser == null) {
                    mUser = new ArrayList<User>(mUserDisplayed);
                }
                /********
                 *
                 *  If charSequence that is received is null returns the original values
                 *  else does the Filtering and returns the filtered list.
                 *
                 ********/
                if (charSequence == null || charSequence.length() == 0) {

                    // set the Original result to return  
                    filterResults.count = mUser.size();
                    filterResults.values = mUser;
                } else {
                    for (int i = 0; i < mUser.size(); i++) {
                        String name = mUser.get(i).name;
                        String surname = mUser.get(i).surname;
                        if (surname.toLowerCase().contains(charSequence.toString().toLowerCase())
                                || name.toLowerCase().contains(charSequence.toString().toLowerCase())) {
                            FilteredList.add(mUser.get(i));
                        }
                    }
                    // set the Filtered result to return
                    filterResults.count = FilteredList.size();
                    filterResults.values = FilteredList;
                }
                return filterResults;
            }

            @Override
            @SuppressWarnings("unchecked")
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mUserDisplayed = (List<User>) filterResults.values;
                notifyDataSetChanged();
            }
        };
        return filter;
    }
}
