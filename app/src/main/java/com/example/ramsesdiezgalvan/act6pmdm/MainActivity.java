package com.example.ramsesdiezgalvan.act6pmdm;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    CallbackManager callbackManager;
    LoginButton loginButton;
    ImageView profileImg;
    TextView name;
    TextView email;
    TextView birthday;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        callbackManager = CallbackManager.Factory.create();


        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions("email");
        profileImg = findViewById(R.id.imageView);
        name = findViewById(R.id.txtName);
        email = findViewById(R.id.txtEmail);
        birthday = findViewById(R.id.txtBirthday);
        // If using in a fragment
        // loginButton.setFragment();

        loginButton.setReadPermissions(Arrays.asList
                ("public_profile","user_friends", "email" , "user_photos","  user_birthday","read_custom_friendlists" ));

        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code

                System.out.println("ESTOY MAS DENTRO");

                GraphRequest request = GraphRequest.newMeRequest(
                        AccessToken.getCurrentAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {




                            @Override
                            public void onCompleted(
                                    JSONObject object,
                                    GraphResponse response) {
                                System.out.println("OBJETO ------> " + object);





                                try {

                                    System.out.println("NOMBRE ------> " + object.get("name").toString());

                                    System.out.println("BIRTHDAY ------> " + object.get("birthday").toString());


                                    Picasso.with(MainActivity.this)
                                            .load( "https://graph.facebook.com/" + object.get("id").toString()+ "/picture?type=large")
                                            .into(profileImg);


                                    name.setText(object.get("name").toString());

                                    birthday.setText(object.get("birthday").toString());

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }


                            }
                        });
                Bundle parameters = new Bundle();
                //Con el parámetro fields especificamos los campos que queremos que nos devuelva el json de facebook
                parameters.putString("fields", "id,name,link,picture,birthday");
                request.setParameters(parameters);
                request.executeAsync();


            }

            @Override
            public void onCancel() {
                // App code
                System.out.println("ESTOY MAS FUERA");
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }
}
