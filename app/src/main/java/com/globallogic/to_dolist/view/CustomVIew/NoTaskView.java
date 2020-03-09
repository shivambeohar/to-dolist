package com.globallogic.to_dolist.view.CustomVIew;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.globallogic.to_dolist.R;

import androidx.annotation.Nullable;

public class NoTaskView extends LinearLayout {

    private ImageView mNoTaskImage;

    public void setNoTaskImage(ImageView noTaskImage) {
        mNoTaskImage = noTaskImage;
    }

    public NoTaskView(Context context) {
        super(context);
        initComponent(context,null,-1);
    }

    public NoTaskView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initComponent(context,attrs,-1);
    }

    public NoTaskView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initComponent(context,attrs,defStyleAttr);
    }

    public NoTaskView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void initComponent(Context context, AttributeSet attrs, int defStyleAttr){
        LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.no_task_layout,null);

        mNoTaskImage = view.findViewById(R.id.no_task_image);

        setNoTaskImage(R.drawable.ic_add_task);
        addView(view);
    }

    /**
     * This Method will set the Image to the ImageView of NoCommentView.
     * @param errorImage
     */
    public void setNoTaskImage(int errorImage){
        mNoTaskImage.setImageResource(errorImage);
    }
}
