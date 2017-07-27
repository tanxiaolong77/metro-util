package com.quanjing.util;

import java.beans.PropertyEditorSupport;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateEditor extends PropertyEditorSupport {

    private static final DateFormat DATEFORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static final DateFormat TIMEFORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final DateFormat ShortTIMEFORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    
    private DateFormat dateFormat;
    private boolean allowEmpty = true;

    public DateEditor() {
    }

    public DateEditor(DateFormat dateFormat) {
        this.dateFormat = dateFormat;
    }

    public DateEditor(DateFormat dateFormat, boolean allowEmpty) {
        this.dateFormat = dateFormat;
        this.allowEmpty = allowEmpty;
    }
    
    /**
     * Parse the Date from the given text, using the specified DateFormat.
     */
    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        if (this.allowEmpty && StringUtils.isEmpty(text)) {
            // Treat empty String as null value.
            setValue(null);
        } else {
            try {
                if(this.dateFormat != null){
                    setValue(this.dateFormat.parse(text));
                }else {
                	//数字的情况
                	try{
                		Long time=Long.parseLong(text);
                		setValue(new Date(time));
                	}catch(NumberFormatException e){
                		 if(text.contains(":")){
                         	if(text.length()<17){
                         		setValue(ShortTIMEFORMAT.parse(text));
                         	}else{
                         		setValue(TIMEFORMAT.parse(text));
                         	}
                         }else{
                             setValue(DATEFORMAT.parse(text));
                         }
                	}
                   
                }
            } catch (ParseException ex) {
                throw new IllegalArgumentException("Could not parse date: " + ex.getMessage(), ex);
            }
        }
    }

    /**
     * Format the Date as String, using the specified DateFormat.
     */
    @Override
    public String getAsText() {
        Date value = (Date) getValue();
        DateFormat dateFormat = this.dateFormat;
        if(dateFormat == null)
            dateFormat = TIMEFORMAT;
        return (value != null ? dateFormat.format(value) : "");
    }
}