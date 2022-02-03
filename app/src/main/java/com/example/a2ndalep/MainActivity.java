package com.example.a2ndalep;

import static android.view.Gravity.CENTER;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    public LinearLayout layout, layout1, layout2;
    TextView textViewPayFinal;
    ScrollView scrollView;
    ArrayList<String> imagelink = new ArrayList<String>();
    ArrayList<String> price = new ArrayList<String>();
    ArrayList<String> title = new ArrayList<String>();
    ArrayList<String> writer = new ArrayList<String>();
    ArrayList<String> availiability = new ArrayList<String>();
    Button buttons[];
    NumberPicker numberPickers[];
    ImageView imageViews[];
    Double price_temp[];
    int k =0;
    void dataColector(){
        buttons= new Button[k];
        numberPickers= new NumberPicker[k];
        imageViews= new ImageView[k];
        price_temp = new Double[k];
        for(int i=0;i<k;i++) {
            price_temp[i] = 0.0;
            appDrawing(i);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (FirebaseAuth.getInstance().getCurrentUser() == null){
            startActivity(new Intent(MainActivity.this , StartActivity.class));
            finish();
        }
        FirebaseDatabase.getInstance().getReference().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int i = 0;
                for(DataSnapshot snap : snapshot.getChildren()){
                    String img = String.valueOf(snap.child("imagelink").getValue());
                    String prc = String.valueOf(snap.child("price").getValue());
                    String title = String.valueOf(snap.child("title").getValue());
                    String writer = String.valueOf(snap.child("writer").getValue());
                    String availiability = String.valueOf(snap.child("availiability").getValue());
                    MainActivity.this.imagelink.add(img);
                    MainActivity.this.price.add(prc);
                    MainActivity.this.title.add(title);
                    MainActivity.this.writer.add(writer);
                    MainActivity.this.availiability.add(availiability);
                    i++;
                }
                k = i;
                dataColector();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    void appDrawing(int i){
        layout = findViewById(R.id.layout_parent);
        scrollView = findViewById(R.id.scrollview_parent);
        textViewPayFinal = findViewById(R.id.complete_price);
        //The next lines of code are used to dynamically make components

        //first layout
        layout1 = new LinearLayout(this);
        layout1.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        //second layout
        layout2 = new LinearLayout(this);


        //text in second layout for book title
        TextView textViewTitle = new TextView(this);
        textViewTitle.setLayoutParams(layoutParams);
        textViewTitle.setText(title.get(i));
        layout2.addView(textViewTitle);


        //horizontal linearlayout
        LinearLayout linearLayoutHorizontal = new LinearLayout(this);
        linearLayoutHorizontal.setOrientation(LinearLayout.HORIZONTAL);
        linearLayoutHorizontal.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        linearLayoutHorizontal.setGravity(CENTER);


        //Create imageViews and display using picasso and add to horizontal layout
        imageViews[i] = new ImageView(this);
        imageViews[i].setLayoutParams(new LinearLayout.LayoutParams(300, 300));
        Picasso.get().load(imagelink.get(i)).into(imageViews[i]);
        linearLayoutHorizontal.addView(imageViews[i]);


        //vertical linearlayout in linearLayoutHorizontal
        LinearLayout linearLayoutVertical = new LinearLayout(this);
        linearLayoutVertical.setOrientation(LinearLayout.VERTICAL);
        //price in linearLayoutVertical
        TextView textViewPrice = new TextView(this);
        textViewPrice.setLayoutParams(layoutParams);
        textViewPrice.setText(price.get(i)+"€");
        linearLayoutVertical.addView(textViewPrice);


        //adds linearLayoutVertical in linearLayoutHorizontal
        linearLayoutHorizontal.addView(linearLayoutVertical);

        //adds numberpicker in linearLayoutHorizonta
        //With max value equals book availability
        numberPickers[i] = new NumberPicker(this);
        numberPickers[i].setMinValue(0);
        numberPickers[i].setMaxValue(Integer.parseInt(availiability.get(i)));
        linearLayoutHorizontal.addView(numberPickers[i]);

        //adds button in linearLayoutHorizontal
        buttons[i] = new Button(this);
        buttons[i].setId(i);
        buttons[i].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                totalPayment(buttons[i].getId());
            }
        });
        buttons[i].setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        buttons[i].setText("Add to cart");
        linearLayoutHorizontal.addView(buttons[i]);

        //adds linearLayoutHorizontal in layout1 and layouts to parent
        layout1.addView(linearLayoutHorizontal);
        this.layout.addView(layout2);
        this.layout.addView(layout1);
    }
    public void logout(View view){
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(MainActivity.this,StartActivity.class));
        finish();
    }
    public void pay(View view){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        for(int j=0;j<k;j++){
            DatabaseReference Ref =database.getReference("book"+(j+1));
            DatabaseReference ref1 = Ref.child("availiability");
            ref1.setValue(Integer.parseInt(availiability.get(j))-numberPickers[j].getValue());
        }
        Toast.makeText(MainActivity.this, "Your order has been placed with a total of "+String.valueOf(full) + "€."+
                " Collect your order from the local store.", Toast.LENGTH_LONG).show();
        finish();
        startActivity(getIntent());
    }
    Double full = 0.0;
    void totalPayment(int id){
        double payTotal = 0;
        for(int gtxs=0;gtxs<k;gtxs++){
            payTotal += numberPickers[gtxs].getValue() * Double.parseDouble(availiability.get(gtxs));
        }
        textViewPayFinal.setText(String.valueOf(payTotal));
        //calculates full price and prints it to ui
        price_temp[id] = Double.parseDouble(price.get(id) )* numberPickers[id].getValue();
        for (Double e: price_temp) {
            full+=e;
        }
        textViewPayFinal.setText(String.format("%.2f",full) + "€");
    }
}