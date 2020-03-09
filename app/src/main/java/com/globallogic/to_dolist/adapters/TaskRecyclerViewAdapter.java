package com.globallogic.to_dolist.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.globallogic.to_dolist.R;
import com.globallogic.to_dolist.Util.Utility;
import com.globallogic.to_dolist.models.Task;
import com.globallogic.to_dolist.view.activities.CommentActivity;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class TaskRecyclerViewAdapter extends RecyclerView.Adapter<TaskRecyclerViewAdapter.ViewHolder> {

    private ArrayList<Task> mTaskList;
    private Context mContext;
    private ArrayList mDocumentReferenceList;

    public TaskRecyclerViewAdapter(ArrayList<Task> taskList, Context context, ArrayList<String> taskDocumentReference) {
        mTaskList = taskList;
        mContext = context;
        mDocumentReferenceList = taskDocumentReference;
    }

    @NonNull
    @Override
    public TaskRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.task_item_recyclerview_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskRecyclerViewAdapter.ViewHolder holder, final int position) {
        holder.taskTitleName.setText(mTaskList.get(position).getNewTask());
        holder.taskDescription.setText(mTaskList.get(position).getDescription());
        holder.taskDate.setText(mTaskList.get(position).getDate());
        holder.taskTime.setText(mTaskList.get(position).getTime());
        holder.progressStatus.setText(mTaskList.get(position).getProgressStatus());
        holder.addComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent commentActivityIntent = new Intent(mContext, CommentActivity.class);
                commentActivityIntent.putExtra("document_id", (String) mDocumentReferenceList.get(position));
                mContext.startActivity(commentActivityIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mTaskList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {

        TextView taskTitleName;
        TextView taskDescription;
        TextView taskDate;
        TextView taskTime;
        TextView progressStatus;
        CardView cardView;
        Button addComment;

        private ViewHolder(@NonNull View itemView) {
            super(itemView);

            taskTitleName = itemView.findViewById(R.id.tv_new_task);
            taskDescription = itemView.findViewById(R.id.tv_task_description);
            taskDate = itemView.findViewById(R.id.tv_date);
            taskTime = itemView.findViewById(R.id.tv_time);
            progressStatus = itemView.findViewById(R.id.tv_task_status);
            cardView = itemView.findViewById(R.id.card_view_task_item);
            addComment = itemView.findViewById(R.id.btn_add_comment);
            cardView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            contextMenu.setHeaderTitle("Task Status");
            Utility.DOCUMENT_REFERENCE_FOR_PROGRESS = String.valueOf(mDocumentReferenceList.get(getAdapterPosition()));
            contextMenu.add(this.getAdapterPosition(), 1, 0, "Completed");
            contextMenu.add(this.getAdapterPosition(), 2, 0, "Pending");
            contextMenu.add(this.getAdapterPosition(), 3, 0, "Later");
        }
    }
}
