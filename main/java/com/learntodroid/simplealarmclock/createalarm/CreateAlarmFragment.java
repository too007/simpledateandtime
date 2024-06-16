package com.learntodroid.simplealarmclock.createalarm;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TimePicker;
import android.Manifest;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import com.learntodroid.simplealarmclock.R;
import com.learntodroid.simplealarmclock.data.Alarm;
import com.learntodroid.simplealarmclock.singledateandtimerpiker.SingleDateAndTimePicker;

import java.util.Date;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CreateAlarmFragment extends Fragment {
//    @BindView(R.id.fragment_createalarm_timePicker)
    TimePicker timePicker;
//    @BindView(R.id.fragment_createalarm_title)
    EditText title;
//    @BindView(R.id.fragment_createalarm_scheduleAlarm)
    Button scheduleAlarm;
//    @BindView(R.id.fragment_createalarm_recurring)
    CheckBox recurring;
//    @BindView(R.id.fragment_createalarm_checkMon)
    CheckBox mon;
//    @BindView(R.id.fragment_createalarm_checkTue)
    CheckBox tue;
//    @BindView(R.id.fragment_createalarm_checkWed)
    CheckBox wed;
//    @BindView(R.id.fragment_createalarm_checkThu)
    CheckBox thu;
//    @BindView(R.id.fragment_createalarm_checkFri)
    CheckBox fri;
//    @BindView(R.id.fragment_createalarm_checkSat)
    CheckBox sat;
//    @BindView(R.id.fragment_createalarm_checkSun)
    CheckBox sun;
//    @BindView(R.id.fragment_createalarm_recurring_options)
    LinearLayout recurringOptions;
    private static final int REQUEST_NOTIFICATION_PERMISSION = 1;
    private static final int NOTIFICATION_ID = 100;
    private CreateAlarmViewModel createAlarmViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createAlarmViewModel = ViewModelProviders.of(this).get(CreateAlarmViewModel.class);
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_createalarm, container, false);

        ButterKnife.bind(this, view);
        timePicker=view.findViewById(R.id.fragment_createalarm_timePicker);
        title=view.findViewById(R.id.fragment_createalarm_title);
        scheduleAlarm=view.findViewById(R.id.fragment_createalarm_scheduleAlarm);
        recurring=view.findViewById(R.id.fragment_createalarm_recurring);
        recurringOptions=view.findViewById(R.id.fragment_createalarm_recurring_options);
        sun=view.findViewById(R.id.fragment_createalarm_checkSun);
        sat=view.findViewById(R.id.fragment_createalarm_checkSat);
        fri=view.findViewById(R.id.fragment_createalarm_checkFri);
        thu=view.findViewById(R.id.fragment_createalarm_checkThu);
        wed=view.findViewById(R.id.fragment_createalarm_checkWed);
        tue=view.findViewById(R.id.fragment_createalarm_checkTue);
        mon=view.findViewById(R.id.fragment_createalarm_checkMon);

        final SingleDateAndTimePicker singleDateAndTimePicker = view.findViewById(R.id.single_day_picker);

        // Example for setting default selected date to yesterday
//        Calendar instance = Calendar.getInstance();
//        instance.add(Calendar.DATE, -1 );
//        singleDateAndTimePicker.setDefaultDate(instance.getTime());
        SingleDateAndTimePicker.OnDateChangedListener changeListener = (displayed, date) -> display(date);
        singleDateAndTimePicker.addOnDateChangedListener(changeListener);
        singleDateAndTimePicker.setCurved(true);
        singleDateAndTimePicker.setCyclic(true);

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) requireContext(),
                    new String[]{Manifest.permission.POST_NOTIFICATIONS},
                    REQUEST_NOTIFICATION_PERMISSION);
        } else {
//            showNotification();
            Toast.makeText(requireContext(), "else", Toast.LENGTH_SHORT).show();
        }

        recurring.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    recurringOptions.setVisibility(View.VISIBLE);
                } else {
                    recurringOptions.setVisibility(View.GONE);
                }
            }
        });

        scheduleAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scheduleAlarm();

                Navigation.findNavController(v).navigate(R.id.action_createAlarmFragment_to_alarmsListFragment);
            }
        });

        return view;
    }

    private void scheduleAlarm() {
        int alarmId = new Random().nextInt(Integer.MAX_VALUE);

        Alarm alarm = new Alarm(
                alarmId,
                TimePickerUtil.getTimePickerHour(timePicker),
                TimePickerUtil.getTimePickerMinute(timePicker),
                title.getText().toString(),
                System.currentTimeMillis(),
                true,
                recurring.isChecked(),
                mon.isChecked(),
                tue.isChecked(),
                wed.isChecked(),
                thu.isChecked(),
                fri.isChecked(),
                sat.isChecked(),
                sun.isChecked()
        );

        createAlarmViewModel.insert(alarm);

        alarm.schedule(getContext());
    }
    private void display(Date toDisplay) {
        Log.e("TAG", "display: "+toDisplay);
    }
}
