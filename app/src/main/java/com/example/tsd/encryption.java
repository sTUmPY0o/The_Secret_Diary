package com.example.tsd;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class encryption extends AppCompatActivity {


    FirebaseDatabase database;
    DatabaseReference ref;

    FirebaseAuth fAuth;
    String userID;


    EditText inputText, inputPassword;
    String outputString;
    String AES = "AES";
    TextView outputText;
    Button encBtn;


    private static final String FILE_NAME = "testone.txt";

    EditText mEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encryption);



        fAuth=FirebaseAuth.getInstance();
        userID=fAuth.getCurrentUser().getUid();



        ref=database.getInstance().getReference();


        mEditText = findViewById(R.id.inputText);

        encBtn = (Button) findViewById(R.id.encBtn);
        inputPassword = (EditText) findViewById(R.id.password);
        outputText = (TextView) findViewById(R.id.OutputText);

        encBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    outputString = encrypt(mEditText.getText().toString(), inputPassword.getText().toString());
                    outputText.setText(outputString);

                    //


                    save();
                    savekey();

                    //

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


    }

    private void savekey() {
        String pass = inputPassword.getText().toString();
        ref.child("User").child(userID).child("userKey").setValue(pass);

    }

    private void save() {

        String text = outputText.getText().toString();
        FileOutputStream fos = null;

        try {
            fos = openFileOutput(FILE_NAME, MODE_PRIVATE);
            fos.write(text.getBytes());

            //outputText.getText().clear();
           // Toast.makeText(this, "Saved to " + getFilesDir() + "/" + FILE_NAME,Toast.LENGTH_LONG).show();
            Toast.makeText(this,"Your File has been Saved!",Toast.LENGTH_SHORT).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


    }


    private String encrypt(String Data, String password) throws Exception {
        SecretKeySpec key = generateKey(password);
        Cipher c = Cipher.getInstance(AES);
        c.init(Cipher.ENCRYPT_MODE, key);
        byte[] encVal = c.doFinal(Data.getBytes());
        String encryptedValue = Base64.encodeToString(encVal, Base64.DEFAULT);
        return encryptedValue;
    }


    private SecretKeySpec generateKey(String password) throws Exception {
        final MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] bytes = password.getBytes("UTF-8");
        digest.update(bytes, 0, bytes.length);
        byte[] key = digest.digest();
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
        return secretKeySpec;
    }
}
