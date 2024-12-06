import java.io.*;

public class A {

    public static void main(String []args){
        try {
            InputStream is=null;
            InputStreamReader isr=null;
            BufferedReader br=null;
            OutputStream os=null;
            OutputStreamWriter osw=null;
            BufferedWriter bw=null;
            try {
                is=A.class.getResourceAsStream("/color/group_color.txt");
                isr=new InputStreamReader(is, "UTF-8");
                br=new BufferedReader(isr);

                String line=null;
                for(int i=0;i<10;i++) line=br.readLine();

                String []ss=line.split(" ");
                for(String s:ss){
                    double r=Integer.parseInt(s.substring(1, 3), 16)/255.0;
                    double g=Integer.parseInt(s.substring(3, 5), 16)/255.0;
                    double b=Integer.parseInt(s.substring(5, 7), 16)/255.0;
                    System.err.print("("+r+", "+g+", "+b+"), ");
                }
            }finally {
                if(bw!=null) bw.close();
                if(osw!=null) osw.close();
                if(os!=null) os.close();
                if(br!=null) br.close();
                if(isr!=null) isr.close();
                if(is!=null) is.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }


    }

}
