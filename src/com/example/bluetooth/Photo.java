package com.example.bluetooth;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class Photo extends Activity {
	
	private ImageView img;
	public int REQUEST_CODE = 0;
	public static Bitmap bp; 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_photo);
		img = (ImageView)findViewById(R.id.imageView1);
		this.open();	
	}

	public void open() {
		Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
		startActivityForResult(intent, REQUEST_CODE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		bp = (Bitmap) data.getExtras().get("data");
		ViewWay VW = new ViewWay(this);
		img.setImageBitmap(bp);
	}
	
	private static class ViewWay extends View{
		private Bitmap mBitmap;  
        private Bitmap mBitmap2;  
        private Bitmap mBitmap3;  
        private Bitmap mBitmap4;  
		public ViewWay(Context ctx){
			super(ctx);
			setFocusable(true);  
			  
          
        
            BitmapFactory.Options opts = new BitmapFactory.Options();  
            opts.inJustDecodeBounds = true;  
       
            opts.inJustDecodeBounds = false;      
            opts.inSampleSize = 4;               
 
            mBitmap = bp;  //通过配置参数解码生成Bitmap  
  
            mBitmap2 = bp;
  
            int w = mBitmap2.getWidth();  
            int h = mBitmap2.getHeight();  
            int[] pixels = new int[w*h];  
            mBitmap2.getPixels(pixels, 0, w, 0, 0, w, h);  
            mBitmap3 = Bitmap.createBitmap(pixels, 0, w, w, h,  
                                           Bitmap.Config.ARGB_8888);  
            //通过缓冲区数据构造Bitmap  
            mBitmap4 = Bitmap.createBitmap(pixels, 0, w, w, h,                                           Bitmap.Config.ARGB_4444);  
  
        }  
  
        @Override  
        protected void onDraw(Canvas canvas) {  
            canvas.drawColor(0xFFCCCCCC);  
            Paint p = new Paint();  
            p.setAntiAlias(true); //设置防锯齿  
            canvas.drawBitmap(mBitmap, 10, 10, null);  
            canvas.drawBitmap(mBitmap2, 10, 170, null);  
            canvas.drawBitmap(mBitmap3, 110, 170, null);  
            canvas.drawBitmap(mBitmap4, 210, 170, null); //通过drawBitmap绘制图  
                
        }  
		}
		
}  
	


