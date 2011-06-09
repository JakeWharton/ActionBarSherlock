/*
 * Copyright (C) 2011 The Android Open Source Project
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

package com.actionbarsherlock.sample.hcgallery;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;

public class DirectoryEntry {
    private String name;
    private int resID;

    public DirectoryEntry(String name, int resID) {
        this.name = name;
        this.resID = resID;
    }

    public String getName() {
        return name;
    }

    public Drawable getDrawable(Resources res) {
        return res.getDrawable(resID);
    }

    public Bitmap getBitmap(Resources res) {
        return BitmapFactory.decodeResource(res, resID);
    }
}
