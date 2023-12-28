package com.example.chatapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.example.chatapp.databinding.ActivityMainBinding;
import com.example.chatapp.utilities.Constants;
import com.example.chatapp.utilities.PreferenceManager;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private PreferenceManager preferenceManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        loadUserDetails();
        getToken();
    }

    private void loadUserDetails(){
        binding.textName.setText(preferenceManager.getString(Constants.KEY_NAME));
    }

    private void showToast(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void getToken() {
        FirebaseMessaging.getInstance().getToken()
                .addOnSuccessListener(this::updateToken)
                .addOnFailureListener(e -> {
                    showToast("Failed to get FCM token: " + e.getMessage());
                    e.printStackTrace();
                });
    }

    private void updateToken(String token) {
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference documentReference =
                database.collection(Constants.KEY_COLLECTION_USERS).document(
                        preferenceManager.getString(Constants.KEY_USER_ID)
                );
        documentReference.update(Constants.KEY_FCM_TOKEN, token)
                .addOnSuccessListener(unused -> showToast("Token Update Successfully"))
                .addOnFailureListener(e -> showToast("Unable to update token"));
    }

}