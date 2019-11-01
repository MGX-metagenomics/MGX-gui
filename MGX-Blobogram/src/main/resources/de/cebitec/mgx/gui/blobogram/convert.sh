#!/bin/bash

rsvg-convert -w 24 -h 24 -o blobogram24.png blobogram.svg 
rsvg-convert -w 32 -h 32 -o blobogram32.png blobogram.svg 
rsvg-convert -w 48 -h 48 -o blobogram48.png blobogram.svg 
rsvg-convert -w 16 -h 16 -o blobogram.png blobogram.svg 

