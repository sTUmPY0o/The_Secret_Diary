package com.example.tsd;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import de.hdodenhof.circleimageview.CircleImageView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class Setting_pg extends AppCompatActivity {

    Button logout,remove_key;
    TextView pro_name,pro_email;

    FirebaseDatabase database;
    DatabaseReference ref,ref2;
    FirebaseAuth fAuth;
    String userID;
    int id=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_pg);

        ref=database.getInstance().getReference().child("User");


        remove_key=findViewById(R.id.Remove);
        pro_name=findViewById(R.id.Profile_name);
        pro_email=findViewById(R.id.email_id);
        logout= findViewById(R.id.Signout_btn);

        fAuth=FirebaseAuth.getInstance();
        userID=fAuth.getCurrentUser().getUid();

        ref2=database.getInstance().getReference().child("User").child(userID).child("userKey");

        CircleImageView pro_img=findViewById(R.id.pro_img);


        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(userID)){
                   // Toast.makeText(Setting_pg.this,"userid exist",Toast.LENGTH_SHORT).show();

                    ref2.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            //Toast.makeText(MainActivity.this,"data is changed !!",Toast.LENGTH_LONG).show();
                            if(dataSnapshot.exists()){

                                id=(int)dataSnapshot.getChildrenCount();
                            }


                            String fetchkey=dataSnapshot.getValue().toString();
                            if (fetchkey.equalsIgnoreCase("null")){
                                remove_key.setVisibility(View.INVISIBLE);
                            }
                            else {
                                //Toast.makeText(Setting_pg.this,"Key is available",Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }else {
                    //Toast.makeText(Setting_pg.this,"Not Exist",Toast.LENGTH_SHORT).show();
                    remove_key.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });





        GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(this);
        if(signInAccount != null){
            pro_name.setText(signInAccount.getDisplayName());
            pro_email.setText(signInAccount.getEmail());
            Picasso.get().load(signInAccount.getPhotoUrl()).into(pro_img);


        }


        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(),Sign_in.class);
                startActivity(intent);
              finish();

            }
        });


        remove_key.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ref.child(userID).child("userKey").setValue("null");
                Toast.makeText(Setting_pg.this,"Key has been Removed",Toast.LENGTH_SHORT).show();
            }
        });









    }
}
