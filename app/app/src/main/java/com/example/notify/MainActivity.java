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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sjecnotify.Model.Admin;
import com.example.sjecnotify.Prevalent.Prevalent;
import com.github.ybq.android.spinkit.style.CubeGrid;
import com.github.ybq.android.spinkit.style.FadingCircle;
import com.github.ybq.android.spinkit.style.FoldingCube;
import com.github.ybq.android.spinkit.style.Wave;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

public class MainActivity extends AppCompatActivity {

    private Button loginBtn;
    private EditText loginName,loginPassword;
    private TextView loginValidation;
    private ProgressDialog loadingBar;
    private String parentDbname="Admin";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(!isConnected(MainActivity.this))
            buildDialog(MainActivity.this).show();

        else {

            loginBtn = findViewById(R.id.login_btn);
            loginName = findViewById(R.id.login_name);
            loginPassword = findViewById(R.id.login_password);
            loginValidation = findViewById(R.id.login_validation);

            loadingBar = new ProgressDialog(this);
            FadingCircle doubleBounce = new FadingCircle();
            loadingBar.setIndeterminateDrawable(doubleBounce);


            loginBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    validateLogin();

//                Intent intent=new Intent(MainActivity.this,UserActivity.class);
//                startActivity(intent);
                }
            });
        }


    }

    private void validateLogin()
    {
        String name=loginName.getText().toString();
        String password=loginPassword.getText().toString();

        if(TextUtils.isEmpty(name))
        {
            loginValidation.setVisibility(View.VISIBLE);
            loginValidation.setText("Enter Your Name");
        }
        else if(TextUtils.isEmpty(password))
        {
            loginValidation.setVisibility(View.VISIBLE);
            loginValidation.setText("Enter Password");
        }
        else
        {
                loadingBar.setTitle("Checking Account");
                loadingBar.setMessage("Please wait, while we are authenticating..");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();
                AllowAccessToAccount(name, password);
        }
    }

    private void AllowAccessToAccount(final String name, final String password)
    {
        final DatabaseReference RootRef;
        RootRef= FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(parentDbname).child(name).exists())
                {
                    Admin userData=dataSnapshot.child(parentDbname).child(name).getValue(Admin.class);

                    if(userData.getName().equals(name))
                    {
                        if(userData.getPassword().equals(password))
                        {

                                Toast.makeText(MainActivity.this, "Welcome Admin, You Logged In Successfully...", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();

                                Intent intent=new Intent(MainActivity.this, AdminNoticeActivity.class);
                                startActivity(intent);
                        }
                        else
                        {
                            loadingBar.dismiss();
                            loginValidation.setVisibility(View.VISIBLE);
                            loginValidation.setText("Password is incorrect.");

                        }
                    }
                }
                else
                {
                    loadingBar.dismiss();
                    loginValidation.setVisibility(View.VISIBLE);
                    loginValidation.setText("Name mismatch");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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

    public AlertDialog.Builder buildDialog(Context c) {

        AlertDialog.Builder builder = new AlertDialog.Builder(c);
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
