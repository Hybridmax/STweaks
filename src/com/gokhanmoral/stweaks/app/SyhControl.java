package com.gokhanmoral.stweaks.app;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

abstract class SyhControl {
	public String description = "";
	public String name = "";
	public String action = "";
	String valueFromScript = "0"; 	//loaded from the kernel script (integer, float, "on"/"off"...)
	String valueFromUser = "0";    	//user input to be applied to the kernel script (integer, float, "on"/"off"...)
	public View view;
	Boolean canGetValueFromScript = true;
	private final String syh_command = "/res/uci.sh ";

	final SyhValueChangedInterface vci; //interface to inform main activity about changed values
    final Context context;
	LinearLayout controlLayout;

    SyhControl(Activity activityIn)
	{
		context = activityIn;
		vci = (SyhValueChangedInterface) activityIn;
	}
		
	public boolean isChanged()
	{
		boolean changed = !valueFromUser.equals(valueFromScript);
		return changed;
	}
	
	// apply user selected value to the kernel script
	public String setValueViaScript() 
	{ 
		String command = syh_command + action + " " + valueFromUser;
		String response = Utils.executeRootCommandInThread(command);
		if(response == null) response = "";
		valueFromScript = valueFromUser;
		return response;		
	}
	
	// get the value from kernel script - user interface NOT CHANGED! 
	public boolean getValueViaScript(boolean optimized)
	{
		boolean isOk = false;
		
		if (this.canGetValueFromScript)
		{
			String command;
			if(optimized)
			{
				command = "`echo " + action + "|awk '{print \". /res/customconfig/actions/\" $1,$1,$2,$3,$4,$5,$6,$7,$8}'`";
			}
			else
			{
				command = syh_command + action;
			}
			String response = Utils.executeRootCommandInThread(command);
			if(response != null)
			{
				if (!response.isEmpty())
				{
					valueFromScript = response.replaceAll("[\n\r]", "");
					isOk = true;
				}
			}
			
			if (!isOk)
			{
				valueFromScript = this.getDefaultValue();
				if (valueFromScript == null)
				{
					valueFromScript = "";
				}
			}
			
			Log.i("getValueViaScript " + this.getClass().getName() + "[" + this.name + "]:", "Value from script:" + valueFromScript);
		}
				
		return isOk;		
	}
	
	public void create()
	{		
		//Assumptions: 
		//1. valueFromScript is set correctly before creation.
		
/*		
 * TODO: Later concern!
		If we use fragments which can be put to stack then we have problems.
		Because of two conditions we are here:
		1.) Control is created for the first time
		2.) Fragment is paused and resuming...
		Question: Which value should be displayed in the user interface:
		          valueFromScript or valueFromUser?
*/
		
		valueFromUser = valueFromScript; //prevent value changed event!!!

        //Moved to xml
		//controlLayout = new LinearLayout(context);
		//controlLayout.setOrientation(LinearLayout.VERTICAL);
		//controlLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		//controlLayout.setPadding(30, 5, 30, 5);

        controlLayout = new LinearLayout(context);
        controlLayout = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.template_control_layout, controlLayout, false);

        //DONE: Move this to xml
		//Control name
		//nameTextView = new TextView(context);
		//-- nameTextView.setBackgroundColor(Color.BLACK);
		//nameTextView.setTextColor(Color.BLACK);
		//nameTextView.setText(name.toUpperCase());
		//nameTextView.setTypeface(null, Typeface.BOLD);
		//controlLayout.addView(nameTextView);
		
		//DONE: Move this to xml
		//Control description
		//descriptionTextView = new TextView(context);
		//descriptionTextView.setPadding(15, 5, 0, 0);
		//-- descriptionTextView.setBackgroundColor(Color.BLACK);
		//descriptionTextView.setTextSize(nameTextView.getTextSize()*0.5f);
		//descriptionTextView.setTypeface(Typeface.defaultFromStyle(Typeface.ITALIC), Typeface.ITALIC);
		//descriptionTextView.setTextColor(Color.BLACK);
		//descriptionTextView.setText(description);
		//controlLayout.addView(descriptionTextView);

        //Control Name
        TextView nameTextView = (TextView) LayoutInflater.from(context).inflate(R.layout.template_textname, controlLayout, false);
        nameTextView.setText(name.toUpperCase());
        controlLayout.addView(nameTextView);

        //Control description
        TextView descriptionTextView = (TextView) LayoutInflater.from(context).inflate(R.layout.template_textdesc, controlLayout, false);
        descriptionTextView.setText(description);
        controlLayout.addView(descriptionTextView);

		createInternal();

        //Panel Separator
        TextView panelSeparatorTextView = (TextView) LayoutInflater.from(context).inflate(R.layout.template_panel_separator, controlLayout, false);
        descriptionTextView.setText(description);
        controlLayout.addView(panelSeparatorTextView);

        // Moved to xml
        //TextView paneSeparatorBlank = new TextView(context);
        //paneSeparatorBlank.setHeight(5);
        //--paneSeparatorBlank.setBackgroundColor(Color.BLACK);
        //paneSeparatorBlank.setText("");
        //controlLayout.addView(paneSeparatorBlank);

        //TextView paneSeparatorLine = new TextView(context);
        //paneSeparatorLine.setHeight(2);
        //paneSeparatorLine.setBackgroundColor(Color.DKGRAY);
        //paneSeparatorLine.setText("");
        //controlLayout.addView(paneSeparatorLine);

        //TextView paneSeparatorBlankAfterLine = new TextView(context);
        //paneSeparatorBlankAfterLine.setHeight(10);
        //--paneSeparatorBlank.setBackgroundColor(Color.BLACK);
        //paneSeparatorBlankAfterLine.setText("");
        //controlLayout.addView(paneSeparatorBlankAfterLine);

		view = controlLayout;
	}
	
	abstract protected void createInternal(); 	//sets the view
	
	abstract protected void applyScriptValueToUserInterface();	//clear user input, set it back to the script value
	
	abstract protected String getDefaultValue();
		
}
