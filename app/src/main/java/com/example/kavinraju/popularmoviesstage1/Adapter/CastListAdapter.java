package com.example.kavinraju.popularmoviesstage1.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kavinraju.popularmoviesstage1.Data_Model.CastDetails;
import com.example.kavinraju.popularmoviesstage1.R;
import com.example.kavinraju.popularmoviesstage1.Utils.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CastListAdapter extends RecyclerView.Adapter<CastListAdapter.CastViewHolder>{

    private ArrayList<CastDetails> castDetails;
    private int totalCasts;
    private Context context;

    public CastListAdapter(ArrayList<CastDetails> castDetails, int totalCasts){
        this.castDetails = castDetails;
        this.totalCasts = totalCasts;
    }
    @NonNull
    @Override
    public CastViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        int layoutID = R.layout.recyclerview_cast_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutID , parent , false);


        return new CastViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CastViewHolder holder, int position) {

        if (castDetails.size() > 0){

            String path = castDetails.get(position).getProfile_path();
            String url = NetworkUtils.buildURL_for_Image(path, false).toString();
            Picasso.with(context)
                    .load(url)
                    .error(context.getResources().getDrawable(R.drawable.error_bk))
                    .into(holder.imageView_cast);
            holder.textView_name.setText(castDetails.get(position).getName());
        }
    }

    @Override
    public int getItemCount() {
        return totalCasts;
    }

    class CastViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView_cast;
        TextView textView_name;

        CastViewHolder(View itemView) {
            super(itemView);
            imageView_cast = itemView.findViewById(R.id.recyclerview_castlist_imageView);
            textView_name  = itemView.findViewById(R.id.recyclerview_castlist_textView_name);

        }
    }
}
