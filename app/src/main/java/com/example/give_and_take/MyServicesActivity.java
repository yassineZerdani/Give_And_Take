package com.example.give_and_take;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.give_and_take.controllers.MyServicesController;
import com.example.give_and_take.databinding.ActivityMyServicesBinding;
import com.example.give_and_take.listeners.ServiceListener;
import com.example.give_and_take.models.Service;
import com.example.give_and_take.utilities.Constants;
import com.example.give_and_take.utilities.PreferenceManager;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class MyServicesActivity extends AppCompatActivity implements ServiceListener {

    private ActivityMyServicesBinding binding;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyServicesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        setListeners();
        getServices();
    }

    private void setListeners(){
        binding.imageBack.setOnClickListener(v -> onBackPressed());
    }

    private void getServices(){
        loading(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_SERVICES)
                .get()
                .addOnCompleteListener(task -> {
                    loading(false);
                    String currentUserId = preferenceManager.getString(Constants.KEY_USER_ID);
                    if(task.isSuccessful() && task.getResult() != null){
                        List<Service> services = new ArrayList<>();
                        for(QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                            if(currentUserId.equals(queryDocumentSnapshot.getString(Constants.KEY_SERVICE_AUTHOR))){
                                continue;
                            }
                            Service service = new Service();
                            service.name = queryDocumentSnapshot.getString(Constants.KEY_SERVICE_NAME);
                            service.description = queryDocumentSnapshot.getString(Constants.KEY_SERVICE_DESCRIPTION);
                            service.author = queryDocumentSnapshot.getString(Constants.KEY_SERVICE_AUTHOR);
                            service.image = queryDocumentSnapshot.getString(Constants.KEY_SERVICE_IMAGE);
                            service.address = queryDocumentSnapshot.getString(Constants.KEY_SERVICE_ADDRESS);
                            service.category = queryDocumentSnapshot.getString(Constants.KEY_SERVICE_CATEGORY);
                            service.id = queryDocumentSnapshot.getId();
                            services.add(service);
                        }
                        if(services.size() > 0){
                            MyServicesController myServicesController = new MyServicesController(services, this);
                            binding.myObjectsRecyclerView.setAdapter(myServicesController);
                            binding.myObjectsRecyclerView.setVisibility(View.VISIBLE);
                        }else{
                            showErrorMessage();
                        }
                    }else{
                        showErrorMessage();
                    }
                });
    }

    private void showErrorMessage(){
        binding.textErrorMessage.setText(String.format("%s", "No Service Available"));
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
    public void onServiceClicked(Service service) {
        Intent intent = new Intent(getApplicationContext(), MyServiceActivity.class);
        intent.putExtra(Constants.KEY_SERVICE, service);
        startActivity(intent);
        finish();
    }
}