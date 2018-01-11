package com.daydvr.store.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Looper;

import com.daydvr.store.base.GameConstant;
import com.daydvr.store.bean.GameListBean;
import com.daydvr.store.manager.GameUriManager;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import static com.daydvr.store.base.GameConstant.DOWNLOADABLE;
import static com.daydvr.store.base.GameConstant.DOWNLOADING;
import static com.daydvr.store.base.GameConstant.INSTALLABLE;
import static com.daydvr.store.base.GameConstant.INSTALLED;
import static com.daydvr.store.base.GameConstant.PAUSED;
import static com.daydvr.store.base.GameConstant.UPDATE;

public class AppInfoUtil {

    public static final String TAG = "daydvr.AppInfoUtil";

    private WeakReference<Context> mContext;
    private PackageManager mPackageManager;

    public AppInfoUtil(Context context) {
        mContext = new WeakReference<Context>(context);
        mPackageManager = context.getPackageManager();
    }


    /**
     * 根据包名获得软件的的版本代码，
     * 如果没有安装该软件则返回 -1
     *
     * @param packagerName 已安装的Apk的包名
     * @return Apk版本代码，不存在则为-1
     */
    public int getApkVesionCode(String packagerName) {
        try {
            PackageInfo packageInfo = mPackageManager.getPackageInfo(packagerName, 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * 根据Apk的路径获得Apk包名
     * 如果没找到相应信息，则返回空串
     *
     * @param apkPath
     * @return Apk包名，不存在则为空串
     */
    public String getApkPackageName(String apkPath) {
        PackageInfo packageInfo = mPackageManager.getPackageArchiveInfo(
                apkPath, PackageManager.GET_ACTIVITIES);
        if (packageInfo != null) {
            return packageInfo.applicationInfo.packageName;
        } else {
            return "";
        }
    }

    /**
     * 根据Apk的信息获得包名
     * 如果没找到相应信息，则返回空串
     *
     * @param applicationInfo
     * @return Apk包名，不存在则为空串
     */
    public String getApkPackageName(ApplicationInfo applicationInfo) {
        if (applicationInfo != null) {
            return applicationInfo.packageName;
        } else {
            return "";
        }
    }

    /**
     * 根据Apk的路径获得Apk包名
     * 如果没找到相应信息，则返回空串
     *
     * @param apkPath
     * @return Apk名字，不存在则为空串
     */
    public String getApkName(String apkPath) {
        PackageInfo packageInfo = mPackageManager.getPackageArchiveInfo(
                apkPath, PackageManager.GET_ACTIVITIES);
        if (packageInfo != null) {

            return mPackageManager.getApplicationLabel(packageInfo.applicationInfo).toString();
        } else {
            return "";
        }
    }

    /**
     * 根据标志判断获取系统应用还是所有应用的ApplicationInfo
     *
     * @param flag 获取标志
     *             flag = 1     系统应用
     *             flag = 0     用户应用
     *             默认 所有应用
     * @return
     */
    public List<ApplicationInfo> getApps(int flag) {
        List<ApplicationInfo> apps = mPackageManager.getInstalledApplications(0);
        List<ApplicationInfo> resultInfo = new ArrayList<>();
        for (ApplicationInfo info : apps) {
            switch (flag) {
                case 1:
                    if ((info.flags & ApplicationInfo.FLAG_SYSTEM) == 1) {
                        resultInfo.add(info);
                    }
                    break;
                case 0:
                    if ((info.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                        resultInfo.add(info);
                    }
                    break;

                default:
                    resultInfo = apps;
                    break;
            }
        }
        return resultInfo;
    }

    /**
     * 根据包名获得软件的的版本号，
     * 如果没有安装该软件则返回 -1
     *
     * @param packagerName
     * @return
     */
    public int getApkVesionName(String packagerName) {
        try {
            PackageInfo packageInfo = mContext.get().getPackageManager().getPackageInfo(packagerName, 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            Logger.d(TAG, "NotFoundPackageName");
        }
        return -1;
    }

    /**
     * 获取APP图片
     *
     * @param info
     * @return
     */
    public Drawable getAppIcon(ApplicationInfo info) {
        return info.loadIcon(mPackageManager);
    }

    /**
     * 获取App 名字
     * @param info
     * @return
     */
    public String getAppName(ApplicationInfo info) {
        return mPackageManager.getApplicationLabel(info).toString();
    }

    /**
     * 获取App大小
     * @param info
     * @return
     */
    public String getAppSizes(ApplicationInfo info) {
        String dir = info.sourceDir;
        //获取应用数据大小
        Long length = new File(dir).length();
        //转换为 M
        float size = length*1f/1024/1024;
        return (float) (Math.round(size * 100)) / 100 + "M";
    }


    /**
     * 根据包名获得版本号后
     * 判断游戏安装/下载状态
     * 若是版本号 -1 代表游戏没安装
     *
     * @param bean
     * @return
     */
    public byte getApplicationStatus(GameListBean bean) {
        int versionCode = getApkVesionName(bean.getPackageName());
        //判断游戏是否被安装了
        if (versionCode != -1) {
            //游戏被安装：已安装/更新
            if (versionCode == bean.getVersion()) {
                //版本号相同，游戏已安装
                return INSTALLED;
            } else {
                //版本号不同，游戏待更新
                return UPDATE;
            }
        } else {
            //游戏未被安装：待下载/待安装/下载中/暂停中
            String apkPath = GameUriManager.getApkPath(bean.getId());
            if (GameUriManager.getApkTaskId(bean.getId()) == null &&
                    GameUriManager.getObbTaskId(bean.getId()) == null) {
                //游戏不存在下载任务 待下载/待安装
                boolean apkIsExist = (new File(apkPath)).exists();
                if (GameUriManager.getObbUrl(bean.getId()) != null) {
                    //OBB文件存在
                    String obbPath = GameUriManager.getObbPath(bean.getId());
                    boolean obbIsExist = (new File(obbPath)).exists();
                    if (apkIsExist && obbIsExist) {
                        //apk ， obb 下载完成，待安装
                        return INSTALLABLE;
                    } else {
                        //apk待下载 / obb待下载
                        return DOWNLOADABLE;
                    }
                } else {
                    //OBB 文件不存在
                    if (apkIsExist) {
                        //apk 已下载，待安装
                        return INSTALLABLE;
                    } else {
                        //apk 待下载
                        return DOWNLOADABLE;
                    }
                }
            } else {
                int apkStatue = 0;
                boolean apkIsExist = (new File(apkPath)).exists();
                String apkUrl = GameUriManager.getApkUrl(bean.getId());
                if (GameUriManager.getObbUrl(bean.getId()) != null) {
                    String obbUrl = GameUriManager.getObbUrl(bean.getId());
                    String obbPath = GameUriManager.getObbPath(bean.getId());
                    boolean obbIsExist = (new File(obbPath)).exists();
                    int obbStatue = 0;
                    if (apkIsExist && obbIsExist) {
                        return INSTALLABLE;
                    }
                    if (apkStatue == PAUSED || obbStatue == PAUSED) {
                        return GameConstant.PAUSED;
                    }
                    return DOWNLOADING;
                } else {
                    if (apkIsExist) {
                        return INSTALLABLE;
                    }
                    if (apkStatue == PAUSED) {
                        return GameConstant.PAUSED;
                    }
                    return DOWNLOADING;
                }
            }
        }
    }

    /**
     * 判断机身内存与网络状态是否适合下载
     *
     * @param bean
     * @return
     */
    public boolean checkMemoryAndNet(final GameListBean bean) {
        if (MemoryManagerUtil.isMemoryLack(bean.getSize())) {
            new AlertDialog.Builder(mContext.get())
                    .setTitle("空间不足")
                    .setMessage("手机内存空间不足，无法下载！\n 请清理手机垃圾！")
                    .setPositiveButton("确定", null)
                    .show();
            return false;
        }

        if (NetWorkUtils.getAPNType(mContext.get()) == 0) {
            new AlertDialog.Builder(mContext.get())
                    .setTitle("无网络")
                    .setMessage("无网络链接！\n 请检查网络！")
                    .setPositiveButton("确定", null)
                    .show();
            return false;
        }

        if (NetWorkUtils.getAPNType(mContext.get()) != 1) {
            final boolean[] back = {false};
            try {
                new AlertDialog.Builder(mContext.get())
                        .setTitle("无WIFI网络")
                        .setMessage("当前并不是wifi网络！\n 是否还要下载！")
                        .setCancelable(false)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Looper.getMainLooper().quitSafely();
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Looper.getMainLooper().quitSafely();
                                back[0] = true;
                            }
                        })
                        .show();
                Looper.loop();
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
            if (back[0]) {
                return false;
            }
        }
        return true;
    }
}