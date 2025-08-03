package com.example.sunmail.util;

import android.content.Context;
import androidx.core.content.ContextCompat;
import com.example.sunmail.R;

public class AvatarColorHelper {
    
    private static final int[] AVATAR_COLOR_RES_IDS = {
            R.color.avatar_color_1,
            R.color.avatar_color_2, 
            R.color.avatar_color_3,
            R.color.avatar_color_4,
            R.color.avatar_color_5,
            R.color.avatar_color_6,
            R.color.avatar_color_7,
            R.color.avatar_color_8,
            R.color.avatar_color_9,
            R.color.avatar_color_10
    };
    
    /**
     * Get a consistent color for a user based on their username
     * @param context Android context
     * @param userName Username to generate color for
     * @return Color resource ID
     */
    public static int getColorForUser(Context context, String userName) {
        int hash = userName != null ? Math.abs(userName.hashCode()) : 0;
        int colorResId = AVATAR_COLOR_RES_IDS[hash % AVATAR_COLOR_RES_IDS.length];
        return ContextCompat.getColor(context, colorResId);
    }
}
