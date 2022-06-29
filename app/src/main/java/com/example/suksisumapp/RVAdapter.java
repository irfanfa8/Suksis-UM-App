package com.example.suksisumapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RVAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private Context context;
    ArrayList<Feeds> list = new ArrayList<>();
    public RVAdapter(Context ctx)
    {
        this.context = ctx;
    }
    public void setItems(ArrayList<Feeds> feeds)
    {
        list.addAll(feeds);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_item,parent,false);
        return new FeedVH(view);
    }
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position)
    {
        Feeds e = null;
        this.onBindViewHolder(holder,position,e);
    }

    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, Feeds e)
    {
        FeedVH vh = (FeedVH) holder;
        Feeds feeds = list.get(position);
        vh.feed_title.setText(feeds.getFeedTitle());
        vh.feed_date.setText(feeds.getFeedDate());
        vh.feed_time.setText(feeds.getFeedTime());
        vh.feed_note.setText(feeds.getFeedNote());
        vh.date.setText(feeds.getDate());
        vh.txt_option.setOnClickListener(v->
        {
            PopupMenu popupMenu =new PopupMenu(context,vh.txt_option);
            popupMenu.inflate(R.menu.option_menu);
            popupMenu.setOnMenuItemClickListener(item->
            {
                switch (item.getItemId())
                {
                    case R.id.menu_edit:
                        Intent intent=new Intent(context, AddFeedActivity.class);
                        intent.putExtra("EDIT",feeds);
                        context.startActivity(intent);
                        break;
                    case R.id.menu_remove:
                        DAOFeeds dao=new DAOFeeds();
                        dao.remove(feeds.getKey()).addOnSuccessListener(suc->
                        {
                            Toast.makeText(context, "Record is removed", Toast.LENGTH_SHORT).show();
                            notifyItemRemoved(position);
                            list.remove(feeds);
                        }).addOnFailureListener(er->
                        {
                            Toast.makeText(context, ""+er.getMessage(), Toast.LENGTH_SHORT).show();
                        });

                        break;
                }
                return false;
            });
            popupMenu.show();
        });
    }

    @Override
    public int getItemCount()
    {
        return list.size();
    }

}
