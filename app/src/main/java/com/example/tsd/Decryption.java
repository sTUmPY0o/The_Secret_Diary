package com.example.tsd;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.MessageDigest;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class Decryption extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference ref,ref2;
    FirebaseAuth fAuth;
    String userID;
    int id=0;



    EditText  inputPassword;
    TextView outputText,inputText;
    Button decBtn,btnfetch;

    String outputString;
   
    String AES = "AES";

    private static final String FILE_NAME = "testone.txt";

    TextView mEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decryption);


        fAuth=FirebaseAuth.getInstance();
        userID=fAuth.getCurrentUser().getUid();


        ref=database.getInstance().getReference().child("User");

        ref2=database.getInstance().getReference().child("User").child(userID).child("userKey");


        mEditText = findViewById(R.id.inputText);



        inputPassword = (EditText) findViewById(R.id.password2);
        outputText = (TextView) findViewById(R.id.OutputText);
        decBtn = (Button) findViewById(R.id.decBtn);
        btnfetch=(Button)findViewById(R.id.button_fetch);




        btnfetch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(userID)){
                    ref2.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            //Toast.makeText(MainActivity.this,"data is changed !!",Toast.LENGTH_LONG).show();
                            if(dataSnapshot.exists()){

                                id=(int)dataSnapshot.getChildrenCount();
                            }


                            String fetchkey=dataSnapshot.getValue().toString();
                            if (fetchkey.equalsIgnoreCase("null")){
                                Toast.makeText(Decryption.this,"Key has been Removed",Toast.LENGTH_SHORT).show();
                            }
                            else {
                                inputPassword.setText(fetchkey);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });



                }else {
                    Toast.makeText(Decryption.this,"No file exist",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



            }
        });


        decBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {

                    outputString = decrypt(mEditText.getText().toString(), inputPassword.getText().toString());
                } catch (Exception e) {

                    Toast.makeText(Decryption.this, "Wrong key", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
                outputText.setText(outputString);
            }
        });


    }

    private String decrypt(String outputString, String password) throws Exception {
        SecretKeySpec key = generateKey(password);
        Cipher c = Cipher.getInstance(AES);
        c.init(Cipher.DECRYPT_MODE, key);
        byte[] decodedValue = Base64.decode(outputString, Base64.DEFAULT);
        byte[] decValue = c.doFinal(decodedValue);
        String decryptedValue = new String(decValue);
        return decryptedValue;
    }

    private SecretKeySpec generateKey(String password) throws Exception {
        final MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] bytes = password.getBytes("UTF-8");
        digest.update(bytes, 0, bytes.length);
        byte[] key = digest.digest();
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
        return secretKeySpec;
    }


    public void load(View v) {
        FileInputStream fis = null;

        try {
            fis = openFileInput(FILE_NAME);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String text;

            while ((text = br.readLine()) != null) {
                sb.append(text).append("\n");
            }

            mEditText.setText(sb.toString());


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

