package com.franqueli.android.popularmovies;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.franqueli.android.popularmovies.model.Review;

/**
 * Created by Franqueli Mendez on 4/11/16.
 * <p>
 * Copyright (c) 2015. Franqueli Mendez, All Rights Reserved
 */
public class MovieReviewViewHolder extends RecyclerView.ViewHolder {

    private Review mReview;
    public final View mView;
    public final TextView mTextView;

    public MovieReviewViewHolder(View itemView) {
        super(itemView);

        mView = itemView;
        mTextView = (TextView) itemView.findViewById(R.id.reviewTextView);

    }

    public void setReview(Review review) {
        mReview = review;

        mTextView.setText(review.getContent());
    }

}
