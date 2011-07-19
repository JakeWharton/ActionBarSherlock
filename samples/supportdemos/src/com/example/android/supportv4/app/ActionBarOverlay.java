/*
 * Copyright (C) 2011 The Android Open Source Project
 * Copyright (C) 2011 Jake Wharton
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.supportv4.app;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.Window;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import com.example.android.supportv4.R;

public class ActionBarOverlay extends FragmentActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actionbar_overlay);
        
        GridView images = (GridView)findViewById(R.id.images);
        images.setAdapter(new ImageAdapter(this));
    }
    
    private static class ImageAdapter extends BaseAdapter {
    	private Context mContext;
    	
    	public ImageAdapter(Context context) {
    		mContext = context;
    	}

		@Override
		public int getCount() {
			return POSTERS.length;
		}

		@Override
		public Object getItem(int arg0) {
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			ImageView imageView;
			if (arg1 == null) {
				imageView = new ImageView(mContext);
				imageView.setLayoutParams(new GridView.LayoutParams(GridView.LayoutParams.FILL_PARENT, GridView.LayoutParams.FILL_PARENT));
				imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
			} else {
				imageView = (ImageView)arg1;
			}
			
			imageView.setImageResource(POSTERS[arg0]);
			return imageView;
		}
    }
    
    private static final int[] POSTERS = new int[] {
    	R.drawable.poster01,
    	R.drawable.poster02,
    	R.drawable.poster03,
    	R.drawable.poster04,
    	R.drawable.poster05,
    	R.drawable.poster06,
    	R.drawable.poster07,
    	R.drawable.poster08,
    	R.drawable.poster09,
    	R.drawable.poster10,
    	R.drawable.poster11,
    	R.drawable.poster12,
    	R.drawable.poster13,
    	R.drawable.poster14,
    	R.drawable.poster15,
    	R.drawable.poster16,
    	R.drawable.poster17,
    	R.drawable.poster18,
    	R.drawable.poster19,
    	R.drawable.poster20,
    	R.drawable.poster21,
    	R.drawable.poster22,
    	R.drawable.poster23,
    	R.drawable.poster24,
    	R.drawable.poster25,
    	R.drawable.poster26,
    	R.drawable.poster27,
    	R.drawable.poster28,
    	R.drawable.poster29,
    	R.drawable.poster30,
    	R.drawable.poster31,
    	R.drawable.poster32,
    	R.drawable.poster33,
    	R.drawable.poster34,
    	R.drawable.poster35,
    	R.drawable.poster36,
    	R.drawable.poster37,
    	R.drawable.poster38,
    	R.drawable.poster39,
    	R.drawable.poster40,
    	R.drawable.poster41,
    	R.drawable.poster42,
    	R.drawable.poster43,
    	R.drawable.poster44,
    	R.drawable.poster45,
    	R.drawable.poster01,
    	R.drawable.poster02,
    	R.drawable.poster03,
    	R.drawable.poster04,
    	R.drawable.poster05,
    	R.drawable.poster06,
    	R.drawable.poster07,
    	R.drawable.poster08,
    	R.drawable.poster09,
    	R.drawable.poster10,
    	R.drawable.poster11,
    	R.drawable.poster12,
    	R.drawable.poster13,
    	R.drawable.poster14,
    	R.drawable.poster15,
    	R.drawable.poster16,
    	R.drawable.poster17,
    	R.drawable.poster18,
    	R.drawable.poster19,
    	R.drawable.poster20,
    	R.drawable.poster21,
    	R.drawable.poster22,
    	R.drawable.poster23,
    	R.drawable.poster24,
    	R.drawable.poster25,
    	R.drawable.poster26,
    	R.drawable.poster27,
    	R.drawable.poster28,
    	R.drawable.poster29,
    	R.drawable.poster30,
    	R.drawable.poster31,
    	R.drawable.poster32,
    	R.drawable.poster33,
    	R.drawable.poster34,
    	R.drawable.poster35,
    	R.drawable.poster36,
    	R.drawable.poster37,
    	R.drawable.poster38,
    	R.drawable.poster39,
    	R.drawable.poster40,
    	R.drawable.poster41,
    	R.drawable.poster42,
    	R.drawable.poster43,
    	R.drawable.poster44,
    	R.drawable.poster45,
    	R.drawable.poster01,
    	R.drawable.poster02,
    	R.drawable.poster03,
    	R.drawable.poster04,
    	R.drawable.poster05,
    	R.drawable.poster06,
    	R.drawable.poster07,
    	R.drawable.poster08,
    	R.drawable.poster09,
    	R.drawable.poster10,
    	R.drawable.poster11,
    	R.drawable.poster12,
    	R.drawable.poster13,
    	R.drawable.poster14,
    	R.drawable.poster15,
    	R.drawable.poster16,
    	R.drawable.poster17,
    	R.drawable.poster18,
    	R.drawable.poster19,
    	R.drawable.poster20,
    	R.drawable.poster21,
    	R.drawable.poster22,
    	R.drawable.poster23,
    	R.drawable.poster24,
    	R.drawable.poster25,
    	R.drawable.poster26,
    	R.drawable.poster27,
    	R.drawable.poster28,
    	R.drawable.poster29,
    	R.drawable.poster30,
    	R.drawable.poster31,
    	R.drawable.poster32,
    	R.drawable.poster33,
    	R.drawable.poster34,
    	R.drawable.poster35,
    	R.drawable.poster36,
    	R.drawable.poster37,
    	R.drawable.poster38,
    	R.drawable.poster39,
    	R.drawable.poster40,
    	R.drawable.poster41,
    	R.drawable.poster42,
    	R.drawable.poster43,
    	R.drawable.poster44,
    	R.drawable.poster45,
    };
}
