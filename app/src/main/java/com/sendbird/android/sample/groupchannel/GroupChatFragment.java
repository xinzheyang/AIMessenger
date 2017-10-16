package com.sendbird.android.sample.groupchannel;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sendbird.android.BaseChannel;
import com.sendbird.android.BaseMessage;
import com.sendbird.android.FileMessage;
import com.sendbird.android.GroupChannel;
import com.sendbird.android.Member;
import com.sendbird.android.PreviousMessageListQuery;
import com.sendbird.android.SendBird;
import com.sendbird.android.SendBirdException;
import com.sendbird.android.User;
import com.sendbird.android.UserMessage;
import com.sendbird.android.sample.R;
import com.sendbird.android.sample.utils.FileUtils;
import com.sendbird.android.sample.utils.MediaPlayerActivity;
import com.sendbird.android.sample.utils.PhotoViewerActivity;
import com.sendbird.android.sample.utils.PreferenceUtils;
import com.sendbird.android.sample.utils.TextUtils;
import com.sendbird.android.sample.utils.UrlPreviewInfo;
import com.sendbird.android.sample.utils.WebUtils;

import org.json.JSONException;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;


public class GroupChatFragment extends Fragment {
    private static final String CONNECTION_HANDLER_ID = "CONNECTION_HANDLER_GROUP_CHAT";

    private static final String LOG_TAG = GroupChatFragment.class.getSimpleName();

    private static final String CHANNEL_HANDLER_ID = "CHANNEL_HANDLER_GROUP_CHANNEL_CHAT";
    private static final String STATE_CHANNEL_URL = "STATE_CHANNEL_URL";
    private static final int INTENT_REQUEST_CHOOSE_MEDIA = 301;
    private static final int PERMISSION_WRITE_EXTERNAL_STORAGE = 13;
    static final String EXTRA_CHANNEL_URL = "EXTRA_CHANNEL_URL";

    private RelativeLayout mRootLayout;
    private RecyclerView mRecyclerView;
    private GroupChatAdapter mChatAdapter;
    private LinearLayoutManager mLayoutManager;
    private EditText mMessageEditText;
    private Button mMessageSendButton;
    private ImageButton mFileUploadButton;
    private View mCurrentEventLayout;
    private TextView mCurrentEventText;

    //modify
    private Button mSuggestionButton1;
    private Button mSuggestionButton2;
    private Button mSuggestionButton3;

    private GroupChannel mChannel;
    private String mChannelUrl;
    private PreviousMessageListQuery mPrevMessageListQuery;

    private boolean mIsTyping;
    //modify
    private static final List<String> posResponses = Arrays.asList("﻿Are u there", "How are you doing now?", "Where are you from?", "Where are you now?", "Where are you from?", "And where are you?", "Where are you from?", "Yes they are", "Where are you from?", "Let’s do that", "Yeah", "Yeah", "Either", "Either way", "Yeah", "Yeah", "Yeah exactly", "Yeah", "See u there", "What do u think", "Yeah", "Oh", "See u there", "Oh", "Oh", "Yeah", "Oh", "Yeah", "Done", "In", "Deal?", "Done", "Let’s do 7", "Let’s do 7", "Done", "Let’s go", "Yep", "In", "Yeah", "Oh ", "Oh", "Oh", "Yeah", "Yeah", "See u there", "Exactly", "Yeah", "Either", "See u there", "Yeah ", "What do u think", "Yeah", "Hah", "TRUE", "How many do u have", "Yeah", "See u there", "Exactly", "Yeah", "Yeah", "Yeah", "Where r u from", "Good how are u", "What r u doing", "How r u doing", "Congratulations", "What about u", "Where r u from", "So what are u doing now", "Yeah", "Oh", "Nope", "In", "Agree", "Either one", "Work", "Correct?", "Let’s try", "That’s correct", "Oh", "Done", "Got it ", "Done", "Done", "Yeah", "See u there", "How many do u have? ", "Oh", "See u there", "Done", "Done", "Correct", "Oh", "How many do u have?", "Done", "Done", "Deal? Done", "Done", "Done", "Done", "Done", "How many do u have", "Oh", "Done", "Oh", "Done", "See u there", "All done", "Let’s wait", "Hi how are u?", "How r u", "How r u", "How r u", "Where are you from?", "Where are you from?", "Ha ha h a", "Thoughts", "TRUE", "I agree with you", "TRUE", "Yeah", "Oh", "What would happen?", "Oh", "Oh", "Oh", "I agree with you", "In", "In", "Oh", "In", "I agree with you", "I agree with you", "See u there", "TRUE", "See u there", "They are", "That’s true", "Yeah", "Exactly", "See u there", "TRUE", "In", "3.33", "TRUE", "TRUE", "Perfs", "See u there", "What do u think ", "Oh", "In ", "How r u", "Hi how are u?", "Hi how r u", "See u there!", "TRUE", "In", "No", "Hope", "In", "OH", "Correct?", "Correct", "That’s correct", "Got it", "Got it", "Let’s go!", "See u there!", "Yay!!!", "In", "Yay", "What happens?", "Yeah", "Hi how r u", "How r u", "How r u?", "How r u", "Nm u?", "Nothing much", "What’s up", "I agree with you", "How is your day going?", "I agree with you", "I agree with you", "How’s life? ", "What are you doing now? ", "No", "Yeah ", "Yep", "As", "Best of luck", "We agreed", "Oh", "Oh", "Go ahead", "TRUE", " Hello? ", "Hello? J", "Nothing", "Done", "What do u think??", "What?", "W O W Where?", "Correct?", "No", "Done", "Nothing", "Oh", "K k", "TRUE", "Just one", "TRUE", "That’s true ", "TRUE", "No", "Nothing", "See u there", "See u there", "See you soon", " Hi how r u", " What’s up", "Just at work", "What are you doing?", "What are you up to this weekend", "Yeah", "And where?", "They were great", "Yeah", "Yeah", "Yeah", "Yeah", "Yeah", "No", "Yeah ", "Yeah", "And where?", "Yeah", "Agree", "In", "Oh", "Yeah", "Yeah", "Yeah", "Oh agree", "Yeah", "Cash", "Yeah true", "So yeah", "Yeah", "Yeah", "I agree with u", "I agree with you", "TRUE", "Yeah true", "Yeah exactly", "TRUE", "Yeah", "How are you doing?", "Yeah true", "Yeah", "Understood?", "Yeah", "Yeah", "Yeah", "Yeah", "TRUE", "Yeah", "Agree", "Yup ", "Yep", "Sigh", "Yep", "See what happens ", "How many do u have", "How many do u have", "Yeah", "Oh", "Thx", "Thx ", "How r u", "Hi how r u", "Good how are u", "Where are you from?", "Where are you from?", "What’s new?", "How are you doing now? ", "TRUE", "TRUE", " J", "Let’s do that", "Done", "Just one?", "See u there", "Either way", "Deal", "Done ", " See what happens", "Deal? ", "Mine too", "Mine too", "In", " Nope!", "No", "How r u", "Hi how are u?", "How are u! ", "Thoughts?", "Go ahead", "Deal?", "Done", "Done ", "Deal?", " J ", "What’s new?", "What are you doing?", "How was you weekend?", " Of course! ", "How was your weekend", "TRUE", "TRUE", "TRUE", "TRUE", "That’s true", "TRUE", "TRUE", "TRUE", "I agree with you", "Correct?", "TRUE", "Any thoughts?", "I agree with you!", "Join us!", "Pshaw", "Yep", "Correct?", "Deal", "Deal?", "Yep", "Yep", "See u there", "No", "Oh", "Oh", "Oh", "Mine too", "Keep in touch", "Thank u too", "Hi how r u", "How r u doing J", "Agreed", "Yeah", "Either way", "Either", "Nothing", "3.33", "Yeah", "Oh", "Oh", "Yeah", "Correct", "TRUE", "TRUE", "3.33", "Gotcha", "Yay", "What’s up", "How was your weekend? J", "Working a lot", "Agreed", "Hello?", "Hi?", "See u there!", "Yep", "On it", "Done", "Yeah", "Yeah", "3.33", "TRUE", "So how are u?", "Done", "Done", "TRUE", "No", "Yep", "So yeah", "Np", "No", "Yes they do", "No", "Done", "Done", "Yep", "What do u mean?", "What are your thoughts?", "Agreed!", "TRUE", "Done", "Asdfasd", "Of course!", "See u there! ", "J ", "Yeah", "Go ahead", "Yeah", "What’s his email? ", "Thank u too", "Nothing much u", "Hello? ", "What do u think?", "Hello?", "What do u think?", "What do u think?", "What did u think?", "How many do u have? ", "TRUE", "Tru", "What do u mean?", "What do u mean?", "I agree with u", "Exactly", "What? ", "Oh", "Oh ", "Sigh ", "No", "Oh", " Oh", "Oh ", "So yeah", "Yeah", "No", "Just finished", "What’s up?", "Nope", "No", "Lmfao", "Hi how r u", "Hi how are u?", "What’s up?", "No worries", "No worries", "Either", "Oh", "In", "Oh", "Understood? ", "Thoughts?", "Mine too", "Deal", "Let’s do that", "TRUE", "Agree", "Let’s do that", "See u there", "See u there", "Hi how r u", "J", "Good how are u", "Where are you?", "Where are you from?", "What are you looking for?", "Thoughts?", "See u there", "All done now", "Either way", "XD", "Yeah exactly", "Exactly", "XD", "Thx", "Let’s do that", "Yeah", "Yeah", "Yeah", "Oh", "Oh", "Done", "Ooops No", "Yeah", "J", "Deal?", "K k", "Thx", "Done ", "Done", "Done", "Done", "Oh", "Done", "Yay", "Yeah", "Yeah", "Yeah", "Yeah", "Yeah", "Gotcha", "Yea", "We are", "They were great", "Sigh", "See u there", "TRUE", "Yeah", "See what happens", "Yeah", "Whatever", "Yeah", "What’s up", "See u there!", "Hi how r u", "J", "How r you", "Where r u from", "What do u mean?", "What r u doing", "What do u mean?", "Oh", "Ouch", "What?", "Oh", "How many do u have", "J", "In", "Will do", "Got it", "Thx", "Deal", "I’m heading home now", "Deal", "Let’s try", "Yay", "Yep", "Yeah", "Done", "Hello?", "That’s correct", "Thx", "Done", "Yeah", "Got it?", "Done", "Let’s go", "Oh", "Yay", "Done", "Oh", "Oh", "What number?", "Done", "Done", "Done", "Oh", "Your turn", "Done", "Let’s do tomorrow", "Yeah", "See u there", "Done", "Done", "Done", "Let’s go", "Done", "Done", "Deal", "Done", "Yeah", "Deal", "That’s correct", "Thx", "And done", "Done", "Go ahead", "Hey what’s up", "There?", "What’s up", "Where are you?", "What’s new?", "Where you from", "How r u doing", "Anytime?", "Ready now", "Exactly", "Yeah", "Yeah", "Thoughts?", "Agree", "In", "Thoughts?", "no ", "Oh", "In", "Oh", "Oh", "How many do u have", "I agree with you", "I agree with you", "Yeah", "Oh", "yeah", "See you soon", "Let’s go now", "I agree with you", "That’s true", "TRUE", "Yep", "In", "Oh", "Oh", "Hey what’s up", "What’s new?", "What are you doing?", "How are u?", "Yay!", "When?", "What do u mean?", "Yeah", "Yeah", "Correct", "Correct ", "J", "No", "Oh got it", "No", "let’s do that", "Yay", "TRUE", "Deal", "In", "Oh got it", "Deal?", "Thx", "On it now", "Yup!", "Hello?", "Hello?", "Yeah", "Correct?", "Yay", "yeah", "Yup!", "L", "What? ", "Hi how r u", "How r u", "What r u doing", "question", "TRUE", "Agree", "Yep", "No", "Correct", "No", "8", "Agree", "TRUE", "Cash", "Oh", "Oh", "Oh", "I agree with you", "Done", "Did u get it?", "Thx", "Oh", "Correct!", "No", "Oh", "Mine too", "Done", "Thx", "Done", "Hello?", "Yeah", "Correct", "See u there", "Thx", "See u there", "We agree", "Yeah", "TRUE", "What do u mean?", "Exactly", "Yep", "Yeah", "No problem", "See u there", "Oh", "Done", "Let’s go now", "No", "Leaving now", "Thank u too", "How did it go?", "Nothing much J", "At work", "How’s life? J", "What are you doing up?", "Yeah", "yeah", "Oh", "OMG ", "Oh", "Yeah", "No", "Yeah", "Yeah", "Exactly", "I agree with you", "Thx", "Either way", "In", "Got it", "Yeah", "Alright", "Sold?", "Yeah", "Yeah", "Yeah", "I agree with u", "Yeah", "Yeah", "Exactly", "So yeah", "Yeah", "Yeah idk", "Yeah ik", "Yeah", "Yeah", "Yeah", "Yeah ik", "What about u?", "Yeah", "Yep", "Yeah", "Yeah", "In", "In", "Yeah", "Yeah", "Yeah", "Yea", "How many do u have", "Hi how r u?", "And how are you?", "What’s new?", "Where are you now?", "Hi?", "In", "Yeah exactly", "Deal?", "Perfs", "Yeah", "J", "U agree? ", "Yeah", "How many do u have", "Mine too", "They are", "I love them They are!", "Hey what’s up", "How are u?", "Correct", "I agree with you", "Correct", "We agree", "Thx", "What do u mean?", "Done", "Done", "What’s new?", "How’s life?", "Password?", "Go ahead!", "Correct?", "Yep", "Yep", "True true!", "Yep", "Exactly", "I agree with you", "I agree with you", "Woohoo!", "Aaagh", "How’s your day going?", "See what happens", "No", "Done", "Let’s go ", "Yeah", "No problem", "Np", "Yeah", "What time?", "Yeah", "Submitted", "Oh", "Yeah", "Nothing", "Oh", "TRUE", "Yeah", "Yea", "Yeah", "So yeah", "Let’s do that", "12", "Oh", "See u there", "Thx", "No", "How many do u have?", "Deal", "Oh", "What’s up?", "Good how are u?", "Congrats!", "How’s life?", "Busy at work", "J", "Oh agree", "What do u mean?", "Correct", "In", "Oh", "Yeah", "TRUE", "Never mind", "See u there", "Oh", "How is your day going?", "Correct", "J", "Correct?", "No problem", "See u there", "See u there", "TRUE", "Cash", "Yeah", "Np", "Hi how are u", "What’s up?", "Good how are u?", "Which ones?", "Deal?", "Let’s do that", "TRUE", "Got it", "In", "No", "Let’s try", "Take care", "We will", "Thanks for help", "No", "See u there", "See u there", "Sent Got it", "See u there!", "Ok will let her know", "Same to u!", "Hi? ", "Hello?", "What are your thoughts?", "Which one? ", "What did u think?", "How did u do it?", "I agree with u", "There", "Exactly", "Exactly", "What do u mean?", "Nope", "TRUE", "Gotcha", "TRUE", "Got it", "Yay!", "No", "No", "No", "See u there!", "Yeah", "Np", "Oh", "Thoughts?", "You agree?", "Got it", "In", "What do u mean?", "No What?", "Yeah", "No ", "Yeah", "Oh", "Yeah", "Either", "Yeah", "TRUE", "Deal", "Yep", "Done", "XD", "Done", "Done", "Oh", "Correct", "Oh", "Oh", "Done", "Done", "Got it", "Done", "Ready now?", "Done", "Done", "Let’s go", "Done", "Works", "Yeah", "Ya course", "Yeah", "Either", "Oh", "Yeah agreed", "In", "TRUE", "yeah", "Yeah", "yeah", "let’s do this", "Go for it", "Yea", "Who r u", "Where u from", "Oh", "Nothing much", "Are u ok", "Oh", "Nothing", "Gotcha", "In", "TRUE", "Done", "Sent", "I agree with you", "I agree with you", "Of course!", "I agree with you!", "Got it", "J", "Done", "Never mind", "Done", "Correct?", "Yeah", "In", "Thx", "Yeah", "Done", "Got it", "Thx", "Oh", "See u there", "Yeah", "What?", "Done", "Done", "Done", "Done", "Done", "Done", "Done", "Np", "Just one", "Done", "Oh", "Yeah", "Done", "See u there", "Yeah", "Done", "Submitted", "See u there", "Done", "No", "Hi what’s up?", "Who  r u", "What’s up", "At work", "How r you", "Where are you located?", "Understood", "How’s your day going?", "So yeah", "Thoughts?", "We all did", "No", "Agree", "Yeah", "oh", "Done", "yeah ", "Exactly", "That’s true", "We agree", "Oh", "Yeah", "Yeah", "What do u mean", "Oh", "Oh", "Got it", "Where are you now?", "Good how are u??", "Yeah!", "Nothing", "No", "Oh", "Sent", "Got it?", "yep", "Will do that now", "Woohoo", "Let’s go!", "Yep!", "Done!", "Yeah", "Done!", "Yep", "Ugh", "No", "What’s up", "How r u?", "How r u", "Nothing much", "What do u mean", "What’s up?", "Nothing", "What are you doing?", "Nope", "That’s correct", "TRUE", "See u there", "TRUE", "Oh", "What do u mean??", "Did u get it??", "Done", "Do u agree", "Deal", "Exactly", "Got it?", "Thx", "Oh", "What?", "correct", "yeah", "Correct", "Just one?", "Yeah", "What?", "Never mind", "What?", "We agree", "Will check later", "No", "TRUE", "See you soon", "I’m back now!", "Nothing much", "TRUE", "I agree with you", "Exactly", "What?", "Never mind", "Yea", "Gotcha", "So yeah", "Let’s do that", "Yeah", "Yeah", "Agreed", "Yeah", "TRUE", "Yeah", "TRUE", "TRUE", "Yeah", "Yea", "Yeah u r right", "Yeah ik", "Thx", "What’s new?", "where are you from?", "J", "J", "Alright", "TRUE", "True true!", "yep", "Exactly", "Deal", "In", "TRUE", "J", "Sent", "Correct", "Sent", "Yeah", "Got it", "Exactly", "Come now!", "See you soon!", "There?", "Hi what’s up", "How r you", "What’s up", "J", "Yeah", "What do u mean?", "I agree with you", "We got this", "Deal?", "Correct", "We agree", "Yeah", "Done", "Deal?", "What’s the question?", "Oh", "See u there", "Good how are u?", "How was your weekend?", "Yay!", "Got it!", "See u there!", "Yep", "I agree with you", "True true!", "Correct", "J", "What do u mean?", "TRUE", "Yeah", "Done", "Agreed", "Correct", "deal", "Yeah", "No", "yeah", "Done", "yeah", "yeah", "Of course she did", "J", "What’s up", "J", "yeah", "So yeah", "Yeah", "No", "How is your day going?", "5?", "Agreed", "How many do u have", "Nothing", "Done", "Oh", "How many do u have", "No", "Got it", "Yeah", "Hey what’s up", "What’s new?", "I agree with you", "Oh", "Yeah", "Yeah ", "Bye for now", "In", "L", "How come…", "Yep", "Sending now", "Correct", "How many do u have", "Ha ha h a", "No", "Done", "No", "Yeah", "Oh", "Yeah", "Whatever", "yeah", "No", "So yeah", "So yeah", "Hey how r u?", "How was your weekend?", "Nothing yet!", "No problem!", "Thoughts?", "Got it", "What do u mean?", "Deal?", "No", "That’s true", "Thanks for help", "Yeah", "Done", "Are u there", "Hello?", "Where r u?", "See u there!", "Thank u!", "Yes u r right", "So what r u up to?", "I agree with u", "Confirming", "TRUE", "That’s true", "Yes they do", "What do u mean?", "What do u mean?", "Gotcha", "Got this", "Yeah!", "What are you doing?", "What do u mean?", "Join us", "Oh", "It’s working now", "All set thanks", "Got it", "Done", "Oh", "Yeah", "No", "Yep", "Done", "Correct", "Correct", "So yeah", "No", "Oh", "Work?", "Np!", "What r u doing", "What u been up to", "Oh", "How r u", "In", "No", "What’s up", "Done", "There?", "Agreed", "Ha thx", "Exactly", "We agree", "Yeah", "question", "See u there", "What do u mean?", "Did u get it?", "In", "Where are you from?", "What do u mean?", "In", "Nevermind", "Leaving now", "See u then", "J  Where are you located?", "Correct?", "J ", "J  ");
    private static final int minimum = 0;
    private static final int maximum = posResponses.size()-1;

    private boolean newSugg = false;
    /**
     * To create an instance of this fragment, a Channel URL should be required.
     */
    public static GroupChatFragment newInstance(@NonNull String channelUrl) {
        GroupChatFragment fragment = new GroupChatFragment();

        Bundle args = new Bundle();
        args.putString(GroupChannelListFragment.EXTRA_GROUP_CHANNEL_URL, channelUrl);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            // Get channel URL from saved state.
            mChannelUrl = savedInstanceState.getString(STATE_CHANNEL_URL);
        } else {
            // Get channel URL from GroupChannelListFragment.
            mChannelUrl = getArguments().getString(GroupChannelListFragment.EXTRA_GROUP_CHANNEL_URL);
        }

        Log.d(LOG_TAG, mChannelUrl);

        mChatAdapter = new GroupChatAdapter(getActivity());
        setUpChatListAdapter();

        // Load messages from cache.
        mChatAdapter.load(mChannelUrl);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_group_chat, container, false);

        setRetainInstance(true);

        mRootLayout = (RelativeLayout) rootView.findViewById(R.id.layout_group_chat_root);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_group_chat);

        mCurrentEventLayout = rootView.findViewById(R.id.layout_group_chat_current_event);
        mCurrentEventText = (TextView) rootView.findViewById(R.id.text_group_chat_current_event);

        mMessageEditText = (EditText) rootView.findViewById(R.id.edittext_group_chat_message);
        mMessageSendButton = (Button) rootView.findViewById(R.id.button_group_chat_send);
        mFileUploadButton = (ImageButton) rootView.findViewById(R.id.button_group_chat_upload);

        //modify
        setSuggesntion(posResponses, rootView);
        setSuggInvisible();


        mMessageSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userInput = mMessageEditText.getText().toString();

                if (userInput == null || userInput.length() <= 0) {
                    return;
                }

                sendUserMessage(userInput);
                mMessageEditText.setText("");
            }
        });

        mFileUploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestMedia();
            }
        });

        //modify
        mSuggestionButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button b = (Button) v;
                String sugg = b.getText().toString();
                sendUserMessage(sugg);
            }
        });

        mSuggestionButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button b = (Button) v;
                String sugg = b.getText().toString();
                sendUserMessage(sugg);
            }
        });

        mSuggestionButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button b = (Button) v;
                String sugg = b.getText().toString();
                sendUserMessage(sugg);
            }
        });

        mIsTyping = false;
        mMessageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!mIsTyping) {
                    setTypingStatus(true);
                }

                if (s.length() == 0) {
                    setTypingStatus(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        setUpRecyclerView();

        setHasOptionsMenu(true);

        return rootView;
    }

    private void refresh() {
        if (mChannel == null) {
            GroupChannel.getChannel(mChannelUrl, new GroupChannel.GroupChannelGetHandler() {
                @Override
                public void onResult(GroupChannel groupChannel, SendBirdException e) {
                    if (e != null) {
                        // Error!
                        e.printStackTrace();
                        return;
                    }

                    mChannel = groupChannel;
                    mChatAdapter.setChannel(mChannel);
                    mChatAdapter.loadLatestMessages(30, new BaseChannel.GetMessagesHandler() {
                        @Override
                        public void onResult(List<BaseMessage> list, SendBirdException e) {
                            mChatAdapter.markAllMessagesAsRead();
                        }
                    });
                    updateActionBarTitle();
                }
            });
        } else {
            mChannel.refresh(new GroupChannel.GroupChannelRefreshHandler() {
                @Override
                public void onResult(SendBirdException e) {
                    if (e != null) {
                        // Error!
                        e.printStackTrace();
                        return;
                    }

                    mChatAdapter.loadLatestMessages(30, new BaseChannel.GetMessagesHandler() {
                        @Override
                        public void onResult(List<BaseMessage> list, SendBirdException e) {
                            mChatAdapter.markAllMessagesAsRead();
                        }
                    });
                    updateActionBarTitle();
                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        mChatAdapter.setContext(getActivity()); // Glide bug fix (java.lang.IllegalArgumentException: You cannot start a load for a destroyed activity)

        // Gets channel from URL user requested

        Log.d(LOG_TAG, mChannelUrl);

        SendBird.addChannelHandler(CHANNEL_HANDLER_ID, new SendBird.ChannelHandler() {
            @Override
            public void onMessageReceived(BaseChannel baseChannel, BaseMessage baseMessage) {

                if (baseChannel.getUrl().equals(mChannelUrl)) {
                    mChatAdapter.markAllMessagesAsRead();
                    // Add new message to view
                    mChatAdapter.addFirst(baseMessage);
                }
                //modify
                updateSugg(posResponses);
                setSuggVisible();
            }

            @Override
            public void onMessageDeleted(BaseChannel baseChannel, long msgId) {
                super.onMessageDeleted(baseChannel, msgId);
                if (baseChannel.getUrl().equals(mChannelUrl)) {
                    mChatAdapter.delete(msgId);
                }
            }

            @Override
            public void onReadReceiptUpdated(GroupChannel channel) {
                if (channel.getUrl().equals(mChannelUrl)) {
                    mChatAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onTypingStatusUpdated(GroupChannel channel) {
                if (channel.getUrl().equals(mChannelUrl)) {
                    List<Member> typingUsers = channel.getTypingMembers();
                    displayTyping(typingUsers);
                }
            }

        });

        SendBird.addConnectionHandler(CONNECTION_HANDLER_ID, new SendBird.ConnectionHandler() {
            @Override
            public void onReconnectStarted() {
            }

            @Override
            public void onReconnectSucceeded() {
                refresh();
            }

            @Override
            public void onReconnectFailed() {
            }
        });

        if (SendBird.getConnectionState() == SendBird.ConnectionState.OPEN) {
            refresh();
        } else {
            if (SendBird.reconnect()) {
                // Will call onReconnectSucceeded()
            } else {
                String userId = PreferenceUtils.getUserId(getActivity());
                if (userId == null) {
                    Toast.makeText(getActivity(), "Require user ID to connect to SendBird.", Toast.LENGTH_LONG).show();
                    return;
                }

                SendBird.connect(userId, new SendBird.ConnectHandler() {
                    @Override
                    public void onConnected(User user, SendBirdException e) {
                        if (e != null) {
                            e.printStackTrace();
                            return;
                        }

                        refresh();
                    }
                });
            }
        }
    }

    @Override
    public void onPause() {
        setTypingStatus(false);

        SendBird.removeChannelHandler(CHANNEL_HANDLER_ID);
        SendBird.removeConnectionHandler(CONNECTION_HANDLER_ID);
        super.onPause();
    }

    @Override
    public void onDestroy() {
        // Save messages to cache.
        mChatAdapter.save();

        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(STATE_CHANNEL_URL, mChannelUrl);

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_group_chat, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_group_channel_invite) {
            Intent intent = new Intent(getActivity(), InviteMemberActivity.class);
            intent.putExtra(EXTRA_CHANNEL_URL, mChannelUrl);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_group_channel_view_members) {
            Intent intent = new Intent(getActivity(), MemberListActivity.class);
            intent.putExtra(EXTRA_CHANNEL_URL, mChannelUrl);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == INTENT_REQUEST_CHOOSE_MEDIA && resultCode == Activity.RESULT_OK) {
            // If user has successfully chosen the image, show a dialog to confirm upload.
            if (data == null) {
                Log.d(LOG_TAG, "data is null!");
                return;
            }

            sendFileWithThumbnail(data.getData());
        }

        // Set this as true to restore background connection management.
        SendBird.setAutoBackgroundDetection(true);
    }

    private void setUpRecyclerView() {
        mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setReverseLayout(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mChatAdapter);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (mLayoutManager.findLastVisibleItemPosition() == mChatAdapter.getItemCount() - 1) {
                    mChatAdapter.loadPreviousMessages(30, null);
                }
            }
        });
    }

    private void setUpChatListAdapter() {
        mChatAdapter.setItemClickListener(new GroupChatAdapter.OnItemClickListener() {
            @Override
            public void onUserMessageItemClick(UserMessage message) {
                // Restore failed message and remove the failed message from list.
                if (mChatAdapter.isFailedMessage(message)) {
                    retryFailedMessage(message);
                    return;
                }

                // Message is sending. Do nothing on click event.
                if (mChatAdapter.isTempMessage(message)) {
                    return;
                }


                if (message.getCustomType().equals(GroupChatAdapter.URL_PREVIEW_CUSTOM_TYPE)) {
                    try {
                        UrlPreviewInfo info = new UrlPreviewInfo(message.getData());
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(info.getUrl()));
                        startActivity(browserIntent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFileMessageItemClick(FileMessage message) {
                // Load media chooser and remove the failed message from list.
                if (mChatAdapter.isFailedMessage(message)) {
                    retryFailedMessage(message);
                    return;
                }

                // Message is sending. Do nothing on click event.
                if (mChatAdapter.isTempMessage(message)) {
                    return;
                }


                onFileMessageClicked(message);
            }
        });
    }

    private void retryFailedMessage(final BaseMessage message) {
        new AlertDialog.Builder(getActivity())
                .setMessage("Retry?")
                .setPositiveButton(R.string.resend_message, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == DialogInterface.BUTTON_POSITIVE) {
                            if (message instanceof UserMessage) {
                                String userInput = ((UserMessage) message).getMessage();
                                sendUserMessage(userInput);
                            } else if (message instanceof FileMessage) {
                                Uri uri = mChatAdapter.getTempFileMessageUri(message);
                                sendFileWithThumbnail(uri);
                            }
                            mChatAdapter.removeFailedMessage(message);
                        }
                    }
                })
                .setNegativeButton(R.string.delete_message, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == DialogInterface.BUTTON_NEGATIVE) {
                            mChatAdapter.removeFailedMessage(message);
                        }
                    }
                }).show();
    }

    /**
     * Display which users are typing.
     * If more than two users are currently typing, this will state that "multiple users" are typing.
     *
     * @param typingUsers The list of currently typing users.
     */
    private void displayTyping(List<Member> typingUsers) {

        if (typingUsers.size() > 0) {
            mCurrentEventLayout.setVisibility(View.VISIBLE);
            String string;

            if (typingUsers.size() == 1) {
                string = typingUsers.get(0).getNickname() + " is typing";
            } else if (typingUsers.size() == 2) {
                string = typingUsers.get(0).getNickname() + " " + typingUsers.get(1).getNickname() + " is typing";
            } else {
                string = "Multiple users are typing";
            }
            mCurrentEventText.setText(string);
        } else {
            mCurrentEventLayout.setVisibility(View.GONE);
        }
    }

    private void requestMedia() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // If storage permissions are not granted, request permissions at run-time,
            // as per < API 23 guidelines.
            requestStoragePermissions();
        } else {
            Intent intent = new Intent();

            // Pick images or videos
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                intent.setType("*/*");
                String[] mimeTypes = {"image/*", "video/*"};
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
            } else {
                intent.setType("image/* video/*");
            }

            intent.setAction(Intent.ACTION_GET_CONTENT);

            // Always show the chooser (if there are multiple options available)
            startActivityForResult(Intent.createChooser(intent, "Select Media"), INTENT_REQUEST_CHOOSE_MEDIA);

            // Set this as false to maintain connection
            // even when an external Activity is started.
            SendBird.setAutoBackgroundDetection(false);
        }
    }

    private void requestStoragePermissions() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // For example if the user has previously denied the permission.
            Snackbar.make(mRootLayout, "Storage access permissions are required to upload/download files.",
                    Snackbar.LENGTH_LONG)
                    .setAction("Okay", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                    PERMISSION_WRITE_EXTERNAL_STORAGE);
                        }
                    })
                    .show();
        } else {
            // Permission has not been granted yet. Request it directly.
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSION_WRITE_EXTERNAL_STORAGE);
        }
    }

    private void onFileMessageClicked(FileMessage message) {
        String type = message.getType().toLowerCase();
        if (type.startsWith("image")) {
            Intent i = new Intent(getActivity(), PhotoViewerActivity.class);
            i.putExtra("url", message.getUrl());
            i.putExtra("type", message.getType());
            startActivity(i);
        } else if (type.startsWith("video")) {
            Intent intent = new Intent(getActivity(), MediaPlayerActivity.class);
            intent.putExtra("url", message.getUrl());
            startActivity(intent);
        } else {
            showDownloadConfirmDialog(message);
        }
    }

    private void showDownloadConfirmDialog(final FileMessage message) {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // If storage permissions are not granted, request permissions at run-time,
            // as per < API 23 guidelines.
            requestStoragePermissions();
        } else {
            new AlertDialog.Builder(getActivity())
                    .setMessage("Download file?")
                    .setPositiveButton(R.string.download, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == DialogInterface.BUTTON_POSITIVE) {
                                FileUtils.downloadFile(getActivity(), message.getUrl(), message.getName());
                            }
                        }
                    })
                    .setNegativeButton(R.string.cancel, null).show();
        }

    }

    private void updateActionBarTitle() {
        String title = "";

        if(mChannel != null) {
            title = TextUtils.getGroupChannelTitle(mChannel);
        }

        // Set action bar title to name of channel
        if (getActivity() != null) {
            ((GroupChannelActivity) getActivity()).setActionBarTitle(title);
        }
    }

    private void sendUserMessageWithUrl(final String text, String url) {
        new WebUtils.UrlPreviewAsyncTask() {
            @Override
            protected void onPostExecute(UrlPreviewInfo info) {
                UserMessage tempUserMessage = null;
                BaseChannel.SendUserMessageHandler handler = new BaseChannel.SendUserMessageHandler() {
                    @Override
                    public void onSent(UserMessage userMessage, SendBirdException e) {
                        if (e != null) {
                            // Error!
                            Log.e(LOG_TAG, e.toString());
                            Toast.makeText(
                                    getActivity(),
                                    "Send failed with error " + e.getCode() + ": " + e.getMessage(), Toast.LENGTH_SHORT)
                                    .show();
                            mChatAdapter.markMessageFailed(userMessage.getRequestId());
                            return;
                        }

                        // Update a sent message to RecyclerView
                        mChatAdapter.markMessageSent(userMessage);
                    }
                };

                try {
                    // Sending a message with URL preview information and custom type.
                    String jsonString = info.toJsonString();
                    tempUserMessage = mChannel.sendUserMessage(text, jsonString, GroupChatAdapter.URL_PREVIEW_CUSTOM_TYPE, handler);
                } catch (Exception e) {
                    // Sending a message without URL preview information.
                    tempUserMessage = mChannel.sendUserMessage(text, handler);
                }


                // Display a user message to RecyclerView
                mChatAdapter.addFirst(tempUserMessage);
            }
        }.execute(url);
    }

    private void sendUserMessage(String text) {
        List<String> urls = WebUtils.extractUrls(text);
        if (urls.size() > 0) {
            sendUserMessageWithUrl(text, urls.get(0));
            return;
        }

        UserMessage tempUserMessage = mChannel.sendUserMessage(text, new BaseChannel.SendUserMessageHandler() {
            @Override
            public void onSent(UserMessage userMessage, SendBirdException e) {
                if (e != null) {
                    // Error!
                    Log.e(LOG_TAG, e.toString());
                    Toast.makeText(
                            getActivity(),
                            "Send failed with error " + e.getCode() + ": " + e.getMessage(), Toast.LENGTH_SHORT)
                            .show();
                    mChatAdapter.markMessageFailed(userMessage.getRequestId());
                    return;
                }

                // Update a sent message to RecyclerView
                mChatAdapter.markMessageSent(userMessage);
            }
        });
        updateSugg(posResponses);
        setSuggVisible();
        // Display a user message to RecyclerView
        mChatAdapter.addFirst(tempUserMessage);
    }

    /**
     * Notify other users whether the current user is typing.
     *
     * @param typing Whether the user is currently typing.
     */
    private void setTypingStatus(boolean typing) {
        if (mChannel == null) {
            return;
        }

        if (typing) {
            mIsTyping = true;
            mChannel.startTyping();
        } else {
            mIsTyping = false;
            mChannel.endTyping();
        }
    }

    //modify
    private int generateRand(int max, int min) {
        Random rn = new Random();
        int range = max - min + 1;
        int randomNum =  rn.nextInt(range) + min;
        return randomNum;
    }

    private void setSuggesntion(List<String> suggList, View rootView) {
        mSuggestionButton1 = (Button) rootView.findViewById(R.id.suggestion1);

        mSuggestionButton2 = (Button) rootView.findViewById(R.id.suggestion2);

        mSuggestionButton3 = (Button) rootView.findViewById(R.id.suggestion3);
        updateSugg(suggList);
    }

    private void updateSugg(List<String> suggList) {

        List<String> threeSugg = new ArrayList<String>();
        while (threeSugg.size() < 3) {
            String s = suggList.get(generateRand(maximum, minimum));
            if (!(threeSugg.contains(s))) {
                threeSugg.add(s);
            }
        }
        mSuggestionButton1.setText(threeSugg.get(0));
        mSuggestionButton2.setText(threeSugg.get(1));
        mSuggestionButton3.setText(threeSugg.get(2));
    }

    private void setSuggVisible() {
        mSuggestionButton1.setVisibility(View.VISIBLE);
        mSuggestionButton2.setVisibility(View.VISIBLE);
        mSuggestionButton3.setVisibility(View.VISIBLE);
    }

    private void setSuggInvisible() {
        mSuggestionButton1.setVisibility(View.GONE);
        mSuggestionButton2.setVisibility(View.GONE);
        mSuggestionButton3.setVisibility(View.GONE);
    }
    /**
     * Sends a File Message containing an image file.
     * Also requests thumbnails to be generated in specified sizes.
     *
     * @param uri The URI of the image, which in this case is received through an Intent request.
     */
    private void sendFileWithThumbnail(Uri uri) {
        // Specify two dimensions of thumbnails to generate
        List<FileMessage.ThumbnailSize> thumbnailSizes = new ArrayList<>();
        thumbnailSizes.add(new FileMessage.ThumbnailSize(240, 240));
        thumbnailSizes.add(new FileMessage.ThumbnailSize(320, 320));

        Hashtable<String, Object> info = FileUtils.getFileInfo(getActivity(), uri);

        if (info == null) {
            Toast.makeText(getActivity(), "Extracting file information failed.", Toast.LENGTH_LONG).show();
            return;
        }

        final String path = (String) info.get("path");
        final File file = new File(path);
        final String name = file.getName();
        final String mime = (String) info.get("mime");
        final int size = (Integer) info.get("size");

        if (path.equals("")) {
            Toast.makeText(getActivity(), "File must be located in local storage.", Toast.LENGTH_LONG).show();
        } else {
            // Send image with thumbnails in the specified dimensions
            FileMessage tempFileMessage = mChannel.sendFileMessage(file, name, mime, size, "", null, thumbnailSizes, new BaseChannel.SendFileMessageHandler() {
                @Override
                public void onSent(FileMessage fileMessage, SendBirdException e) {
                    if (e != null) {
                        Toast.makeText(getActivity(), "" + e.getCode() + ":" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        mChatAdapter.markMessageFailed(fileMessage.getRequestId());
                        return;
                    }

                    mChatAdapter.markMessageSent(fileMessage);
                }
            });

            mChatAdapter.addTempFileMessageInfo(tempFileMessage, uri);
            mChatAdapter.addFirst(tempFileMessage);
        }
    }
}
