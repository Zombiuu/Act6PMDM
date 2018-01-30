package com.example.ramsesdiezgalvan.act6pmdm;

import android.content.Intent;
import android.nfc.Tag;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.TwitterAuthProvider;
import com.squareup.picasso.Picasso;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    CallbackManager callbackManager;
    LoginButton loginButton_fb;
    TwitterLoginButton loginButton_tw;
    ImageView profileImg;
    TextView name;
    TextView email;
    TextView birthday;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Twitter.initialize(this);
        setContentView(R.layout.activity_main);
        callbackManager = CallbackManager.Factory.create();
        firebaseAuth = FirebaseAuth.getInstance();
        //TWITTER
        loginButton_tw = (TwitterLoginButton) findViewById(R.id.login_button_tw);






        loginButton_tw.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                handleTwitterSession(result.data);
                // Do something with result, which provides a TwitterSession for making API calls
                Log.v("twitter", "Estoy mas dentro");
                Log.v("twitter", "RESULT: " + result);
                Log.v("twitter", "RESULT: " + result);
                TwitterSession session = TwitterCore.getInstance().getSessionManager().getActiveSession();
                TwitterAuthToken authToken = session.getAuthToken();

                String token = authToken.token;
                String secret = authToken.secret;
                Log.v("twitter", "TOKEN: " + token);
                Log.v("twitter", "SECRET: " + secret);

                name.setText("@"+session.getUserName().toString());

            }


            @Override
            public void failure(TwitterException exception) {
                Log.v("twitter", "Estoy mas fuera");
                // Do something on failure
            }
        });


        //FACEBOOK
        loginButton_fb = (LoginButton) findViewById(R.id.login_button);

        loginButton_fb.setReadPermissions("email");
        profileImg = findViewById(R.id.imageView);
        name = findViewById(R.id.txtName);
        email = findViewById(R.id.txtEmail);
        birthday = findViewById(R.id.txtBirthday);


        loginButton_fb.setReadPermissions(Arrays.asList
                ("public_profile", "user_friends", "email", "user_photos", "  user_birthday", "read_custom_friendlists"));


        // Callback registration
        loginButton_fb.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {


                System.out.println("ESTOY MAS DENTRO");

                handleFacebookAccessToken(loginResult.getAccessToken());

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
                                            .load("https://graph.facebook.com/" + object.get("id").toString() + "/picture?type=large")
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
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == 140) {
            loginButton_tw.onActivityResult(requestCode, resultCode, data);
        } else if (requestCode == 64206) {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }

    }


    private void handleFacebookAccessToken(AccessToken token) {
        Log.v("TOKEN","Esto es el token ---->"+token);
        Log.v("TOKEN","Esto es el token string ---->"+token.toString());
        Log.v("TOKEN","Esto es el token getToken ---->"+token.getToken());
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("", "signInWithCredential:success");
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("", "signInWithCredential:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }

    private void handleTwitterSession(TwitterSession session) {
        Log.d("", "handleTwitterSession:" + session);
        Log.v("twitter pass","1: "+ session.getAuthToken().token);
        Log.v("twitter pass","2: "+ session.getAuthToken().secret);
        AuthCredential credential = TwitterAuthProvider.getCredential(
                session.getAuthToken().token,
                session.getAuthToken().secret);



        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("", "signInWithCredential:success");
                            FirebaseUser user = firebaseAuth.getCurrentUser();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("", "signInWithCredential:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }

                        // ...
                    }
                });
    }



    //Twitter
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        callbackManager.onActivityResult(requestCode, resultCode, data);
//
//        super.onActivityResult(requestCode, resultCode, data);
//        Log.v("facebook", "Que es esto: " + requestCode);
//
//    }
}
