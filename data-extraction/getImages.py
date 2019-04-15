import glob, os, re

from subprocess import call

# get pdf files
files = glob.glob(os.getcwd() + '/**/*.pdf', recursive=True)

files = sorted(files)

for file in files:
    print(file)

    # extract images
    call(["pdftoppm", file, file, "-png"])

    # delete pdf files
    call(["rm", file])

# get converted images
files = glob.glob(os.getcwd() + '/**/*.pdf-1.png', recursive=True)

files = sorted(files)

for file in files:
    print(file)

    # rename files
    newFilename = file[:-10] +".png"
    call(["mv", file, newFilename])
