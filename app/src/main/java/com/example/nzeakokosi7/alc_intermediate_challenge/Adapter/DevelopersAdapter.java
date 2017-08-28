package com.example.nzeakokosi7.alc_intermediate_challenge.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.nzeakokosi7.alc_intermediate_challenge.Model.Developers;
import com.example.nzeakokosi7.alc_intermediate_challenge.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by nzeakokosi7 on 8/28/17.
 */

public class DevelopersAdapter extends RecyclerView.Adapter<DevelopersAdapter.DevelopersAdapterViewHolder> implements Filterable {


    //VIEW TYPES

    private static final int LIST_VIEW = 0;
    private static final int GRID_VIEW = 1;
    private int mViewType;


    private final OnItemClickedListener onItemClickedListener;
    private ImageView mDevelopersImage;

    //LIST OF DEVELOPERS
    private List<Developers> mData = Collections.emptyList();
    private List<Developers> mFilteredData;


    public DevelopersAdapter(OnItemClickedListener ClickedListener,List<Developers> names,int viewType) {
        onItemClickedListener = ClickedListener;
        mData =names;
        mFilteredData =names;
        this.mViewType=viewType;

    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String charString=constraint.toString();
                if (charString.isEmpty()){
                    mFilteredData = mData;
                }else {
                    ArrayList<Developers> filteredList = new ArrayList<>();
                    for (Developers developers : mFilteredData) {
                        if (developers.getmNames().toLowerCase().contains(charString)) {
                            filteredList.add(developers);
                        }
                        mFilteredData = filteredList;
                    }
                }
                FilterResults filterResults=new FilterResults();
                filterResults.values= mFilteredData;

                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                mFilteredData = (List<Developers>) results.values;
                notifyDataSetChanged();
                Glide.clear(mDevelopersImage);

            }
        };
    }

    public interface OnItemClickedListener{
        void Onclick(Developers clicked);
    }

    @Override
    public DevelopersAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context=parent.getContext();

        View view=null;


        switch (mViewType){
            case LIST_VIEW:
                view = LayoutInflater.from(context).inflate(R.layout.developers_list_item,parent, false);
                break;
            case GRID_VIEW:
                view= LayoutInflater.from(context).inflate(R.layout.developers_grid_item,parent, false);
                break;
            default:
                break;

        }

        return new DevelopersAdapterViewHolder(view);

    }

    @Override
    public void onBindViewHolder(DevelopersAdapterViewHolder holder, int position) {
        holder.mDevelopersName.setText(mFilteredData.get(position).getmNames());
        setImage(holder.itemView.getContext(), mFilteredData.get(position).getmImageUrl());


    }

    @Override
    public int getItemCount() {
        return mFilteredData.size();
    }


    private void setImage(Context context, String uri){

        Glide.with(context).load(uri).dontAnimate().into(mDevelopersImage);
    }


    class DevelopersAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView mDevelopersName;


        DevelopersAdapterViewHolder(View itemView) {
            super(itemView);
            mDevelopersName=(TextView)itemView.findViewById(R.id.developers_name);
            mDevelopersImage =(ImageView)itemView.findViewById(R.id.developers_image);
            setIsRecyclable(false);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            int postion=getAdapterPosition();
            onItemClickedListener.Onclick(mFilteredData.get(postion));
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

}

