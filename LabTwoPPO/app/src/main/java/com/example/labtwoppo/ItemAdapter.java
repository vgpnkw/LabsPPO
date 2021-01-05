package com.example.labtwoppo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.navigation.Navigation;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import Data.SequenceDatabase;
import Interfaces.ItemTouchHelperAdapter;
import Models.Sequence;
import ViewModels.AppViewModel;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder>
        implements ItemTouchHelperAdapter {
    private final List<Sequence> mValues;
    private final Context mContext;
    private Sequence mSequence;
    private static AppViewModel mAppViewModel;




    public ItemAdapter(List<Sequence> items, Context context, AppViewModel appViewModel) {
        mValues = items;
        mContext = context;
        mAppViewModel = appViewModel;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_item, parent, false);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        int fontSize = sharedPreferences.getInt("fontSize", 1);
        FontChangeSize.changeFontSizeInViews(view, fontSize);

        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mContentView.setText(mValues.get(position).name);
        holder.mContentView.setBackgroundColor(mValues.get(position).colour);
        holder.mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_itemFragment_to_timerFragment);
            }
        });
        holder.mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = holder.getAdapterPosition();
                mAppViewModel.setCurrentSequence(mValues.get(pos)); //position
                PopupMenu popupMenu = new PopupMenu(mContext, holder.mContentView);
                popupMenu.inflate(R.menu.list_menu);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @SuppressLint("NonConstantResourceId")
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.menuItemDelete:
                                //findSeqToDelete(holder.mContentView.getText().toString());
                                deleteSequence(mValues.get(pos)); //position
                                mValues.remove(pos); //position
                                notifyDataSetChanged();
                                break;
                            case R.id.menuItemUpdate:
                                mSequence = mValues.get(pos); //position
                                Navigation.findNavController(view).navigate(R.id.action_itemFragment_to_editSeqFragment);
                                break;
                            case R.id.menuItemOpen:
                                Navigation.findNavController(view).navigate(R.id.action_itemFragment_to_timerFragment);
                        }
                        return true;
                    }
                });
                popupMenu.show();
                //return true;
            }
        });

    }


//////////////////////////////////////////////////////////////////////
    private void findSeqToDelete(String name) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                mSequence = SequenceDatabase.getDatabase(mContext).timerDao().findByName(name);
            }
        });
    }

    private void deleteSequence(Sequence sequence) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                SequenceDatabase.getDatabase(mContext).timerDao().delete(sequence);
            }
        });
    }





    @Override
    public int getItemCount() {
        if (mValues == null) {
            return 0;
        }
        return mValues.size();
    }





    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        Sequence next = mValues.get(toPosition);
        Sequence prev = mValues.remove(fromPosition);
        mValues.add(toPosition, prev);
        //mValues.add(toPosition > fromPosition ? toPosition - 1 : toPosition, prev);
        notifyItemMoved(fromPosition, toPosition);
    }






    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mContentView;
        public Sequence mItem;

        public ViewHolder(View view) {
            super(view);

            mView = view;
            mContentView = (TextView) view.findViewById(R.id.seqNameInList);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }


}