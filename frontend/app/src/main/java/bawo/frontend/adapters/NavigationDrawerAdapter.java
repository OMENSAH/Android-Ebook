package bawo.frontend.adapters;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import bawo.frontend.R;
import bawo.frontend.activities.LoginActivity;
import bawo.frontend.activities.SettingsActivity;
import bawo.frontend.models.NavigationDrawerItem;
import bawo.frontend.utils.Constants;


public class NavigationDrawerAdapter extends RecyclerView.Adapter<NavigationDrawerAdapter.MyViewHolder>{
    private List<NavigationDrawerItem> mDataList;
    private LayoutInflater inflater;
    private Context context;

    public NavigationDrawerAdapter(Context context, List<NavigationDrawerItem> data){
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.mDataList = data;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.nav_drawer_list_item, parent, false);
        MyViewHolder  holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        NavigationDrawerItem current  = mDataList.get(position);

        holder.imgIcon.setImageResource(current.getImageId());
        holder.title.setText(current.getTitle());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                    switch (position){
                        case 0:
                            goToSettings();
                            break;
                        case 1:
                            logout();
                            break;
                    }
            }
        });

    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    public class MyViewHolder extends  RecyclerView.ViewHolder{
        TextView title;
        ImageView imgIcon;
        public MyViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            imgIcon = itemView.findViewById(R.id.imgIcon);
        }
    }

    private void logout() {
        ((Activity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(context.getApplicationContext(), LoginActivity.class);
                intent.putExtra(Constants.KEY_CLEAR_CREDENTIALS, true);
                context.startActivity(intent);
                ((Activity)context).finish();
            }
        });
    }
    private void goToSettings(){
        Intent intent = new Intent(context.getApplicationContext(), SettingsActivity.class);
        intent.putExtra(Constants.KEY_CLEAR_CREDENTIALS, true);
        context.startActivity(intent);
        ((Activity)context).finish();
    }

}
