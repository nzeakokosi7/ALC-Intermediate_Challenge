package com.example.nzeakokosi7.alc_intermediate_challenge.Activities;

import android.content.Intent;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.nzeakokosi7.alc_intermediate_challenge.R;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.R.attr.offset;

public class ProfileActivity extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener, View.OnClickListener {

    private static final float PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR = 0.9f;
    private static final float PERCENTAGE_TO_HIDE_TITLE_DETAILS = 0.3f;
    private static final int ALPHA_ANIMATIONS_DURATION = 200;

    private boolean mIsTheTitleVisible = false;
    private boolean mIsTheTitleContainerVisible = true;

    private LinearLayout mTitleContainer;
    private TextView mTitle;
    private TextView mGithubLink;
    private AppBarLayout mAppBarLayout;
    private CircleImageView image;
    private ImageView mHeaderImageView;
    private FloatingActionButton mFloatingActionButton;
    private String mDevelopersUserName;
    private String mDevelopersImageUrl;
    private String mDevelopersProfileUrl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.activity_profile);
        bindActivity();
        mAppBarLayout.addOnOffsetChangedListener(this);

        startAlphaAnimation(mTitle, 0, View.INVISIBLE);
        getIntentsFromMainActivity();
        mTitle.setText(mDevelopersUserName);
        Glide.with(ProfileActivity.this).load(mDevelopersImageUrl).into(image);
        Glide.with(ProfileActivity.this).load(mDevelopersImageUrl).into(mHeaderImageView);
        mGithubLink.setText(mDevelopersProfileUrl);
        mFloatingActionButton.setOnClickListener(this);




    }

    private void getIntentsFromMainActivity() {

        Intent intent = getIntent();
        mDevelopersUserName = intent.getStringExtra("name");
        mDevelopersImageUrl = intent.getStringExtra("image");
        mDevelopersProfileUrl = intent.getStringExtra("url");

    }

    private void bindActivity() {

        mTitle = (TextView) findViewById(R.id.main_textview_title);
        mGithubLink = (TextView) findViewById(R.id.GithubLink);
        mTitleContainer = (LinearLayout) findViewById(R.id.main_linearlayout_title);
        mAppBarLayout = (AppBarLayout) findViewById(R.id.main_appbar);
        image = (CircleImageView) findViewById(R.id.mainImage);
        mHeaderImageView = (ImageView) findViewById(R.id.main_imageview_placeholder);
        mFloatingActionButton = (FloatingActionButton) findViewById(R.id.shareFloatingBar);

    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        int maxScroll = appBarLayout.getTotalScrollRange();
        float percentage = (float) Math.abs(offset) / (float) maxScroll;

        handleAlphaOnTitle(percentage);
        handleToolbarTitleVisibility(percentage);

    }

    private void handleToolbarTitleVisibility(float percentage) {
        if (percentage >= PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR) {

            if (!mIsTheTitleVisible) {
                startAlphaAnimation(mTitle, ALPHA_ANIMATIONS_DURATION, View.VISIBLE);
                mIsTheTitleVisible = true;
            }

        } else {

            if (mIsTheTitleVisible) {
                startAlphaAnimation(mTitle, ALPHA_ANIMATIONS_DURATION, View.INVISIBLE);
                mIsTheTitleVisible = false;
            }
        }
    }

    private void handleAlphaOnTitle(float percentage) {
        if (percentage >= PERCENTAGE_TO_HIDE_TITLE_DETAILS) {
            if (mIsTheTitleContainerVisible) {
                startAlphaAnimation(mTitleContainer, ALPHA_ANIMATIONS_DURATION, View.INVISIBLE);
                mIsTheTitleContainerVisible = false;
            }

        } else {

            if (!mIsTheTitleContainerVisible) {
                startAlphaAnimation(mTitleContainer, ALPHA_ANIMATIONS_DURATION, View.VISIBLE);
                mIsTheTitleContainerVisible = true;
            }
        }
    }

    private static void startAlphaAnimation(View v, long duration, int visibility) {
        AlphaAnimation alphaAnimation = (visibility == View.VISIBLE)
                ? new AlphaAnimation(0f, 1f)
                : new AlphaAnimation(1f, 0f);

        alphaAnimation.setDuration(duration);
        alphaAnimation.setFillAfter(true);
        v.startAnimation(alphaAnimation);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.shareFloatingBar:
                String shareText = "Check out this awesome developer @" + mTitle.getText().toString() + "," + mGithubLink.getText().toString();

                Intent shareIntent = ShareCompat.IntentBuilder.from(ProfileActivity.this)
                        .setType("text/plain")
                        .setText(shareText)
                        .getIntent();
                if (shareIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(shareIntent);
                }
        }
    }
}

