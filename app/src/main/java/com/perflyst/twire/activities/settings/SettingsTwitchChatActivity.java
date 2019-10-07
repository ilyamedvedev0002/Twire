package com.perflyst.twire.activities.settings;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.perflyst.twire.R;
import com.perflyst.twire.activities.ThemeActivity;
import com.perflyst.twire.service.DialogService;
import com.perflyst.twire.service.Settings;
import com.rey.material.widget.Slider;

public class SettingsTwitchChatActivity extends ThemeActivity {
    private String LOG_TAG = getClass().getSimpleName();
    private Settings settings;
    private TextView emoteSizeSummary, messageSizeSummary, chatLandscapeWidthSummary, chatLandscapeToggleSummary, chatLandscapeSwipeToShowSummary;
    private CheckedTextView chatLandscapeToggle, chatSwipeToShowToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_twitch_chat);
        settings = new Settings(getBaseContext());

        final Toolbar toolbar = (Toolbar) findViewById(R.id.settings_player_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        emoteSizeSummary = (TextView) findViewById(R.id.chat_emote_size_summary);
        messageSizeSummary = (TextView) findViewById(R.id.message_size_summary);
        chatLandscapeWidthSummary = (TextView) findViewById(R.id.chat_landscape_summary);
        chatLandscapeToggleSummary = (TextView) findViewById(R.id.chat_landscape_enable_summary);
        chatLandscapeSwipeToShowSummary = (TextView) findViewById(R.id.chat_landscape_swipe_summary);

        chatLandscapeToggle = (CheckedTextView) findViewById(R.id.chat_landscape_enable_title);
        chatSwipeToShowToggle = (CheckedTextView) findViewById(R.id.chat_landscape_swipe_title);
        updateSummaries();
    }

    private void updateSummaries() {
        String[] sizes = getResources().getStringArray(R.array.ChatSize);
        emoteSizeSummary.setText(sizes[settings.getEmoteSize() - 1]);
        messageSizeSummary.setText(sizes[settings.getMessageSize() - 1]);
        chatLandscapeWidthSummary.setText(String.format(getString(R.string.percent), settings.getChatLandscapeWidth()));

        // Chat enabled in landscape
        chatLandscapeToggle.setChecked(settings.isChatInLandscapeEnabled());
        if (settings.isChatInLandscapeEnabled()) {
            chatLandscapeToggleSummary.setText(getString(R.string.enabled));
        } else {
            chatLandscapeToggleSummary.setText(getString(R.string.disabled));
        }

        // Chat showable by swiping
        chatSwipeToShowToggle.setChecked(settings.isChatLandscapeSwipable());
        if (settings.isChatLandscapeSwipable()) {
            chatLandscapeSwipeToShowSummary.setText(getString(R.string.enabled));
        } else {
            chatLandscapeSwipeToShowSummary.setText(getString(R.string.disabled));
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.overridePendingTransition(R.anim.fade_in_semi_anim, R.anim.slide_out_right_anim);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    public void onClickEmoteSize(View v) {
        MaterialDialog dialog = DialogService.getChooseChatSizeDialog(this, R.string.chat_emote_size, R.array.ChatSize, settings.getEmoteSize(), new MaterialDialog.ListCallbackSingleChoice() {
            @Override
            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                settings.setEmoteSize(which + 1);
                updateSummaries();
                return true;
            }
        });
        dialog.show();
    }

    public void onClickMessageSize(View v) {
        MaterialDialog dialog = DialogService.getChooseChatSizeDialog(this, R.string.chat_message_size, R.array.ChatSize, settings.getMessageSize(), new MaterialDialog.ListCallbackSingleChoice() {
            @Override
            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                settings.setMessageSize(which + 1);
                updateSummaries();
                return true;
            }
        });
        dialog.show();
    }

    public void onClickChatLandscapeEnable(View v) {
        settings.setShowChatInLandscape(!settings.isChatInLandscapeEnabled());
        updateSummaries();
    }

    public void onClickChatLandscapeSwipeable(View v) {
        settings.setChatLandscapeSwipable(!settings.isChatLandscapeSwipable());
        updateSummaries();
    }

    public void onClickChatLandScapeWidth(View v) {
        final int landscapeWidth = settings.getChatLandscapeWidth();

        DialogService.getSliderDialog(
                this,
                new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        settings.setChatLandscapeWidth(landscapeWidth);
                        updateSummaries();
                    }
                },
                new Slider.OnPositionChangeListener() {
                    @Override
                    public void onPositionChanged(Slider view, boolean fromUser, float oldPos, float newPos, int oldValue, int newValue) {
                        settings.setChatLandscapeWidth(newValue);
                        updateSummaries();
                    }
                },
                landscapeWidth,
                25,
                60,
                getString(R.string.chat_landscape_width_dialog)
        ).show();
    }
}
