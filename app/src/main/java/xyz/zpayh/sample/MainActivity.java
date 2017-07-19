package xyz.zpayh.sample;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.google.zxing.WriterException;

import xyz.zpayh.qrzxing.encode.QRCodeEncoder;

public class MainActivity extends AppCompatActivity {

    private ImageView mQRCodeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                encodeQR();
            }
        });

        mQRCodeView = (ImageView) findViewById(R.id.iv_qrcode);
    }

    private void encodeQR() {
        new AsyncTask<Void,Void,Bitmap>(){
            int width = mQRCodeView.getWidth();
            @Override
            protected Bitmap doInBackground(Void... voids) {
                try {

                    return QRCodeEncoder.encode("我喜欢钟婷娜呀",width);
                } catch (WriterException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                if (bitmap != null){
                    mQRCodeView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    mQRCodeView.setImageBitmap(bitmap);
                }
            }
        }.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
