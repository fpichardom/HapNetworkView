package org;

import javafx.scene.image.Image;
import javafx.scene.text.Font;
import org.apache.batik.transcoder.Transcoder;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.ImageTranscoder;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.fop.svg.PDFTranscoder;
import org.graphstream.ui.geom.Point3;
import org.graphstream.ui.view.camera.Camera;

import java.awt.*;
import java.io.*;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum  Global {

    INSTANCE;

    //--

    public String os=System.getProperty("os.name").toLowerCase();
    public String file_encoding=System.getProperty("file.encoding");
    public String dll_exe_dir;
    public Object locker=new Object();
    //-- graph
    public Map<String, Point3> id_pos=new HashMap<>();
    public Map<String, Double> id_size=new HashMap<>();
    public Map<String, Double> edge_size=new HashMap<>();
    public Camera cam;
    //-- font
    public Font font_times;
    //-- type
    public String lable_type1;
    public String lable_type2;
    public String lable_type3;
    public String lable_type2_for_button;
    public String lable_legend;
    public String text_type1;
    public String text_type2;
    public String text_type3;
    public String button_type1;
    public String button_type2;
    public String button_type3;
    public String tableView_type1;
    public String tableView_type2;
    public String tableView_type3;
    //--img
    public Image img_color;

    Global(){
        //-- sys_tmp_dir
        try {
            String path = getClass().getResource("/dll_and_so").getPath().replaceAll("%20"," ");
            path=URLDecoder.decode(path, "UTF-8");
            if(File.separatorChar=='\\'){    //windows
                StringBuilder sb=new StringBuilder();
                char []cs=path.toCharArray();
                int i=cs[0]=='/' ? 1:0;
                for(;i<cs.length;i++){
                    if(cs[i]=='/') sb.append('\\');
                    else sb.append(cs[i]);
                }
                path=sb.toString();
            }
            dll_exe_dir=path+File.separatorChar;
            System.out.println("dll_and_so="+dll_exe_dir);
        }catch (Exception e){e.printStackTrace();}
        //-- font
        font_times=Font.loadFont(getClass().getResourceAsStream("/fonts/times.ttf"), 12);
        //-- type
        lable_type1 ="-fx-background-color: transparent;-fx-background-radius: 0;-fx-text-fill: rgb(0, 0, 0);-fx-font-family: '"+font_times.getName()+"';-fx-font-size: 16;";
        lable_type2 ="-fx-background-color: transparent;-fx-background-radius: 0;-fx-text-fill: rgb(0, 0, 0);-fx-font-family: '"+font_times.getName()+"';-fx-font-size: 14;";
        lable_type3 ="-fx-background-color: transparent;-fx-background-radius: 0;-fx-text-fill: rgb(0, 0, 0);-fx-font-family: '"+font_times.getName()+"';-fx-font-size: 12;";
        lable_type2_for_button ="-fx-background-color: #c0c0c0;-fx-background-radius: 0;-fx-text-fill: rgb(0, 0, 0);-fx-font-family: '"+font_times.getName()+"';-fx-font-size: 12;";
        lable_legend ="-fx-background-color: transparent;-fx-background-radius: 0;-fx-text-fill: rgb(0, 0, 0);-fx-font-family: '"+font_times.getName()+"';";
        text_type1 ="-fx-background-color: rgb(255,255,255);-fx-background-radius: 5;-fx-text-fill: rgb(0, 0, 0);-fx-font-family: '"+font_times.getName()+"';-fx-font-size: 16;";
        text_type2 ="-fx-background-color: rgb(255,255,255);-fx-background-radius: 5;-fx-text-fill: rgb(0, 0, 0);-fx-font-family: '"+font_times.getName()+"';-fx-font-size: 14;";
        text_type3 ="-fx-background-color: rgb(255,255,255);-fx-background-radius: 5;-fx-text-fill: rgb(0, 0, 0);-fx-font-family: '"+font_times.getName()+"';-fx-font-size: 12;";
        button_type1 ="-fx-background-color: rgb(140,211,236);-fx-background-radius: 5;-fx-text-fill: rgb(0, 0, 0);-fx-font-family: '"+font_times.getName()+"';-fx-font-size: 16;-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.7), 10, 0, 0, 1);";
        button_type2 ="-fx-background-color: #c0c0c0;-fx-background-radius: 5;-fx-text-fill: rgb(0, 0, 0);-fx-font-family: '"+font_times.getName()+"';-fx-font-size: 14;-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.7), 10, 0, 0, 1);";
        button_type3 ="-fx-background-color: #c0c0c0;-fx-background-radius: 5;-fx-text-fill: rgb(0, 0, 0);-fx-font-family: '"+font_times.getName()+"';-fx-font-size: 12;-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.7), 10, 0, 0, 1);";
        //--
        tableView_type1="-fx-font-family: '"+font_times.getName()+"';-fx-font-size: 16;-fx-text-fill: rgb(0, 0, 0);";
        tableView_type2="-fx-font-family: '"+font_times.getName()+"';-fx-font-size: 14;-fx-text-fill: rgb(0, 0, 0);";
        tableView_type3="-fx-font-family: '"+font_times.getName()+"';-fx-font-size: 13;-fx-text-fill: rgb(0, 0, 0);";
        //-- img
        img_color=new Image(getClass().getResourceAsStream("/picture/color.jpeg"));
    }

    public boolean is_integer(String str){
        try {
            Integer.parseInt(str);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    public String resource_file_to_path(String file, String path){
        try {
            InputStream is=null;
            OutputStream os=null;
            try {
                is=getClass().getResourceAsStream(file);
                os=new FileOutputStream(path);

                while (true){
                    int a=is.read();
                    if(a==-1) break;
                    os.write(a);
                }
            }finally {
                if(os!=null) os.close();
                if(is!=null) is.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return path;
    }

    public byte[] str_2_encoding_array(String str){
        try {
            return str.getBytes(file_encoding);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public String get_network_exe_path(){
        String os=System.getProperty("os.name").toLowerCase();
        String posfix;
        if(os.startsWith("win")) posfix="_win.exe";
        else if(os.contains("mac")) posfix="_mac_arm";
        else posfix="_linux_x86";

        String path1= dll_exe_dir+"fastHaN"+posfix;
        String path2="."+File.separatorChar+"fastHaN"+posfix;
        new File(path1).setExecutable(true);

        return path2;
    }

    public String get_file_posfix(String file){
        if(file.endsWith(".gz")){
            String name=file.substring(0, file.length()-3);
            int index=name.lastIndexOf(".");
            if(index==-1) return ".gz";
            else return file.substring(index);
        }else{
            int index=file.lastIndexOf(".");
            if(index==-1) return null;
            else return file.substring(index);
        }
    }

    public void convertSvgToPdf(String svgPath, String pdfPath) {
        try {
            // 创建Transcoder实例
            Transcoder transcoder = new PDFTranscoder();

            // 设置转换参数，如需要可以设置
            // transcoder.addTranscodingHint(PDFTranscoder.KEY_PIXEL_UNIT_TO_MILLIMETER, 0.2);

            // 设置输入源
            TranscoderInput input = new TranscoderInput(new FileInputStream(svgPath));

            // 设置输出结果
            TranscoderOutput output = new TranscoderOutput(new FileOutputStream(pdfPath));

            // 执行转换
            transcoder.transcode(input, output);

            // 关闭输入输出流
            input.getInputStream().close();
            output.getOutputStream().close();
        } catch (IOException | TranscoderException e) {
            e.printStackTrace();
        }
    }

    public void convertSvgToPng(String svgPath, String pngPath) {
        try {
            FileInputStream svgFileInputStream = new FileInputStream(svgPath);
            TranscoderInput inputSvgImage = new TranscoderInput(svgFileInputStream);

            // 创建输出流，并将其作为转码器的输出
            OutputStream pngOutputStream = new FileOutputStream(pngPath);
            TranscoderOutput outputPngImage = new TranscoderOutput(pngOutputStream);

            // 使用 PNGTranscoder 将 SVG 转换为 PNG
            PNGTranscoder transcoder = new PNGTranscoder();

            // 将转码器设置为透明背景
            transcoder.addTranscodingHint(ImageTranscoder.KEY_BACKGROUND_COLOR, new Color(255, 255, 255, 255));
            transcoder.transcode(inputSvgImage, outputPngImage);

            // 关闭流
            pngOutputStream.flush();
            pngOutputStream.close();
            svgFileInputStream.close();
        } catch (IOException | TranscoderException e) {
            e.printStackTrace();
        }
    }

    public void write_file_content(String file, List<String> list){
        try {
            InputStream is=null;
            InputStreamReader isr=null;
            BufferedReader br=null;
            OutputStream os=null;
            OutputStreamWriter osw=null;
            BufferedWriter bw=null;
            try {
                os=new FileOutputStream(file);
                osw=new OutputStreamWriter(os, "UTF-8");
                bw=new BufferedWriter(osw);

                for(String line:list){
                    bw.write(line);
                    bw.newLine();
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

    //================================================

    private class Invalid{
        public void gdsteslktjeslkt(){

        }
    }

}
