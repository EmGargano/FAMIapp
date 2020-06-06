package it.emgargano.famiapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.Toast;
//import android.widget.VideoView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.List;
import java.util.Vector;

import it.emgargano.famiapp.models.User;
import it.emgargano.famiapp.models.YouTubeVideos;
import it.emgargano.famiapp.sms.prova.R;

public class VideoActivity extends AppCompatActivity {

    private static final String TAG = "VideoActivity";
    private DatabaseReference mUserReference;
    private int mRole;
    private String uId;
    private RecyclerView recyclerView;
    Vector<YouTubeVideos> youTubeVideos = new Vector<YouTubeVideos>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        recyclerView = findViewById(R.id.recyclerViewVideo);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        //Get userId
        uId = user.getUid();
        mUserReference = FirebaseDatabase.getInstance().getReference("user").child(uId);
        mUserReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get User object
                User user = dataSnapshot.getValue(User.class);
                mRole = user.getRole();
//                Set the right viedo URLs for the correct user
                setVideoURLs(mRole);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Getting User Role failed, log a message
                Log.w(TAG, "loadUser:onCancelled", databaseError.toException());
                Toast.makeText(VideoActivity.this, "Failed to load user.",
                        Toast.LENGTH_SHORT).show();
            }
        });

    }
        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            if (resultCode == Activity.RESULT_CANCELED) {
                Intent refresh = new Intent(this, VideoActivity.class);
                startActivity(refresh);
                this.finish();
            }
        }


    public void setVideoURLs(int role_id) {
        if (role_id == 2) {
            //Richiedente asilo Role
            youTubeVideos.add(new YouTubeVideos("<iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/XcXTqYlyJy4\" frameborder=\"0\" allow=\"accelerometer; autoplay; encrypted-media; gyroscope; picture-in-picture\" allowfullscreen></iframe>"));
            youTubeVideos.add(new YouTubeVideos("<iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/tUYZJBvyXz8\" frameborder=\"0\" allow=\"accelerometer; autoplay; encrypted-media; gyroscope; picture-in-picture\" allowfullscreen></iframe>"));
            youTubeVideos.add(new YouTubeVideos("<iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/MjbUmpb53RU\" frameborder=\"0\" allow=\"accelerometer; autoplay; encrypted-media; gyroscope; picture-in-picture\" allowfullscreen></iframe>"));
            youTubeVideos.add(new YouTubeVideos("<iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/d3D6dn6GlfE\" frameborder=\"0\" allow=\"accelerometer; autoplay; encrypted-media; gyroscope; picture-in-picture\" allowfullscreen></iframe>"));
            youTubeVideos.add(new YouTubeVideos("<iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/qjdq-ug3dSI\" frameborder=\"0\" allow=\"accelerometer; autoplay; encrypted-media; gyroscope; picture-in-picture\" allowfullscreen></iframe>"));
            youTubeVideos.add(new YouTubeVideos("<iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/nO6vrfbkJ9M\" frameborder=\"0\" allow=\"accelerometer; autoplay; encrypted-media; gyroscope; picture-in-picture\" allowfullscreen></iframe>"));
            youTubeVideos.add(new YouTubeVideos("<iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/0bc_cZ8Xy44\" frameborder=\"0\" allow=\"accelerometer; autoplay; encrypted-media; gyroscope; picture-in-picture\" allowfullscreen></iframe>"));
            youTubeVideos.add(new YouTubeVideos("<iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/h3g9rQhzwHk\" frameborder=\"0\" allow=\"accelerometer; autoplay; encrypted-media; gyroscope; picture-in-picture\" allowfullscreen></iframe>"));
            youTubeVideos.add(new YouTubeVideos("<iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/WJpdCtjJHjQ\" frameborder=\"0\" allow=\"accelerometer; autoplay; encrypted-media; gyroscope; picture-in-picture\" allowfullscreen></iframe>"));
            youTubeVideos.add(new YouTubeVideos("<iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/QJ9VF8wJ_is\" frameborder=\"0\" allow=\"accelerometer; autoplay; encrypted-media; gyroscope; picture-in-picture\" allowfullscreen></iframe>"));
        } else if (role_id == 3) {
            // Dottore Role
            youTubeVideos.add(new YouTubeVideos("<iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/3CshmqNwuKs\" frameborder=\"0\" allow=\"accelerometer; autoplay; encrypted-media; gyroscope; picture-in-picture\" allowfullscreen></iframe>"));
        }
        VideoAdapter videoAdapter = new VideoAdapter(youTubeVideos);
        recyclerView.setAdapter(videoAdapter);
    }

        public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {
            List<YouTubeVideos> youtubeVideoList;
            public VideoAdapter() {
            }
            public VideoAdapter(List<YouTubeVideos> youtubeVideoList) {
                this.youtubeVideoList = youtubeVideoList;
            }
            @Override
            public VideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from( parent.getContext()).inflate(R.layout.video_view, parent, false);
                return new VideoViewHolder(view);
            }
            @Override
            public void onBindViewHolder(VideoViewHolder holder, int position) {
                holder.videoWeb.loadData( youtubeVideoList.get(position).getVideoUrl(), "text/html" , "utf-8" );
            }
            @Override
            public int getItemCount() {
                return youtubeVideoList.size();
            }
            public class VideoViewHolder extends RecyclerView.ViewHolder{
                WebView videoWeb;
                public VideoViewHolder(View itemView) {
                    super(itemView);
                    videoWeb = itemView.findViewById(R.id.videoWebView);
                    videoWeb.getSettings().setJavaScriptEnabled(true);
                    videoWeb.setWebChromeClient(new MyChrome() {
                    });
                }
            }
        }
        private class MyChrome extends WebChromeClient {
            private View mCustomView;
            private WebChromeClient.CustomViewCallback mCustomViewCallback;
            protected FrameLayout mFullscreenContainer;
            private int mOriginalOrientation;
            private int mOriginalSystemUiVisibility;
            MyChrome() {}
            public Bitmap getDefaultVideoPoster()
            {
                if (mCustomView == null) {
                    return null;
                }
                return BitmapFactory.decodeResource(getApplicationContext().getResources(), 2130837573);
            }
            public void onHideCustomView()
            {
                ((FrameLayout)getWindow().getDecorView()).removeView(this.mCustomView);
                this.mCustomView = null;
                getWindow().getDecorView().setSystemUiVisibility(this.mOriginalSystemUiVisibility);
                setRequestedOrientation(this.mOriginalOrientation);
                this.mCustomViewCallback.onCustomViewHidden();
                this.mCustomViewCallback = null;
            }
            public void onShowCustomView(View paramView, WebChromeClient.CustomViewCallback paramCustomViewCallback)
            {
                if (this.mCustomView != null)
                {
                    onHideCustomView();
                    return;
                }
                this.mCustomView = paramView;
                this.mOriginalSystemUiVisibility = getWindow().getDecorView().getSystemUiVisibility();
                this.mOriginalOrientation = getRequestedOrientation();
                this.mCustomViewCallback = paramCustomViewCallback;
                ((FrameLayout)getWindow().getDecorView()).addView(this.mCustomView, new FrameLayout.LayoutParams(-1, -1));
                getWindow().getDecorView().setSystemUiVisibility(3846);
            }
        }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}