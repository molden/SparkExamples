/* SimpleSQLApp.java */
import org.apache.spark.api.java.*;
import org.apache.spark.sql.*;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.*;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.mapreduce.*;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.util.Bytes;
import scala.Tuple2;
//import java.util.List;



public class SimpleSQLApp {
  public static void main(String[] args) {
    
    // define Spark Context
    SparkConf sparkConf = new SparkConf().setAppName("SparkHBaseTest").setMaster("local[4]");
    JavaSparkContext jsc = new JavaSparkContext(sparkConf);
    SQLContext sqlContext = new SQLContext(jsc);     
      
    // create connection with HBase
    Configuration config = null;
    try {
        config = HBaseConfiguration.create();
        config.set("hbase.zookeeper.quorum", args[0]);
        config.set("hbase.zookeeper.property.clientPort","2181");
        config.set("hbase.master", args[1]+":60000");
        HBaseAdmin.checkHBaseAvailable(config);
        System.out.println("HBase is running!");
    } 
    catch (MasterNotRunningException e) {
     System.out.println("HBase is not running!");
     System.exit(1);
    }
    catch (Exception ce){ 
      ce.printStackTrace();
    }
                                                                               
    config.set(TableInputFormat.INPUT_TABLE, "test");
    config.set(TableInputFormat.SCAN_COLUMN_FAMILY, "cf"); // column family 
    config.set(TableInputFormat.SCAN_COLUMNS, "cf:a cf:b"); // 2 column qualifiers 

    JavaPairRDD<ImmutableBytesWritable, Result> hBaseRDD = 
          jsc.newAPIHadoopRDD(config, TableInputFormat.class, ImmutableBytesWritable.class, Result.class);

    JavaPairRDD<String, TestData> rowPairRDD = hBaseRDD.mapToPair(
        new PairFunction<Tuple2<ImmutableBytesWritable, Result>, String, TestData>() {
        @Override
    public Tuple2<String, TestData> call(
        Tuple2<ImmutableBytesWritable, Result> entry) throws Exception {
         
            Result r = entry._2;
            String keyRow = Bytes.toString(r.getRow());
         
            // define java bean  
            TestData cd = new TestData();
            cd.setRowkey(keyRow);
            cd.setA((String) Bytes.toString(r.getValue(Bytes.toBytes("cf"), Bytes.toBytes("a"))));
            cd.setB((String) Bytes.toString(r.getValue(Bytes.toBytes("cf"), Bytes.toBytes("b"))));
            return new Tuple2<String, TestData>(keyRow, cd);
         }
    });
            
    DataFrame schemaRDD =   sqlContext.createDataFrame(rowPairRDD.values(), TestData.class); 
 
    schemaRDD.show();
    schemaRDD.cache();
    schemaRDD.repartition(100);
    schemaRDD.printSchema();
   
    schemaRDD.registerTempTable("test");

// SQL can be run over RDDs that have been registered as tables.
    DataFrame results = sqlContext.sql("SELECT a FROM test WHERE b='value2'");

    results.show();
  }
}
