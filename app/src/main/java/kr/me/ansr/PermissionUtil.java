package kr.me.ansr;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by KMS on 2016-12-20.
 */
public class PermissionUtil {
//    권한 요청하기
    public static boolean checkAndRequestPermission(Activity activity, int permissionRequestCode, String... permissions) {
        String[] requiredPermissions = getRequiredPermissions(activity, permissions);
//        if (requiredPermissions.length > 0 && !activity.isDestroyedCompat()) {
        if (requiredPermissions.length > 0 ) {
            ActivityCompat.requestPermissions(activity, requiredPermissions, permissionRequestCode);
            return false;
        } else {
            return true;
        }
    }

    public static boolean checkAndRequestPermission(Fragment fragment, int permissionRequestCode, String... permissions) {
        String[] requiredPermissions = getRequiredPermissions(fragment.getContext() != null ?
                fragment.getContext() : fragment.getActivity(), permissions);

        if (requiredPermissions.length > 0 && fragment.isAdded()) {
            fragment.requestPermissions(requiredPermissions, permissionRequestCode);
            return false;
        } else {
            return true;
        }
    }

//    요청이 필요한 권한 가져오기
    public static String[] getRequiredPermissions(Context context, String... permissions) {
        List<String> requiredPermissions = new ArrayList<>();

        // Context가 null이면 무조건 권한을 요청하도록 requiredPermissions가 존재한다고 reutrn 한다
        if (context == null) return requiredPermissions.toArray(new String[1]);

        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                requiredPermissions.add(permission);
            }
        }

        return requiredPermissions.toArray(new String[requiredPermissions.size()]);
    }

//    권한을 받아왔는지 체크

    public static boolean verifyPermissions(int[] grantResults) {
        // At least one result must be checked.
        if (grantResults.length < 1) return false;

        // Verify that each required permission has been granted, otherwise return false.
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED)
                return false;
        }
        return true;
    }

//    메세지가져오기
    public static final int PERMISSION_CAMERA = 0;
    public static final int PERMISSION_LOCATION = 1;
    public static final int PERMISSION_STORAGE = 2;

    public static String getRationalMessage(Context context, int code) {
        switch (code) {
//            case PERMISSION_CAMERA:
//                return getRationalMessage(context,
//                        context.getString(R.string.permission_camera_rational), context.getString(R.string.permission_camera));
            case PERMISSION_LOCATION:
                return getRationalMessage(context,
                        context.getString(R.string.permission_location), context.getString(R.string.permission_location_rational));

        }
        return "";
    }



    public static String getRationalMessage(Context context, String rational, String permission) {
        return String.format(context.getString(R.string.permission_request), permission, rational);
    }

    public static void showRationalDialog(Context context, int message) {
        showRationalDialog(context, context.getString(message));
    }


//    다이얼로그보여주기
    public static void showRationalDialog(final Context context, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("");
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setPositiveButton(context.getString(R.string.word_settings), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                            .setData(Uri.parse("package:" + context.getPackageName()));
                    context.startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                    Intent intent = new Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
                    context.startActivity(intent);
                }
            }
        });
        builder.create().show();

//        DialogCreator.create(context, message, (dialog, which) -> {
//            try {
//                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
//                        .setData(Uri.parse("package:" + context.getPackageName()));
//                context.startActivity(intent);
//            } catch (ActivityNotFoundException e) {
//                e.printStackTrace();
//
//                Intent intent = new Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
//                context.startActivity(intent);
//            }
//        }, R.string.word_settings, (dialog, which) -> {
//            // Do nothing
//        }, R.string.word_close, 0).show();
    }


}
