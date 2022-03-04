package com.example.give_and_take.controllers;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.give_and_take.databinding.ServicesContainerBinding;
import com.example.give_and_take.listeners.ServiceListener;
import com.example.give_and_take.models.Service;

import java.util.List;

public class ServicesController extends RecyclerView.Adapter<ServicesController.ServiceViewHolder> {

    private final List<Service> services;
    private final ServiceListener serviceListener;

    public ServicesController(List<Service> services, ServiceListener serviceListener) {
        this.services = services;
        this.serviceListener = serviceListener;
    }

    @NonNull
    @Override
    public ServicesController.ServiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ServicesContainerBinding servicesContainerBinding = ServicesContainerBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );

        return new ServicesController.ServiceViewHolder(servicesContainerBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ServicesController.ServiceViewHolder holder, int position) {
        holder.setServiceData(services.get(position));
    }

    @Override
    public int getItemCount() {

        return services.size();
    }

    class ServiceViewHolder extends RecyclerView.ViewHolder {
        ServicesContainerBinding binding;
        ServiceViewHolder(ServicesContainerBinding servicesContainerBinding){
            super(servicesContainerBinding.getRoot());
            binding = servicesContainerBinding;
        }
        void setServiceData(Service service){
            binding.textName.setText(service.name);
            binding.textAddress.setText(service.address);
            binding.imageService.setImageBitmap(getServiceImage(service.image));
            binding.getRoot().setOnClickListener(v -> serviceListener.onServiceClicked(service));
        }
    }

    private Bitmap getServiceImage(String encodedImage){
        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

}
