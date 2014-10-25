/*
 * Copyright (C) 2013 Chris Lacy Licensed under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law
 * or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */

package com.ring.ytjojo.UI;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;

import com.ring.ytjojo.app.AppSettings;

public class SettingsActivity extends PreferenceActivity implements
        OnSharedPreferenceChangeListener {

    public static final String KEY_THEME_PREFERENCE = "theme_preference";
    public static final String KEY_CUSTOMIZE_LANES_PREFERENCE = "customizelanes_preference";
    public static final String KEY_REMOVE_ACCOUNT_PREFERENCE = "removeaccount_preference";
    public static final String KEY_SHOW_TABLET_MARGIN_PREFERENCE = "showtabletmargin_preference";
    public static final String KEY_DISPLAY_TIME_PREFERENCE = "displaytime_preference";
    public static final String KEY_DISPLAY_NAME_PREFERENCE = "displayname_preference";
    public static final String KEY_STATUS_SIZE_PREFERENCE = "statussize_preference";
    public static final String KEY_PROFILE_IMAGE_SIZE_PREFERENCE = "profileimagesize_preference";
    public static final String KEY_MEDIA_IMAGE_SIZE_PREFERENCE = "mediaimagesize_preference";
    public static final String KEY_VOLSCROLL_PREFERENCE = "volscroll_preference";
    public static final String KEY_DOWNLOADIMAGES_PREFERENCE = "downloadimages_preference";
    public static final String KEY_SHOW_TWEET_SOURCE_PREFERENCE = "showtweetsource_preference";
    public static final String KEY_CACHE_SIZE_PREFERENCE = "cachesize_preference";
    public static final String KEY_QUOTE_TYPE_PREFERENCE = "quotetype_preference";
    private static final String KEY_CREDITS_PREFERENCE = "preference_credits";
    private static final String KEY_SOURCE_CODE_PREFERENCE = "preference_source";
    private static final String KEY_DONATE_PREFERENCE = "preference_donate";
    private static final String KEY_VERSION_PREFERENCE = "version_preference";
    public static final String KEY_RINGTONE_PREFERENCE = "ringtone_preference";
    public static final String KEY_NOTIFICATION_TIME_PREFERENCE = "notificationtime_preference";
    public static final String KEY_NOTIFICATION_TYPE_PREFERENCE = "notificationtype_preference";
    public static final String KEY_NOTIFICATION_VIBRATION = "notificationvibration_preference";
    public static final String KEY_AUTO_REFRESH_PREFERENCE = "autorefresh_preference";
    public static final String KEY_DISPLAY_URL_PREFERENCE = "displayurl_preference";

    private ListPreference mThemePreference;
    private CheckBoxPreference mShowTabletMarginPreference;
    private ListPreference mStatusSizePreference;
    private ListPreference mDisplayTimePreference;
    private ListPreference mDisplayNamePreference;
    private ListPreference mProfileImageSizePreference;
    private ListPreference mMediaImageSizePreference;
    private ListPreference mCacheSizePreference;
    private CheckBoxPreference mDownloadImagesPreference;
    private CheckBoxPreference mShowTweetSourcePreference;
    private ListPreference mQuoteTypePreference;
    private CheckBoxPreference mVolScrollPreference;
    private CheckBoxPreference mAutoRefreshPreference;
    private CheckBoxPreference mDisplayUrlPreference;
    private Preference mCreditsPreference;
    private Preference mSourceCodePreference;
    private Preference mDonatePreference;
    private Preference mVersionPreference;
    private ListPreference mNotificationTimePreference;
    private ListPreference mNotificationTypePreference;
    private AlertDialog mRemoveAccountDialog;

 
    /*
     * (non-Javadoc)
     *
     * @see android.preference.PreferenceActivity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

    }


	@Override
	public void onSharedPreferenceChanged(SharedPreferences arg0, String arg1) {
		// TODO Auto-generated method stub
		
	}

    
}
