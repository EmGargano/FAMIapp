package it.emgargano.famiapp;


import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import it.emgargano.famiapp.adapter.MessageAdapter;
import it.emgargano.famiapp.models.Message;
import it.emgargano.famiapp.models.User;
import it.emgargano.famiapp.sms.prova.R;

public class ChatActivity extends AppCompatActivity {
    //variable declaration
    private static final String TAG = "ChatActivity";
    private static  boolean FLAG_CHAT_OPEN = true;
    private DatabaseReference mChatReference;
    private EditText mMessage;
    private ImageView mSend;
    private List<Message> mMessagesList;
    private MessageAdapter messageAdapter;
    private RecyclerView recyclerView;
    IntentFilter s_intentFilter;
    private FloatingActionButton fabAutomaticScroll;
    public static final int nMessaggiInvisibili = 3;
    static boolean fabHided = true;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mMessage = findViewById(R.id.message_area);
        mSend = findViewById(R.id.sendMessage);
        recyclerView = findViewById(R.id.chatList);
        mSend.setOnClickListener(mSend_listener);

//        Create intetnfilter for broadcastReceiver to check if time allowed chat to be opened or closed
        s_intentFilter = new IntentFilter();
        s_intentFilter.addAction(Intent.ACTION_TIME_TICK);
        s_intentFilter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
        s_intentFilter.addAction(Intent.ACTION_TIME_CHANGED);

        fabAutomaticScroll = (FloatingActionButton) findViewById(R.id.scroll_automatic);
        fabAutomaticScroll.setScaleX(0);
        fabAutomaticScroll.setScaleY(0);
        final AnimatorSet show_fab = (AnimatorSet) AnimatorInflater.loadAnimator(this,
                R.animator.show_fab);
        final AnimatorSet hide_fab = (AnimatorSet) AnimatorInflater.loadAnimator(this,
                R.animator.hide_fab);

        recyclerView.setHasFixedSize(true);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                recyclerView.scrollToPosition(messageAdapter.getItemCount() - 1);
            }
        }, 300);

        // Comportamento FAB relativo allo scroll automatico fino all'ultimo messaggio della chat
        recyclerView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                int scrollPosition = linearLayoutManager.findLastVisibleItemPosition();
                if(scrollPosition <= (messageAdapter.getItemCount() - nMessaggiInvisibili) && fabHided) {
                    show_fab.setTarget(fabAutomaticScroll);
                    show_fab.start();
                    fabHided = false;
                }else if(scrollPosition > (messageAdapter.getItemCount() - nMessaggiInvisibili) && !fabHided){
                    hide_fab.setTarget(fabAutomaticScroll);
                    hide_fab.start();
                    fabHided = true;
                }
            }
        });

        fabAutomaticScroll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.smoothScrollToPosition(messageAdapter.getItemCount() - 1);
            }
        });

        mMessage.setImeActionLabel(getString(R.string.send_button_label),KeyEvent.FLAG_EDITOR_ACTION);
        mMessage.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == KeyEvent.KEYCODE_ENTER) {
                    Log.d(TAG, "dentro messagearea handler");
                    handled = true;
                }
                return handled;
            }
        });

    }

    @Override
    protected void onStart(){  //Method called when the activity is started
        super.onStart();
        checkTimeOpenChat();
        readMessages();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(m_timeChangedReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(m_timeChangedReceiver, s_intentFilter);
    }



    public void hideKeyboard() {
        if (getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            getCurrentFocus().clearFocus();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) { //receive result from activity
            //Create new Intent
            Intent refresh = new Intent(this, ChatActivity.class);
            startActivity(refresh);
            this.finish();
    }


    public void sendMessage(){
        // Initialize FirebaseUser
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        // Get userId Logged
        final String userId = user.getUid();
        final String message = mMessage.getText().toString();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        Date date = new Date();
        final String current_data = formatter.format(date);
        hideKeyboard();
        recyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                recyclerView.scrollToPosition(messageAdapter.getItemCount() - 1);
            }
        }, 300);

        FirebaseDatabase.getInstance().getReference("user").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Get obj user
                User user_data = dataSnapshot.getValue(User.class);
                //New Constructor message
                Message messageObj = new Message(
                        user_data.name,
                        user_data.surname,
                        message,
                        current_data,
                        userId
                );
                //Adding value to DB
                FirebaseDatabase.getInstance().getReference("chat").push().setValue(messageObj).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            //Success message
                            mMessage.setText("");
                        } else {
                            //Failure message
                            Toast.makeText(ChatActivity.this, getString(R.string.failedMessage), Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Getting Messages failed
                Log.w(TAG, "loadMessages:onCancelled", databaseError.toException());
                Toast.makeText(ChatActivity.this, getString(R.string.failedMessageLoading),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void readMessages(){

        //Condition opening chat
        if (FLAG_CHAT_OPEN) {
            mSend.setEnabled(true);
        } else {
            mSend.setEnabled(false);
        }

        // ArrayList variable
        mMessagesList = new ArrayList<>();
        // Initialize Database Reference
        mChatReference = FirebaseDatabase.getInstance().getReference("chat");
        // Add value
        mChatReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Clear ArrayList
                mMessagesList.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    // Get Obj message
                    Message message = snapshot.getValue(Message.class);
                    // Add obj to ArrayList
                    mMessagesList.add(message);

                    messageAdapter = new MessageAdapter(ChatActivity.this, mMessagesList);
                    //Called to associate adapter with the list
                    recyclerView.setAdapter(messageAdapter);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Getting Messages failed
                Log.w(TAG, "loadMessages:onCancelled", databaseError.toException());
                Toast.makeText(ChatActivity.this, getString(R.string.failedMessageLoading),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void sendMessageNotAllowed(View view){
        String time_start = getString(R.string.openChatTime);
        String time_closed = getString(R.string.closedChatTime);
        String snackBar = getString(R.string.closedChatMessage, time_start, time_closed);
        Snackbar.make(view,
                snackBar,
                Snackbar.LENGTH_INDEFINITE)
                .show();
    }


    public View.OnClickListener mSend_listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            boolean isEmptyMessage = mMessage.getText().toString().isEmpty();
            if(!isEmptyMessage) {
                sendMessage();
            }
        }
    };

    private final BroadcastReceiver m_timeChangedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(Intent.ACTION_TIME_TICK)) {
                checkTimeOpenChat();
            }
        }
    };

    public void checkTimeOpenChat() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        String getCurrentTime = sdf.format(c.getTime());
        String time_start = getString(R.string.openChatTime);
        String time_end = getString(R.string.closedChatTime);
        ImageView sendButton = findViewById(R.id.sendMessage);
        if (getCurrentTime.compareTo(time_end) > 0 && getCurrentTime.compareTo(time_start) < 0  ) {
            FLAG_CHAT_OPEN = false;
            sendMessageNotAllowed(sendButton);
            sendButton.setEnabled(false);
        }else {
            FLAG_CHAT_OPEN = true;
            sendButton.setEnabled(true);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
            hideKeyboard();
        return super.dispatchTouchEvent(ev);
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
