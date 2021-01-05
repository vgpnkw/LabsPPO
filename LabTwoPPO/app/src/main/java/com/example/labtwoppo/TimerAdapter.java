package com.example.labtwoppo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import Models.SequenceInterval;
import ViewModels.AppViewModel;


public class TimerAdapter extends RecyclerView.Adapter<TimerAdapter.ViewHolder> {

    private final List<SequenceInterval> mIntervals;
    private final AppViewModel mAppViewModel;
    private final int mColor;
    private final Context mContext;

    public TimerAdapter(AppViewModel appViewModel, List<SequenceInterval> intervals, int color, Context context) {
        mAppViewModel = appViewModel;
        mIntervals = intervals;
        mColor = color;
        mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_timer, parent, false);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        int fontSize = sharedPreferences.getInt("fontSize", 1);
        FontChangeSize.changeFontSizeInViews(view, fontSize);

        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mContentView.setText(Integer.toString(position + 1) + ". " + mIntervals.get(position).name
                + ": " + Integer.toString(mIntervals.get(position).value));
        holder.mContentView.setBackgroundColor(mColor);
    }

    @Override
    public int getItemCount() {
        return mIntervals.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mContentView;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mContentView = (TextView) view.findViewById(R.id.contentTimerItem);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}