#SimpleSQLApp

In this example we will interact with a table in HBase using Spark SQL.

##Build

  module load Java/1.7.0_51
  module load Maven/3.3.3
  mvn package
  
##Use

  module load myhadoop/v41-beta
  module load Hadoop/2.7.1
  module load Spark/1.4.1
  module load HBase/0.98.13
  export HADOOP_CONF_DIR=$PBS_O_WORKDIR/hadoop-conf.$PBS_JOBID
  myhadoop-configure.sh -s $VSC_SCRATCH_NODE/$USER/$PBS_JOBID
  
###Start Hadoop

  start-all.sh
  
### Start Spark and HBase

To start HBase and Spark you should follow the instructions that were printed out after executing the myhadoop-configure.sh script.

### Submit the App

  spark-submit --driver-class-path $(hbase classpath) --jars /apps/leuven/common/hbase-0.98.13-hadoop2/lib/hbase-common-0.98.13-hadoop2.jar,/apps/leuven/common/hbase-0.98.13-hadoop2/lib/hbase-client-0.98.13-hadoop2.jar,/apps/leuven/common/hbase-0.98.13-hadoop2/lib/hbase-server-0.98.13-hadoop2.jar --class "SimpleSQLApp" --master local[4] target/simple-sql-project-1.0.jar


