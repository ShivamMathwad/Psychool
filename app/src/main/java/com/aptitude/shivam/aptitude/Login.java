package com.aptitude.shivam.aptitude;

import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.aptitude.shivam.aptitude.Model.StatusModel;
import com.aptitude.shivam.aptitude.Model.UserModel;
import com.aptitude.shivam.aptitude.Network.NetworkClient;
import com.aptitude.shivam.aptitude.Utils.Constants;

import static android.util.Log.d;

public class Login extends AppCompatActivity implements View.OnClickListener{

    EditText username,password;
    Button login;
    TextView register;
    String str_username, str_password;
    Dialog loadingDialog;
    NetworkClient.ServerCommunicator communicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Logging you in");
        builder.setMessage("Please Wait...");
        loadingDialog=builder.create();
        loadingDialog.setCanceledOnTouchOutside(false);

        init();
    }

    public void init(){
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        login = findViewById(R.id.login);
        register = findViewById(R.id.register);

        login.setOnClickListener(this);
        register.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.login:
                str_username = username.getText().toString();
                str_password = password.getText().toString();

                if(validate()){
                    loadingDialog.show();
                    UserModel userModel = new UserModel();
                    userModel.setUsername(str_username);
                    userModel.setPassword(str_password);
                    userModel.setAptitudeResult(null);
                    userModel.setOceanResult(null);

                    communicator = NetworkClient.getCommunicator(Constants.SERVER_URL);
                    Call<StatusModel> call = communicator.login(userModel);
                    call.enqueue(new OnLoginHandler());
                }
                break;

            case R.id.register:
                startActivity(new Intent(Login.this, Signup.class));
                break;
        }
    }

    public boolean validate() {
        boolean flag = true;

        if (str_username.length() < 2) {
            username.setError("Username must be more than 1 character");
            flag = false;
        }
        if (str_password.length() < 2) {
            password.setError("Password must be more than 1 character");
            flag = false;
        }
        return flag;
    }

    private class OnLoginHandler implements Callback<StatusModel> {
        @Override
        public void onResponse(Call<StatusModel> call, Response<StatusModel> response) {
            StatusModel statusModel = response.body();

            if(statusModel.getStatus().equals("Success")){
                Constants.Username = str_username;
                Intent intent = new Intent(Login.this, MainActivity.class);
                startActivity(intent);
                Toast.makeText(Login.this, "Welcome "+str_username, Toast.LENGTH_SHORT).show();
            } else if(statusModel.getStatus().equals("Invalid details")){
                Toast.makeText(Login.this, "Account details are invalid!", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(Login.this, "Error!!", Toast.LENGTH_LONG).show();
            }
            loadingDialog.dismiss();
        }

        @Override
        public void onFailure(Call<StatusModel> call, Throwable t) {
            d("TAG","Error :"+t.getMessage());
            loadingDialog.dismiss();
            Toast.makeText(Login.this, "Error! No Internet..", Toast.LENGTH_LONG).show();
        }
    }
}
