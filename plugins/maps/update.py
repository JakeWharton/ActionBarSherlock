#!/usr/bin/env python

import os
import re

dir_maps_base = os.path.dirname(os.path.realpath(__file__))
dir_repo_base = os.path.dirname(os.path.dirname(dir_maps_base))
dir_lib_base  = os.path.join(dir_repo_base, 'library')

path_to_source_activity = 'src/android/support/v4/app/FragmentActivity.java'.split('/')
path_to_dest_activity   = 'src/android/support/v4/app/FragmentMapActivity.java'.split('/')

file_source = os.path.join(dir_lib_base , *path_to_source_activity)
file_dest   = os.path.join(dir_maps_base, *path_to_dest_activity)

# Read in entire source file
code = None
with open(file_source) as f:
  code = f.read()


code = code.split('\n')

# Add MapActivity import
code.insert(31, 'import com.google.android.maps.MapActivity;')

code = '\n'.join(code)

# Class declaration
code = code.replace('class FragmentActivity extends Activity', 'abstract class FragmentMapActivity extends MapActivity')

# TAG variable content
code = code.replace('"FragmentActivity"', '"FragmentMapActivity"')

# Update inner class references
code = code.replace('FragmentActivity.this', 'FragmentMapActivity.this')

# Class constructor
code = code.replace('FragmentActivity()', 'FragmentMapActivity()')

# Javadoc
code = code.replace('Fragment, and Loader APIs.', 'Fragment, Loader, and Google Map APIs.')


# Exit stage left
with open(file_dest, 'w') as f:
  f.write(code)
