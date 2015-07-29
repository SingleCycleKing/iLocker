package com.avazu.applocker.view.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.avazu.applocker.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class SectionView extends LinearLayout {
    @InjectView(R.id.section_title)
    TextView mTitle;

    public SectionView(Context context, AttributeSet attrs) {
        super(context, attrs);

        View view = ((LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(
                R.layout.section_view, this, true);

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.SectionView);
        mTitle.setText(array.getString(R.styleable.SectionView_section_text));
        array.recycle();

        ButterKnife.inject(this, view);
    }

    public void setTitle(String title) {
        mTitle.setText(title);
    }
}
