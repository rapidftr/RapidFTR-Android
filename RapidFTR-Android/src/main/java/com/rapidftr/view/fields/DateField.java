package com.rapidftr.view.fields;


import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.DatePicker;
import com.rapidftr.R;
import com.rapidftr.utils.RapidFtrDateTime;
import org.json.JSONException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class DateField extends TextField implements DatePickerDialog.OnDateSetListener, DialogInterface.OnClickListener, View.OnTouchListener {

    public DateField(Context context) {
        super(context);
    }

    public DateField(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void initialize() throws JSONException {
        super.initialize();
        getEditTextView().setOnTouchListener(this);
    }

    protected DateFormat getDateFormatter() {
        return new SimpleDateFormat(RapidFtrDateTime.formatForChildRegister);
    }

    protected Calendar getDate() {
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(getDateFormatter().parse(getText()));
            return calendar;
        } catch (Exception e) {
            return null;
        }
    }

    protected void setDate(Calendar newValue) {
        setText(getDateFormatter().format(newValue.getTime()));
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            Calendar cal = getDate();
            if (cal == null)
                cal = new GregorianCalendar();

            DatePickerDialog dialog = new DatePickerDialog(getContext(), this, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
            dialog.setButton3(getContext().getString(R.string.clear), this);
            dialog.show();
        }

        return true;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, monthOfYear);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        setDate(calendar);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        setText(null);
    }

}
