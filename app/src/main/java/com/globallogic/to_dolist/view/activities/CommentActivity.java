package com.globallogic.to_dolist.view.activities;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.globallogic.to_dolist.R;
import com.globallogic.to_dolist.adapters.CommentRecyclerViewAdapter;
import com.globallogic.to_dolist.models.Comment;
import com.globallogic.to_dolist.view.CustomVIew.NoCommentsView;
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
import java.util.List;
import java.util.Map;
import java.util.Objects;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class CommentActivity extends AppCompatActivity {

    private FloatingActionButton mAddComment;
    private Toolbar mAddCommentToolbar;
    private NoCommentsView mNoCommentView;
    private FirebaseFirestore mDatabase = FirebaseFirestore.getInstance();
    private static final String COMMENT_KEY = "comment";
    private CollectionReference mCollectionReference;
    private RecyclerView mCommentsRecyclerView;
    private ArrayList<Map> mCommentList;
    private String mUserTaskReference;
    private ArrayList<Comment> mCommentArrayList = new ArrayList<>();
    private Map<String, String> mInsertComment = new HashMap<>();
    private CommentRecyclerViewAdapter mCommentRecyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        /**
         * This method will get the reference of the views from the layout file.
         */
        initViewReferences();

        mAddCommentToolbar.setTitle("Comments");
        setSupportActionBar(mAddCommentToolbar);

        displayComments();

        initCommentRecyclerView();

        mAddComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayCommentDialog();
            }
        });
    }

    /**
     * This Method will set the layoutManager and the adapter to the comment RecyclerView.
     */
    private void initCommentRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        mCommentsRecyclerView.setLayoutManager(linearLayoutManager);
        mCommentRecyclerViewAdapter = new CommentRecyclerViewAdapter(mCommentArrayList, this);
        mCommentsRecyclerView.setAdapter(mCommentRecyclerViewAdapter);
    }

    /**
     * This method will display the comments in the recycler view.
     */
    private void displayComments() {
        mCollectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("TAG", "listen:error", e);
                    return;
                }
                Map<String, Object> comments;
                mCommentList = new ArrayList<>();
                assert queryDocumentSnapshots != null;
                for (DocumentChange documentChange : queryDocumentSnapshots.getDocumentChanges()) {
                    comments = documentChange.getDocument().getData();
                    mCommentList.add(comments);
                }

                //This code will add the Task Objects to the List.
                Map map;
                List<String> list;
                int l = 0;
                for (int i = 0; i < mCommentList.size(); i++) {
                    map = mCommentList.get(i);
                    list = new ArrayList<String>(map.values());
                    mCommentArrayList.add(new Comment(list.get(i - l)));
                    mCommentRecyclerViewAdapter.notifyDataSetChanged();
                    l++;
                }
                checkIfCommentsExits();
            }
        });
    }

    /**
     * This method will get the reference of the views from the layout file.
     */
    public void initViewReferences() {
        mAddComment = findViewById(R.id.fab_add_comment);
        mNoCommentView = findViewById(R.id.no_comment_view);
        mAddCommentToolbar = findViewById(R.id.toolbar_add_comment_activity);
        mUserTaskReference = getIntent().getStringExtra("document_id");
        mCommentsRecyclerView = findViewById(R.id.comment_recycler_view);
        mCollectionReference = mDatabase.collection("Users")
                .document(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).
                        collection("Task").document(mUserTaskReference).collection("Comments");
    }


    /**
     * This method will prompt the dialog box for the user to enter its comment to its specific task.
     */
    private void displayCommentDialog() {
        AlertDialog.Builder addTaskDialog = new AlertDialog.Builder(CommentActivity.this);
        View dialogView = getLayoutInflater().inflate(R.layout.add_comment_dialoge_layout, null);

        final EditText addComment = dialogView.findViewById(R.id.edt_comment);
        final Button saveCommentBtn = dialogView.findViewById(R.id.btn_save_comment);

        addTaskDialog.setView(dialogView);
        final AlertDialog dialog = addTaskDialog.create();
        dialog.show();

        saveCommentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(addComment.getText().toString().trim())) {
                    mInsertComment.put(COMMENT_KEY, addComment.getText().toString().trim());
                    mCollectionReference.document().set(mInsertComment);
                    Toast.makeText(CommentActivity.this, "Comment Added Successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(CommentActivity.this, R.string.snackbar_for_empty_fields,
                            Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            }
        });
    }

    /**
     * This method will check the comment arrayList and if it size is zero custom view will be displayed instead of recycler view.
     */
    private void checkIfCommentsExits(){
        if(mCommentArrayList.size() == 0){
            mCommentsRecyclerView.setVisibility(View.GONE);
            mNoCommentView.setVisibility(View.VISIBLE);
        }else {
            mCommentsRecyclerView.setVisibility(View.VISIBLE);
            mNoCommentView.setVisibility(View.GONE);
        }
    }
}
