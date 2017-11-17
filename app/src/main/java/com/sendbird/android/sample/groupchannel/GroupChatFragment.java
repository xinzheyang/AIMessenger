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

import java.io.BufferedWriter;
import java.io.FileWriter;
import android.content.Context;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.IOException;
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

    private Button mSuggestionButton1;
    private Button mSuggestionButton2;
    private Button mSuggestionButton3;

    private GroupChannel mChannel;
    private String mChannelUrl;
    private PreviousMessageListQuery mPrevMessageListQuery;

    private boolean mIsTyping;

    private Context mContext;




    private static final List<String> posResponses = Arrays.asList("Hope", "Thank you!", "If you're interested!", "Good ones", "Does that work? I'm flexible", "7 is good", "Ok sure sure", "Yay!", "Yes please!", "I have to go now", "Indeed I did", "Perfect", "I agree!", "Correct", "Yes I agree", "Yep right", "Oh got it", "Lmao", "Confirmed", "It looks better", "Better", "Yes please", "Works", "Yup!", "I like that idea", "That works!", "Hahaha", "Yeah sounds good", "Okay thanks!", "Yes for sure!!", "It's great!", "Yes for sure", "I liked that one", "I'm good thanks, how are you?", "Yes!", "Yeah maybe", "Haha", "I'm doing good", "Yeah agreed", "I'm ok thanks", "And good luck", "Let's go!", "A good one", "Yay!!!", "let's do this", "That's my point", "So that's good", "Go for it", "Ok!", "Good luck!", "I see", "Welcome back!!", "Hey how are you? Exactly", "Well done", "Couldn't agree more", "Cool thanks", "Am fine thank you", "See u then", "Ok got it", "Cool", "Okay thx Cool", "Hope you are doing well", "Don't you think?", "That's good", ";)", "I agree with you!", ":) Okay", "Join us!", "And I miss you!", "But it worked!", "So that's fine", "Yes sounds good", "That's right", "I got it", "Thanks", "Ya that's fine", "Done! Thanks!! Thx", "Correct!", "We're ready", "Yay", "Nice", "Great", "So just let me know!", "Yup! Ok! Perfect", "We agree", "Yeah", "That's fine", "That could work", "Yes pls", "Hi what's up?", "See u there! Ok let me know!", "You are lucky", "I am open", "Okay", "We can wait", "Keep in touch", "Makes sense", "Thank u too", "Talk to you later", "Yeah exactly", "Agreed", "Please advise", "Good point", "Then we are good", "Sure", "What do u think", "I like your idea", "Good", "That should be fine", "Sounds good", "It seems good", "So it's good", "Otherwise looks good", "Cool?", "Good! How are you?", "Good to hear!", "I think so too", "Sounds right", "I'm back", "Yes", "Good to hear!", "Not bad", "That's a good idea", "That's good!", "That is good", "Done", "That's perfect", "It's ok", "Good how are u??", "Hi?", "Best of luck", "I am!", "That would be better", "Yeah!", "That is fine", "Right?", "Yep", "We agreed", "I like that", "Exactly", "Yeah I guess so", "Sounds fair", "So that's fine", "Souunds good", "Yes it would", "I am lucky", "Yeah", "I do", "Woohoo", "Yep!", "Wow", "Yes they do", "Done!", "TRUE", "Thanks", "I'm good!", "Haha", "Wow that's beautiful", "Same here!", "I'm ready", "Agreed!", "Me too!", "Alright thanks", "Yeah it's fine", "Okay cool thanks", "Thanks!", "Yes it was!", "I can do that", "Yeah I think so", "Good job", "Alright", "I'm proud of you", "Of course!", "All great", "Sold?", "Thanks again", "Happy to help", "Oh okay cool", "Please", "I will", "Good how are u", "I understand", "Good", "See u there!", "I'm happy with that", "Ok I agree", "Cool.", "Congratulations", "Okay thanks!", "Good job!", "I have to go now", "Yeah ik", "LOL", "Ok thanks", "W O W", "Thanks! You're the best!", "Yeah that's fine", "Good luck", "Yeah sure!", "Would that be better?", "Thanks anyways", "Okay thanks", "W O W Where?", "Okay!", "Okay okay!", "That's good to hear", "Yes sure", "Ok great", "Okay let's do it", "K k", "Yeah it does", "Okay let's do it", "I'm fine with that", "Thanks for the idea", "Tru", "I'm fine with hat", "Thanks though!", "For sure", "That's true", "Thanks so much", "Yes that sounds like a good idea", "Thanks thanks!", "Please thanks", ":D", "Agree", "Welcome back", "Okay thx", "Thanks for the feedback", "Ok that makes sense", "It's good", "I don't see it", "That makes more sense", "Welcome", "I love them", "I think you are right", "Yes we do", "I love them They are!", "Come now!", "I accept", "Let's try", "See you soon!", "Looks great", "Hi what's up", "Yes can do", "You are right", "Wow thanks", "We got this", "I'm flexible", "Lmfao", "Yes Yes I think so Sure", "Let's do it again", "I am here now!", "That's correct", "Got it!", "Let's do it", "Yeah that makes sense", "We are ready", "I'm ready!", "Cool!", "Ok sounds good", "Okay I will", "K that's good", "So that's good", "Pretty good", "I'm doing good", "That's ok", "Of course she did", "Is that ok for you?", "Great!", "Oh good", "Go ahead!", "That makes sense now", "See you soon", "You're the best", "Awesome!", "True true!", "Ya I know", "I agree", "Ok thanks!", "Could be", "Thank you", "Ok Thank you", "Just confirming", "I'm sure!", "Correct", "You're right!", "I'm back!", "Woohoo!", "Sure sounds fine", "It is better", "I'm doing good", "All done", "Thanks mate!", "Fixed", "So just me me know!", "I'm good and u?", "Have fun", "I'll work on that", "Doing great", "Ok see you later", "XD", "Good to hear", "Yes agreed", "See ya there", "No problem!", "I'm fine with it", "That's pretty good", "That sounds right", "What are you up to this weekend", "Ha ha h a", "Yeah we can", "Yerah sure np", "So that's good", "Thoughts", "I like it", "Thank u!", "Good how are u?", "Yea", "They were great", "Congrats!", "I agree with you", "That makes a lot of sense", "Haha Ok then", "But it makes sense", "Confirming", "yep totally fine", "Good question", "Thanks though", "Thanks so much!", "Thanks for your help!", "Fine", "Beautiful", "Got this", "That's true", "Yeah I agree", "Cool that works for me", "And good luck", "Would that work", "Those are pretty good", "Thanks for your help", "It's working now", "I love you", "Yes true", "Cool thanks for letting me know", "Okay good", "All set thanks", "We can do it", "Sure just let me know when", "That makes sense", "Yeah ok", "Thanks for the offer!", "That sounds right That's correct", "Of course! Happy to help", "Yes right?", "Ok cool", "I'm in", "I'll do that!", "Oh agree", "Ok let me know", "Yeah for sure!", "Thanks  Done", "I'm excited", "Absolutely correct", "Okay thank you", "Ha thx", "It's correct", "Works for me", "I'm good thanks", "Yeah true", "So yeah", "That's good enough", "What do you think? So that's good", "Oh right", "I'm good", "Okay sure", "I am good!", "Cool", "Yep I agree", "I agree with u", "Okay! Okay done", "Yes Yeah", "Great work!", "I am ready!", "Take care", "Hey what's up", "Yes sure!", "Okay Yeah", "Sure!", "Good and u", "Thanks for help", "See u there!", "Same here!", "Oh cool!", "Same to u!", "Looks good", "Hi?", "I think you're right", "Thanks for explaining", "Okay that's good", "I agree with that", "Ya I know", "Well done!", "Yeah I do", "Oh ok", "Yes I am", "Yes I'm here", "Free", "Okay that's good", "Yup", "Thx", "Thx", "Doing good thanks", "You are the best", "Sounds good", "Yes I agree", "Good catch", "Yes thanks", "thanks!", "Sure", "As many as you want", "Deal", "Great job!", "You rock", "Haha thx", "They're cute", "Haha", "So take your time", "Sounds good?", "Sure I agree", "It's fun", "I vote yes", "Good!", "Hope you are doing well!", "I miss you!", "I miss you!", "Great! How are you?", "Of course!", "Happy to help.", "Yes will do!", "Absolutely", "Let's do it!", "Absolutely!", "I like it!");
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
        mContext = mChatAdapter.getmContext();
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
                    writeToFile("Received: "+baseMessage.toString(), mContext);
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
        writeToFile("User send:" + text, mContext);
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
        writeToFile(threeSugg.toString(), mContext);
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

    private void writeToFile(String data,Context context) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("config.txt", Context.MODE_APPEND));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
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
