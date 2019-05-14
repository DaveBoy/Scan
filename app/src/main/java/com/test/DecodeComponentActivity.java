package com.test;

import com.honeywell.barcode.HSMDecodeComponent;
import com.honeywell.barcode.HSMDecodeResult;
import com.honeywell.barcode.HSMDecoder;
import com.honeywell.plugins.decode.DecodeResultListener;
import com.test.plugin.MyCustomPluginResultListener;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

public class DecodeComponentActivity extends Activity implements DecodeResultListener, MyCustomPluginResultListener
{
	private EditText editTextDisplay;
	private HSMDecoder hsmDecoder;
	private HSMDecodeComponent decCom;
	private int scanCount = 0;
	
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
    	super.onCreate(savedInstanceState);
    	
    	//stop the device from going to sleep and hide the title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE); 
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);  
        
    	setContentView(R.layout.mydecodeactivity);

		decCom = (HSMDecodeComponent)findViewById(R.id.hsm_decodeComponent);

    	editTextDisplay = (EditText)findViewById(R.id.editTextDisplay);
    	editTextDisplay.setEnabled(false);
    	editTextDisplay.setTextColor(Color.WHITE);
    	
    	//get the singleton instance to HSMDecoder
    	hsmDecoder = HSMDecoder.getInstance(this);
    }

    @Override
    public void onStart() 
    {
    	super.onStart();
    	
    	scanCount = 0;
    	
    	if( this.getIntent().getBooleanExtra("DEFAULT_ENABLED", false) )
    		hsmDecoder.addResultListener(this);
    	
    	if( this.getIntent().getBooleanExtra("CUSTOM_ENABLED", false) )
    		MainActivity.addCustomPluginListener(this);
    }
    
    @Override
    public void onStop() 
    {
    	super.onStop();
    	
    	//we need to remove this activity as a listener each time we stop it, because our main activity can't disable default decoding if there are any active listeners
    	hsmDecoder.removeResultListener(this);
    	MainActivity.removeCustomPluginListener(this);

		//dispose of the decode component
		decCom.dispose();
    }
    
	@Override
	public void onHSMDecodeResult(HSMDecodeResult[] barcodeData)
	{
		HSMDecodeResult firstResult = barcodeData[0];
		String msg = "Scan Count: " + ++scanCount + "\n\n" +
					 "onHSMDecodeResult\n" +
					 "Data: " + firstResult.getBarcodeData() + "\n" +
				     "Symbology: " + firstResult.getSymbology() + "\n" +
				     "Length: " + firstResult.getBarcodeDataLength()  + "\n" +
				     "Decode Time: " + firstResult.getDecodeTime() + "ms";
    	editTextDisplay.setText(msg);
	}

	@Override
	public void onCustomPluginResult(HSMDecodeResult[] barcodeData) 
	{
		HSMDecodeResult firstResult = barcodeData[0];
		String msg = "Scan Count: " + ++scanCount + "\n\n" +
					 "onMyCustomPluginResult\n" +
					 "Data: " + firstResult.getBarcodeData() + "\n" +
					 "Symbology: " + firstResult.getSymbology() + "\n" +
					 "Length: " + firstResult.getBarcodeDataLength()  + "\n" +
					 "Decode Time: " + firstResult.getDecodeTime() + "ms";
		editTextDisplay.setText(msg);
	}
}