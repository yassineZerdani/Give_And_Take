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

import com.example.give_and_take.databinding.ActivityAddObjectBinding;
import com.example.give_and_take.utilities.Constants;
import com.example.give_and_take.utilities.PreferenceManager;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;

public class AddObjectActivity extends AppCompatActivity {

    private ActivityAddObjectBinding binding;
    private String encodedImage;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddObjectBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        setListeners();
    }

    private void setListeners(){
        binding.addObject.setOnClickListener(v -> {
            if(isValidObjectDetails()){
                addObject();
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

    private void addObject(){
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        HashMap<String, Object> object = new HashMap<>();
        String currentUserId = preferenceManager.getString(Constants.KEY_USER_ID);
        object.put(Constants.KEY_OBJECT_IMAGE, encodedImage);
        object.put(Constants.KEY_OBJECT_AUTHOR, currentUserId);
        object.put(Constants.KEY_OBJECT_NAME, binding.objectName.getText().toString());
        object.put(Constants.KEY_OBJECT_DESCRIPTION, binding.objectDescription.getText().toString());
        object.put(Constants.KEY_OBJECT_ADDRESS, binding.objectAddress.getText().toString());
        object.put(Constants.KEY_OBJECT_CATEGORY, binding.objectCategory.getText().toString());
        object.put(Constants.KEY_OBJECT_TARGET, binding.objectTarget.getText().toString());
        object.put(Constants.KEY_OBJECT_TIMESTAMP, new Date());
        database.collection(Constants.KEY_COLLECTION_OBJECTS).add(object).addOnSuccessListener(documentReference -> {
            Intent intent = new Intent(getApplicationContext(), ObjectsActivity.class);
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
                            binding.imageObject.setImageBitmap(bitmap);
                            binding.textAddImage.setVisibility(View.GONE);
                            encodedImage = encodeImage(bitmap);
                        }catch(FileNotFoundException e){
                            e.printStackTrace();
                        }
                    }
                }
            }
    );

    private Boolean isValidObjectDetails(){
        if(encodedImage == null){
            showToast("Select Object Image");
            return false;
        }
        else if(binding.objectName.getText().toString().trim().isEmpty()){
            showToast("Enter Object Name");
            return false;
        }
        else if(binding.objectDescription.getText().toString().trim().isEmpty()){
            showToast("Enter Object Description");
            return false;
        }
        else if(binding.objectAddress.getText().toString().trim().isEmpty()){
            showToast("Enter Object Address");
            return false;
        }
        else if(binding.objectCategory.getText().toString().trim().isEmpty()){
            showToast("Enter Object Category");
            return false;
        }
        else if(binding.objectTarget.getText().toString().trim().isEmpty()){
            showToast("Enter Object Target");
            return false;
        }
        else {
            return true;
        }
    }
}