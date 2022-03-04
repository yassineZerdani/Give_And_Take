package com.example.give_and_take;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.give_and_take.controllers.ServicesController;
import com.example.give_and_take.databinding.ActivityServicesBinding;
import com.example.give_and_take.listeners.ServiceListener;
import com.example.give_and_take.models.Service;
import com.example.give_and_take.utilities.Constants;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ServicesActivity extends AppCompatActivity implements ServiceListener {

    private ActivityServicesBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityServicesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
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
                    if(task.isSuccessful() && task.getResult() != null){
                        List<Service> services = new ArrayList<>();
                        for(QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {

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
                            ServicesController servicesController = new ServicesController(services, this);
                            binding.usersRecyclerView.setAdapter(servicesController);
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
        Intent intent = new Intent(getApplicationContext(), ServiceActivity.class);
        intent.putExtra(Constants.KEY_SERVICE, service);
        startActivity(intent);
        finish();
    }
}