package com.example.marks;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Marks extends AppCompatActivity {

    FirebaseAuth mAuth;
    String userID;

    public static DocumentReference userData;
    public static Map<String, Object> docData;
    RecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marks);
        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();
        userData = FirebaseFirestore.getInstance().collection("UserData").document(userID);
//        saveData();
        getData();
        userData.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (documentSnapshot != null && documentSnapshot.exists()) {
                    docData = documentSnapshot.getData();
                    setupRecyclerView();
                } else {
                    System.out.print("Current data: null");
                }
            }
        });


    }

    public void getData() {
        userData.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    docData = documentSnapshot.getData();
                    setupRecyclerView();
                } else {
                    docData = new HashMap<>();
                    userData.set(docData);
                }
            }
        });
    }

    public ArrayList<Course> parseData() {
        ArrayList<Course> coursesList = new ArrayList<>();
        for (Map.Entry<String, Object> courses : docData.entrySet()) {
            coursesList.add(new Course(courses.getKey(), (Map<String, String>) courses.getValue()));
        }
        return coursesList;
    }

    private void setupRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        adapter = new RecyclerAdapter(parseData(), this);
        recyclerView.setAdapter(adapter);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        new ItemTouchHelper(itemTouch).attachToRecyclerView(recyclerView);

        FloatingActionButton addCourse = findViewById(R.id.addcourse);

        addCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LayoutInflater li = LayoutInflater.from(v.getContext());
                View promptsView = li.inflate(R.layout.addcourseprompt, null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        v.getContext());

                alertDialogBuilder.setView(promptsView);

                final EditText courseName = promptsView
                        .findViewById(R.id.courseName);

                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {

                                        String courseNameS = courseName.getText().toString();

                                        adapter.addItem(new Course(courseNameS, new HashMap<String, String>()));
                                        docData.put(courseNameS, new HashMap<String, String>());

                                        update();

                                    }
                                })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();

            }
        });

    }

    public static void update() {
        userData.set(docData);
    }

    public static void updateCourse(Course courseToUpdate) {
        for (Map.Entry<String, Object> entry : docData.entrySet()) {
            if (entry.getKey().equals(courseToUpdate.course)) {
                entry.setValue(courseToUpdate.tests);
                break;
            }
        }
        userData.set(docData);
    }

    ItemTouchHelper.SimpleCallback itemTouch = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            docData.remove(adapter.courses.get(viewHolder.getAdapterPosition()).course);
            adapter.removeItem(viewHolder.getAdapterPosition());
            update();
        }
    };

}
