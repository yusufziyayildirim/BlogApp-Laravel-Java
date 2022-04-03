package com.blog.blogapp.Fragments;


import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.blog.blogapp.AuthActivity;
import com.blog.blogapp.Constant;
import com.blog.blogapp.R;
import com.blog.blogapp.UserInfoActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SignUpFragment extends Fragment {
    private View view;
    private TextInputLayout layoutEmail,layoutPassword,layoutConfirm;
    private TextInputEditText txtEmail,txtPassword,txtConfirm;
    private TextView txtSignIn;
    private Button btnSignUp;
    private ProgressDialog dialog;

    public SignUpFragment(){}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.layout_sign_up,container,false);
        init();
        return view;
    }

    private void init() {
        layoutPassword = view.findViewById(R.id.txtLayoutPasswordSignUp);
        layoutEmail = view.findViewById(R.id.txtLayoutEmailSignUp);
        layoutConfirm = view.findViewById(R.id.txtLayoutConfrimSignUp);
        txtPassword = view.findViewById(R.id.txtPasswordSignUp);
        txtConfirm = view.findViewById(R.id.txtConfirmSignUp);
        txtSignIn = view.findViewById(R.id.txtSignIn);
        txtEmail = view.findViewById(R.id.txtEmailSignUp);
        btnSignUp = view.findViewById(R.id.btnSignUp);
        dialog = new ProgressDialog(getContext());
        dialog.setCancelable(false);

        txtSignIn.setOnClickListener(v->{
            //fragment değiştir
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameAuthContainer,new SignInFragment()).commit();
        });

        btnSignUp.setOnClickListener(v->{
            //alanları doğrulama
            if (validate()){
                register();
            }
        });


        txtEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!txtEmail.getText().toString().isEmpty()){
                    layoutEmail.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        txtPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (txtPassword.getText().toString().length()>7){
                    layoutPassword.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        txtConfirm.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (txtConfirm.getText().toString().equals(txtPassword.getText().toString())){
                    layoutConfirm.setErrorEnabled(false);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    private boolean validate (){
        if (txtEmail.getText().toString().isEmpty()){
            layoutEmail.setErrorEnabled(true);
            layoutEmail.setError("Email alanı zorunludur");
            return false;
        }
        if (txtPassword.getText().toString().length()<8){
            layoutPassword.setErrorEnabled(true);
            layoutPassword.setError("Şifre en 8 karakter olmalıdır");
            return false;
        }
        if (!txtConfirm.getText().toString().equals(txtPassword.getText().toString())){
            layoutConfirm.setErrorEnabled(true);
            layoutConfirm.setError("Şifreler eşleşmiyor");
            return false;
        }


        return true;
    }


    private void register(){
        dialog.setMessage("Kayıt Yapılıyor");
        dialog.show();
        StringRequest request = new StringRequest(Request.Method.POST, Constant.REGISTER, response -> {
            //bağlantı başarılı olursa yanıt alırız
            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("success")){
                    JSONObject user = object.getJSONObject("user");
                    //paylaşılan tercih kullanıcısı yap
                    SharedPreferences userPref = getActivity().getApplicationContext().getSharedPreferences("user",getContext().MODE_PRIVATE);
                    SharedPreferences.Editor editor = userPref.edit();
                    editor.putString("token",object.getString("token"));
                    editor.putString("name",user.getString("name"));
                    editor.putInt("id",user.getInt("id"));
                    editor.putString("lastname",user.getString("lastname"));
                    editor.putString("photo",user.getString("photo"));
                    editor.putBoolean("isLoggedIn",true);
                    editor.apply();
                    //if success
                    startActivity(new Intent(((AuthActivity)getContext()), UserInfoActivity.class));
                    ((AuthActivity) getContext()).finish();
                    Toast.makeText(getContext(), "Kayıt Başarılı", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            dialog.dismiss();

        },error -> {
            // bağlantı başarılı değilse hata
            error.printStackTrace();
            dialog.dismiss();
        }){

            // parametre ekle


            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> map = new HashMap<>();
                map.put("email",txtEmail.getText().toString().trim());
                map.put("password",txtPassword.getText().toString());
                return map;
            }
        };

        //bu isteği requestqueue'ya ekle
        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(request);
    }


}
