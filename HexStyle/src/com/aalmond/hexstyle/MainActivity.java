package com.aalmond.hexstyle;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends ActionBarActivity
{
	/*
	 * TODO:
	 * - Settings to have foreground/background only
	 * - Settings to change font size, bold, underline, italic
	 * - Export to CSS
	 * - Test internationalization
	 */
	
	private AlertDialog levelDialog;
	
	// TODO Have this read SeekBar.getMax();
	private final int MAX_RAW = 100;
	private static final Typeface[] fonts = {Typeface.SANS_SERIF,
		Typeface.SERIF, Typeface.MONOSPACE};
	
	/**
	 * Progress bar values for each color.
	 */
	private int redRaw = 0, greenRaw = 0, blueRaw = 0,
			redBackRaw = MAX_RAW, greenBackRaw = MAX_RAW, blueBackRaw = MAX_RAW;
	private String outputText;
	private int selectedFontIndex = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		if (savedInstanceState == null)
		{
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
	}
	
	@Override
	protected void onPostCreate(Bundle savedInstanceState)
	{
		super.onPostCreate(savedInstanceState);
		
		try {
			
			SeekBar redBar = (SeekBar) findViewById(R.id.seekbar_red);
			SeekBar greenBar = (SeekBar) findViewById(R.id.seekbar_green);
			SeekBar blueBar = (SeekBar) findViewById(R.id.seekbar_blue);
			SeekBar redBackBar = (SeekBar) findViewById(R.id.seekbar_red_back);
			SeekBar greenBackBar = (SeekBar) findViewById(R.id.seekbar_green_back);
			SeekBar blueBackBar = (SeekBar) findViewById(R.id.seekbar_blue_back);
			
			// Set progress bars before attaching listeners.
			redBar.setProgress(redRaw);
			greenBar.setProgress(greenRaw);
			blueBar.setProgress(blueRaw);
			redBackBar.setProgress(redBackRaw);
			greenBackBar.setProgress(greenBackRaw);
			blueBackBar.setProgress(blueBackRaw);
			
			redBar.setOnSeekBarChangeListener(new ColorChangeListener("red"));
			greenBar.setOnSeekBarChangeListener(new ColorChangeListener("green"));
			blueBar.setOnSeekBarChangeListener(new ColorChangeListener("blue"));
			redBackBar.setOnSeekBarChangeListener(new ColorChangeListener("red (background)"));
			greenBackBar.setOnSeekBarChangeListener(new ColorChangeListener("green (background)"));
			blueBackBar.setOnSeekBarChangeListener(new ColorChangeListener("blue (background)"));
			Log.d("onCreate", "Added listeners to SeekBars");
			
			if (outputText == null)
				outputText = getString(R.string.output);
			
			updateAll();
		}
		catch (NullPointerException e)
		{
			Log.w("onCreate", "SeekBars were null");
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		int id = item.getItemId();
		if (id == R.id.action_change_text)
		{
			// Get user input			
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(getString(R.string.action_change_text));
			final EditText outputTextField = new EditText(this);
			outputTextField.setInputType(InputType.TYPE_CLASS_TEXT);
			builder.setView(outputTextField);
			
			builder.setPositiveButton("OK", new DialogInterface.OnClickListener() { 
			    @Override
			    public void onClick(DialogInterface dialog, int which) {
			    	outputText = outputTextField.getText().toString();
			        updateText();
			    }
			});
			builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			    @Override
			    public void onClick(DialogInterface dialog, int which) {
			        dialog.cancel();
			    }
			});

			builder.show();
			return true;
		}
		else if (id == R.id.action_change_font)
		{
			final int[] fontNameIds = {R.string.fontname_sans_serif,
					R.string.fontname_serif, R.string.fontname_monospace};
			final CharSequence[] fontNames = new CharSequence[fontNameIds.length];
			for (int i = 0; i < fontNameIds.length; i++)
				fontNames[i] = getString(fontNameIds[i]);
			
			// Get user input
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Select The Difficulty Level");
            builder.setSingleChoiceItems(fontNames, -1, new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int item) {
	                if (item < fonts.length)
	                {
	                	selectedFontIndex = item;
	                	updateFont();
	                }
	                levelDialog.dismiss();    
	            }
            });
            levelDialog = builder.create();
            levelDialog.show();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState)
	{
		super.onSaveInstanceState(savedInstanceState);
		
		try
		{			
			savedInstanceState.putInt("redRaw", redRaw);
			savedInstanceState.putInt("greenRaw", greenRaw);
			savedInstanceState.putInt("blueRaw", blueRaw);
			savedInstanceState.putInt("redBackRaw", redBackRaw);
			savedInstanceState.putInt("greenBackRaw", greenBackRaw);
			savedInstanceState.putInt("blueBackRaw", blueBackRaw);
			
			savedInstanceState.putString("outputText", outputText);
			savedInstanceState.putInt("selectedFontIndex", selectedFontIndex);
		}
		catch (NullPointerException e)
		{
			// Do nothing.
		}
	}
	
	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
	  super.onRestoreInstanceState(savedInstanceState);
	  
	  redRaw = savedInstanceState.getInt("redRaw");
	  greenRaw = savedInstanceState.getInt("greenRaw");
	  blueRaw = savedInstanceState.getInt("blueRaw");
	  redBackRaw = savedInstanceState.getInt("redBackRaw");
	  greenBackRaw = savedInstanceState.getInt("greenBackRaw");
	  blueBackRaw = savedInstanceState.getInt("blueBackRaw");
	  
	  outputText = savedInstanceState.getString("outputText");
	  selectedFontIndex = savedInstanceState.getInt("selectedFontIndex");
	}
	
	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment
	{
		
		public PlaceholderFragment()
		{
		}
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState)
		{
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			return rootView;
		}
	}
	
	private class ColorChangeListener implements SeekBar.OnSeekBarChangeListener {

		private String colorName;
		
		public ColorChangeListener(String colorName)
		{
			this.colorName = colorName;
		}
		
        public void onProgressChanged(SeekBar seekBar, int progress,
                boolean fromUser) {
        	Log.d(colorName, String.format("Progres is %d/100", progress));
        	if (fromUser)
        	{
	            updateAll();
        	}
        }

        public void onStartTrackingTouch(SeekBar seekBar) {}

        public void onStopTrackingTouch(SeekBar seekBar) {}

    }
	
	/**
	 * Gets the percentage of the seekbar for a specified seekbar
	 * @param seekBar
	 * @return 0.00 (0%) to 1.00 (100%)
	 */
	private double getColorPct(SeekBar seekBar)
	{
		return (double) seekBar.getProgress() / seekBar.getMax();
	}
	
	/**
	 * Gets the color value of a seek bar from 0x00 to 0xff.
	 * @param seekBar
	 * @return
	 */
	private int getHex(SeekBar seekBar)
	{
		int hexMax = 0xff;
		return (int)(getColorPct(seekBar) * hexMax);
	}
	
	/**
	 * Converts RGB integer values into a hex code, of the format
	 * @param red
	 * @param green
	 * @param blue
	 * @return
	 */
	private String intToHexCode(int red, int green, int blue)
	{
		return String.format("#%02X%02X%02X", red, green, blue);
	}
	
	/**
	 * Updates everything based on the prior state and moves the seek bars accordingly.
	 */
	public void updateAll()
	{
		TextView outputTextView = (TextView) findViewById(R.id.output_textview);
		TextView foregroundCodeView = (TextView) findViewById(R.id.foreground_hex);
		TextView backgroundCodeView = (TextView) findViewById(R.id.background_hex);
		
		SeekBar redBar = (SeekBar) findViewById(R.id.seekbar_red);
		SeekBar greenBar = (SeekBar) findViewById(R.id.seekbar_green);
		SeekBar blueBar = (SeekBar) findViewById(R.id.seekbar_blue);
		SeekBar redBackBar = (SeekBar) findViewById(R.id.seekbar_red_back);
		SeekBar greenBackBar = (SeekBar) findViewById(R.id.seekbar_green_back);
		SeekBar blueBackBar = (SeekBar) findViewById(R.id.seekbar_blue_back);
		
		redRaw = redBar.getProgress();
		greenRaw = greenBar.getProgress();
		blueRaw = blueBar.getProgress();
		redBackRaw = redBackBar.getProgress();
		greenBackRaw = greenBackBar.getProgress();
		blueBackRaw = blueBackBar.getProgress();
		
		//Log.d("redRaw/greenRaw/blueRaw", String.format("%d/%d/%d", redRaw, greenRaw, blueRaw));
		//Log.d("redBackRaw/greenBackRaw/blueBackRaw", String.format("%d/%d/%d", redBackRaw, greenBackRaw, blueBackRaw));
		
		int red = getHex(redBar);
		int green = getHex(greenBar);
		int blue = getHex(blueBar);
		int redBack = getHex(redBackBar);
		int greenBack = getHex(greenBackBar);
		int blueBack = getHex(blueBackBar);
		
		int redPct = (int)(getColorPct(redBar) * 100);
		int greenPct = (int)(getColorPct(greenBar) * 100);
		int bluePct = (int)(getColorPct(blueBar) * 100);
		int redBackPct = (int)(getColorPct(redBackBar) * 100);
		int greenBackPct = (int)(getColorPct(greenBackBar) * 100);
		int blueBackPct = (int)(getColorPct(blueBackBar) * 100);
		
		//Log.d("red", String.valueOf(red));
		//Log.d("green", String.valueOf(green));
		//Log.d("blue", String.valueOf(blue));
		//Log.d("redBack", String.valueOf(redBack));
		//Log.d("greenBack", String.valueOf(greenBack));
		//Log.d("blueBack", String.valueOf(blueBack));
		
		outputTextView.setTextColor(Color.rgb(red, green, blue));
		outputTextView.setBackgroundColor(Color.rgb(redBack, greenBack, blueBack));
		
		// Set percentages
		TextView redPctView = (TextView) findViewById(R.id.ptFRed);
		TextView greenPctView = (TextView) findViewById(R.id.ptFGreen);
		TextView bluePctView = (TextView) findViewById(R.id.ptFBlue);
		TextView redBackPctView = (TextView) findViewById(R.id.ptBRed);
		TextView greenBackPctView = (TextView) findViewById(R.id.ptBGreen);
		TextView blueBackPctView = (TextView) findViewById(R.id.ptBBlue);
		
		redPctView.setText(String.format("%3d%%", redPct));
		greenPctView.setText(String.format("%3d%%", greenPct));
		bluePctView.setText(String.format("%3d%%", bluePct));
		redBackPctView.setText(String.format("%3d%%", redBackPct));
		greenBackPctView.setText(String.format("%3d%%", greenBackPct));
		blueBackPctView.setText(String.format("%3d%%", blueBackPct));
		
		// Set hex display values
		String foregroundCode = intToHexCode(red, green, blue);
		String backgroundCode = intToHexCode(redBack, greenBack, blueBack);
		
		foregroundCodeView.setText(foregroundCode);
		backgroundCodeView.setText(backgroundCode);
		
		updateText();
		updateFont();
	}

	public void updateText()
	{
		TextView outputTextView = (TextView) findViewById(R.id.output_textview);
		outputTextView.setText(outputText);
	}
	
	public void updateFont()
	{
		TextView outputTextView = (TextView) findViewById(R.id.output_textview);
		outputTextView.setTypeface(fonts[selectedFontIndex]);
	}
}
