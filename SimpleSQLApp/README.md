#SimpleSQLApp

In this example we will interact with a table in HBase using Spark SQL.

##Build

Go to the folder where SimpleSQLApp is located.

```bash
cd $PATH_TO_SPARK_EXAMPLES/SimpleSQLApp
```
```bash
module load Java/1.7.0_51
module load Maven/3.3.3
mvn package
```
This will create a target folder with a jar file in it.
  
##Use

You should submit an interactive job requesting a minimum of 3 nodes. For example:
```bash
qsub -I -lnodes=4:ppn=20:ivybridge
```
Once the job started you can execute the following commands
```bash
module load myhadoop/v41-beta
module load Hadoop/2.7.1
module load Spark/1.4.1
module load HBase/0.98.13
export HADOOP_CONF_DIR=$PBS_O_WORKDIR/hadoop-conf.$PBS_JOBID
myhadoop-configure.sh -s $VSC_SCRATCH_NODE/$USER/$PBS_JOBID
```
  
###Start Hadoop

```bash
start-all.sh
```
  
### Start Spark and HBase

To start HBase and Spark you should follow the instructions that were printed out after executing the myhadoop-configure.sh script.

### Create your Test HBase table

```bash
hbase shell
create 'test', 'cf'
put 'test', 'row1', 'cf:a', 'value1'
put 'test', 'row2', 'cf:b', 'value2'
```

check table content

```bash
scan 'test'
```
exit hbase shell
```bash
quit
```

It is also possible to import data from a HBase dump.
```bash
hbase org.apache.hadoop.hbase.mapreduce.Import <tablename> <inputdir>
```
By the way when your job finishes your created HBase table is gone. So if you want to save the data in you HBase table you should make an export. In another job you can import this data and continue working on it.

```bash
hbase org.apache.hadoop.hbase.mapreduce.Export <tablename> <outputdir> 
```

### Submit the App

Go to the SimpleSQLApp folder

```bash
cd $PATH_TO_SPARK_EXAMPLES/SimpleSQLApp
```
Submit the App.
```bash
spark-submit --driver-class-path $(hbase classpath) --jars $HBASE_HOME/lib/hbase-common-0.98.13-hadoop2.jar,$HBASE_HOME/lib/hbase-client-0.98.13-hadoop2.jar,$HBASE_HOME/lib/hbase-server-0.98.13-hadoop2.jar --class "SimpleSQLApp" --master local[4] target/simple-sql-project-1.0.jar
```

