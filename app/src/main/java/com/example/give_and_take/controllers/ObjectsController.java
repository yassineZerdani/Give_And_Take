package com.example.give_and_take.controllers;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.give_and_take.databinding.ObjectsContainerBinding;
import com.example.give_and_take.listeners.ObjectListener;
import com.example.give_and_take.models.Object;

import java.util.List;

public class ObjectsController extends RecyclerView.Adapter<ObjectsController.ObjectViewHolder> {

    private final List<Object> objects;
    private final ObjectListener objectListener;

    public ObjectsController(List<Object> objects, ObjectListener objectListener) {
        this.objects = objects;
        this.objectListener = objectListener;
    }

    @NonNull
    @Override
    public ObjectsController.ObjectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ObjectsContainerBinding objectsContainerBinding = ObjectsContainerBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );

        return new ObjectsController.ObjectViewHolder(objectsContainerBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ObjectsController.ObjectViewHolder holder, int position) {
        holder.setObjectData(objects.get(position));
    }

    @Override
    public int getItemCount() {

        return objects.size();
    }

    class ObjectViewHolder extends RecyclerView.ViewHolder {
        ObjectsContainerBinding binding;
        ObjectViewHolder(ObjectsContainerBinding objectsContainerBinding){
            super(objectsContainerBinding.getRoot());
            binding = objectsContainerBinding;
        }
        void setObjectData(Object object){
            binding.textName.setText(object.name);
            binding.textAddress.setText(object.address);
            binding.imageObject.setImageBitmap(getObjectImage(object.image));
            binding.getRoot().setOnClickListener(v -> objectListener.onObjectClicked(object));
        }
    }

    private Bitmap getObjectImage(String encodedImage){
        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

}
