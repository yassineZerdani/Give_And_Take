package com.example.give_and_take.controllers;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.give_and_take.databinding.MyServicesContainerBinding;
import com.example.give_and_take.listeners.ServiceListener;
import com.example.give_and_take.models.Service;


import java.util.List;

public class MyServicesController extends RecyclerView.Adapter<MyServicesController.MyServiceViewHolder> {

    private final List<Service> services;
    private final ServiceListener serviceListener;

    public MyServicesController(List<Service> services, ServiceListener serviceListener) {
        this.services = services;
        this.serviceListener = serviceListener;
    }

    @NonNull
    @Override
    public MyServicesController.MyServiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        MyServicesContainerBinding myServicesContainerBinding = MyServicesContainerBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );

        return new MyServicesController.MyServiceViewHolder(myServicesContainerBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyServicesController.MyServiceViewHolder holder, int position) {
        holder.setServiceData(services.get(position));
    }

    @Override
    public int getItemCount() {

        return services.size();
    }

    class MyServiceViewHolder extends RecyclerView.ViewHolder {
        MyServicesContainerBinding binding;
        MyServiceViewHolder(MyServicesContainerBinding myServicesContainerBinding){
            super(myServicesContainerBinding.getRoot());
            binding = myServicesContainerBinding;
        }
        void setServiceData(Service service){
            binding.textName.setText(service.name);
            binding.imageService.setImageBitmap(getServiceImage(service.image));
            binding.getRoot().setOnClickListener(v -> serviceListener.onServiceClicked(service));
        }
    }

    private Bitmap getServiceImage(String encodedImage){
        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }


}
