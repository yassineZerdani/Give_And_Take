package com.example.give_and_take;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;

import com.example.give_and_take.databinding.ActivityMyServiceBinding;
import com.example.give_and_take.models.Service;
import com.example.give_and_take.utilities.Constants;
import com.google.firebase.firestore.FirebaseFirestore;


public class MyServiceActivity extends AppCompatActivity {

    private ActivityMyServiceBinding binding;
    private Service service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyServiceBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        loadServiceDetails();
    }



    private Bitmap getBitmapFromEncodedString(String encodedImage){
        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    private void loadServiceDetails(){
        service = (Service) getIntent().getSerializableExtra(Constants.KEY_SERVICE);
        binding.imageService.setImageBitmap(getBitmapFromEncodedString(service.image));
        binding.textName.setText(service.name);
        binding.textAddress.setText(service.address);
        binding.textDescription.setText(service.description);
        binding.textAuthor.setText(service.author);
        binding.textCategory.setText(service.category);
        binding.delete.setOnClickListener(v -> {
            FirebaseFirestore database = FirebaseFirestore.getInstance();
            database.collection(Constants.KEY_COLLECTION_SERVICES).document(service.id).delete();
            Intent intent = new Intent(getApplicationContext(), MyServicesActivity.class);
            startActivity(intent);
            finish();
        });
    }

}