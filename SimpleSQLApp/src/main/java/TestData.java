public class TestData implements java.io.Serializable {

    private String rowkey;
    private String a;
    private String b;

    public String getRowkey(){
        return this.rowkey;
    }

    public void setRowkey(String rowkey){
        this.rowkey = rowkey;
    }

    public String getA(){
        return this.a;
    }

    public void setA(String a){
        this.a = a;
    }
    public String getB(){
        return this.b;
    }

    public void setB(String b){
        this.b = b;
    }

}
