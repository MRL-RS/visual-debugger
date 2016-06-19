# visual-debugger
 This project provides a visual debugger for RoboCup Rescue Simulation System — 

To use Visual Debugger you should do following steps:

#### STEP 1: Start rescue simulation kernel
There are two approaches to run kernel and load a scenario:
##### approach #1:
Download roborescue kernel from [sourceforge](https://sourceforge.net/projects/roborescue/).

##### approach #2 (RECOMMENDED):
You can use control-panel project to start kernel and load a scenario. Find it [here](https://github.com/MRL-RS/control-panel/).

#### STEP 2: Starting Visual Debugger
Download latest visual debugger released jar file from [here](https://github.com/MRL-RS/visual-debugger/releases).Run it with following command:
```
java -jar {PATH_TO_JAR}/visual-debugger-1.0-jar-with-dependencies.jar -h "KERNEL_HOST" -p "KERNEL_PORT"
```
**Note:** Default kernel host address and port are 127.0.0.1 and 7000 respectively.

#### STEP 3: Pass data from agents to visual debugger
Download latest visual debugger client jar file from [here](https://github.com/MRL-RS/visual-debugger-client/releases), and include it as one of your agent simulation project dependencies.

Put following lines of code in the main class of your project( Where you connect your agents to Kernel):

```java
VDClient vdClient = VDClient.getInstance();
vdClient.init(VISUAL_DEBUGGER_HOST , VISUAL_DEBUGGER_PORT);
```
**Note:** You may want to use ```java vdClient.init()``` without arguments if you want to run visual debugger in your local machine.

**Note:** Default visual debugger host and port are 127.0.0.1 and 1099 respectively.

Add following line of code wherever you want to pass data to visual-debugger:

```java
vdClient.draw(ENTITY_ID, "LAYER_TAG" , DATA);
```
Here ```ENTITY_ID``` is the value of the ID of an entity you want to send some data related to it, for example it can be the id of an agent with which you want to send its seen buildings. ```LAYER_TAG``` is the tag related to a layer in visual debugger. Ny specifying this tag, the data will be available through the related layer with which you can customize its drawing and ```DATA``` is your serializable data such as java.awt.Polygon. 

**Note:** You can find samples in [sample-rescue-agents](https://github.com/MRL-RS/sample-rescue-agents) project.

**Note:** If you want to send data asynchronously you can use following line instead:
```java
vdClient.drawAsync(ENTITY_ID, "LAYER_TAG" , DATA);
```

##### Following are some provided layer tags that you can pass your data:
| LayerTag | Data | Description |
| --- | --- | --- |
| MrlSampleBuildingsLayer | Integer Collection of BuildingIds | Find passed data on a layer named "Sample buildings" in visual debugger panel |
| MrlSampleRoadsLayer | Integer Collection of RoadIds | Find passed data on a layer named "Sample roads" in visual debugger panel |
| MrlSampleHumansLayer | Integer Collection of HumanIds | Find passed data on a layer named "Sample humans" in visual debugger panel |
| SamplePoint | Collection of java.awt.Point | Find passed data on a layer named "Sample points" in visual debugger panel |
| SampleLine | Collection of java.awt.geom.Line2D | Find passed data on a layer named "Sample lines" in visual debugger panel |
| SamplePolygon | Collection of java.awt.Polygon | Find passed data on a layer named "Sample polygons" in visual debugger panel |


**Attention:** Data that you want to pass to Visual Debugger must be serializable, so if you want to pass a collection of data, you must use a serializable instance of it such as ArrayList or HashSet, or you can cast it to java.io.Serializable.

Now run agent code and see the data you sent in visual debugger layers and have fun ;)
