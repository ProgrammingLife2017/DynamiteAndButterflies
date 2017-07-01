[![Build Status](https://travis-ci.org/ProgrammingLife2017/DynamiteAndButterflies.svg?branch=master)](https://travis-ci.org/ProgrammingLife2017/DynamiteAndButterflies)
![Logo](http://imgur.com/cpfQEav.png)
# DynamiteAndButterflies
This application was built by the team Dynamite and Butterflies. The core usability of the programme revolves around parsing and visualising .gfa file. These files represent genomes that have been compared in a graph structure.
 
 Besides this the application can parse and visualize annotations on this graph structure in the form of annotations that can be loaded in with a .gff file.

## Team members
| Jasper van Tilburg                        | Lex Boleij                             | Eric Dammeijer                          | Marc Visser                             | Jip Rietveld                           |
|-------------------------------------------|----------------------------------------|-----------------------------------------|-----------------------------------------|----------------------------------------|
| ![jasper](http://i.imgur.com/xHGjfa3.jpg) | ![Lex](http://i.imgur.com/s8z6wXz.jpg) | ![Eric](http://i.imgur.com/N381Hu6.jpg) | ![marc](http://i.imgur.com/3Y9fqJA.jpg) | ![jip](http://i.imgur.com/W3MpLr7.jpg) |

## Preview
![main screen](http://imgur.com/hWiM9Cs.png)
![human annotations](http://imgur.com/rOnD247.png)
![TB328 layout](http://imgur.com/jfL6yYS.png)

## Before you start
When opening the current release for the first time, there might be some problems with the database files already on your computer. To fix this, please delete those files once (they can be found in the folder that also holds you .jar file).

## List of Known issues
* Sometimes, when clicking on the minimap, the application zooms all the way in.
* Some annotations can't be reached due to a stackOverFlow due to the complexity of some graphs (is caught by a pop-up).
* Saving a selection after text filter only save those in view. It does not keep track of the rest of your selection.
* When panning the edges can jump a little in a odd manner.
* Viewing a lot (200+) genomes in rainbow view causes panning and zooming to lag.
