package com.globallogic.to_dolist.view.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.globallogic.to_dolist.R;
import com.globallogic.to_dolist.Util.Utility;
import com.globallogic.to_dolist.adapters.TaskRecyclerViewAdapter;
import com.globallogic.to_dolist.models.Task;
import com.globallogic.to_dolist.view.CustomVIew.NoTaskView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.globallogic.to_dolist.Util.Utility.showDatePickerDialog;
import static com.globallogic.to_dolist.Util.Utility.showTimePickerDialog;


public class AddTaskActivity extends AppCompatActivity {

    private FloatingActionButton mAddTaskBtn;
    private Toolbar mAddTaskToolbar;
    private NoTaskView mNoTaskView;
    private FirebaseFirestore mDatabase = FirebaseFirestore.getInstance();
    private RecyclerView mTasksRecyclerView;
    private CollectionReference mCollectionReference;
    private ArrayList<Map> mTasksList;
    private ArrayList<String> mDocumentReferenceList = new ArrayList<>();
    private ArrayList<Task> mTaskArrayList = new ArrayList<>();
    private Map<String, String> mUserTask;
    private TaskRecyclerViewAdapter mTaskRecyclerViewAdapter;
    private static final String COLLECTION_PATH_USERS = "Users";
    private static final String COLLECTION_PATH_TASKS = "Task";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        intiViewReferences();

        setSupportActionBar(mAddTaskToolbar);

        displayTasks();

        initTaskRecyclerView();

        mAddTaskBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddTaskDialog();
            }
        });
    }

    /**
     * This method will display the task in the recycler view.
     */
    private void displayTasks() {
        //Getting tasks from the fireStore of logged in user.
        mCollectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("TAG", "listen:error", e);
                    return;
                }
                Map<String, Object> tasks;
                mTasksList = new ArrayList<>();
                assert queryDocumentSnapshots != null;
                for (DocumentChange documentChange : queryDocumentSnapshots.getDocumentChanges()) {
                    mDocumentReferenceList.add(documentChange.getDocument().getId());
                    tasks = documentChange.getDocument().getData();
                    mTasksList.add(tasks);
                }

                //This code will add the Task Objects to the List.
                Map map;
                for (int i = 0; i < mTasksList.size(); i++) {
                    map = mTasksList.get(i);
                    Task t = new Task(
                            String.valueOf(map.get(Task.NEW_TASK)),
                            String.valueOf(map.get(Task.DESCRIPTION)),
                            String.valueOf(map.get(Task.DATE)),
                            String.valueOf(map.get(Task.TIME)),
                            String.valueOf(map.get(Task.PROGRESS)));
                    mTaskArrayList.add(t);
                    mTaskRecyclerViewAdapter.notifyDataSetChanged();
                }
                checkIfTaskExists();
            }
        });
    }

    /**
     * This Method will set the layoutManager and the adapter to the task RecyclerView.
     */
    private void initTaskRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        mTasksRecyclerView.setLayoutManager(linearLayoutManager);

        mTaskRecyclerViewAdapter = new TaskRecyclerViewAdapter(mTaskArrayList, this, mDocumentReferenceList);
        mTasksRecyclerView.setAdapter(mTaskRecyclerViewAdapter);
    }


    /**
     * This method will prompt the dialog box for the user to enter its daily task.
     */
    private void showAddTaskDialog() {
        AlertDialog.Builder addTaskDialog = new AlertDialog.Builder(AddTaskActivity.this);
        View dialogView = getLayoutInflater().inflate(R.layout.add_task_dialoge_layout, null);

        //get the reference of the view of dialog.
        final EditText newTask = dialogView.findViewById(R.id.tv_new_task);
        final EditText taskDescription = dialogView.findViewById(R.id.tv_description);
        final TextView taskTime = dialogView.findViewById(R.id.tv_time);
        final TextView taskDate = dialogView.findViewById(R.id.tv_date);
        final CheckBox reminder = dialogView.findViewById(R.id.cb_reminder_checkbox);
        Button saveTask = dialogView.findViewById(R.id.btn_save);

        addTaskDialog.setView(dialogView);
        final AlertDialog dialog = addTaskDialog.create();
        dialog.show();

        //On clicking taskTime a Clock will prompt.
        taskTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePickerDialog(taskTime, AddTaskActivity.this);
            }
        });

        //On clicking the taskDate a Calendar dialog will prompt.
        taskDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog(taskDate, AddTaskActivity.this);
            }
        });

        saveTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(newTask.getText().toString().trim())
                        && !TextUtils.isEmpty(taskDescription.getText().toString().trim())) {

                    mUserTask = new HashMap<>();
                    mUserTask.put(Task.NEW_TASK, newTask.getText().toString());
                    mUserTask.put(Task.DESCRIPTION, taskDescription.getText().toString());
                    mUserTask.put(Task.TIME, taskTime.getText().toString());
                    mUserTask.put(Task.DATE, taskDate.getText().toString());
                    mUserTask.put(Task.REMINDER, String.valueOf(reminder.isChecked()));
                    mUserTask.put(Task.PROGRESS, "incomplete");

                    mDatabase.collection("Users").document(Objects.requireNonNull(FirebaseAuth.getInstance()
                            .getCurrentUser()).getUid()).collection("Task").document().set(mUserTask);
                    Toast.makeText(AddTaskActivity.this, "Task Added Successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(AddTaskActivity.this, R.string.snackbar_for_empty_fields,
                            Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            }
        });
    }

    /**
     * This method will get the reference of the views from the layout file.
     */
    private void intiViewReferences() {
        mAddTaskBtn = findViewById(R.id.fab);
        mNoTaskView = findViewById(R.id.no_task_view);
        mAddTaskToolbar = findViewById(R.id.toolbar_add_task_activity);
        mTasksRecyclerView = findViewById(R.id.task_recycler_view);
        mCollectionReference = mDatabase.collection(COLLECTION_PATH_USERS)
                .document(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                .collection(COLLECTION_PATH_TASKS);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sign_out:
                FirebaseAuth.getInstance().signOut();
                Intent signOutIntent = new Intent(AddTaskActivity.this,
                        LoginUserActivity.class);

                //Activity will be removed from the stack
                signOutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                signOutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(signOutIntent);
                finish();
                break;

            default:
                return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        mDatabase.collection("Users").document(Objects.requireNonNull(FirebaseAuth.getInstance()
                .getCurrentUser()).getUid()).collection("Task").document(Utility.DOCUMENT_REFERENCE_FOR_PROGRESS)
                .update(Task.PROGRESS, item.getTitle());
        return super.onContextItemSelected(item);
    }

    /**
     * This method will check the task arrayList and if it size is zero custom view will be displayed instead of recycler view.
     */
    private void checkIfTaskExists(){
        if(mTaskArrayList.size() == 0){
            mTasksRecyclerView.setVisibility(View.GONE);
            mNoTaskView.setVisibility(View.VISIBLE);
        }else {
            mTasksRecyclerView.setVisibility(View.VISIBLE);
            mNoTaskView.setVisibility(View.GONE);
        }
    }
}
