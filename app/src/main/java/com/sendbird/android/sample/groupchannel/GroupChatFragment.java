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




    private static final List<String> posResponses = Arrays.asList("Yes", "I’m good thanks, how are you?", "I’m ok thanks", "What about you?", "What about you?", "Cool", "What about you?", "That’s good", "Either works for me", "What do you think?", "Thanks", "Nice", "I’m fine with either", "Okay", "Ok", "Good point", "Then we are good", "Sure", "Sure", "Cool", "Cool", "Okay", "Sure", "Sounds good", "Ok", "Cool?", "Ok", "Sure", "Ok", "Cool?", "Ok", "Ok", "Sure", "Sounds right", "Ok", "Yes ", "Yes", "Yes", "Ok", "Ok", "Ok", "Ok", "Ok", "Ok", "OK", "Ok", "Yes", "Okay", "We’ll go", "Ok", "Ok Okay", "Ok", "Okay", "Ok", "Right?", "Right?", "Haha", "Haha", "Ok", "Souunds good", "Sure", "Ok", "Cool", "Nice", "Thanks", "Haha ", "You’re right", "Haha", "Ok", "Haha", "Haha", "I’m ready", "What do you think?", "Yeah it’s fine", "Sure", "Haha", "Thanks!", "Thanks", "Good job", "I’m proud of you", "Thanks again", "Thanks", "Ok", "Good", "Ok", "Ok thanks", "What about you?", "Would that be better?", "Sure", "Okay", "Okay thanks", "What do you think?", "Sure", "Okay!", "I’m fine with that", "Thanks though!", "Ok", "Sure", "Sure", "Thanks thanks!", "Thanks", "Do you agree?", "Thanks", "Are you available?", "I am fine with either", "What do you think?", "Ok", "Okay", "Thanks!", "Okay!", "Ok", "Yes", "Yes we do", "Ok", "Ok", "Okay", "Okay", "Cool?", "So that fine", "It’s your turn", "Yes", "It’s your turn", "Yes Yes I think so Sure", "Okay ", "Okay thanks", "Okay", "Okay", "Thanks", "Yes", "Right?", "Okay", "Okay thanks", "Okay", "What do you think?", "Cool? ", "Thanks", "Ok", "Ok", "Okay", "Okay", "Right?", "Thanks", "It’s your turn", "Ok", "Better?", "K that’s good", "Pretty good", "Ok", "Cool", "Okay", "Okay", "Okay", "Right?", "Okay", "Ok", "Okay", "Ok", "Sure", "Okay", "Thanks", "Okay", "Thanks", "See you then", "Ok", "It’s your turn", "Okay", "Sure", "Ok", "How many do you have?", "Ok", "Ok ", "Sure", "Ok", "Thanks", "Cool", "Ok done", "Ok", "Ok", "Ok ", "Okay", "Thanks", "Ok", "Yes", "Ok", "Yes", "Ok", "Thanks", "Thanks", "Right? ", "Cool", "Cool", "Ok", "Okay", "Ok", "Okay", "Okay", "How many do you want?", "Yes", "See you then", "Okay", "Okay", "Okay", "How many do you have?", "Cool", "Thanks mate! ", "Yes", "Ok", "I’m good and u?", "Doing great", "Good to hear", "What you up to today", "How are you?", "Yes. How are you?", "Ok", "I am ready", "Is that correct?", "Okay thanks", "Do you agree?", "I like it", "Ok thanks", "What’s your opinion?", "Okay", "What do you think?", "What do you think?", "What do you think?", "What do you think?", "That makes a lot of sense", "Haha", "I believe", "Good question", "Right", "Good job", "Ok", "Cool", "Cool", "Okay", "Ok", "Sounds good", "Okay", "Ok", "Sounds good", "Okay", "Thanks", "I’m ready", "Sounds good", "Okay", "Cool", "What do you think?", "Ok", "Right?", "And how much", "Ok", "Ok", "Sure", "Works for me", "Sounds good", "Haha", "Okay", "Cool", "Okay", "Thanks", "Haha", "Sure", "Ok", "Feeling better?", "I’m good", "How are you?", "I am good!", "How are you?", "How are you??", "What about you?", "How are you?", "I am ready!", "I am ready!", "Yes sure!", "Sure!", "Oh cool!", "Ok", "Sure", "I am heading out now", "Thank you!", "Yeah definitely!", "If you're interested!", "Yes", "Sure!", "Yes", "Yes", "Cool", "Ok", "Ok let me know then", "What do you think?", "Perfect", "Ok", "Lmao", "Better", "What do you think?", "Hahaha", "I liked that one", "Yes", "Thank you!", "Good question", "Ok", "Sure", "Sure", "Yes", "Ok!", "Ok", "Right?", "Right?", "Sure", "Ok", "Ok", "Ok got it", "Ok", "Ok sure", "Right?", "K that’s fine", "So that’s fine", "Done! Thanks!! Thx", "Right?", "Yes", "Yes", "Thanks!", "Yup! Ok! Perfect", "See u there! Ok let me know!", "Ok let me know!", "Ok", "How are you?", "Good ", "What do you think?", "Fine ", "What about you?", "Hahaha", "It’s ok", "Thanks", "Yes", "Yes", "Yes", "It’s your choice", "Ok Okay", "What do you think?", "What do you think?", "What do you think? ", "Sounds fair", "Yes", "Right?", "Ok", "Do you agree?", "Ok", "Yes ", "Ok", "Thanks ", "Thanks", "Right? So it’s good ", "Okay", "Okay thanks", "Sure", "Sounds good? ", "What do you think?", "Ok", "Sounds good", "Ok", "Yes", "Works for me", "Do you agree?", "Ok", "Ok", "Ok", "Yes", "That is correct", "Yes", "Right?", "Ok", "Right?", "Yes", "Yes", "Thanks", "Ok", "Yes", "Cool", "That is correct", "Thanks", "Ok", "What do you think?", "Right?", "Ok", "Right?", "Okay", "Right?", "Ok", "I think you are right", "I accept", "Right?", "Yes", "Ok", "You are right", "Thanks", "Thanks", "How many do you have?", "Right? ", "Yes", "Yes", "Ok", "Ok", "We are ready", "Ok", "Ok", "I am ready", "Ok", "That works", "Ok", "What do you think?", "Yes", "Sure", "Ok", "See you then", "Thanks", "Thank you", "Hi! How are you?", "What do you think?", "Ok", "What do you think?", "What do you think?", "Right", "What do you think?", "Sure", "Okay", "Okay thanks", "Okay", "I like the first one", "Okay", "Okay", "How many do you have?", "Okay", "Okay good", "Is that correct?", "Yes", "Okay", "That makes sense", "Right?", "That sounds right That’s correct", "Ok cool", "Ok cool", "Right?", "Okay", "Okay", "What do you think?", "Sounds good", "Ok", "What do you think? So that’s good", "Okay", "Okay", "Okay Yeah", "What do you think?", "Okay that's good", "Sure", "Sure", "Okay", "Oh ok", "Okay", "Okay", "So just let me know! Thanks", "Okay", "Okay that’s good", "Okay", "Okay", "Thanks", "Are you okay?", "Would that be better? /:", "Ok", "Good", "Ok", "Okay", "Ok thx", "Good job", "Doing good thanks", "That’s good", "Yes. How are you?", "Sure", "Are you free?", "Okay", "Hi how are you?", "Thanks", "Sure", "Okay thank you J", "Good question", "Okay", "It’s your turn", "Okay", "What do you think?", "Sure", "Okay", "What do you think? ", "And what do you think?", "What’s your opinion? ", "What do you think?", "Cool", "Thanks", "You are the best", "What do you think?", "Sure", "What do you think?", "Sure", "Ok", "Sounds good ", "Yes", "Cool", "Thanks", "What do you think?", "Sure", "What do you think?", "What do you think?", "What do you think?", "Sure", " Right?", "What do you think?", "Do you agree?", "Sounds good", "Thanks", "Good catch", " Yes thanks", " thanks!", "Thanks", "Thanks ", "Sure ", "As many as you want", "Ok", "Cool?", "Thanks", "Pretty good", "Cool", "Great job!", " You rock ", "Thank you!", "Thank you!", "Haha thx", "They’re cute", "Thanks", " Haha ", "Okay", "Okay", "Thanks!", "How are you?", "Yes? ", "Sure!", "Sure", "Yes", "Thanks!", "Okay", "Sounds good?", "Sure", "What do you think?", "Ok sure", "Okay", "Okay", "Okay", "I liked that one", "Okay thanks", "Do you agree?", "It’s fun", "Okay", "Sure", "I’m fine with that", "Okay thanks", "Do you agree?", "Okay", "Yes", "I vote yes", "Sounds good", "Thanks thanks!", "Right? ", "Yes", "Good question", "Yes", "It’s your turn", "Okay", "Okay", " What do you think?", "Yes", "What do you think?", "Yes", "Right?", "Ok", "Good!", "Hope you are doing well!", "Good", "Doing good thanks", " How are you?", "Great! How are you?", "What about you? ", "How are you? ", "Sure!", "Happy to help. ", "Yes will do!", "How are you?", "Absolutely", "When are you free?", "Sure", "Okay!", "Absolutely!", "Perfect", "I like it!", "Okay thanks! ", "It’s great!", "Cool", "Haha", "Okay", "Cool", " Haha", " And good luck", "Okay", "Hey how are you? Exactly", "Sure", "Cool thanks", "Thank you", "What do you think? ", "That’s right", "What do you think?", "Okay ", "Okay", "Good question", "Cool", "Ok", "Sounds good", "Okay", "Ok", "Yes", "Okay", "That works", "Ok", "So that’s fine", "How many do you have?", "Ok", "Are you sure?", "Yes", "Right?", "Yes", "Ok", "It’s ok It is", "Cool", "That’s fine ", "Yes", "Thanks", "That’s fine", "Ok", "Yes", "Thanks", "Thanks", "That works?", "Sure", "Cool", "Ok", "What do you think?", "Cool", "Haha", "Haha", "Cool", "Haha", "Thanks", "Talk to you later", "Sure ", "Yes", "Yes", "Ok", "What do you think?", "What do you think?", "Ok", "is that correct?", "Haha", "How many do you have?", "Sure", "What do you think?", "Ok sure", "I’ll do that", "Ok", "Ok", "Yes", "What do you think?", "Sounds good", "Thanks!", "Okay", "Thanks ", "What’s your question?", "Thank you", "Haha", "Cool", "How many do you have?", "What do you think?", "Thanks", "Okay", "How many do you have?", "Cool", "Ok", "Cool", "That should be fine", "Yes", "Yes", "Yes", "How many do you need?", "Ok", "Ok", "Ok", "Ok", "Okay", "Okay", "So it’s good", "Good! How are you?", "Good to hear! ", "Good to hear!", "That’s good!", "Good question", "What about you?", "Good", "Good", "Is that correct?", "It’s your turn", "How many do you have?", "What about you?", "Yes", "Cool", "Right", "Cool", "That works", "Ok", "That would be better", "Thanks", "Then we are good", "Ok", "Ok thanks", "That is fine", "Ok see you then", "Thanks", "Thanks", "Do you agree?", "So that's fine", "Ok", "Do you agree?", "Thank you", "Yes", "Yes", "Thanks", "Thanks", "How many do you have?", "I am lucky", "I am lucky", "Yes", "I am lucky", "Thanks", "Thanks", "Thanks", "Sure", "Cool thanks", "What do you think?", "Ok let me know then", "Ok", "Ok", "Ok", "Do you agree?", "Ok", "Ok", "Ok", "Ok", "Thanks", "Right?", "Thanks", "Is that correct?", "Ok thanks", "Okay", "Thank you", "Ok", "See you then", "Lmao", "Yes", "Yes", "It’s ok", "Hi how are you?", "How are you?", "I’m good thanks, how are you?", "Good to hear!", "I'm good!", "Good!", "What do you think?", "What about you?", "Cool thanks", "How many do you have? ", "Okay", "Sure", "Okay done", "Okay", "Thanks", "Okay", "Okay", "Yes it is", "Happy to help", "What do you think? ", "Cool", "What do you think?", "Okay", "What do you think?", "Sure ", "Thanks", "Yes", "Sure", "I will", "Thanks", "Sure", "Yes", "Ok sure", "What works for you", "What time works for you?", "See you then", "Thanks", "Yes", "Thanks", "What about you?", "Thanks", "Yes", "Sure", "Right?", "Thanks", "Cool.", "Okay thanks!", "Good job!", "Thanks!", "Thanks", "LOL", "Okay thanks", "Thanks! You’re the best!", "Are you there?", "I’m ready", "Cool", "What do ya think?", "Ok let me know!", "Ok", "Ok", "Oh ok", "Oh haha", "Ok that makes sense", "That makes sense", "That makes more sense", "That would make more sense", "Looks great", "Almost there", "Thanks", "Haha", "Yes", "I vote yes", "Yes", "Yes", "Good question", "Okay", "Yeah that makes sense", "What about you?", "I’m ready!", "Sure", "I’m ready", "Okay!", "And what do you think?", "Okay", "Cool", "Okay", "Ok", "So that's good", "What do you think?", "Cool", "Ok", "What do you think?", "I vote yes", "Okay", "Sure", "Ok", "Is that ok for you?", "Is that correct?", "Ok", "Do you agree? ", "Yes that’s fine", "Sure", "Ok", "Yes", "Thanks", "Yes", "Yes", "Ok", "Cool", "Sure", "Sure", "Awesome!", "Ok", "Ok", "What do you think?", "Ok ", "Yes", "I like it", "Cool", "Ok", "Cool?", "Yes", "Is that correct?", "Thank you", "Ok Thank you", "Ok ", "Yes", "That should be ok", "Yes", "Yes", "Yes", "Yes", "I’m doing good J", "Good to hear", "Thanks", "Thanks", "Ok", "What did you think? ", "Ok", "How are you?", "What about you?", "What’s your opinion", "Either works", "Do you agree? ", "Okay", "Ok", "Ok", "Haha", "LOL", "Ok sure", "Okay", "Haha", "Yes agreed", "Haha", "haha", "Ok sure", "Haha", "Ok", "Haha", "Okay", "Ok", "See ya there", "Cool", "Haha", "Cool", "Yes", "Okay", "Okay", "Right?", "OK", "Yes", "Cool", "Ok", "Sure", "*cool*", "That sounds right", "How many do you have?", "Ok", "Ok", "Okay", "Okay", "Ok", "Okay", "Right", "Okay", "Sounds good", "Ok", "Sure", "Ok", "YES", "Ok", "Right", "Right", "Okay", "LOL", "Haha", "Thanks", "I’ll look :/", "okay", "Ok", "Thanks", "Sure", "Ok", "Good question", "Okay", "Ok cool", "Ok", "Haha", "Haha", "Haha Ok then", "Haha", "Good", "Haha", "It’s your turn", "Haha", "I’m fine with either", "yep totally fine", "Thanks though", "Okay", "Thanks so much!", "Cool", "Thanks for your help!", "Haha thx", "I’m good and u?", "Fine", "LOL", "Okay", "What about you?", "Okay", "Cool that works for me", "Okay!", "And good luck", "Yes", "Those are pretty good", "Yes we did", "Cool thanks for letting me know", "Okay", "Sounds good", "Sure will do", "What do you think?", "Thanks", "Thanks for the offer!", "Of course! Happy to help", "Thank you", "Cool", "I vote yes", "Sure", "What do you think?", "I like the first one", "Do you agree?", "K that’s fine", "Okay", "Almost done", "Right?", "Ok", "Yes ", "Sure", "Ok let me know", "Okay!", "Thanks", "Thanks  Done", "Ok", "Absolutely correct", "Ok", "Okay thank you", "Right?", "Okay thanks", "Okay thanks", "Okay", "Okay", "It’s correct", "Ok", "Cool", "Ok", "Ok", "Yes", "Ok", "Okay", "Fine", "Cool", "Good job!", "Is this ok", "Okay", "Ok", "That’s good enough", "Yes", "Sounds good", "Ok", "Right?", "Yes", "Ok", "Thank you", "Sounds good", "Thanks", "Okay", "Right?", "How many do you want?", "Thanks", "Yes", "Ok", "That works", "Okay", "Ok", "Yes", "How many do you need?", "Cool", "Okay", "How many do you have?", "Okay", "Sure", "Ok", "Cool", "How many do you have?", "Right", "Okay", "Yes", "Thanks", "Right?", "Okay", "Sure", "Right?", "That works", "Cool", "Yes", "Ok", "Okay", "Right?", "Ok", "Ok", "It’s your turn", "Cool?", "Okay", "Ok", "Okay done", "Perfect", "Cool ", "Okay", "Sure", "Okay", "Ok", "Cool?", "Great work!", "Sure", "Ok", "Right?", "Okay", "How are you?", "Good and u", "I’m good", "That’s good", "I am ready", "Okay", "Okay", "Ready when you are", "I am ready", "Ok", "Is that correct?", "Ok", "Looks good", "I like it", "Ok", "What do you think?", "Do you agree? ", "I think you’re right", "Haha", "Good question", "Cool", "Yes", "Good", "Thank you", "Thanks", "Ok", "Ok", "Cool?", "Okay", "Thanks", "Hi how are you?", "I agree with that", "Yes", "Okay", "Thanks", "Cool", "What do you think?", "Ok", "Okay", "I’m fine with that", "Thanks", "Good ones", "Good question", "Okay", "What do you think?", "7 is good", "Okay", "Yes", "Cool", "See you then", "What works for you", "Cool", "Okay", "Right?", "Ok", "Ok sure", "Haha", "Nice", "How are you?", "Good", "I’m good", "Good to hear", "How are you?", "I am ready", "Great! How are you?", "I’m ready!", "Sure", "Yes please!", "Thanks!", "What about you?", "Ok sure", "Thanks!", "Thanks!", "Absolutely!", "Is that correct?", "Right?", "LOL", "Yep right", "Okay", "Okay", "Okay", "Right?", "Do you agree?", "Okay", "Ok", "Ok done", "Haha", "Lmao", "Yes", "Ok", "Lmao", "Okay", "Yes please", "Thank you!", "Yes please", "Sure", "Okay!", "Sure", "Right?", "Okay", "Okay", "Okay", "Okay", "Ok let me know", "Okay", "Is that correct?", "Ok", "That works!", "Yes for sure!!", "Yes!", "Ok", "Hi how are you?", "Nm you ", "A good one", "What do you think?", "Right?", "Good", "haha", "Hahaha", "Thanks", "Thanks", "Thanks", "Okay thx Cool", "Yes", "It’s your choice", "Haha", "Sure", "Right?", "Sounds good", "Then we are good", "Ok", "That’s fine", "Thank you", "Ok", "Ok", ":) Okay", "Ok", "It’s your choice", "Ok", "Sure", "Okay", "See you then", "Sounds good", "Ya that’s fine", "See you then", "Is that correct?", "Yes", "Okay", "Cool", "Good job", "Sure", "Cool", "You understand?", "Yes", "Ok", "Okay", "How many do you have?", "Yes we do", "Right?", "Yes", "Sure", "Right?", "Yes", "What do you think?", "Sounds good", "What do you think?", "Right?", "Sounds fair", "Okay", "Ok", "Yes", "Yes", "Good", "That is correct", "You are lucky", "Ok", "Ok", "I am open", "Okay", "We can wait", "I am ready", "I am open", "Sure", "Okay", "Yes", "Thank you", "Thank you", "Thank you", "How are you?!", "Cool", "Right?", "Cool", "Good question", "It seems good", "Otherwise looks good", "Okay", "Haha", "Oh okay", "I vote yes", "yes", "Okay", "Wow", "What about you?", "Ok", "Okay", "Thanks", "Ok", "Wow that’s beautiful", "Thank you", "Ok", "Alright thanks", "Alright thanks", "Okay cool thanks", "Thanks", "Right", "Ok got it", "Oh okay cool", "Lol", "Okay", "That’s what I think", "Ok", "I’m happy with that", "Okay thanks", "Ok I agree", "Right", "What about you?", "Lmao", "Sure", "Okay", "Good luck", "Okay", "Nice", "Ok", "Ok", "Good ones", "Ok", "Thanks anyways", "Okay", "Good", "I’m ok thanks", "That’s good to hear J", "Hi how are you?", "Yes please", "What about you?", "Ok", "What do you think?", "Yes sure J", "Ok great J", "Okay!", "I’m ready", "Ok", "Okay let’s do it ", "LOL", "Ok sure", "Okay let’s do it", "Thanks for the idea", "Okay", "What about you?", "I’m fine with either", "Right?", "Thank you", "I’m fine with hat", "Right?", "For sure", "Okay", "Sounds good", "Yes that sounds like a good idea", "Haha", "Okay", "Ok", "Thanks", "What do you think?", "Otherwise looks good", "Ok", "Okay thx", "What do you think?", "I’ll double check", "It is", "Ok", "Haha", "Okay", "haha", "Thanks", "Thanks", "Haha", "I love them", "Haha", "If not that’s fine", "Ok", "Ok", "What do you think?", "Yes", "Sure", "Thanks!", "Okay", "Cool?", "Ok sounds good", "Thank you", "Ok thx", "Ok", "Thanks!", "Sure", "Sure", "yes", "Thanks", "Yes? Is that correct?", "Okay", "Yes?", "I’m ready", "Ok", "Okay", "Right?", "Yes?", "Okay", "Thanks", "How are you?", "I’m doing good ", "Good!", "Great!", "Yes please", "Thanks!", "Cool thanks", "Thank you!", "haha", "Okay!", "Do you agree?", "You’re the best", "I will", "Okay", "Haha", "Okay", "Yes", "Yes", "Okay", "Okay", "Good question", "You’re right!", "Okay", "You’re right", "Ok", "I’ll keep you posted", "Okay", "Cool?", "Sounds good", "Ok", "Sure", "Sounds good", "What do you think?", "That’s right", "Sure", "Right", "Ok", "Thanks", "Okay", "Ok", "Thanks", "Are you sure?", "I’m fine with it", "How many do you have?", "Sure", "Okay", "Okay", "Sure", "Cool", "Are you sure?", "Sure", "Cool", "Perfect", "Ok", "Thanks", "Okay", "Okay", "Ok", "Ok", "Nice", "Thanks", "Thanks", "Of course! Happy to help", "Hi how are you?", "Ok", "Sure", "Okay", "Ok", "Do you agree?", "Cool", "Okay", "Ok", "What do you think?", "Good question", "Sure", "What about you?", "How many do you have?", "Thank you", "Okay", "Haha", "Ok sure", "Ok", "Ok thanks", "Haha", "Ok", "What do you think?", "Sure", "Ok", "Yes", "Yes", "Cool thanks", "I’ll check it out now", "Okay", "Thanks", "What about you?", "Cool", "Okay", "So that’s good", "Okay", "Ok", "That works", "Sure", "Sure", "Yes", "Sure", "Good question", "Ok", "Fine", "What about you?", "Good job", "Good!", "That’s good!", "Good!", "Good", "Good question", "Good question? ", "Ok", "What do you think?", "Perfect?", "I like it", "Right", "Ok", "Ok sure", "Then we are good", "Thank you", "Okay", "Lol", "Ok thanks", "See you then", "Are you feeling better?", "Right?", "Yes", "Thank you", "Thank you", "Thanks for your help", "I love you", "Ok", "Thank you", "Yes true ", "Lol", "How many do you have?", "Cool", "Okay", "So there we go", "Sure", "Yeah ok", "Is that correct?", "Ok", "Just checking", "Haha", "Cool", "Cool thanks", "What do you think?", "I believe", "Ok", "Yes", "Yes", "Okay", "Haha", "Okay thx", "Yes right?", "Ok thx", "Thanks", "Here we go", "Okay", "Lol", "I’m ready", "Sure", "Yes", "Ok", "Right?", "Good job", "Good question", "Okay!", "Ok", "What about you?", "What do you thnink?", "Thank you", "Sure", "Okay sure", "Yep I agree", "Okay! Okay done", "Better?", "Yes", "Okay", "Yes Yeah", "Good question", "Thanks", "Haha", "Haha", "Sure", "You are right", "Of course! Happy to help", "Yes please", "Sure", "Okay", "Okay", "Okay", "How are you?", "I’m ready", "Cool", "Ok let me know! ", "Right?", "Yep I agree", "Thanks for explaining", "Ahhh ok", "IS that correct?", "Ok", "How are you?", "What’s your question?", "Oh haha", "That makes more sense", "Well done!", "Cool", "Yes", "Yes?", "Sure", "Yes we do", "Ok", "Hi how are you?", "Ok", "How are you?", "How are you?", "Ok", "Free", "Thanks", "Yes", "Good luck", "Right?", "Right?", "Ok", "Ok", "Good question", "Okay", "Yes ", "Ok sure", "Ok sure", "Sure", "Sure", "Ok Sure", "Yes", "Ok", "Ok sure", "Ok sure sure", "Ok", "Sure", "Sure", "That works", "Thanks", "Cool", "yes", "Yes", "Ok let me check", "Okay ", "Thanks", "Ok", "I’ll do that", "What do you think", "Ok done", "yes", "Ok", "Yes", "Hi how are you?", "Ok", "That’s good", "Good question", "Okay", "What are you up to?", "Okay", "Ok", "That makes sense", "What do you think?", "Whatever you want", "Okay", "Okay", "What about you?", "What do you think?", "Yes", "Nice", "Ok cool", "Haha", "Sounds good", "That works", "Ok", "Ok", "Cool", "Ok", "Ok", "Confirmed", "Cool", "Okay", "Okay", "How many do you have?", "Sure", "Cool", "Cool", "Cool?", "Cool?", "Cool", "Right", "Ah okay", "Yes", "For sure", "Sure", "Haha", "Good question", "Yeah sounds good", "Yes for sure", "Haha", "Haha", "How many do you have?", "Thanks", "Thanks", "Haha", "It is", "Hahaha", "Okay", "haha", "Ok sure", "Good luck!", "Welcome back!!", "Thanks for your help", "Sounds good", "I will", "How are you?", "Am fine thank you", "Good and u", "Fine", "Ok sure", "Haha", "I will look it up", "Oh ok", "What do you…", "Yes sounds good", "What about you?", "Ok thx", "Sure that’s a good id…", "So that’s good", "Ok sure", "I’ll look", "That sounds right", "Sounds good", "Sounds good", "Cool", "We’re ready", "I’m fine with that", "Do you agree?", "Yes", "I am fine with either", "Okay", "Ok", "What do you think?", "Ok", "Ok!", "Okay", "Okay", "Right", "LOL", "Okay", "I’m ready", "OK", "Ok thanks", "It’s your turn", "Yes", "Ok", "Okay good", "Cool", "ok", "Okay done", "Right", "Okay done", "Okay", "Thanks", "It’s your turn", "Great", "Ok", "Wait", "Thank you", "It’s your turn", "Perfect", "Okay", "Right", "Sure", "Okay", "Right", "Cool", "Ok", "Yes pls", "How many do you have?", "Cool", "Right", "Okay", "How many do you have?", "Works for me", "Ok", "Ok", "Okay", "Thanks", "Fine", "Yes right?", "How many do you want?", "Ok", "Okay", "How many do you have?", "Cool?", "Okay", "I’m ready", "Okay", "How many do you have?", "Perfect", "Perfect", "See you then", "Ok", "That works", "Ok", "Yes", "Ok", "How many do you have?", "Sure", "Thanks", "Haha", "Okay", "We can wait", "Almost there", "Are you there?", "What are you up to?", "Any feedback?", "Are you free now?", "Cool", "I’m ready", "Cool", "Wow", "I am ready", "What do you think?", "Haha", "Okay", "Yep I agree", "What did you think?", "That’s what I was thinking", "Haha", "Wow", "And how much", "Ok", "It’s your turn", "Okay", "Okay", "Haha", "I believe", "Okay", "Wow", "Do you agree?", "That is fine", "Thanks", "Okay", "Okay good", "Thanks", "Thanks for your help", "Thanks", "Okay", "Is that correct?", "That’s a good idea", "What do you think?", "Good", "Sure", "That is good", "And what do you think?", "Sounds good", "I’m fine with either", "Sounds good", "Sounds good", "Ok sure", "Okay thx", "That’s perfect", "Cool", "Ok", "Ok", "Haha", "Fine", "Hi how are you?", "Good", "What about you??", "I’m ready", "I’m ready", "Cool", "Then we are good", "I am ready!", "Yes!", "Cool", "Wow", "Cool", "I like that", "Ok", "Okay thanks", "Sure", "Haha", "Is that correct?", "Sure", "Yes", "Ok let me know then", "I believe", "haha", "Lmao", "Looks good", "Would that be better?", "Ok done", "Yes it would", "Ok", "Yes", "Ok got it", "Okay thx", "What do you think?", "Okay", "yes", "Cool", "Yes", "Ya that’s fine", "Ok sure", "K that’s fine", "That’s fine", "Yes!", "Thanks!", "Haha", "Okay", "Here we go", "How are you?", "I’m ready", "What do you think?", "What do you think?", "Absolutely", "I believe", "That’s good", "No thank you", "Haha", "Okay", "See you", "Yup right", "That’s fine", "Whenever you want", "Right?", "Ok sure", "It makes more sense", "Sure", "Ok", "How many do you want?", "Good question", "Yes", "Okay", "That’s good", "Cool", "Sure", "Ok sure", "It’s your turn", "Ok sure", "Okay", "Thank you", "Sure", "Sure", "Yes", "Cool", "Yes", "Right?", "Right?", "Cool", "Oh okay", "Ok", "How many do you have?", "Ok", "Right", "All great", "Right", "Right", "Wow", "Yes", "Yes?", "Okay", "Just checking", "Yes", "How many do you want?", "Okay", "Sure", "I’m ready", "Ok", "Thanks", "You can go", "sure", "Ok", "See you shortly", "Thanks!", "How are you?", "Cool", "Yeah for you", "Ok", "Okay", "What did you think?", "So there w…", "Okay", "Yes", "Okay", "Ok I agree", "I vote yes", "Ok", "Good question?", "Cool", "What do you think?", "Cool", "Haha", "Sure", "I like the first one", "Oh okay cool", "I’ll try again", "Yeah that’s fine", "Yeah that makes sense", "Okay", "Yeah sure!", "Is that correct?", "Ok done", "So that’s good", "Haha", "Oh ok", "Sure", "Ok sure", "Yes", "Okay okay!", "Okay", "Right", "Ok", "I love you", "Sounds good", "Haha", "Yes", "Cool", "Thanks", "That’s good", "Sure", "That would be better", "Right?", "Okay", "Okay", "I’m good thanks, how are you?", "How are you?", "How are you?", "I’m ready", "Thanks", "Yeah sure!", "Thanks so much J", "Please thanks", "What do you think?", "I’m okay with either", "What about you?", "Ok", "What do you think?", "Okay", "I like that", "What do you think?", "Cool", "Yes right?", "Yes", "I’m fine with that", "Yes", "Cool", "Thank you", "Sure", "Ok", "Do you agree?", "Ok", "Ok sure", "Is that correct?", "Welcome back", "Thanks for the feedback", "Yes", "Makes sens…", "Okay", "Ok", "Thanks though", "haha", "Ok", "Sounds good", "Cool", "Perfect", "It’s good", "Welcome", "Haha", "haha", "Ok see u there", "Cool", "Thanks!", "Ok", "What do you think?", "Ok", "Ok will do", "Okay!", "Sounds good?", "Okay!", "Do you agree?", "Haha", "Wow thanks", "Do you agree?", "Ok", "Do you agree?", "Yes", "Yes?", "It’s your turn", "Ok", "Ok", "Yes?", "It’s your turn", "Good", "Cool!", "Okay I will", "Haha", "Yes", "Okay", "Yep I agree", "Do you agree?", "Yes", "Yep totally fine", "Ok sure", "Sounds good", "What are you up to?", "That is correct", "Okay!", "Okay", "I’m ready", "Okay", "I’m ready", "You’re right", "Right?", "Ok", "Sure", "Cool", "Cool", "It’s ok", "That’s ok", "Okay", "Thanks though", "Is that correct?", "How many do you have?", "Right", "Ok", "Cool", "Yes", "Right?", "nice", "Haha", "Oh good", "Ok", "Ok sure", "Sure", "I’m ready", "Sure", "Thanks", "I’m fine with that", "What do you think?", "That makes sense now", "Yes", "Okay", "Oh okay", "Sure", "Ya that’s fine", "Ok", "Haha", "Ok", "haha", "Cool", "Good ", "Hey how are you?", "Ok thanks!", "Ok thanks", "Thanks", "Okay", "Ok", "It’s your turn", "What about you?", "Thanks", "Right?", "Sure", "Sure", "Okay", "Okay", "Free", "I will", "That’s good!", "Good!", "Good!", "I’m sure!", "Haha", "Haha", "Yes", "That’s good", "Good question", "Yes right", "What about you?", "Right?", "Sure sounds fine", "Ok", "How many do y…", "It is better", "Cool", "Yes it is", "Okay thx", "Ok", "Cool", "Where do you live?", "Wow", "Good", "what is your number?", "Thanks", "Sure", "Thanks", "Ok", "Ok", "Sounds good", "Thank you", "I believe", "Sure", "Have fun", "Ok see you later", "Right?", "Right?", "Thanks", "Good question", "I believe", "Thanks", "Okay", "Welcome", "You are welcome…", "Good", "Okay", "Ok", "Right", "Cool", "Right?", "Cool", "Ok", "I’ll do that", "Just checking", "Ok I’m done", "Ok", "Good! how are you?", "What do you think?", "I’ll check", "Okay", "That’s pretty good", "haha", "Ok", "Cool that works for me", "I’ll do that", "Otherwise looks good", "It’s your turn", "Is this okay?", "Okay done", "And what do you think?", "Ok", "haha", "Yes", "Yes", "Sure", "Thank you", "Yes", "Yerah sure np", "Ok", "Ok", "Nm you?", "I am ready!", "Better?", "Are u sure?", "Hmm ok", "Hmm ok", "Yep I agree", "Yes", "Hey how are you?", "What about you?", "Oh okay", "Ah okay", "Cool", "That makes more sense", "Beautiful", "Ok let me know then", "Sure", "It’s your turn", "Okay", "Thanks!", "Okay thx", "For sure?", "Cool", "Ok thanks", "Ok sure", "Sure", "Ok", "Ok I agree", "Sure", "haha sure", "That’s fine", "I’m fine with that", "Cool", "Okay", "I like it", "Cool", "Sure", "Ok", "Good", "Thank you", "Ok", "Thanks", "Ok sure", "That would be better", "Ok", "Thanks!", "Happy to help", "I’ll keep you posted", "Yes", "Cool", "I am ready", "Sure I’ll do that", "I’ll do that!", "Yeah for sure!", "I’m excited", "Right?", "Haha", "What’s your opinion?", "Sure", "See you then", "I’m ready", "Yes that’s fine", "Haha", "Yes", "It’s your turn", "Thank you", "I’m ready", "I’m good thanks", "Okay", "Cool", "Yes", "Ok", "Doing good thanks", "Sure", "Ok", "I'm doing good", "Doing good thanks", "Yes", "Okay thx", "What do you think?", "And what do you think?", "Sounds good", "J  So that’s good", "Yes", "L  How many do you have?", "J  Well done", "Okay", "Hi how are you?", "Hope you are doing well", "I’m doing good J", "Cool thanks", "Haha", "Yes", "Good to hear", "Yes sure!", "Great!", "Cool", "Ok", "Okay ", "That’s fine", "Thanks", "Yes", "Sure", "Ok", "It’s your turn", " Right?", "Right?");
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
