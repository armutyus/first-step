package com.armutyus.phonebookofd.ui.main;

import static android.content.Context.MODE_PRIVATE;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.room.Room;

import com.armutyus.phonebookofd.R;
import com.armutyus.phonebookofd.databinding.FragmentSecondBinding;
import com.google.android.material.snackbar.Snackbar;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class SecondFragment extends Fragment {

    private FragmentSecondBinding secondBinding;
    ActivityResultLauncher<Intent> activityResultLauncher;
    ActivityResultLauncher<String> permissionLauncher;
    Bitmap selectedImage;
    PhoneDao phoneDao;
    PhoneDb phoneDb;
    private final CompositeDisposable mDisposable = new CompositeDisposable();
    SQLiteDatabase database;
    PhoneRoom phoneRoomFromMain;
    String info = "";

    public SecondFragment() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerLauncher();
        phoneDb = Room.databaseBuilder(requireActivity(),
                PhoneDb.class, "Persons")
                .build();

        phoneDao = phoneDb.phoneDao();

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //return inflater.inflate(R.layout.fragment_second, container, false);
        secondBinding = FragmentSecondBinding.inflate(inflater,container,false);
        View view = secondBinding.getRoot();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        database = requireActivity().openOrCreateDatabase("Persons", MODE_PRIVATE,null);

        if(getArguments() != null) {
            info = SecondFragmentArgs.fromBundle(getArguments()).getInfo();
        } else {
            info = "new";
        }


        ImageView imageButton = view.findViewById(R.id.selectImage);
        imageButton.setOnClickListener(this::selectButton);

        Button savedButton = view.findViewById(R.id.saveTusu);
        savedButton.setOnClickListener(this::saveButton);

        Button deletedButton = view.findViewById(R.id.deleteTusu);
        deletedButton.setOnClickListener(this::deleteButton);

        if (info.equals("new")) {
            secondBinding.nameText.setText("");
            secondBinding.phoneText.setText("");
            secondBinding.saveTusu.setVisibility(View.VISIBLE);
            secondBinding.deleteTusu.setVisibility(View.GONE);

            secondBinding.selectImage.setImageResource(R.drawable.clickimage);

        } else {
            int personId = SecondFragmentArgs.fromBundle(getArguments()).getPersonId();
            secondBinding.saveTusu.setVisibility(View.GONE);
            secondBinding.deleteTusu.setVisibility(View.VISIBLE);

            mDisposable.add(phoneDao.getPhoneRoomById(personId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(SecondFragment.this::handleResponseWithOldArt));
        }

    }

    private void handleResponseWithOldArt(PhoneRoom phoneRoom) {
        phoneRoomFromMain = phoneRoom;
        secondBinding.nameText.setText(phoneRoom.name);
        secondBinding.phoneText.setText(phoneRoom.phone);

        Bitmap bitmap = BitmapFactory.decodeByteArray(phoneRoom.personImage,0,phoneRoom.personImage.length);
        secondBinding.selectImage.setImageBitmap(bitmap);
    }


    public void selectButton(View view) {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Snackbar.make(view,"Permission needed for gallery", Snackbar.LENGTH_INDEFINITE).setAction("Give Permission", v -> permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)).show();
            } else {
                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        } else {
            Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            activityResultLauncher.launch(intentToGallery);
        }

    }

    public void saveButton(View view) {

        String nameText = secondBinding.nameText.getText().toString();
        String phoneText = secondBinding.phoneText.getText().toString();

        Bitmap smallImage = makeSmallerImage(selectedImage,300);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        smallImage.compress(Bitmap.CompressFormat.PNG,50,outputStream);
        byte[] byteArray = outputStream.toByteArray();

        PhoneRoom phoneRoom = new PhoneRoom(nameText, phoneText, byteArray);

        mDisposable.add(phoneDao.insert(phoneRoom)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(SecondFragment.this::handleResponse));

    }

    public void deleteButton(View view) {
        mDisposable.add(phoneDao.delete(phoneRoomFromMain)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(SecondFragment.this::handleResponse));
    }

    private void handleResponse() {
        NavDirections action = SecondFragmentDirections.actionSecondFragmentToMainFragment();
        Navigation.findNavController(requireView()).navigate(action);
    }

    public void registerLauncher() {
        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent intentFromResult = result.getData();
                        if (intentFromResult != null) {
                            Uri imageData = intentFromResult.getData();
                            try {

                                if (Build.VERSION.SDK_INT >= 28) {
                                    ImageDecoder.Source source = ImageDecoder.createSource(requireActivity().getContentResolver(),imageData);
                                    selectedImage = ImageDecoder.decodeBitmap(source);
                                    secondBinding.selectImage.setImageBitmap(selectedImage);

                                } else {
                                    selectedImage = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(),imageData);
                                    secondBinding.selectImage.setImageBitmap(selectedImage);
                                }

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                    }
                });


        permissionLauncher =
                registerForActivityResult(new ActivityResultContracts.RequestPermission(), result -> {
                    if(result) {
                        //permission granted
                        Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        activityResultLauncher.launch(intentToGallery);

                    } else {
                        //permission denied
                        Toast.makeText(getActivity(),"Permission needed!",Toast.LENGTH_LONG).show();
                    }
                });
    }

    public Bitmap makeSmallerImage(Bitmap image, int maximumSize) {

        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;

        if (bitmapRatio > 1) {
            width = maximumSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maximumSize;
            width = (int) (height * bitmapRatio);
        }

        return Bitmap.createScaledBitmap(image,width,height,true);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        secondBinding = null;
        mDisposable.clear();
    }


}