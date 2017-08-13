package com.crm.sharepointevo.bootcamplocator;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by ksfgh on 13/08/2017.
 */

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.ViewHolder>{

    private ArrayList<BootCamp> bootCamps;

    public LocationAdapter(ArrayList<BootCamp> bootCamps){
        this.bootCamps = bootCamps;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.bootcamp_layout, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mTitle.setText(bootCamps.get(position).getTitle());
        holder.mSubTitle.setText(bootCamps.get(position).getSubtitle());
        holder.mImage.setImageResource(R.drawable.img);
    }

    @Override
    public int getItemCount() {
        return bootCamps.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mTitle;
        private TextView mSubTitle;
        private ImageView mImage;

        public ViewHolder(View itemView) {
            super(itemView);

            mTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            mSubTitle = (TextView) itemView.findViewById(R.id.tvSubTitle);
            mImage = (ImageView) itemView.findViewById(R.id.ivImage);

        }
    }
}
