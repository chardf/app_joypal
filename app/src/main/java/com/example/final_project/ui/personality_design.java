package com.example.final_project.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.final_project.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.example.final_project.data.database.AppDatabase;
import com.example.final_project.data.DAO.ImageRoleDao;
import com.example.final_project.data.model.Entity.ImageRoleEntity;

public class personality_design extends AppCompatActivity {

    private EditText nameEditText, lookEditText, genderEditText, personalityEditText;
    private MaterialButton nextButton, skip;
    private TextView uploadReferenceImage, referenceImageFilename, changeReferenceImage;
    private ImageView deleteReferenceImage;
    private LinearLayout imagePreviewContainer;
    private String selectedImagePath = null;

    private ActivityResultLauncher<String> requestPermissionLauncher;
    private ActivityResultLauncher<Intent> pickImageLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.personality_design);

        initializeViews();
        setupLaunchers();
        setupListeners();

        updateNextButtonState(false);
    }

    private void initializeViews() {
        nameEditText = findViewById(R.id.nameEditText);
        lookEditText = findViewById(R.id.look);
        genderEditText = findViewById(R.id.gender);
        personalityEditText = findViewById(R.id.personality);
        nextButton = findViewById(R.id.button_design2);
        skip = findViewById(R.id.skip);
        uploadReferenceImage = findViewById(R.id.upload_reference_image);

        // New views for image preview
        imagePreviewContainer = findViewById(R.id.image_preview_container);
        deleteReferenceImage = findViewById(R.id.delete_reference_image);
        referenceImageFilename = findViewById(R.id.reference_image_filename);
        changeReferenceImage = findViewById(R.id.change_reference_image);
    }

    private void setupLaunchers() {
        requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            if (isGranted) {
                launchImagePicker();
            } else {
                showPermissionDeniedDialog();
            }
        });

        pickImageLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK && result.getData() != null && result.getData().getData() != null) {
                Uri selectedImageUri = result.getData().getData();
                selectedImagePath = selectedImageUri.toString();

                String filename = getFileName(selectedImageUri);
                referenceImageFilename.setText(filename);

                uploadReferenceImage.setVisibility(View.GONE);
                imagePreviewContainer.setVisibility(View.VISIBLE);

                Toast.makeText(this, "Reference image selected", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupListeners() {
        setupInputListeners();
        uploadReferenceImage.setOnClickListener(v -> handleImageUploadClick());
        deleteReferenceImage.setOnClickListener(v -> handleDeleteImageClick());
        changeReferenceImage.setOnClickListener(v -> handleImageUploadClick());
        setupNextButton();
        setupSkipButton();
        setupBottomNavigationView();
    }

    private void handleDeleteImageClick() {
        selectedImagePath = null;
        imagePreviewContainer.setVisibility(View.GONE);
        uploadReferenceImage.setVisibility(View.VISIBLE);
        Toast.makeText(this, "Reference image removed", Toast.LENGTH_SHORT).show();
    }

    private void handleImageUploadClick() {
        String permission = getRequiredPermission();
        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
            launchImagePicker();
        } else if (shouldShowRequestPermissionRationale(permission)) {
            showPermissionRationaleDialog();
        } else {
            requestPermissionLauncher.launch(permission);
        }
    }

    private String getRequiredPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return Manifest.permission.READ_MEDIA_IMAGES;
        } else {
            return Manifest.permission.READ_EXTERNAL_STORAGE;
        }
    }

    private void launchImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        pickImageLauncher.launch(intent);
    }
    
    private void showPermissionRationaleDialog() {
        new AlertDialog.Builder(this)
            .setTitle("Storage Permission Needed")
            .setMessage("This app needs the Storage permission to select an image. Please grant the permission.")
            .setPositiveButton("OK", (dialog, which) -> {
                requestPermissionLauncher.launch(getRequiredPermission());
            })
            .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
            .create()
            .show();
    }

    private void showPermissionDeniedDialog() {
        new AlertDialog.Builder(this)
            .setTitle("Permission Denied")
            .setMessage("You have denied the storage permission. To select an image, please go to settings and grant the permission.")
            .setPositiveButton("Go to Settings", (dialog, which) -> {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
            })
            .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
            .create()
            .show();
    }

    private void setupNextButton() {
        nextButton.setOnClickListener(v -> {
            if (nextButton.isEnabled()) {
                String name = nameEditText.getText().toString().trim();
                String look = lookEditText.getText().toString().trim();
                String gender = genderEditText.getText().toString().trim();
                String personality = personalityEditText.getText().toString().trim();
                String userInput = combineInputsAsString();

                // 新增：查重逻辑
                String userId = getSharedPreferences("user_prefs", MODE_PRIVATE).getString("userId", "");
                AppDatabase db = AppDatabase.getDatabase(this);
                ImageRoleDao dao = db.imageRoleDao();
                new Thread(() -> {
                    ImageRoleEntity exist = dao.getRoleByNameAndUserId(name, userId);
                    runOnUiThread(() -> {
                        if (exist != null) {
                            Toast.makeText(this, "你已有该角色", Toast.LENGTH_SHORT).show();
                        } else {
                            Intent intent = new Intent(this, oc_loading.class);
                            intent.putExtra("userInput", userInput);
                            intent.putExtra("roleName", name);
                            intent.putExtra("name", name);
                            intent.putExtra("look", look);
                            intent.putExtra("gender", gender);
                            intent.putExtra("personality", personality);
                            if (selectedImagePath != null) {
                                intent.putExtra("referenceImagePath", selectedImagePath);
                            }
                            startActivity(intent);
                            finish();
                        }
                    });
                }).start();
            }
        });
    }

    private void setupInputListeners() {
        TextWatcher textWatcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { checkInputs(); }
            @Override public void afterTextChanged(Editable s) {}
        };
        nameEditText.addTextChangedListener(textWatcher);
        lookEditText.addTextChangedListener(textWatcher);
        genderEditText.addTextChangedListener(textWatcher);
        personalityEditText.addTextChangedListener(textWatcher);
    }

    private void checkInputs() {
        boolean isValid = isInputValid(nameEditText) && isInputValid(lookEditText) &&
                isInputValid(genderEditText) && isInputValid(personalityEditText);
        updateNextButtonState(isValid);
    }

    private boolean isInputValid(EditText editText) {
        return editText.getText().toString().trim().length() > 0 && editText.getText().toString().trim().length() <= 30;
    }

    private void updateNextButtonState(boolean isEnabled) {
        nextButton.setEnabled(isEnabled);
        nextButton.setBackgroundColor(isEnabled ? Color.parseColor("#044132") : Color.parseColor("#B0B0B0"));
    }

    private String combineInputsAsString() {
        return "Name: " + nameEditText.getText().toString().trim() +
               ", Gender: " + genderEditText.getText().toString().trim() +
               ", Personality: " + personalityEditText.getText().toString().trim() +
               ", Look: " + lookEditText.getText().toString().trim();
    }

    private void setupBottomNavigationView() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.menu_create);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.menu_home) {
                startActivity(new Intent(this, getstart.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP));
                return true;
            } else if (itemId == R.id.menu_create) {
                return true;
            } else if (itemId == R.id.menu_joypal) {
                startActivity(new Intent(this, joypal_chat.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP));
                return true;
            } else if (itemId == R.id.menu_settings) {
                startActivity(new Intent(this, settings.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP));
                return true;
            }
            return false;
        });
    }

    private void setupSkipButton() {
        skip.setOnClickListener(v -> {
            Intent intent = new Intent(this, oc_loading.class);
            intent.putExtra("userInput", "Name: , Gender: , Personality: , Look: ");
            intent.putExtra("roleName", "");
            intent.putExtra("name", "");
            intent.putExtra("look", "");
            intent.putExtra("gender", "");
            intent.putExtra("personality", "");
            if (selectedImagePath != null) {
                intent.putExtra("referenceImagePath", selectedImagePath);
            }
            startActivity(intent);
            finish();
        });
    }

    @SuppressLint("Range")
    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }
}