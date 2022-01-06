package com.dhinakaran.dgsofttech.worker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class addnewaddress extends AppCompatActivity {


    private Button saveBTN;
    private Dialog loadingdialog;
    private EditText name, phone, city, locality, flatno, pincode, landmark, onumber;
    private boolean UPDATEADDRESSES = false;
    private addressesModel addressesModel;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_address);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Add a new address");

        name = findViewById(R.id.user_name);
        phone = findViewById(R.id.userPhoneNumber);
        city = findViewById(R.id.userCity);
        locality = findViewById(R.id.userLocality);
        flatno = findViewById(R.id.userBuildingNo);
        pincode = findViewById(R.id.UserPincode);
        landmark = findViewById(R.id.userLandmark);
        onumber = findViewById(R.id.userOptionalPhone);

        loadingdialog = new Dialog(addnewaddress.this);
        loadingdialog.setContentView(R.layout.loading_prog);
        loadingdialog.setCancelable(false);
        loadingdialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.slider_bg));
        loadingdialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        saveBTN = findViewById(R.id.save_btn);
        if (getIntent().getStringExtra("INTENT").equals("update_address")) {
            UPDATEADDRESSES = true;
            position = getIntent().getIntExtra("index", -1);
            addressesModel = dbQuaries.addressesModelList.get(position);

            city.setText(addressesModel.getCity());
            pincode.setText(addressesModel.getPincode());
            landmark.setText(addressesModel.getLandmark());
            locality.setText(addressesModel.getLocality());
            phone.setText(addressesModel.getPhone());
            onumber.setText(addressesModel.getOnumber());
            flatno.setText(addressesModel.getFlatno());
            name.setText(addressesModel.getName());
            saveBTN.setText("Update");

        } else {
            position = (int) dbQuaries.addressesModelList.size();
        }

        saveBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chkinputs();
            }
        });

    }

    private void chkinputs() {
        if (!name.getText().toString().isEmpty()) {
            if (!phone.getText().toString().isEmpty() && phone.getText().length() == 10) {
                if (!city.getText().toString().isEmpty()) {
                    if (!locality.getText().toString().isEmpty()) {
                        if (!flatno.getText().toString().isEmpty()) {
                            if (!pincode.getText().toString().isEmpty() && (pincode.getText().toString().length() == 6)) {
                                if (!landmark.getText().toString().isEmpty()) {
                                    if (!onumber.getText().toString().isEmpty()) {
                                        loadingdialog.show();

                                        Map<String, Object> addAddresses = new HashMap<>();
                                        addAddresses.put("mobile_no_" + String.valueOf(position + 1), phone.getText().toString());
                                        addAddresses.put("alter_mobile_no_" + String.valueOf(position + 1), onumber.getText().toString());
                                        addAddresses.put("fullname_" + String.valueOf(position + 1), name.getText().toString());
                                        addAddresses.put("city_" + String.valueOf(position + 1), city.getText().toString());
                                        addAddresses.put("locality_" + String.valueOf(position + 1), locality.getText().toString());
                                        addAddresses.put("flatno_" + String.valueOf(position + 1), flatno.getText().toString());
                                        addAddresses.put("pincode_" + String.valueOf(position + 1), pincode.getText().toString());
                                        addAddresses.put("landmark_" + String.valueOf(position + 1), landmark.getText().toString());

                                        if (!UPDATEADDRESSES) {
                                            addAddresses.put("list_size", (long) dbQuaries.addressesModelList.size() + 1);
                                            if (getIntent().getStringExtra("INTENT").equals("manage")) {
                                                if (dbQuaries.addressesModelList.size() == 0) {
                                                    addAddresses.put("selected_" + String.valueOf(position + 1), true);
                                                } else {
                                                    addAddresses.put("selected_" + String.valueOf(position + 1), true);
                                                }
                                            } else {
                                                addAddresses.put("selected_" + String.valueOf(position + 1), false);
                                            }

                                            if (dbQuaries.addressesModelList.size() > 0) {
                                                addAddresses.put("selected_" + (dbQuaries.selectedAddresses + 1), false);
                                            }
                                        }
                                        FirebaseFirestore.getInstance().collection("USERS")
                                                .document(FirebaseAuth.getInstance().getUid()).collection("USERDATA")
                                                .document("MY_ADDRESSES")
                                                .update(addAddresses).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    if (!UPDATEADDRESSES) {
                                                        if (dbQuaries.addressesModelList.size() > 0) {
                                                            dbQuaries.addressesModelList.get(dbQuaries.selectedAddresses).setSelected(false);
                                                        }
                                                        dbQuaries.addressesModelList.add(new addressesModel(
                                                                name.getText().toString(),
                                                                phone.getText().toString(),
                                                                city.getText().toString(),
                                                                locality.getText().toString(),
                                                                flatno.getText().toString(),
                                                                pincode.getText().toString(),
                                                                landmark.getText().toString(),
                                                                onumber.getText().toString(),
                                                                true));
                                                        if (getIntent().getStringExtra("INTENT").equals("manage")) {
                                                            if (dbQuaries.addressesModelList.size() == 0) {
                                                                dbQuaries.selectedAddresses = dbQuaries.addressesModelList.size() - 1;
                                                            }
                                                        } else {
                                                            dbQuaries.selectedAddresses = dbQuaries.addressesModelList.size() - 1;
                                                        }


                                                    } else {
                                                        dbQuaries.addressesModelList.set(position, new addressesModel(
                                                                name.getText().toString(),
                                                                phone.getText().toString(),
                                                                city.getText().toString(),
                                                                locality.getText().toString(),
                                                                flatno.getText().toString(),
                                                                pincode.getText().toString(),
                                                                landmark.getText().toString(),
                                                                onumber.getText().toString(),
                                                                true));
                                                    }

                                                    if (getIntent().getStringExtra("INTENT").equals("deliveryIntent")) {
                                                        Intent intent = new Intent(addnewaddress.this, delivery.class);
                                                        startActivity(intent);
                                                    } else {
                                                        myaddresses.refreshitem(dbQuaries.selectedAddresses, dbQuaries.addressesModelList.size() - 1);
                                                    }
                                                    finish();
                                                } else {
                                                    Toast.makeText(addnewaddress.this, "Error: " + task.getException(), Toast.LENGTH_SHORT).show();
                                                }
                                                loadingdialog.dismiss();
                                            }
                                        });
                                    } else {
                                        onumber.setError("This is an mandatory field");
                                    }
                                } else {
                                    landmark.setError("This is an mandatory field");
                                }
                            } else {
                                loadingdialog.dismiss();
                                pincode.setError("This is an mandatory field");
                                Toast.makeText(addnewaddress.this, "Please provide valid pincode", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            flatno.setError("This is an mandatory field");
                        }
                    } else {
                        locality.setError("This is an mandatory field");
                    }
                } else {
                    city.setError("This is an mandatory field");
                }
            } else {
                phone.setError("This is an mandatory field");
                Toast.makeText(addnewaddress.this, "Please provide valid phone number", Toast.LENGTH_SHORT).show();
            }
        } else {
            name.setError("This is an mandatory field");
        }

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}