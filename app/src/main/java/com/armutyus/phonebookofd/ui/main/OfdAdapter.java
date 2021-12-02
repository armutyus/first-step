package com.armutyus.phonebookofd.ui.main;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;


import com.armutyus.phonebookofd.databinding.RecyclerRowBinding;

import java.util.List;

import io.reactivex.rxjava3.annotations.NonNull;

public class OfdAdapter extends RecyclerView.Adapter<OfdAdapter.OfdHolder> {


    List<PhoneRoom> phoneRoomList;

    public OfdAdapter(List<PhoneRoom> phoneRoomList) {
        this.phoneRoomList = phoneRoomList;
    }

    class OfdHolder extends RecyclerView.ViewHolder {

        private RecyclerRowBinding binding;

        public OfdHolder(RecyclerRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }


    @NonNull
    @Override
    public OfdHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerRowBinding binding = RecyclerRowBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new OfdHolder(binding);
    }

    @Override
    public void onBindViewHolder(OfdAdapter.OfdHolder holder, int position) {
        holder.binding.textView.setText(phoneRoomList.get(position).name);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainFragmentDirections.ActionMainFragmentToSecondFragment action = MainFragmentDirections.actionMainFragmentToSecondFragment("old");
                action.setPersonId(phoneRoomList.get(position).id);
                action.setInfo("old");
                Navigation.findNavController(view).navigate(action);

            }
        });
    }


    @Override
    public int getItemCount() {
        return phoneRoomList.size();
    }

}
