# 3/12/15
## Implementation of ECI to LLA in Java
This will be done with the help of a recursive function.
The source is [here](http://de.mathworks.com/help/aeroblks/ecefpositiontolla.html).    
The other way is to use what Orekit is capable of. It can transform different coordinates to each other using the transform function that comes with the body. As of the Orekit documentation, it only offers one kind of body which is  *OneAxisEllipsoid.* Starts and planets can be simulated using this abstract method.
The reference frame to be used here is the ITRF (Inertial Terrestrial reference Frame) which resembles the ECI.


## List of Important Pages:
* [MatlabControl](http://matlabcontrol.googlecode.com/svn-history/r503/javadocs/doc/matlabcontrol/MatlabOperations.html)
* [Orekit Index](https://www.orekit.org/static/apidocs/index-all.html)
* [Orekit Docs](https://www.orekit.org/forge/projects/orekit/wiki/)
