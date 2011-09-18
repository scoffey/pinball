Pinball
=======

This project was an assignment of the Computer Graphics course at [ITBA] [1] in 2008. See copyright notes at the bottom.

It features a [**pinball simulation game**] [2] based in Java libraries, namely jMonkeyEngine, the Lightweight Java Game Library, Java3D and JOGL, among others.

See instructions below on how to run.

  [1]: http://www.itba.edu.ar
  [2]: http://en.wikipedia.org/wiki/Pinball

Features
---------

  - Traditional arcade game where a ball is manipulated on an inclined playfield
  - Plunger that can launch the ball with different impulse (press the space bar to pull)
  - Flippers (press `,` and `.` to flip)
  - Ability to hit ground, glass cover, walls and other objects with the ball
  - Bumpers that push the ball away with greater momentum
  - Ramps
  - Magnets that divert ball path
  - One-way paths
  - Tilt (press tab)
  - Score and "lives" display
  - Frames per second (FPS) counter
  - Ability to move camera in the 6 directions of 3D space (press WASDQZ keys)
  - Ability to select 3 different camera angles (press 8 to change)
  - Configurable screen width, height and color depth via pinball.properties file
  - Configurable scene via pinball.x3d file; e.g.: playfield inclination angle
  - Default scene contains pinball machine, floor, walls and ceiling

File contents
-------------

  - `build`: Contains pinball.jar, optional pinball.properties and run scripts
  - `build.xml`: Apache Ant build file
  - `COPYING`: GNU General Public License
  - `lib`: Required external libraries
  - `README.markdown`: This README file
  - `resources`: Scene file (pinball.x3d), images (jpg) and sounds (wav)
  - `src`: Source code, fully written in Java

Program usage
-------------

On Linux: (see `pinball.sh` script)

    java -Djava.library.path=lib/lwjgl/native/linux:lib -jar build/pinball.jar resources/pinball.x3d

On Windows: (see `pinball.bat` script)

    java -Djava.library.path=lib/lwjgl/native/win32:lib -jar build/pinball.jar resources/pinball.x3d

On Mac OS X:

    java -Djava.library.path=lib/lwjgl/native/macosx:lib -jar build/pinball.jar resources/pinball.x3d

Copyright
---------

    Copyright (c) 2008
     - Rafael Martín Bigio <rbigio@itba.edu.ar>
     - Santiago Andrés Coffey <scoffey@itba.edu.ar>
     - Andrés Santiago Gregoire <agregoir@itba.edu.ar>

    The following additional provisions apply to third party software
    included as part of this product:
     - Java3D library: Copyright (c) 1996-2008 Sun Microsystems, Inc.
       Licensed under the GNU General Public License (GPL), version 2,
       with the CLASSPATH exception. See <http://java3d.java.net/>
     - XJ3D library: Copyright (c) 2001-2007 Web3D Consortium.
       Licensed under the GNU LGPL v2.1. See <http://www.xj3d.org/>
     - jMonkeyEngine library: Copyright (c) 2003-2010 jMonkeyEngine.
       Licensed under the BSD license. See <http://jmonkeyengine.org/>
     - LWJGL library: Copyright (c) 2002-2007 LWJGL Project.
       Licensed under the BSD license. See <http://www.lwjgl.org/>
     - JOGL library: Copyright (c) 2003-2009 Sun Microsystems, Inc.
       Licensed under the BSD license. See <http://java.net/projects/jogl/>
     - JUnit library: Copyright (c) Kent Beck et al.
       Licensed under the Common Public License. See <http://junit.org/>
     - JOrbis library: Copyright (c) 2000 JCraft, Inc.
       Licensed under the GNU LGPL. See <http://www.jcraft.com/jorbis/>

    Pinball is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Pinball is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Pinball.  If not, see <http://www.gnu.org/licenses/>.

