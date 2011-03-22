#!/usr/bin/env python
#
#Quick and dirty python script to initialize dependencies for the samples.

dependencies = [
    {
        'name': 'android-actionbar',
        'user': 'johannilsson',
        'repo': 'android-actionbar',
        'sha' : '9df99aa71e228b00be6aecb634f94d3d0744dc9d',
        'path': 'actionbar',
    },
    {
        'name': 'greendroid',
        'user': 'hameno',
        'repo': 'GreenDroid',
        'sha' : '63eae89e17524dba33328685ca65fb19e05bf715',
        'path': 'GreenDroid',
    },
]

import shutil
import os
import subprocess
import zipfile

vendor_dir = os.path.join(os.path.dirname(os.path.abspath(__file__)), 'vendor')
if os.path.exists(vendor_dir):
    shutil.rmtree(vendor_dir)
os.mkdir(vendor_dir)

for dependency in dependencies:
    repo_dir = os.path.join(vendor_dir, dependency['name'])
    zip_dir = os.path.join(repo_dir, dependency['path']) if dependency['path'] is not None else repo_dir
    zip_name = dependency['name'] + '.apklib'
    zip_file_name = os.path.join(vendor_dir, zip_name)

    #Clone the repository
    subprocess.Popen(['git', 'clone', 'git://github.com/%s/%s.git' % (dependency['user'], dependency['repo']), dependency['name']], cwd=vendor_dir).wait()
    #Checkout the desired version
    subprocess.Popen(['git', 'checkout', dependency['sha']], cwd=repo_dir).wait()
    
    #Zip target path
    zip_file = zipfile.ZipFile(zip_file_name, 'w', compression=zipfile.ZIP_DEFLATED)
    cwd = os.getcwd()
    os.chdir(zip_dir)
    for root, dirs, files in os.walk('.'):
        for file_name in files:
            zip_file.write(os.path.join(root, file_name))
    zip_file.close()
    os.chdir(cwd)

    #Maven install
    subprocess.Popen([
        'mvn', 'install:install-file',
        '-Dfile=' + zip_name,
        '-DgroupId=com.github', '-DartifactId=' + dependency['user'] + '-' + dependency['name'],
        '-Dversion=0.0.0',
        '-Dpackaging=apklib',
    ], cwd=vendor_dir).wait()

    #Remove .apklib file
    os.remove(zip_file_name)
