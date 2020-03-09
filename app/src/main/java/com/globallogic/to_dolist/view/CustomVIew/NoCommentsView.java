package com.globallogic.to_dolist.view.CustomVIew;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.globallogic.to_dolist.R;

import androidx.annotation.Nullable;


public class NoCommentsView extends LinearLayout {

    private ImageView mNoCommentImage;

    public NoCommentsView(Context context) {
        super(context);
        initComponent(context,null,-1);
    }

    public NoCommentsView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initComponent(context,attrs,-1);
    }

    public NoCommentsView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initComponent(context,attrs,defStyleAttr);
    }

    public void initComponent(Context context, AttributeSet attrs, int defStyleAttr){
        LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.no_comment_layout,null);

        mNoCommentImage = view.findViewById(R.id.no_comment_image);

        setNoCommentImage(R.drawable.comment);
        addView(view);
    }

    /**
     * This Method will set the Image to the ImageView of NoCommentView.
     * @param errorImage
     */
    public void setNoCommentImage(int errorImage){
        mNoCommentImage.setImageResource(errorImage);
    }
}
