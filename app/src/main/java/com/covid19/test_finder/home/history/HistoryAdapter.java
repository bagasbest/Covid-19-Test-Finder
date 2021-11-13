package com.covid19.test_finder.home.history;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.covid19.test_finder.R;

import java.util.ArrayList;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private final ArrayList<HistoryModel> listHistory = new ArrayList<>();
    @SuppressLint("NotifyDataSetChanged")
    public void setData(ArrayList<HistoryModel> items) {
        listHistory.clear();
        listHistory.addAll(items);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(listHistory.get(position));
    }

    @Override
    public int getItemCount() {
        return listHistory.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView detail, location, date, checkMethod, status;
        private ImageView img;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            detail = itemView.findViewById(R.id.detail);
            location = itemView.findViewById(R.id.place);
            date = itemView.findViewById(R.id.dateTime);
            checkMethod = itemView.findViewById(R.id.checkMethod);
            img = itemView.findViewById(R.id.img);
            status = itemView.findViewById(R.id.textView18);
        }

        @SuppressLint("SetTextI18n")
        public void bind(HistoryModel model) {
            Glide.with(itemView.getContext())
                    .load(model.getImg())
                    .into(img);

            location.setText("Location: " + model.getLocation());
            date.setText("Date: " + model.getDateTime());
            checkMethod.setText("Method: " + model.getCheckMethod());
            status.setText("Payment Status: " + model.getStatus());

            detail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(itemView.getContext(), HistoryDetailActivity.class);
                    intent.putExtra(HistoryDetailActivity.EXTRA_HISTORY, model);
                    itemView.getContext().startActivity(intent);
                }
            });

        }
    }
}
