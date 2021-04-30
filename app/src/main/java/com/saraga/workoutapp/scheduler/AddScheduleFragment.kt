package com.saraga.workoutapp.scheduler

import android.app.*
import android.app.DatePickerDialog.OnDateSetListener
import android.app.TimePickerDialog.OnTimeSetListener
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.ToggleButton
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.saraga.workoutapp.MainApplication
import com.saraga.workoutapp.R
import com.saraga.workoutapp.data.Schedule
import com.saraga.workoutapp.services.AlertReceiver
import com.saraga.workoutapp.utils.Constants
import com.saraga.workoutapp.utils.DateUtility
import java.util.*


class AddScheduleFragment : Fragment() {
    private lateinit var datePickerDialog: DatePickerDialog
    private lateinit var btnDateSchedule: AppCompatButton

    private lateinit var btnBeginClock: AppCompatButton
    private lateinit var btnEndClock: AppCompatButton

    private var day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
    private var month = Calendar.getInstance().get(Calendar.MONTH) + 1
    private var year = Calendar.getInstance().get(Calendar.YEAR)

    private var hour1 = 0
    private var minute1 = 0
    private var hour2 = 0
    private var minute2 = 0

    private val viewModel: SchedulerViewModel by viewModels {
        SchedulerViewModelFactory((requireActivity().application as MainApplication).scheduleRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_schedule, container, false)

        setupDateAndClock(view)

        val toggleIsRun = view.findViewById<ToggleButton>(R.id.toggleIsRun)
        val cboxJustOnce = view.findViewById<CheckBox>(R.id.cboxJustOnce)

        val clNotJustOnce = view.findViewById<ConstraintLayout>(R.id.clNotJustOnce)
        val tMon = view.findViewById<ToggleButton>(R.id.toggleMon)
        val tTue = view.findViewById<ToggleButton>(R.id.toggleTue)
        val tWed = view.findViewById<ToggleButton>(R.id.toggleWed)
        val tThu = view.findViewById<ToggleButton>(R.id.toggleThu)
        val tFri = view.findViewById<ToggleButton>(R.id.toggleFri)
        val tSat = view.findViewById<ToggleButton>(R.id.toggleSat)
        val tSun = view.findViewById<ToggleButton>(R.id.toggleSun)

        val clJustOnce = view.findViewById<ConstraintLayout>(R.id.clJustOnce)
        val btnDate = view.findViewById<AppCompatButton>(R.id.btnDateSchedule)

        val btnBeginClock = view.findViewById<AppCompatButton>(R.id.btnBeginClock)
        val btnEndClock = view.findViewById<AppCompatButton>(R.id.btnEndClock)

        val tvTarget = view.findViewById<TextView>(R.id.tvTarget)
        val etTarget = view.findViewById<EditText>(R.id.etTarget)
        val btnCreate = view.findViewById<AppCompatButton>(R.id.btnCreate)
        val btnCancel = view.findViewById<AppCompatButton>(R.id.btnCancel)

        toggleIsRun.setOnClickListener {
            if (toggleIsRun.isChecked){
                tvTarget.text = "TargetTraining Target (how many steps)"
            } else{
                tvTarget.text = "Training Target (in kilometers)"
            }
        }

        cboxJustOnce.setOnClickListener {
            if (cboxJustOnce.isChecked){
                clJustOnce.visibility = VISIBLE
                clNotJustOnce.visibility = GONE
            } else{
                clNotJustOnce.visibility = VISIBLE
                clJustOnce.visibility = GONE
            }
        }

        btnCancel.setOnClickListener {
            findNavController().popBackStack()
        }

        btnCreate.setOnClickListener {
            var cal = Calendar.getInstance()
            cal.set(year, month - 1, day, 0, 0)
            cal.clear(Calendar.SECOND)

            var beginClock = Calendar.getInstance()
            beginClock.set(0, 0, 0, hour1, minute1)
            beginClock.clear(Calendar.SECOND)
            var endClock = Calendar.getInstance()
            endClock.set(0, 0, 0, hour2, minute2)
            endClock.clear(Calendar.SECOND)

            var target = etTarget.text.toString().toDouble()
            if (toggleIsRun.isChecked){
                target *= Constants.STEP_TO_METER
            } else{
                target *= 1000
            }

            val schedule = Schedule(
                0,
                toggleIsRun.isChecked,
                cboxJustOnce.isChecked,
                cal.time,
                tMon.isChecked,
                tTue.isChecked,
                tWed.isChecked,
                tThu.isChecked,
                tFri.isChecked,
                tSat.isChecked,
                tSun.isChecked,
                beginClock.time,
                endClock.time,
                target
            )
            viewModel.insert(schedule)
//            var ccal = Calendar.getInstance()
//            ccal.add(Calendar.SECOND, 10)
            startAlarm(schedule)
            findNavController().popBackStack()
        }

        return view
    }

    private fun startAlarm(sched: Schedule) {
        val beginDate = sched.getNext()
        var cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.time = beginDate
        cal.set(
            Calendar.HOUR_OF_DAY,
            DateUtility.getElement(sched.beginClock, Calendar.HOUR_OF_DAY)
        )
        cal.set(Calendar.MINUTE, DateUtility.getElement(sched.beginClock, Calendar.MINUTE))

        if (cal.before(Calendar.getInstance())) {
            return
        }
        val bef = cal
        // TODO: KASIH PESAN SURUH OLAHRAGA
        addAlarm(cal, sched.id * 2)
        cal.set(Calendar.HOUR_OF_DAY, DateUtility.getElement(sched.endClock, Calendar.HOUR_OF_DAY))
        cal.set(Calendar.MINUTE, DateUtility.getElement(sched.endClock, Calendar.MINUTE))
        if (cal.before(bef)) {
            cal.add(Calendar.DATE, 1)
        }
        // TODO: KASIH PESAN PEMBERITAHUAN JADWAL SELESAI
        addAlarm(cal, sched.id * 2 + 1)
    }

    private fun addAlarm(c: Calendar, id: Int){
        val alarmManager = requireActivity().getSystemService(Context.ALARM_SERVICE) as AlarmManager?
        val intent = Intent(requireContext(), AlertReceiver::class.java).also{
            it.action = id.toString()
        }
        val pendingIntent = PendingIntent.getBroadcast(requireContext(), id, intent, 0)
        Log.d("Alert", c.toString())
        alarmManager!!.setExact(AlarmManager.RTC_WAKEUP, c.timeInMillis, pendingIntent)
    }

    private fun setupDateAndClock(view: View){
        btnDateSchedule = view.findViewById(R.id.btnDateSchedule)
        btnBeginClock = view.findViewById(R.id.btnBeginClock)
        btnEndClock = view.findViewById(R.id.btnEndClock)


        btnDateSchedule.text = getTodaysDate()

        val dateSetListener =
            OnDateSetListener { datePicker, year, month, day ->
                var month = month
                month = month + 1
                val date: String = makeDateString(day, month, year)
                this.year = year
                this.month = month
                this.day = day
                btnDateSchedule.text = date
            }

        val cal: Calendar = Calendar.getInstance()
        val year: Int = cal.get(Calendar.YEAR)
        val month: Int = cal.get(Calendar.MONTH)
        val day: Int = cal.get(Calendar.DAY_OF_MONTH)

        val style: Int = AlertDialog.THEME_HOLO_LIGHT

        datePickerDialog = DatePickerDialog(
            requireContext(),
            style,
            dateSetListener,
            year,
            month,
            day
        )
        btnDateSchedule.setOnClickListener {
            datePickerDialog.show()
        }

        val onTimeSetListener1 =
            OnTimeSetListener { timePicker, selectedHour, selectedMinute ->
                val hour = selectedHour
                val minute = selectedMinute
                this.hour1 = hour
                this.minute1 = minute
                btnBeginClock.setText(
                    java.lang.String.format(
                        Locale.getDefault(),
                        "%02d:%02d",
                        hour,
                        minute
                    )
                )
            }

        val timePickerDialog1 =
            TimePickerDialog(context, AlertDialog.THEME_HOLO_DARK, onTimeSetListener1, 0, 0, true)

        btnBeginClock.setOnClickListener {
            timePickerDialog1.setTitle("Select Time")
            timePickerDialog1.show()
        }

        val onTimeSetListener2 =
            OnTimeSetListener { timePicker, selectedHour, selectedMinute ->
                val hour = selectedHour
                val minute = selectedMinute
                this.hour2 = hour
                this.minute2 = minute
                btnEndClock.setText(
                    java.lang.String.format(
                        Locale.getDefault(),
                        "%02d:%02d",
                        hour,
                        minute
                    )
                )
            }

        val timePickerDialog2 =
            TimePickerDialog(context, AlertDialog.THEME_HOLO_DARK, onTimeSetListener2, 0, 0, true)

        btnEndClock.setOnClickListener {
            timePickerDialog2.setTitle("Select Time")
            timePickerDialog2.show()
        }
    }

    private fun getTodaysDate(): String {
        val cal = Calendar.getInstance()
        val year = cal[Calendar.YEAR]
        var month = cal[Calendar.MONTH]
        month = month + 1
        val day = cal[Calendar.DAY_OF_MONTH]
        return makeDateString(day, month, year)
    }

    private fun makeDateString(day: Int, month: Int, year: Int): String {
        return getMonthFormat(month) + " " + day.toString() + " " + year.toString()
    }

    private fun getMonthFormat(month: Int): String {
        if (month == 1) return "JAN"
        if (month == 2) return "FEB"
        if (month == 3) return "MAR"
        if (month == 4) return "APR"
        if (month == 5) return "MAY"
        if (month == 6) return "JUN"
        if (month == 7) return "JUL"
        if (month == 8) return "AUG"
        if (month == 9) return "SEP"
        if (month == 10) return "OCT"
        if (month == 11) return "NOV"
        return if (month == 12) "DEC" else "JAN"
    }
}