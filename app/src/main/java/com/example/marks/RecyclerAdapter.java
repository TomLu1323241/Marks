package com.example.marks;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder> {

    public ArrayList<Course> courses;
    private LayoutInflater mInflater;

    RecyclerAdapter(ArrayList<Course> courses, Context context) {
        this.courses = courses;
        this.mInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.list_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Course currentCourse = courses.get(position);
        holder.setData(currentCourse, position);
    }

    public void addItem(int pos, Course course) {
        courses.add(pos, course);
        notifyItemInserted(pos);
    }

    void addItem(Course course) {
        courses.add(course);
        notifyItemInserted(courses.size() - 1);
    }

    public void removeItem(int pos) {
        courses.remove(pos);
        notifyItemRemoved(pos);
    }

    public void removeItem(Course course) {
        for(int i = 0; i < courses.size(); i++) {
            if (courses.get(i).equals(course)) {
                courses.remove(i);
                notifyItemRemoved(i);
                break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return courses.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView courseName;
        TextView tests;
        TextView marks;
        TextView average;
        ImageButton remove;
        ImageButton add;

        int pos;
        Course course;

        private MyViewHolder(@NonNull View itemView) {
            super(itemView);
            courseName = itemView.findViewById(R.id.course);
            tests = itemView.findViewById(R.id.tests);
            marks = itemView.findViewById(R.id.marks);
            average = itemView.findViewById(R.id.average);
            remove = itemView.findViewById(R.id.remove);
            add = itemView.findViewById(R.id.add);
        }

        private void setData(final Course currentCourse, int position) {
            this.course = currentCourse;
            this.pos = position;

            courseName.setText(currentCourse.course);
            tests.setText(currentCourse.singleTests());
            marks.setText(currentCourse.singleMarks());
            average.setText("Average: " + currentCourse.getAverage());

            add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(v.getContext(), currentCourse.course, Toast.LENGTH_SHORT).show();

                    LayoutInflater li = LayoutInflater.from(v.getContext());
                    View promptsView = li.inflate(R.layout.addtestprompt, null);
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                            v.getContext());

                    alertDialogBuilder.setView(promptsView);

                    final EditText testName = (EditText) promptsView
                            .findViewById(R.id.testName);
                    final EditText mark = (EditText) promptsView
                            .findViewById(R.id.mark);
                    final EditText weight = (EditText) promptsView
                            .findViewById(R.id.weight);

                    alertDialogBuilder
                            .setCancelable(false)
                            .setPositiveButton("OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            // get user input and set it to result
                                            // edit text

                                            String testNameS = testName.getText().toString();
                                            String markS = mark.getText().toString();
                                            String weightS = weight.getText().toString();

                                            currentCourse.updateAdd(testNameS, markS, weightS);

                                            Marks.updateCourse(currentCourse);

                                            tests.setText(currentCourse.singleTests());
                                            marks.setText(currentCourse.singleMarks());
                                            average.setText("Average: " + currentCourse.getAverage());
                                            Log.w("TAG", tests.getText().toString());
                                        }
                                    })
                            .setNegativeButton("Cancel",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,int id) {
                                            dialog.cancel();
                                        }
                                    });

                    // create alert dialog
                    AlertDialog alertDialog = alertDialogBuilder.create();

                    // show it
                    alertDialog.show();
                }
            });

            remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(v.getContext(), currentCourse.course, Toast.LENGTH_SHORT).show();

                    LayoutInflater li = LayoutInflater.from(v.getContext());
                    View promptsView = li.inflate(R.layout.removetestprompt, null);
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                            v.getContext());

                    alertDialogBuilder.setView(promptsView);

                    final NumberPicker removeTests = (NumberPicker) promptsView
                            .findViewById(R.id.removeTests);

                    final String[] testsList = currentCourse.singleTests().split("\n");
                    removeTests.setMinValue(0);
                    removeTests.setMaxValue(testsList.length-1);
                    removeTests.setDisplayedValues(testsList);

                    alertDialogBuilder
                            .setCancelable(false)
                            .setPositiveButton("OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            // get user input and set it to result
                                            // edit text

                                            currentCourse.updateRemove(testsList[removeTests.getValue()]);

                                            Marks.updateCourse(currentCourse);

                                            tests.setText(currentCourse.singleTests());
                                            marks.setText(currentCourse.singleMarks());
                                            average.setText("Average: " + currentCourse.getAverage());
                                            Log.w("TAG", tests.getText().toString());
                                        }
                                    })
                            .setNegativeButton("Cancel",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,int id) {
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
    }
}
