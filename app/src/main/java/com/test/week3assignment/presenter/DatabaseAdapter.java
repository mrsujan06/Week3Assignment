package com.test.week3assignment.presenter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.test.week3assignment.R;
import com.test.week3assignment.model.ParkingResponse;
import com.test.week3assignment.view.MapsActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sujan on 04/03/2018.
 */

public class DatabaseAdapter extends RecyclerView.Adapter<DatabaseAdapter.DatabaseAdapterHolder >{

    private List<ParkingResponse> parkingResponses = new ArrayList<>();

    public DatabaseAdapter(List<ParkingResponse> parkingResponses) {
        this.parkingResponses = parkingResponses;
    }


    @Override
    public DatabaseAdapterHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_layout, parent , false);
        return new DatabaseAdapterHolder(view);
    }

    @Override
    public void onBindViewHolder(DatabaseAdapterHolder holder, int position) {
        holder.bind(position);
    }


    @Override
    public int getItemCount() {
        return parkingResponses.size();
    }

    public void addSpaces(List<ParkingResponse> parkingResponses) {
        parkingResponses.addAll(parkingResponses);
        notifyDataSetChanged();
    }

    class DatabaseAdapterHolder extends RecyclerView.ViewHolder  {
        final TextView tv_name;
        final TextView tv_isReserved;

        DatabaseAdapterHolder (View itemView) {
            super(itemView);

            tv_name = itemView.findViewById(R.id.tv_name);
            tv_isReserved =itemView.findViewById(R.id.tv_isReserved);

        }
        void bind(int position) {
                tv_name.setText(String.valueOf(parkingResponses.get(position).getName()));
                tv_isReserved.setText("Reserved until: " + String.valueOf(parkingResponses.get(position).getIsReserved()));
            }
        }
    }

