package com.example.anthonybs.baking.ui;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.anthonybs.baking.R;

import java.util.ArrayList;

import data.Recipe;

public class StepListAdapter extends RecyclerView.Adapter<StepListAdapter.StepViewHolder> {
    private static final String TAG = StepListAdapter.class.getSimpleName();
    private final StepListAdapterOnClickListener mOnClickListener;
    private ArrayList<Recipe.Step> mSteps;

    public interface StepListAdapterOnClickListener {
        void onClick(ArrayList<Recipe.Step> steps, int selectedPosition);
    }

    public StepListAdapter(StepListAdapterOnClickListener itemClickListener, ArrayList<Recipe.Step> steps) {
        mOnClickListener = itemClickListener;
        mSteps = steps;
    }

    public class StepViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mStepTextView;
        private ImageView mThumbnailView;

        public StepViewHolder(View itemView) {
            super(itemView);
            mStepTextView = (TextView) itemView.findViewById(R.id.tv_step);
            mThumbnailView = (ImageView) itemView.findViewById(R.id.iv_step);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            //Recipe.Step selectedStep = mSteps.get(clickedPosition);
            mOnClickListener.onClick(mSteps, clickedPosition);
        }
    }

    @NonNull
    @Override
    public StepViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.step_list_item, parent, false);
        return new StepViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StepViewHolder holder, int position) {
        Recipe.Step step = mSteps.get(position);
        holder.mStepTextView.setText(step.getShortDescription());

        if (!step.getThumbnailUrl().equals("")) {
            Uri thumbnailUri = Uri.parse(step.getThumbnailUrl());
            Glide.with(holder.mThumbnailView.getContext())
                    .load(thumbnailUri)
                    .into(holder.mThumbnailView);
        }
    }

    @Override
    public int getItemCount() {
        if (mSteps == null || mSteps.size() == 0)
            return 0;
        else
            return mSteps.size();
    }
}
