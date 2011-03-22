#!/usr/bin/env python
#
#Quick and dirty python script to initialize dependencies for the samples.

dependencies = [
    {
        'name': 'android-actionbar',
        'user': 'johannilsson',
        'repo': 'android-actionbar',
        'sha' : '9df99aa71e228b00be6aecb634f94d3d0744dc9d',
    },
    {
        'name': 'greendroid',
        'user': 'hameno',
        'repo': 'GreenDroid',
        'sha' : 'b96aa400e31d386b7afd2dc6eee14ca42b2a22ae',
    },
]

import shutil
import os
import subprocess

vendor_dir = os.path.join(os.path.dirname(__file__), 'vendor')

if os.path.exists(vendor_dir):
    shutil.rmtree(vendor_dir)
os.mkdir(vendor_dir)

for dependency in dependencies:
    subprocess.Popen(['git', 'clone', 'git://github.com/%s/%s.git' % (dependency['user'], dependency['repo']), dependency['name']], cwd=vendor_dir).wait()
    repo_dir = os.path.join(vendor_dir, dependency['name'])
    subprocess.Popen(['git', 'checkout', dependency['sha']], cwd=repo_dir).wait()
