package ir.snapp.box.interview.task.fcm

import android.app.ActivityManager
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import ir.snapp.box.interview.task.const.Constants
import ir.snapp.box.interview.task.data.AppRepositoryImpl
import ir.snapp.box.interview.task.service.AlarmReceiver
import org.koin.android.ext.android.inject
import org.koin.core.component.KoinComponent


class FirebaseInstance : FirebaseMessagingService(), KoinComponent {

    private val appRepositoryImpl: AppRepositoryImpl by inject()

    override fun onMessageReceived(value: RemoteMessage) {
        super.onMessageReceived(value)
        // if app is foreground call repository
        // otherwise set alarm manager to wake
        // phone up and bring application to front
        if (!isInBackground()) {
            appRepositoryImpl.onDataReceived(value = value)
        } else {
            startAlarmManager(Gson().toJson(value.data))
        }
    }

    // check app is in background
    private fun isInBackground(): Boolean {
        val process = ActivityManager.RunningAppProcessInfo()
        ActivityManager.getMyMemoryState(process)
        return process.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
    }


    private fun startAlarmManager(jsonData: String) {
        val alarmIntent = Intent(applicationContext, AlarmReceiver::class.java)
        alarmIntent.putExtra(Constants.EXTRA_DATA, jsonData)
        val pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0)
        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis() + Constants.TRIGGER_ALARM_AT,
            pendingIntent
        )
    }

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
    }
}