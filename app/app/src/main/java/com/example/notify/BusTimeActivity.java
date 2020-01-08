package com.example.sjecnotify;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;

public class BusTimeActivity extends AppCompatActivity {

    private EditText busTime,busNum,busStops,busDriver,driverNumber;
    private Button upload;
    private TextView validate;
    private ImageView back;

    String time,num,place,driver,driveernum;


    private DatabaseReference ProductRef;
    private RelativeLayout r1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_time);
        if(!isConnected(BusTimeActivity.this))
            buildDialog(BusTimeActivity.this).show();

        else {

            busTime = findViewById(R.id.bus_time);
            busNum = findViewById(R.id.bus_number);
            busStops = findViewById(R.id.bus_place);
            busDriver = findViewById(R.id.bus_driver_name);
            driverNumber = findViewById(R.id.bus_driver_number);
            upload = findViewById(R.id.bus_upload_btn);
            validate = findViewById(R.id.bus_upload_validtion);
            back = findViewById(R.id.backimage);

            r1 = findViewById(R.id.l1);

            ProductRef = FirebaseDatabase.getInstance().getReference().child("Bus");

            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(BusTimeActivity.this, AdminNoticeActivity.class));
                }
            });


            upload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    validateBusNotice();
                }
            });
        }




    }

    private void validateBusNotice()
    {
        time=busTime.getText().toString();
        num=busNum.getText().toString();
        place=busStops.getText().toString();
        driver=busDriver.getText().toString();
        driveernum=driverNumber.getText().toString();


        if(TextUtils.isEmpty(time))
        {
            validate.setVisibility(View.VISIBLE);
            validate.setText("Set Bus Time");
        }
        else if(TextUtils.isEmpty(num))
        {
            validate.setVisibility(View.VISIBLE);
            validate.setText("Bus number is mandatory");
        }
        else if(TextUtils.isEmpty(place))
        {
            validate.setVisibility(View.VISIBLE);
            validate.setText("Enter Bus Stops");
        }
        else if(TextUtils.isEmpty(driver))
        {
            validate.setVisibility(View.VISIBLE);
            validate.setText("Enter Bus Driver's Name");
        }
        else if(TextUtils.isEmpty(driveernum))
        {
            validate.setVisibility(View.VISIBLE);
            validate.setText("Enter Bus Driver's Phone Number");
        }
        else
        {
            StoreBusInformation();
        }
    }

    private void StoreBusInformation()
    {
        r1.setVisibility(View.VISIBLE);

        HashMap<String,Object> productMap=new HashMap<>();

        productMap.put("time",time);
        productMap.put("busnumber",num);
        productMap.put("stops",place);
        productMap.put("driverName",driver);
        productMap.put("driverNumber",driveernum);

        ProductRef.child(num).updateChildren(productMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                if(task.isSuccessful())
                {
                    Toast.makeText(BusTimeActivity.this, "Bus Details Uploaded Successfully...", Toast.LENGTH_SHORT).show();
                    r1.setVisibility(View.GONE);
                    Intent intent=new Intent(BusTimeActivity.this, AdminNoticeActivity.class);
                    startActivity(intent);



                }
                else
                {
                    r1.setVisibility(View.GONE);
                    String message=task.getException().toString();
                    Toast.makeText(BusTimeActivity.this, "Error: "+message, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public boolean isConnected(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netinfo = cm.getActiveNetworkInfo();

        if (netinfo != null && netinfo.isConnectedOrConnecting()) {
            android.net.NetworkInfo wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            android.net.NetworkInfo mobile = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            if((mobile != null && mobile.isConnectedOrConnecting()) || (wifi != null && wifi.isConnectedOrConnecting())) return true;
            else return false;
        } else
            return false;
    }

    public android.app.AlertDialog.Builder buildDialog(Context c) {

        android.app.AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle("No Internet Connection");
        builder.setMessage("You need to have Mobile Data or wifi to access this. Press ok to Exit");

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                finish();
            }
        });

        return builder;
    }
}
