package com.mozhimen.imagek.ucrop;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;

import androidx.annotation.AnimRes;
import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.FloatRange;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.mozhimen.imagek.ucrop.annors.GestureTypes;
import com.mozhimen.imagek.ucrop.mos.AspectRatio;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

/**
 * Created by Oleksii Shliama (https://github.com/shliama).
 * <p/>
 * Builder class to ease Intent setup.
 */
public class UCrop {

    private static final String EXTRA_PREFIX = "com.mozhimen.imagek.ucrop";

    //////////////////////////////////////////////////////////////////////////////////////

    public static final int REQUEST_CROP = 69;
    public static final int RESULT_ERROR = 96;

    public static final String EXTRA_INPUT_URI = EXTRA_PREFIX + ".InputUri";
    public static final String EXTRA_OUTPUT_URI = EXTRA_PREFIX + ".OutputUri";
    public static final String EXTRA_OUTPUT_CROP_ASPECT_RATIO = EXTRA_PREFIX + ".CropAspectRatio";
    public static final String EXTRA_OUTPUT_IMAGE_WIDTH = EXTRA_PREFIX + ".ImageWidth";
    public static final String EXTRA_OUTPUT_IMAGE_HEIGHT = EXTRA_PREFIX + ".ImageHeight";
    public static final String EXTRA_OUTPUT_OFFSET_X = EXTRA_PREFIX + ".OffsetX";
    public static final String EXTRA_OUTPUT_OFFSET_Y = EXTRA_PREFIX + ".OffsetY";
    public static final String EXTRA_ERROR = EXTRA_PREFIX + ".Error";
    public static final String EXTRA_ASPECT_RATIO_X = EXTRA_PREFIX + ".AspectRatioX";
    public static final String EXTRA_ASPECT_RATIO_Y = EXTRA_PREFIX + ".AspectRatioY";
    public static final String EXTRA_MAX_SIZE_X = EXTRA_PREFIX + ".MaxSizeX";
    public static final String EXTRA_MAX_SIZE_Y = EXTRA_PREFIX + ".MaxSizeY";
    public static final String EXTRA_WINDOW_EXIT_ANIMATION = EXTRA_PREFIX + ".WindowAnimation";
    public static final String EXTRA_NAV_BAR_COLOR = EXTRA_PREFIX + ".navBarColor";

    //////////////////////////////////////////////////////////////////////////////////////

    private Intent mCropIntent;
    private Bundle mCropOptionsBundle;

    private UCrop(@NonNull Uri source, @NonNull Uri destination) {
        mCropIntent = new Intent();
        mCropOptionsBundle = new Bundle();
        mCropOptionsBundle.putParcelable(EXTRA_INPUT_URI, source);
        mCropOptionsBundle.putParcelable(EXTRA_OUTPUT_URI, destination);
    }

    /**
     * This method creates new Intent builder and sets both source and destination image URIs.
     *
     * @param source      Uri for image to crop
     * @param destination Uri for saving the cropped image
     */
    public static UCrop of(@NonNull Uri source, @NonNull Uri destination) {
        return new UCrop(source, destination);
    }

    /**
     * Retrieve cropped image Uri from the result Intent
     *
     * @param intent crop result intent
     */
    @Nullable
    public static Uri getOutput(@NonNull Intent intent) {
        return intent.getParcelableExtra(EXTRA_OUTPUT_URI);
    }

    /**
     * Retrieve the width of the cropped image
     *
     * @param intent crop result intent
     */
    public static int getOutputImageWidth(@NonNull Intent intent) {
        return intent.getIntExtra(EXTRA_OUTPUT_IMAGE_WIDTH, -1);
    }

    /**
     * Retrieve the height of the cropped image
     *
     * @param intent crop result intent
     */
    public static int getOutputImageHeight(@NonNull Intent intent) {
        return intent.getIntExtra(EXTRA_OUTPUT_IMAGE_HEIGHT, -1);
    }

    /**
     * Retrieve cropped image aspect ratio from the result Intent
     *
     * @param intent crop result intent
     * @return aspect ratio as a floating point value (x:y) - so it will be 1 for 1:1 or 4/3 for 4:3
     */
    public static float getOutputCropAspectRatio(@NonNull Intent intent) {
        return intent.getFloatExtra(EXTRA_OUTPUT_CROP_ASPECT_RATIO, 1);
    }

    /**
     * Method retrieves error from the result intent.
     *
     * @param result crop result Intent
     * @return Throwable that could happen while image processing
     */
    @Nullable
    public static Throwable getError(@NonNull Intent result) {
        return (Throwable) result.getSerializableExtra(EXTRA_ERROR);
    }

    /**
     * Set an aspect ratio for crop bounds.
     * User won't see the menu with other ratios options.
     *
     * @param x aspect ratio X
     * @param y aspect ratio Y
     */
    public UCrop withAspectRatio(float x, float y) {
        mCropOptionsBundle.putFloat(EXTRA_ASPECT_RATIO_X, x);
        mCropOptionsBundle.putFloat(EXTRA_ASPECT_RATIO_Y, y);
        return this;
    }

    /**
     * Set an aspect ratio for crop bounds that is evaluated from source image width and height.
     * User won't see the menu with other ratios options.
     */
    public UCrop useSourceImageAspectRatio() {
        mCropOptionsBundle.putFloat(EXTRA_ASPECT_RATIO_X, 0);
        mCropOptionsBundle.putFloat(EXTRA_ASPECT_RATIO_Y, 0);
        return this;
    }

    /**
     * Set maximum size for result cropped image.
     *
     * @param width  max cropped image width
     * @param height max cropped image height
     */
    public UCrop withMaxResultSize(@IntRange(from = 100) int width, @IntRange(from = 100) int height) {
        mCropOptionsBundle.putInt(EXTRA_MAX_SIZE_X, width);
        mCropOptionsBundle.putInt(EXTRA_MAX_SIZE_Y, height);
        return this;
    }

    public UCrop withOptions(@NonNull Options options) {
        mCropOptionsBundle.putAll(options.getOptionBundle());
        return this;
    }

    /**
     * Send the crop Intent from animation an Activity
     *
     * @param activity Activity to receive result
     */
    public void startAnimation(@NonNull Activity activity, @NonNull Class<?> clazz, @AnimRes int activityCropEnterAnimation) {
        if (activityCropEnterAnimation != 0) {
            start(activity, clazz, REQUEST_CROP, activityCropEnterAnimation);
        } else {
            start(activity, clazz, REQUEST_CROP);
        }
    }

    /**
     * Send the crop Intent from an Activity with a custom request code or animation
     *
     * @param activity    Activity to receive result
     * @param requestCode requestCode for result
     */
    public void start(@NonNull Activity activity, Class<?> clazz, int requestCode, @AnimRes int activityCropEnterAnimation) {
        activity.startActivityForResult(getIntent(activity, clazz), requestCode);
        activity.overridePendingTransition(activityCropEnterAnimation, R.anim.ucrop_anim_fade_in);
    }

    /**
     * Send the crop Intent from an Activity
     *
     * @param activity Activity to receive result
     */
    public void start(@NonNull Activity activity, @NonNull Class<?> clazz) {
        start(activity, clazz, REQUEST_CROP);
    }

    /**
     * Send the crop Intent from an Activity with a custom request code
     *
     * @param activity    Activity to receive result
     * @param requestCode requestCode for result
     */
    public void start(@NonNull Activity activity, @NonNull Class<?> clazz, int requestCode) {
        activity.startActivityForResult(getIntent(activity, clazz), requestCode);
    }

    /**
     * Send the crop Intent from a Fragment
     *
     * @param fragment Fragment to receive result
     */
    public void start(@NonNull Context context, @NonNull Fragment fragment, @NonNull Class<?> clazz) {
        start(context, clazz, fragment, REQUEST_CROP);
    }

    /**
     * Send the crop Intent with a custom request code
     *
     * @param fragment    Fragment to receive result
     * @param requestCode requestCode for result
     */
    public void start(@NonNull Context context, @NonNull Class<?> clazz, @NonNull Fragment fragment, int requestCode) {
        fragment.startActivityForResult(getIntent(context, clazz), requestCode);
    }

    /**
     * Get Intent to start {UCropActivity}
     *
     * @return Intent for {UCropActivity}
     */
    public Intent getIntent(@NonNull Context context, Class<?> clazz) {
        mCropIntent.setClass(context, clazz);
        mCropIntent.putExtras(mCropOptionsBundle);
        return mCropIntent;
    }

    //////////////////////////////////////////////////////////////////////////////////////

    /**
     * Class that helps to setup advanced configs that are not commonly used.
     * Use it with method {@link #withOptions(Options)}
     */
    public static class Options {

        public static final String EXTRA_COMPRESSION_FORMAT_NAME = EXTRA_PREFIX + ".CompressionFormatName";
        public static final String EXTRA_COMPRESSION_QUALITY = EXTRA_PREFIX + ".CompressionQuality";

        public static final String EXTRA_ALLOWED_GESTURES = EXTRA_PREFIX + ".AllowedGestures";

        public static final String EXTRA_MAX_BITMAP_SIZE = EXTRA_PREFIX + ".MaxBitmapSize";
        public static final String EXTRA_MAX_SCALE_MULTIPLIER = EXTRA_PREFIX + ".MaxScaleMultiplier";
        public static final String EXTRA_IMAGE_TO_CROP_BOUNDS_ANIM_DURATION = EXTRA_PREFIX + ".ImageToCropBoundsAnimDuration";

        public static final String EXTRA_DIMMED_LAYER_COLOR = EXTRA_PREFIX + ".DimmedLayerColor";
        public static final String EXTRA_CIRCLE_DIMMED_LAYER = EXTRA_PREFIX + ".CircleDimmedLayer";

        public static final String EXTRA_SHOW_CROP_FRAME = EXTRA_PREFIX + ".ShowCropFrame";
        public static final String EXTRA_CROP_FRAME_COLOR = EXTRA_PREFIX + ".CropFrameColor";
        public static final String EXTRA_CROP_FRAME_STROKE_WIDTH = EXTRA_PREFIX + ".CropFrameStrokeWidth";

        public static final String EXTRA_SHOW_CROP_GRID = EXTRA_PREFIX + ".ShowCropGrid";
        public static final String EXTRA_CROP_GRID_ROW_COUNT = EXTRA_PREFIX + ".CropGridRowCount";
        public static final String EXTRA_CROP_GRID_COLUMN_COUNT = EXTRA_PREFIX + ".CropGridColumnCount";
        public static final String EXTRA_CROP_GRID_COLOR = EXTRA_PREFIX + ".CropGridColor";
        public static final String EXTRA_CROP_GRID_STROKE_WIDTH = EXTRA_PREFIX + ".CropGridStrokeWidth";

        public static final String EXTRA_TOOL_BAR_COLOR = EXTRA_PREFIX + ".ToolbarColor";
        public static final String EXTRA_STATUS_BAR_COLOR = EXTRA_PREFIX + ".StatusBarColor";
        public static final String EXTRA_UCROP_COLOR_WIDGET_ACTIVE = EXTRA_PREFIX + ".UcropColorWidgetActive";

        public static final String EXTRA_UCROP_WIDGET_COLOR_TOOLBAR = EXTRA_PREFIX + ".UcropToolbarWidgetColor";
        public static final String EXTRA_UCROP_TITLE_TEXT_TOOLBAR = EXTRA_PREFIX + ".UcropToolbarTitleText";
        public static final String EXTRA_UCROP_WIDGET_CANCEL_DRAWABLE = EXTRA_PREFIX + ".UcropToolbarCancelDrawable";
        public static final String EXTRA_UCROP_WIDGET_CROP_DRAWABLE = EXTRA_PREFIX + ".UcropToolbarCropDrawable";

        public static final String EXTRA_UCROP_WIDGET_CROP_OPEN_WHITE_STATUSBAR = EXTRA_PREFIX + ".openWhiteStatusBar";

        public static final String EXTRA_UCROP_LOGO_COLOR = EXTRA_PREFIX + ".UcropLogoColor";

        public static final String EXTRA_FREE_STYLE_CROP = EXTRA_PREFIX + ".FreeStyleCrop";

        public static final String EXTRA_CUT_CROP = EXTRA_PREFIX + ".cuts";

        public static final String EXTRA_FREE_STATUS_FONT = EXTRA_PREFIX + ".StatusFont";

        public static final String EXTRA_ASPECT_RATIO_SELECTED_BY_DEFAULT = EXTRA_PREFIX + ".AspectRatioSelectedByDefault";
        public static final String EXTRA_ASPECT_RATIO_OPTIONS = EXTRA_PREFIX + ".AspectRatioOptions";

        public static final String EXTRA_UCROP_ROOT_VIEW_BACKGROUND_COLOR = EXTRA_PREFIX + ".UcropRootViewBackgroundColor";

        public static final String EXTRA_DRAG_CROP_FRAME = EXTRA_PREFIX + ".DragCropFrame";

        //////////////////////////////////////////////////////////////////////////////////////

        private final Bundle mOptionBundle;

        //////////////////////////////////////////////////////////////////////////////////////

        public Options() {
            mOptionBundle = new Bundle();
        }

        @NonNull
        public Bundle getOptionBundle() {
            return mOptionBundle;
        }

        /**
         * Set one of {@link Bitmap.CompressFormat} that will be used to save resulting Bitmap.
         * 设置{@link Bitmap.CompressFormat}中的一个，用于保存生成的位图。
         */
        public Options setCompressionFormat(@NonNull Bitmap.CompressFormat format) {
            mOptionBundle.putString(EXTRA_COMPRESSION_FORMAT_NAME, format.name());
            return this;
        }

        /**
         * Set compression quality [0-100] that will be used to save resulting Bitmap.
         * 设置压缩质量[0-100]，用于保存生成的位图。
         */
        public Options setCompressionQuality(@IntRange(from = 0) int compressQuality) {
            mOptionBundle.putInt(EXTRA_COMPRESSION_QUALITY, compressQuality);
            return this;
        }

        /**
         * Choose what set of gestures will be enabled on each tab - if any.
         * 选择要在每个选项卡上启用的手势集-如果有的话。
         */
        public Options setAllowedGestures(@GestureTypes int tabScale,
                                          @GestureTypes int tabRotate,
                                          @GestureTypes int tabAspectRatio) {
            mOptionBundle.putIntArray(EXTRA_ALLOWED_GESTURES, new int[]{tabScale, tabRotate, tabAspectRatio});
            return this;
        }

        /**
         * This method sets multiplier that is used to calculate max image scale from min image scale.
         * 该方法设置用于从最小图像尺度计算最大图像尺度的乘数。
         *
         * @param maxScaleMultiplier - (minScale * maxScaleMultiplier) = maxScale
         */
        public Options setMaxScaleMultiplier(@FloatRange(from = 1.0, fromInclusive = false) float maxScaleMultiplier) {
            mOptionBundle.putFloat(EXTRA_MAX_SCALE_MULTIPLIER, maxScaleMultiplier);
            return this;
        }

        /**
         * This method sets animation duration for image to wrap the crop bounds
         * 此方法设置图像环绕裁剪边界的动画持续时间
         *
         * @param durationMillis - duration in milliseconds
         */
        public Options setImageToCropBoundsAnimDuration(@IntRange(from = 100) int durationMillis) {
            mOptionBundle.putInt(EXTRA_IMAGE_TO_CROP_BOUNDS_ANIM_DURATION, durationMillis);
            return this;
        }

        /**
         * Setter for max size for both width and height of bitmap that will be decoded from an input Uri and used in the view.
         * 设置位图的宽度和高度的最大大小，将从输入Uri解码并在视图中使用
         *
         * @param maxBitmapSize - size in pixels
         */
        public Options setMaxBitmapSize(@IntRange(from = 100) int maxBitmapSize) {
            mOptionBundle.putInt(EXTRA_MAX_BITMAP_SIZE, maxBitmapSize);
            return this;
        }

        /**
         * @param color - desired color of dimmed area around the crop bounds
         * 作物边界周围暗淡区域的所需颜色
         */
        public Options setDimmedLayerColor(@ColorInt int color) {
            mOptionBundle.putInt(EXTRA_DIMMED_LAYER_COLOR, color);
            return this;
        }

        /**
         * @param isCircle - set it to true if you want dimmed layer to have an circle inside 设置为true，
         * 如果你想暗层有一个圆圈在里面
         */
        public Options setCircleDimmedLayer(boolean isCircle) {
            mOptionBundle.putBoolean(EXTRA_CIRCLE_DIMMED_LAYER, isCircle);
            return this;
        }

        /**
         * @param show - set to true if you want to see a crop frame rectangle on top of an image
         * 如果您想在图像顶部看到裁剪框矩形，请设置为true
         */
        public Options setShowCropFrame(boolean show) {
            mOptionBundle.putBoolean(EXTRA_SHOW_CROP_FRAME, show);
            return this;
        }

        /**
         * @param color - desired color of crop frame
         * 裁切框所需颜色
         */
        public Options setCropFrameColor(@ColorInt int color) {
            mOptionBundle.putInt(EXTRA_CROP_FRAME_COLOR, color);
            return this;
        }

        /**
         * @param width - desired width of crop frame line in pixels
         *              裁切帧线的所需宽度(以像素为单位)
         */
        public Options setCropFrameStrokeWidth(@IntRange(from = 0) int width) {
            mOptionBundle.putInt(EXTRA_CROP_FRAME_STROKE_WIDTH, width);
            return this;
        }

        /**
         * @param show - set to true if you want to see a crop grid/guidelines on top of an image
         *             如果你想在图像的顶部看到裁剪网格/指导线，设置为true
         */
        public Options setShowCropGrid(boolean show) {
            mOptionBundle.putBoolean(EXTRA_SHOW_CROP_GRID, show);
            return this;
        }

        /**
         * @param isDragFrame
         * 是否可拖动裁剪框
         */
        public Options setDragFrameEnabled(boolean isDragFrame) {
            mOptionBundle.putBoolean(EXTRA_DRAG_CROP_FRAME, isDragFrame);
            return this;
        }

        /**
         * @param count - crop grid rows count.
         *              作物网格行计数。
         */
        public Options setCropGridRowCount(@IntRange(from = 0) int count) {
            mOptionBundle.putInt(EXTRA_CROP_GRID_ROW_COUNT, count);
            return this;
        }

        /**
         * @param count - crop grid columns count.
         *              裁剪网格列计数。
         */
        public Options setCropGridColumnCount(@IntRange(from = 0) int count) {
            mOptionBundle.putInt(EXTRA_CROP_GRID_COLUMN_COUNT, count);
            return this;
        }

        /**
         * @param color - desired color of crop grid/guidelines
         *              期望的裁剪网格/指导线颜色
         */
        public Options setCropGridColor(@ColorInt int color) {
            mOptionBundle.putInt(EXTRA_CROP_GRID_COLOR, color);
            return this;
        }

        /**
         * @param width - desired width of crop grid lines in pixels
         *              所需的裁剪网格线宽度(以像素为单位)
         */
        public Options setCropGridStrokeWidth(@IntRange(from = 0) int width) {
            mOptionBundle.putInt(EXTRA_CROP_GRID_STROKE_WIDTH, width);
            return this;
        }

        /**
         * @param color - desired resolved color of the toolbar 工具栏所需的已解析颜色
         */
        public Options setToolbarColor(@ColorInt int color) {
            mOptionBundle.putInt(EXTRA_TOOL_BAR_COLOR, color);
            return this;
        }

        /**
         * @param color - desired resolved color of the statusbar 状态栏所需的解析颜色
         */
        public Options setStatusBarColor(@ColorInt int color) {
            mOptionBundle.putInt(EXTRA_STATUS_BAR_COLOR, color);
            return this;
        }

        /**
         * @param color - desired resolved color of the active and selected widget (default is orange) and progress wheel middle line 状态栏所需的解析颜色
         */
        public Options setActiveWidgetColor(@ColorInt int color) {
            mOptionBundle.putInt(EXTRA_UCROP_COLOR_WIDGET_ACTIVE, color);
            return this;
        }

        /**
         * @param color - desired resolved color of Toolbar text and buttons (default is darker orange)
         *              工具栏文本和按钮的理想颜色(默认为深橙色)
         */
        public Options setToolbarWidgetColor(@ColorInt int color) {
            mOptionBundle.putInt(EXTRA_UCROP_WIDGET_COLOR_TOOLBAR, color);
            return this;
        }

        /**
         * @param openWhiteStatusBar - Change the status bar font color —修改状态栏字体颜色
         */
        public Options isOpenWhiteStatusBar(boolean openWhiteStatusBar) {
            mOptionBundle.putBoolean(EXTRA_UCROP_WIDGET_CROP_OPEN_WHITE_STATUSBAR, openWhiteStatusBar);
            return this;
        }

        /**
         * @param text - desired text for Toolbar title 工具栏标题所需的文本
         */
        public Options setToolbarTitle(@Nullable String text) {
            mOptionBundle.putString(EXTRA_UCROP_TITLE_TEXT_TOOLBAR, text);
            return this;
        }

        /**
         * @param drawable - desired drawable for the Toolbar left cancel icon 所需绘制的工具栏左取消icom
         */
        public Options setToolbarCancelDrawable(@DrawableRes int drawable) {
            mOptionBundle.putInt(EXTRA_UCROP_WIDGET_CANCEL_DRAWABLE, drawable);
            return this;
        }

        /**
         * @param drawable - desired drawable for the Toolbar right crop icon 所需绘制工具栏右侧裁剪图标
         */
        public Options setToolbarCropDrawable(@DrawableRes int drawable) {
            mOptionBundle.putInt(EXTRA_UCROP_WIDGET_CROP_DRAWABLE, drawable);
            return this;
        }

        /**
         * @param color - desired resolved color of logo fill (default is darker grey)
         *              理想的Logo填充颜色(默认为深灰色)
         */
        public Options setLogoColor(@ColorInt int color) {
            mOptionBundle.putInt(EXTRA_UCROP_LOGO_COLOR, color);
            return this;
        }

        /**
         * @param -set cuts path
         * 设置切割路径
         */
        public Options setCutListData(ArrayList<String> list) {
            mOptionBundle.putStringArrayList(EXTRA_CUT_CROP, list);
            return this;
        }

        /**
         * @param enabled - set to true to let user resize crop bounds (disabled by default) 设
         *                置为true允许用户调整裁剪边界大小(默认禁用)
         */
        public Options setFreeStyleCropEnabled(boolean enabled) {
            mOptionBundle.putBoolean(EXTRA_FREE_STYLE_CROP, enabled);
            return this;
        }

        /**
         * @param statusFont - Set status bar black
         *                   设置状态栏为黑色
         */
        public Options setStatusFont(boolean statusFont) {
            mOptionBundle.putBoolean(EXTRA_FREE_STATUS_FONT, statusFont);
            return this;
        }

        /**
         * Pass an ordered list of desired aspect ratios that should be available for a user.
         * 传递用户可用的所需长宽比的有序列表。
         *
         * @param selectedByDefault - index of aspect ratio option that is selected by default (starts with 0).
         * @param aspectRatio       - list of aspect ratio options that are available to user
         */
        public Options setAspectRatioOptions(int selectedByDefault, AspectRatio... aspectRatio) {
            if (selectedByDefault > aspectRatio.length) {
                throw new IllegalArgumentException(String.format(Locale.US,
                        "Index [selectedByDefault = %d] cannot be higher than aspect ratio options count [count = %d].",
                        selectedByDefault, aspectRatio.length));
            }
            mOptionBundle.putInt(EXTRA_ASPECT_RATIO_SELECTED_BY_DEFAULT, selectedByDefault);
            mOptionBundle.putParcelableArrayList(EXTRA_ASPECT_RATIO_OPTIONS, new ArrayList<Parcelable>(Arrays.asList(aspectRatio)));
            return this;
        }

        /**
         * @param color - desired background color that should be applied to the root view
         *              应该应用于根视图的所需背景色
         */
        public Options setRootViewBackgroundColor(@ColorInt int color) {
            mOptionBundle.putInt(EXTRA_UCROP_ROOT_VIEW_BACKGROUND_COLOR, color);
            return this;
        }

        /**
         * Set an aspect ratio for crop bounds.
         * User won't see the menu with other ratios options.
         * 设置裁剪边界的宽高比。用户将看不到其他比例选项的菜单。
         *
         * @param x aspect ratio X
         * @param y aspect ratio Y
         */
        public Options withAspectRatio(float x, float y) {
            mOptionBundle.putFloat(EXTRA_ASPECT_RATIO_X, x);
            mOptionBundle.putFloat(EXTRA_ASPECT_RATIO_Y, y);
            return this;
        }

        /**
         * Set an aspect ratio for crop bounds that is evaluated from source image width and height.
         * User won't see the menu with other ratios options.
         * 设置从源图像宽度和高度计算的裁剪边界的宽高比。用户将看不到其他比例选项的菜单。
         */
        public Options useSourceImageAspectRatio() {
            mOptionBundle.putFloat(EXTRA_ASPECT_RATIO_X, 0);
            mOptionBundle.putFloat(EXTRA_ASPECT_RATIO_Y, 0);
            return this;
        }

        /**
         * Set maximum size for result cropped image.
         * 设置结果裁剪图像的最大尺寸。
         *
         * @param width  max cropped image width
         * @param height max cropped image height
         */
        public Options withMaxResultSize(int width, int height) {
            mOptionBundle.putInt(EXTRA_MAX_SIZE_X, width);
            mOptionBundle.putInt(EXTRA_MAX_SIZE_Y, height);
            return this;
        }

        /**
         * @param activityCropExitAnimation activity exit animation 活动退出动画
         */
        public Options setCropExitAnimation(@AnimRes int activityCropExitAnimation) {
            mOptionBundle.putInt(EXTRA_WINDOW_EXIT_ANIMATION, activityCropExitAnimation);
            return this;
        }

        /**
         * @param navBarColor set NavBar Color 活动退出动画
         */
        public Options setNavBarColor(@ColorInt int navBarColor) {
            mOptionBundle.putInt(EXTRA_NAV_BAR_COLOR, navBarColor);
            return this;
        }
    }
}
