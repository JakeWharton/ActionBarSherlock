#!/usr/bin/env python
#
#Quick and dirty python script to initialize dependencies for the samples.

dependencies = [
    {
        'name': 'android_actionbar',
        'user': 'johannilsson',
        'repo': 'android-actionbar',
        'sha' : '9df99aa71e228b00be6aecb634f94d3d0744dc9d',
        'path': 'actionbar',
    },
    {
        'name': 'greendroid',
        'user': 'hameno',
        'repo': 'GreenDroid',
        'sha' : 'ec0f8a08c0537bee49d1f1f9a178307344b5cc47',
        'path': 'GreenDroid',
    },
]

classpath = '''<?xml version="1.0" encoding="UTF-8"?>
<classpath>
    <classpathentry kind="src" path="src"/>
    <classpathentry kind="src" path="gen"/>
    <classpathentry kind="con" path="com.android.ide.eclipse.adt.ANDROID_FRAMEWORK"/>
    <classpathentry kind="output" path="bin"/>
</classpath>
'''

project = '''<?xml version="1.0" encoding="UTF-8"?>
<projectDescription>
    <name>android-actionbarsherlock-vendor-%s</name>
    <comment></comment>
    <projects></projects>
    <buildSpec>
        <buildCommand>
            <name>com.android.ide.eclipse.adt.ResourceManagerBuilder</name>
            <arguments></arguments>
        </buildCommand>
        <buildCommand>
            <name>com.android.ide.eclipse.adt.PreCompilerBuilder</name>
            <arguments></arguments>
        </buildCommand>
        <buildCommand>
            <name>org.eclipse.jdt.core.javabuilder</name>
            <arguments></arguments>
        </buildCommand>
        <buildCommand>
            <name>com.android.ide.eclipse.adt.ApkBuilder</name>
            <arguments></arguments>
        </buildCommand>
    </buildSpec>
    <natures>
        <nature>com.android.ide.eclipse.adt.AndroidNature</nature>
        <nature>org.eclipse.jdt.core.javanature</nature>
    </natures>
</projectDescription>
'''

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
    subprocess.call(['git', 'clone', 'git://github.com/%(user)s/%(repo)s.git' % dependency, dependency['name']], cwd=vendor_dir)
    #Checkout the desired version
    subprocess.call(['git', 'checkout', dependency['sha']], cwd=repo_dir)

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
    subprocess.call([
        'mvn', 'install:install-file',
        '-Dfile=' + zip_name,
        '-DgroupId=com.github', '-DartifactId=' + dependency['user'] + '-' + dependency['name'],
        '-Dversion=0.0.0-' + dependency['sha'][:7],
        '-Dpackaging=apklib',
    ], cwd=vendor_dir)

    #Remove .apklib file
    os.remove(zip_file_name)

    #Create .classpath and .project files
    with open(os.path.join(zip_dir, '.classpath'), 'w') as f:
        f.write(classpath)
    with open(os.path.join(zip_dir, '.project'), 'w') as f:
        f.write(project % dependency['name'])
