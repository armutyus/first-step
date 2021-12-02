package com.armutyus.phonebookofd.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.armutyus.phonebookofd.R;
import com.armutyus.phonebookofd.databinding.MainFragmentBinding;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainFragment extends Fragment {

    private MainFragmentBinding binding;
    PhoneDb phoneDb;
    PhoneDao phoneDao;
    private final CompositeDisposable mDisposable = new CompositeDisposable();
    OfdAdapter phoneListAdapter;


    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        phoneDb = Room.databaseBuilder(requireContext(),
                PhoneDb.class, "Persons")
                .build();

        phoneDao = phoneDb.phoneDao();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //return inflater.inflate(R.layout.main_fragment, container, false);
        binding = MainFragmentBinding.inflate(inflater,container,false);
        View view = binding.getRoot();
        return view;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(requireContext());
        binding.recyclerView.setLayoutManager(layoutManager);
        getData();

    }

    public void getData() {

        mDisposable.add(phoneDao.getPhoneRoomWithNameAndId()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(MainFragment.this::handleResponse));

    }

    private void handleResponse(List<PhoneRoom> phoneRoomList) {

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        phoneListAdapter = new OfdAdapter(phoneRoomList);
        binding.recyclerView.setAdapter(phoneListAdapter);
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        MenuInflater menuInflater = requireActivity().getMenuInflater();
        menuInflater.inflate(R.menu.phone_menu,menu);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.new_number) {
            NavDirections directions = MainFragmentDirections.actionMainFragmentToSecondFragment("new");
            NavHostFragment.findNavController(this).navigate(directions);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        mDisposable.clear();
    }
}