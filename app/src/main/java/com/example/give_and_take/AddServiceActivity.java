package com.example.give_and_take;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Toast;

import com.example.give_and_take.databinding.ActivityAddServiceBinding;
import com.example.give_and_take.utilities.Constants;
import com.example.give_and_take.utilities.PreferenceManager;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;

public class AddServiceActivity extends AppCompatActivity {

    private ActivityAddServiceBinding binding;
    private String encodedImage;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddServiceBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        setListeners();
    }

    private void setListeners(){
        binding.addService.setOnClickListener(v -> {
            if(isValidServiceDetails()){
                addService();
            }
            binding.textAddImage.setOnClickListener(d -> {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                pickImage.launch(intent);
            });
        });
    }

    private void showToast(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void addService(){
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        HashMap<String, Object> service = new HashMap<>();
        String currentUserId = preferenceManager.getString(Constants.KEY_USER_ID);
        service.put(Constants.KEY_SERVICE_IMAGE, encodedImage);
        service.put(Constants.KEY_SERVICE_AUTHOR, currentUserId);
        service.put(Constants.KEY_SERVICE_NAME, binding.serviceName.getText().toString());
        service.put(Constants.KEY_SERVICE_DESCRIPTION, binding.serviceDescription.getText().toString());
        service.put(Constants.KEY_SERVICE_ADDRESS, binding.serviceAddress.getText().toString());
        service.put(Constants.KEY_SERVICE_CATEGORY, binding.serviceCategory.getText().toString());
        service.put(Constants.KEY_SERVICE_TIMESTAMP, new Date());
        database.collection(Constants.KEY_COLLECTION_SERVICES).add(service).addOnSuccessListener(documentReference -> {
            Intent intent = new Intent(getApplicationContext(), ServicesActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }).addOnFailureListener(exception -> {
            showToast(exception.getMessage());
        });
    }

    private String encodeImage(Bitmap bitmap){
        int previewWidth = 150;
        int previewHeight = bitmap.getHeight() + previewWidth / bitmap.getWidth();
        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap, previewWidth, previewHeight, false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        previewBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();

        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }


    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if(result.getResultCode() == RESULT_OK){
                    if(result.getData() != null){
                        Uri imageUri = result.getData().getData();
                        try{
                            InputStream inputStream = getContentResolver().openInputStream(imageUri);
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            binding.imageService.setImageBitmap(bitmap);
                            binding.textAddImage.setVisibility(View.GONE);
                            encodedImage = encodeImage(bitmap);
                        }catch(FileNotFoundException e){
                            e.printStackTrace();
                        }
                    }
                }
            }
    );

    private Boolean isValidServiceDetails(){
        if(encodedImage == null){
            showToast("Select Service Image");
            return false;
        }
        else if(binding.serviceName.getText().toString().trim().isEmpty()){
            showToast("Enter Service Name");
            return false;
        }
        else if(binding.serviceDescription.getText().toString().trim().isEmpty()){
            showToast("Enter Service Description");
            return false;
        }
        else if(binding.serviceAddress.getText().toString().trim().isEmpty()){
            showToast("Enter Service Address");
            return false;
        }
        else if(binding.serviceCategory.getText().toString().trim().isEmpty()){
            showToast("Enter Service Category");
            return false;
        }
        else {
            return true;
        }
    }
}