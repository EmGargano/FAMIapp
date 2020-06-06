package it.emgargano.famiapp;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.google.zxing.WriterException;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import it.emgargano.famiapp.sms.prova.R;

public class PopUpQrcodeActivity extends AppCompatActivity {

    private static final String TAG = "PopUpQrcode"; //tag too long
    private Bitmap bitmap;
    private QRGEncoder qrgEncoder;
    private ImageView imageViewQr;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop_up_qrcode);
        imageViewQr = findViewById(R.id.imageViewQr);
        //GENERATE QR CODE FOR USER ID
        String uId = getIntent().getExtras().getString("userId");
        qrgEncoder = new QRGEncoder(uId, null, QRGContents.Type.TEXT, 700);
        try {
            // Getting QR-Code as Bitmap
            bitmap = qrgEncoder.encodeAsBitmap();
            // Setting Bitmap to ImageView
            imageViewQr.setImageBitmap(bitmap);
        } catch (WriterException e) {
            Log.v(TAG, e.toString());
        }
    }
}
