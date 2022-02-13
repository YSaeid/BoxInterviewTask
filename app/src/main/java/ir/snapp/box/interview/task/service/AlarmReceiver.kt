package ir.snapp.box.interview.task.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import ir.snapp.box.interview.task.const.Constants
import ir.snapp.box.interview.task.ui.MainActivity

// when this broadcast receive alarm
// it will trigger onReceive method
// as of android restriction for starting activity
// from service, this class implemented for
// start and bring activity to front
// it will pass extra data to activity
// because of LiveData problem (not observe when
// application is in background)
class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val bundle = intent?.extras
        val offerPageIntent = Intent(context, MainActivity::class.java)
        offerPageIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        if (bundle != null) {
            offerPageIntent.putExtra(Constants.EXTRA_DATA, bundle.getString(Constants.EXTRA_DATA))
        }
        context?.startActivity(offerPageIntent)
    }
}