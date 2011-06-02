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

public class Directory {
    private static DirectoryCategory[] mCategories;

    public static void initializeDirectory() {
      mCategories = new DirectoryCategory[] {
                new DirectoryCategory("Balloons", new DirectoryEntry[] {
                        new DirectoryEntry("Red Balloon", R.drawable.red_balloon),
                        new DirectoryEntry("Green Balloon", R.drawable.green_balloon),
                        new DirectoryEntry("Blue Balloon", R.drawable.blue_balloon)}),
                new DirectoryCategory("Bikes", new DirectoryEntry[] {
                        new DirectoryEntry("Old school huffy", R.drawable.blue_bike),
                        new DirectoryEntry("New Bikes", R.drawable.rainbow_bike),
                        new DirectoryEntry("Chrome Fast", R.drawable.chrome_wheel)}),
                new DirectoryCategory("Androids", new DirectoryEntry[] {
                        new DirectoryEntry("Steampunk Android", R.drawable.punk_droid),
                        new DirectoryEntry("Stargazing Android", R.drawable.stargazer_droid),
                        new DirectoryEntry("Big Android", R.drawable.big_droid) }),
                new DirectoryCategory("Pastries", new DirectoryEntry[] {
                        new DirectoryEntry("Cupcake", R.drawable.cupcake),
                        new DirectoryEntry("Donut", R.drawable.donut),
                        new DirectoryEntry("Eclair", R.drawable.eclair),
                        new DirectoryEntry("Froyo", R.drawable.froyo), }), };

    }

    public static int getCategoryCount() {
        return mCategories.length;
    }

    public static DirectoryCategory getCategory(int i) {
        return mCategories[i];
    }
}
