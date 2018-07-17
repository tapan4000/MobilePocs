package com.example.tapanj.mapsdemo.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.tapanj.mapsdemo.R;

public class GenericRecyclerViewAdapter<T> extends RecyclerView.Adapter<GenericRecyclerViewAdapter.ViewHolder> {
    //region All private variables
    private T[] mDataSet;

    private final OnRecyclerItemClickListener onItemClickListener;
    //endregion

    //region Constructors
    public GenericRecyclerViewAdapter(T[] dataSet, OnRecyclerItemClickListener onItemClickListener){
        this.mDataSet = dataSet;
        this.onItemClickListener = onItemClickListener;
    }
    //endregion

    //region All Interface definitions
    public interface OnRecyclerItemClickListener<U> {
        void onItemClick(U itemData);
    }
    //endregion

    //region All Static classes
    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you can provide access to all the views for a data item in a view holder.
    public static class ViewHolder<V> extends RecyclerView.ViewHolder{
        public TextView mTextView;
        public ViewHolder(TextView v){
            super(v);
            mTextView = v;
        }

        public void bind(final V itemData, final OnRecyclerItemClickListener onItemClickListener)
        {
            this.mTextView.setText(itemData.toString());
            this.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(itemData);
                }
            });
        }
    }
    //endregion

    //region Overridden Recyclerview Adapter methods
    // Create new views (invoked by the layout manager)
    @Override
    public GenericRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        TextView v = (TextView) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_textview, parent, false);
        //v.setOnClickListener(this.onItemClickListener);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // Get element from your dataset at this position
        // Replace the content of the view with that element
        holder.bind(mDataSet[position], this.onItemClickListener);
        //holder.mTextView.setText(mDataSet[position].toString());
        //holder.mTextView
    }

    @Override
    public int getItemCount() {
        return mDataSet.length;
    }
    //endregion
}
