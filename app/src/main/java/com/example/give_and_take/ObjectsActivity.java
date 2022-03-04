package com.example.give_and_take;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.give_and_take.controllers.ObjectsController;
import com.example.give_and_take.databinding.ActivityObjectsBinding;
import com.example.give_and_take.listeners.ObjectListener;
import com.example.give_and_take.models.Object;
import com.example.give_and_take.utilities.Constants;
import com.example.give_and_take.utilities.PreferenceManager;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ObjectsActivity extends AppCompatActivity implements ObjectListener {

    private ActivityObjectsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityObjectsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListeners();
        getObjects();
    }

    private void setListeners(){
        binding.imageBack.setOnClickListener(v -> onBackPressed());
    }

    private void getObjects(){
        loading(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_OBJECTS)
                .get()
                .addOnCompleteListener(task -> {
                    loading(false);
                    if(task.isSuccessful() && task.getResult() != null){
                        List<Object> objects = new ArrayList<>();
                        for(QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {

                            Object object = new Object();
                            object.name = queryDocumentSnapshot.getString(Constants.KEY_OBJECT_NAME);
                            object.description = queryDocumentSnapshot.getString(Constants.KEY_OBJECT_DESCRIPTION);
                            object.image = queryDocumentSnapshot.getString(Constants.KEY_OBJECT_IMAGE);
                            object.address = queryDocumentSnapshot.getString(Constants.KEY_OBJECT_ADDRESS);
                            object.id = queryDocumentSnapshot.getId();
                            objects.add(object);
                        }
                        if(objects.size() > 0){
                            ObjectsController objectsController = new ObjectsController(objects, this);
                            binding.usersRecyclerView.setAdapter(objectsController);
                            binding.usersRecyclerView.setVisibility(View.VISIBLE);
                        }else{
                            showErrorMessage();
                        }
                    }else{
                        showErrorMessage();
                    }
                });
    }

    private void showErrorMessage(){
        binding.textErrorMessage.setText(String.format("%s", "No Object Available"));
        binding.textErrorMessage.setVisibility(View.VISIBLE);
    }

    private void loading(Boolean isLoading){
        if(isLoading){
            binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            binding.progressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onObjectClicked(Object object) {
        Intent intent = new Intent(getApplicationContext(), ObjectActivity.class);
        intent.putExtra(Constants.KEY_OBJECT, object);
        startActivity(intent);
        finish();
    }
}