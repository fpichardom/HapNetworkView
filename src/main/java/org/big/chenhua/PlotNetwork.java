package org.big.chenhua;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.Global;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.stream.SourceBase;
import org.graphstream.ui.fx_viewer.FxViewPanel;
import org.graphstream.ui.fx_viewer.FxViewer;
import org.graphstream.ui.geom.Point2;
import org.graphstream.ui.geom.Point3;
import org.graphstream.ui.graphicGraph.GraphicElement;
import org.graphstream.ui.javafx.FxGraphRenderer;
import org.graphstream.ui.layout.Layout;
import org.graphstream.ui.layout.springbox.BarnesHutLayout;
import org.graphstream.ui.layout.springbox.implementations.SpringBox;
import org.graphstream.ui.spriteManager.Sprite;
import org.graphstream.ui.spriteManager.SpriteManager;
import org.graphstream.ui.view.Viewer;
import org.graphstream.ui.view.camera.Camera;
import org.graphstream.ui.view.util.InteractiveElement;
import org.jfree.graphics2d.svg.SVGGraphics2D;

import java.awt.*;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;
import java.util.zip.GZIPInputStream;

public class PlotNetwork extends Application {
    private static final long serialVersionUID = 8394236698316485656L;
    private static Object locker=new Object();
    private static int sys_tmp_id=1;
    private static double max_width;
    private static double max_height;
    private static double width;
    private static double height;

    private Graph graph=new MultiGraph("HaplotypeNetworkView");
    private SpriteManager sman=new SpriteManager(graph);
    private FxViewer viewer=new FxViewer(graph, Viewer.ThreadingModel.GRAPH_IN_GUI_THREAD);
    private FxGraphRenderer renderer=new FxGraphRenderer();
    private FxViewPanel view=(FxViewPanel)viewer.addDefaultView(false, renderer);
    private Layout layout=new SpringBox();

    private String pre_file_path=null;
    private String graph_file=null;
    private int cache_colors_max_num=0;
    private boolean is_display_hover=true;
    private double min_node_weight;
    private double max_node_weight;
    private double min_node_size=10.0;
    private double max_node_size=10.0;
    private double node_size_slope=1.0;
    private double node_size_intercept=0.0;
    private boolean is_all_node_equal=false;
    private double min_edge_weight;
    private double max_edge_weight;
    private double min_edge_size=1.0;
    private double max_edge_size=1.0;
    private double edge_size_slope=1.0;
    private double edge_size_intercept=0.0;
    private boolean is_all_edge_equal=false;
    private Map<Integer, List<String>> cache_colors=new HashMap<>();
    private Map<String, Vertex> id_vertex =new HashMap<>();
    //private Map<Integer, Double> id_size=new HashMap<>();
    private Map<String, Double> edge_weight=new HashMap<>();
    private Map<String, Map<Integer, String>> edge_pos_snps=new HashMap<>();
    private Map<String, String> hap_id=new HashMap<>();
    private Map<String, String> edge_label=new HashMap<>();
    private Map<String, Sprite> id_sprite=new HashMap<>();
    private Map<String, String> hap_group=new LinkedHashMap<>();
    private Map<String, String> group_color =new HashMap<>();
    private Map<String, Map<String, Integer>> id_name_freq1 =new HashMap<>();
    private Map<String, Map<String, Double>> id_name_freq2 =new HashMap<>();
    private String []groups;
    private int name_size=9;
    private int legend_name_size =12;
    private int right_size =300;
    private double line_width=0.5;
    private boolean is_current_display_panel=false;
    private boolean is_group_phylip_format=false;
    private int phy_num=0;
    private int phy_len=0;
    private int phy_max_group_num=10;
    private Map<String, String> phy_name_seq=new HashMap<>();
    private Map<Integer, String> phy_pos_snps=new LinkedHashMap<>();
    private Map<Integer, Integer> phy_pos_map=new HashMap<>();
    private boolean []phy_pre_status=null;
    private double legend_x;
    private double legend_y;
    private double view_x1;
    private double view_y1;
    private double view_x2;
    private double view_y2;
    private boolean is_in_dragged=false;

    private Stage stage;
    private Pane main_pane=new Pane();
    private Pane p_pane1 =new Pane();
    private Pane jl_legend;
    private Stage display_frame=new Stage();
    private Label label0_1;
    private Label label0_2;
    private TextField tf_chr=new TextField();
    private TextField tf_min_chr_pos=new TextField();
    private TextField tf_max_chr_pos=new TextField();
    private Button vcf_run;
    private CheckBox cb_hover;
    private CheckBox cb_auto_layout;
    private CheckBox cb_node;
    private CheckBox cb_edge;
    private CheckBox cb_node_true_outline;
    private CheckBox cb_node_intermediate_outline;
    private TextField tf_min_node_size=new TextField();
    private TextField tf_max_node_size=new TextField();
    private TextField tf_min_edge_size=new TextField();
    private TextField tf_max_edge_size=new TextField();
    private TextField tf_edge_force =new TextField();
    private TextField tf_node_gravity =new TextField();
    private TextField tf_line_width=new TextField();
    private ColorPicker node_color1=new ColorPicker(str_2_color("#ffffff"));
    private ColorPicker node_color2=new ColorPicker(str_2_color("#ffffff"));
    private TableView<GroupItem> jt_groups=new XTableView1<>();
    private TableView<HapItem> jt_haps=new XTableView1<>();
    private TableView<SnpItem> jt_snps=new XTableView1<>();



    @Override
    public void start(Stage stage) throws Exception {
        Locale.setDefault(Locale.ENGLISH);
        this.stage=stage;
        stage.setTitle("HaplotypeNetworkView");
        //--
        Global.INSTANCE.cam=renderer.getCamera();
        SourceBase.viewer=viewer;
        SourceBase.plot=this;
        SourceBase.graph=graph;
        //--
        this.read_cache_colors();
        display_frame.initStyle(StageStyle.TRANSPARENT);
        display_frame.setAlwaysOnTop(true);
        //--
        this.content();
        stage.setScene(new Scene(main_pane, width, height));
        stage.setOnCloseRequest(e->{
            System.exit(0);
        });
        stage.show();
        //--
        stage.widthProperty().addListener(e->{
            width=stage.getWidth();
            height=stage.getHeight();
            if(width>max_width) width=max_width;
            if(height>max_height) height=max_height;
            content();
        });
        stage.heightProperty().addListener(e->{
            width=stage.getWidth();
            height=stage.getHeight();
            if(width>max_width) width=max_width;
            if(height>max_height) height=max_height;
            content();
        });
    }

    public void content(){
        main_pane.getChildren().removeAll();
        main_pane.setLayoutX(0);
        main_pane.setLayoutY(0);
        main_pane.setPrefWidth(width);
        main_pane.setPrefHeight(height);

        {
            p_pane1.getChildren().removeAll();
            p_pane1.setLayoutX(width-right_size);
            p_pane1.setLayoutY(0);
            p_pane1.setPrefWidth(right_size);
            p_pane1.setPrefHeight(height);
            p_pane1.setStyle("-fx-background-color: #ececec;");
            p_pane1.setOnKeyPressed(e->{
                if(e.getCode().getName().toLowerCase().equals("enter")){
                    flush_graph();
                }
            });
            p_pane1.setOnMouseMoved(e->{
                display_frame.hide();
            });
            {
                Button open_graph=new Button("Open(.gml .phylip .vcf)");
                open_graph.setStyle(Global.INSTANCE.button_type2);
                open_graph.setLayoutX(10);
                open_graph.setLayoutY(10);
                open_graph.setPrefWidth(205);
                open_graph.setPrefHeight(20);
                open_graph.setOnAction(e->{
                    is_display_hover=false;
                    FileChooser chooser=new FileChooser();
                    if(pre_file_path!=null) chooser.setInitialDirectory(new File(pre_file_path));
                    chooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Graph file", "*.gml", "*.phy", "*.phylip", "*.phy.gz", "*.phylip.gz", "*.vcf", "*.vcf.gz"));
                    File file=chooser.showOpenDialog(null);
                    if(file!=null){
                        pre_file_path=file.getParentFile().getAbsolutePath();
                        clear_graph();

                        p_pane1.getChildren().removeAll(tf_chr, label0_1, tf_min_chr_pos, label0_2, tf_max_chr_pos, vcf_run);

                        String name=file.getAbsolutePath();
                        this.graph_file=name;
                        if(name.endsWith(".gml")) read_graph(name);
                        else if(name.endsWith(".phy") || name.endsWith(".phylip") || name.endsWith(".phy.gz") || name.endsWith(".phylip.gz")){
                            String cmd_path=Global.INSTANCE.get_network_exe_path();
                            int id=new Random().nextInt(10000);
                            String output=Global.INSTANCE.dll_exe_dir+id;
                            try {
                                if(Global.INSTANCE.os.contains("mac")){
                                    String name1=id+Global.INSTANCE.get_file_posfix(name);
                                    String name2=Global.INSTANCE.dll_exe_dir+name1;
                                    if(new File(name2).exists()) new File(name2).delete();
                                    Files.createSymbolicLink(Paths.get(name2), Paths.get(name));

                                    String cmd=cmd_path+" mjn -i "+name1+" -o "+id;
                                    System.out.println("cmd="+cmd);

                                    InputStream is=Runtime.getRuntime().exec(cmd, null, new File(Global.INSTANCE.dll_exe_dir)).getInputStream();
                                    byte []buff=new byte[1024];
                                    while (is.read(buff)!=-1);
                                    is.close();

                                    new File(name2).delete();
                                }else{
                                    byte []arr1=Global.INSTANCE.str_2_encoding_array(name);
                                    byte []arr2=Global.INSTANCE.str_2_encoding_array(output);

                                    NetworkNative.generate_network(arr1, arr2);
                                }
                            }catch (Exception e1){e1.printStackTrace();}
                            read_graph(output+".gml");
                            //--
                            new File(output+".gml").delete();
                            new File(output+".json").delete();
                            //--
                            clear_graph();
                            read_hap_group_file(name);
                            phy_pre_status=new boolean[phy_pos_snps.size()];
                            ObservableList<SnpItem> data=FXCollections.observableArrayList();
                            int row=0;
                            for(Integer pos:phy_pos_snps.keySet()){
                                data.add(new SnpItem(row++, pos, phy_pos_snps.get(pos)));
                            }
                            jt_snps.getItems().clear();
                            jt_snps.setItems(data);
                            p_pane1.getChildren().add(jt_snps);
                            flush_graph();
                        }else if(name.endsWith(".vcf") || name.endsWith(".vcf.gz")){
                            if(!p_pane1.getChildren().contains(tf_chr)) p_pane1.getChildren().add(tf_chr);
                            if(!p_pane1.getChildren().contains(label0_1)) p_pane1.getChildren().add(label0_1);
                            if(!p_pane1.getChildren().contains(tf_min_chr_pos)) p_pane1.getChildren().add(tf_min_chr_pos);
                            if(!p_pane1.getChildren().contains(label0_2)) p_pane1.getChildren().add(label0_2);
                            if(!p_pane1.getChildren().contains(tf_max_chr_pos)) p_pane1.getChildren().add(tf_max_chr_pos);
                            if(!p_pane1.getChildren().contains(vcf_run)) p_pane1.getChildren().add(vcf_run);
                            String chr=null;
                            int start=1;
                            try {
                                InputStream is=null;
                                InputStreamReader isr=null;
                                BufferedReader br=null;
                                OutputStream os=null;
                                OutputStreamWriter osw=null;
                                BufferedWriter bw=null;
                                try {
                                    is=new FileInputStream(name);
                                    if(name.endsWith(".gz")) is=new GZIPInputStream(is);
                                    isr=new InputStreamReader(is, "UTF-8");
                                    br=new BufferedReader(isr);

                                    while (true){
                                        String line= br.readLine();
                                        if(line==null) break;
                                        if(line.length()==0) continue;
                                        if(line.charAt(0)=='#') continue;
                                        String []ss=line.split("\t");
                                        chr=ss[0];
                                        start=Integer.parseInt(ss[1]);
                                        break;
                                    }
                                }finally {
                                    if(bw!=null) bw.close();
                                    if(osw!=null) osw.close();
                                    if(os!=null) os.close();
                                    if(br!=null) br.close();
                                    if(isr!=null) isr.close();
                                    if(is!=null) is.close();
                                }
                            }catch (Exception e1){
                                e1.printStackTrace();
                            }
                            tf_chr.setText(chr);
                            tf_min_chr_pos.setText(start+"");
                            tf_max_chr_pos.setText((start+50000)+"");
                            read_vcf();
                        }
                    }
                    if(graph.nodes().count()<500) is_display_hover=true;
                });
                p_pane1.getChildren().add(open_graph);
                //--
                Button save_pic=new Button("Save");
                save_pic.setStyle(Global.INSTANCE.button_type2);
                save_pic.setLayoutX(220);
                save_pic.setLayoutY(10);
                save_pic.setPrefWidth(70);
                save_pic.setPrefHeight(20);
                save_pic.setOnAction(e->{
                    is_display_hover=false;
                    FileChooser chooser=new FileChooser();
                    if(pre_file_path!=null) chooser.setInitialDirectory(new File(pre_file_path));
                    chooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Save picture", "*.svg", ".png", ".pdf"));
                    File file=chooser.showSaveDialog(null);
                    if(file!=null) {
                        pre_file_path=file.getParentFile().getAbsolutePath();
                        String name=file.getAbsolutePath();
                        try {
                            if(name.endsWith(".svg")) name=name.substring(0, name.length()-4);
                            if(name.endsWith(".png")) name=name.substring(0, name.length()-4);
                            if(name.endsWith(".pdf")) name=name.substring(0, name.length()-4);
                            String svg_file=name+".svg";
                            String pdf_file=name+".pdf";
                            String png_file=name+".png";

                            graph_2_svg(svg_file);
                            Global.INSTANCE.convertSvgToPdf(svg_file, pdf_file);
                            Global.INSTANCE.convertSvgToPng(svg_file, png_file);
                        } catch (Exception exp) {
                            exp.printStackTrace();
                        }
                    }
                    is_display_hover=true;
                });
                p_pane1.getChildren().add(save_pic);
                //--
                tf_chr.setStyle(Global.INSTANCE.text_type2);
                tf_chr.setLayoutX(10);
                tf_chr.setLayoutY(43);
                tf_chr.setPrefWidth(60);
                tf_chr.setPrefHeight(25);
                //--
                label0_1=new Label(":");
                label0_1.setStyle(Global.INSTANCE.lable_type2);
                label0_1.setLayoutX(75);
                label0_1.setLayoutY(43);
                label0_1.setPrefWidth(5);
                label0_1.setPrefHeight(25);
                //--
                tf_min_chr_pos.setStyle(Global.INSTANCE.text_type2);
                tf_min_chr_pos.setLayoutX(85);
                tf_min_chr_pos.setLayoutY(43);
                tf_min_chr_pos.setPrefWidth(65);
                tf_min_chr_pos.setPrefHeight(25);
                //--
                label0_2=new Label("-");
                label0_2.setStyle(Global.INSTANCE.lable_type2);
                label0_2.setLayoutX(155);
                label0_2.setLayoutY(43);
                label0_2.setPrefWidth(5);
                label0_2.setPrefHeight(25);
                //--
                tf_max_chr_pos.setStyle(Global.INSTANCE.text_type2);
                tf_max_chr_pos.setLayoutX(165);
                tf_max_chr_pos.setLayoutY(43);
                tf_max_chr_pos.setPrefWidth(65);
                tf_max_chr_pos.setPrefHeight(25);
                //--
                vcf_run=new Button("Run");
                vcf_run.setStyle(Global.INSTANCE.button_type2);
                vcf_run.setLayoutX(240);
                vcf_run.setLayoutY(43);
                vcf_run.setPrefWidth(50);
                vcf_run.setPrefHeight(20);
                vcf_run.setOnAction(e->{this.read_vcf();});
                //--
                cb_hover=new CheckBox("Hover");
                cb_hover.setStyle(Global.INSTANCE.lable_type2);
                cb_hover.setLayoutX(10);
                cb_hover.setLayoutY(75);
                cb_hover.setOnAction(e->{
                    is_display_hover=cb_hover.isSelected();
                });
                cb_hover.setSelected(true);
                if(!p_pane1.getChildren().contains(cb_hover)) p_pane1.getChildren().add(cb_hover);
                //--
                cb_auto_layout=new CheckBox("AutoLayout");
                cb_auto_layout.setStyle(Global.INSTANCE.lable_type2);
                cb_auto_layout.setLayoutX(80);
                cb_auto_layout.setLayoutY(75);
                cb_auto_layout.setSelected(true);
                cb_auto_layout.setOnAction(e->{
                    if(cb_auto_layout.isSelected()) viewer.enableAutoLayout();
                    else viewer.disableAutoLayout();
                });
                cb_auto_layout.setSelected(true);
                if(!p_pane1.getChildren().contains(cb_auto_layout)) p_pane1.getChildren().add(cb_auto_layout);
                //--
                cb_node=new CheckBox("Node");
                cb_node.setStyle(Global.INSTANCE.lable_type2);
                cb_node.setLayoutX(180);
                cb_node.setLayoutY(75);
                cb_node.setOnAction(e->{
                    if(cb_node.isSelected()){
                        graph.nodes().forEach(n->{
                            Vertex v= id_vertex.get(n.getId());
                            Sprite sprite=id_sprite.get(v.id);
                            sprite.setAttribute("ui.label", v.name);
                        });
                    }else{
                        graph.nodes().forEach(n->{
                            Vertex v= id_vertex.get(n.getId());
                            Sprite sprite=id_sprite.get(v.id);
                            sprite.removeAttribute("ui.label");
                        });
                    }
                });
                if(!p_pane1.getChildren().contains(cb_node)) p_pane1.getChildren().add(cb_node);
                //--
                cb_edge=new CheckBox("Edge");
                cb_edge.setStyle(Global.INSTANCE.lable_type2);
                cb_edge.setLayoutX(240);
                cb_edge.setLayoutY(75);
                cb_edge.setOnAction(e->{
                    if(cb_edge.isSelected()){
                        graph.edges().forEach(edge->{
                            String label=edge_label.get(edge.getId());
                            edge.setAttribute("ui.label", label);
                        });
                    }else{
                        graph.edges().forEach(edge->{
                            edge.removeAttribute("ui.label");
                        });
                    }
                });
                if(!p_pane1.getChildren().contains(cb_edge)) p_pane1.getChildren().add(cb_edge);
                //--
                Label label1=new Label("Node size:");
                label1.setStyle(Global.INSTANCE.lable_type2);
                label1.setLayoutX(10);
                label1.setLayoutY(100);
                label1.setPrefWidth(70);
                label1.setPrefHeight(25);
                p_pane1.getChildren().add(label1);
                //--
                tf_min_node_size.setStyle(Global.INSTANCE.text_type2);
                tf_min_node_size.setLayoutX(85);
                tf_min_node_size.setLayoutY(100);
                tf_min_node_size.setPrefWidth(65);
                tf_min_node_size.setPrefHeight(25);
                if(!p_pane1.getChildren().contains(tf_min_node_size)) p_pane1.getChildren().add(tf_min_node_size);
                //--
                Label label2=new Label("-");
                label2.setStyle(Global.INSTANCE.lable_type2);
                label2.setLayoutX(155);
                label2.setLayoutY(100);
                label2.setPrefWidth(5);
                label2.setPrefHeight(25);
                p_pane1.getChildren().add(label2);
                //--
                tf_max_node_size.setStyle(Global.INSTANCE.text_type2);
                tf_max_node_size.setLayoutX(165);
                tf_max_node_size.setLayoutY(100);
                tf_max_node_size.setPrefWidth(65);
                tf_max_node_size.setPrefHeight(25);
                if(!p_pane1.getChildren().contains(tf_max_node_size)) p_pane1.getChildren().add(tf_max_node_size);
                //--
                Button bt_node_size=new Button("Run");
                bt_node_size.setStyle(Global.INSTANCE.button_type2);
                bt_node_size.setLayoutX(240);
                bt_node_size.setLayoutY(100);
                bt_node_size.setPrefWidth(50);
                bt_node_size.setPrefHeight(20);
                bt_node_size.setOnAction(e->{
                    flush_graph();
                });
                p_pane1.getChildren().add(bt_node_size);
                //--
                Label label3 = new Label("Edge size:");
                label3.setStyle(Global.INSTANCE.lable_type2);
                label3.setLayoutX(10);
                label3.setLayoutY(130);
                label3.setPrefWidth(70);
                label3.setPrefHeight(25);
                p_pane1.getChildren().add(label3);
                //--
                tf_min_edge_size.setStyle(Global.INSTANCE.text_type2);
                tf_min_edge_size.setLayoutX(85);
                tf_min_edge_size.setLayoutY(130);
                tf_min_edge_size.setPrefWidth(65);
                tf_min_edge_size.setPrefHeight(25);
                if(!p_pane1.getChildren().contains(tf_min_edge_size)) p_pane1.getChildren().add(tf_min_edge_size);
                //--
                Label label4 = new Label("-");
                label4.setStyle(Global.INSTANCE.lable_type2);
                label4.setLayoutX(155);
                label4.setLayoutY(130);
                label4.setPrefWidth(5);
                label4.setPrefHeight(25);
                p_pane1.getChildren().add(label4);
                //--
                tf_max_edge_size.setStyle(Global.INSTANCE.text_type2);
                tf_max_edge_size.setLayoutX(165);
                tf_max_edge_size.setLayoutY(130);
                tf_max_edge_size.setPrefWidth(65);
                tf_max_edge_size.setPrefHeight(25);
                if(!p_pane1.getChildren().contains(tf_max_edge_size)) p_pane1.getChildren().add(tf_max_edge_size);
                //--
                Button bt_edge_size = new Button("Run");
                bt_edge_size.setStyle(Global.INSTANCE.button_type2);
                bt_edge_size.setLayoutX(240);
                bt_edge_size.setLayoutY(130);
                bt_edge_size.setPrefWidth(50);
                bt_edge_size.setPrefHeight(20);
                bt_edge_size.setOnAction(e -> {
                    flush_graph();
                });
                p_pane1.getChildren().add(bt_edge_size);
                //--
                Label label5 = new Label("Edge force:\t[0.01-5.0]");
                label5.setStyle(Global.INSTANCE.lable_type2);
                label5.setLayoutX(10);
                label5.setLayoutY(160);
                label5.setPrefWidth(150);
                label5.setPrefHeight(25);
                p_pane1.getChildren().add(label5);
                //--
                tf_edge_force.setStyle(Global.INSTANCE.text_type2);
                tf_edge_force.setLayoutX(165);
                tf_edge_force.setLayoutY(160);
                tf_edge_force.setPrefWidth(65);
                tf_edge_force.setPrefHeight(20.0);
                tf_edge_force.setText("1.0");
                if(!p_pane1.getChildren().contains(tf_edge_force)) p_pane1.getChildren().add(tf_edge_force);
                //--
                Button bt_edge_force = new Button("Run");
                bt_edge_force.setStyle(Global.INSTANCE.button_type2);
                bt_edge_force.setLayoutX(240);
                bt_edge_force.setLayoutY(160);
                bt_edge_force.setPrefWidth(50);
                bt_edge_force.setPrefHeight(20);
                bt_edge_force.setOnAction(e -> {
                    flush_graph();
                });
                p_pane1.getChildren().add(bt_edge_force);
                //--
                Label label5_2 = new Label("Node gravity:\t[0.00-1.0]");
                label5_2.setStyle(Global.INSTANCE.lable_type2);
                label5_2.setLayoutX(10);
                label5_2.setLayoutY(190);
                label5_2.setPrefWidth(150);
                label5_2.setPrefHeight(25);
                p_pane1.getChildren().add(label5_2);
                //--
                tf_node_gravity.setStyle(Global.INSTANCE.text_type2);
                tf_node_gravity.setLayoutX(165);
                tf_node_gravity.setLayoutY(190);
                tf_node_gravity.setPrefWidth(65);
                tf_node_gravity.setPrefHeight(20.0);
                tf_node_gravity.setText(0.0+"");
                if(!p_pane1.getChildren().contains(tf_node_gravity)) p_pane1.getChildren().add(tf_node_gravity);
                //--
                Button bt_node_gravity = new Button("Run");
                bt_node_gravity.setStyle(Global.INSTANCE.button_type2);
                bt_node_gravity.setLayoutX(240);
                bt_node_gravity.setLayoutY(190);
                bt_node_gravity.setPrefWidth(50);
                bt_node_gravity.setPrefHeight(20);
                bt_node_gravity.setOnAction(e -> {
                    flush_graph();
                });
                p_pane1.getChildren().add(bt_node_gravity);
                //--
                Label label5_3 = new Label("Line width:\t[0.01-9.0]");
                label5_3.setStyle(Global.INSTANCE.lable_type2);
                label5_3.setLayoutX(10);
                label5_3.setLayoutY(220);
                label5_3.setPrefWidth(150);
                label5_3.setPrefHeight(25);
                p_pane1.getChildren().add(label5_3);
                //--
                tf_line_width.setStyle(Global.INSTANCE.text_type2);
                tf_line_width.setLayoutX(165);
                tf_line_width.setLayoutY(220);
                tf_line_width.setPrefWidth(65);
                tf_line_width.setPrefHeight(20.0);
                tf_line_width.setText(0.5+"");
                if(!p_pane1.getChildren().contains(tf_line_width)) p_pane1.getChildren().add(tf_line_width);
                //--
                Button bt_line_width = new Button("Run");
                bt_line_width.setStyle(Global.INSTANCE.button_type2);
                bt_line_width.setLayoutX(240);
                bt_line_width.setLayoutY(220);
                bt_line_width.setPrefWidth(50);
                bt_line_width.setPrefHeight(20);
                bt_line_width.setOnAction(e -> {
                    flush_graph();
                });
                p_pane1.getChildren().add(bt_line_width);
                //--
                Label label6=new Label("Node(true):");
                label6.setStyle(Global.INSTANCE.lable_type2);
                label6.setLayoutX(10);
                label6.setLayoutY(250);
                label6.setPrefWidth(100);
                label6.setPrefHeight(25);
                p_pane1.getChildren().add(label6);
                //--
                cb_node_true_outline=new CheckBox("Outline");
                cb_node_true_outline.setStyle(Global.INSTANCE.lable_type2);
                cb_node_true_outline.setLayoutX(160);
                cb_node_true_outline.setLayoutY(250);
                cb_node_true_outline.setPrefWidth(70);
                cb_node_true_outline.setPrefHeight(25);
                cb_node_true_outline.setOnAction(e->{flush_graph();});
                if(!p_pane1.getChildren().contains(cb_node_true_outline)) p_pane1.getChildren().add(cb_node_true_outline);
                //--
                node_color1.setLayoutX(240);
                node_color1.setLayoutY(250);
                node_color1.setPrefWidth(50);
                node_color1.setPrefHeight(20);
                node_color1.setOnAction(e->{
                    graph.nodes().forEach(n->{
                        Vertex v=id_vertex.get(n.getId());
                        if(v.isIntermediate) return;
                        replace_ui_style_attribute(id_sprite.get(v.id), "fill-color", color_2_str(node_color1.getValue()));
                    });
                });
                if(!p_pane1.getChildren().contains(node_color1)) p_pane1.getChildren().add(node_color1);
                //--
                Label label7=new Label("Node(intermediate):");
                label7.setStyle(Global.INSTANCE.lable_type2);
                label7.setLayoutX(10);
                label7.setLayoutY(275);
                label7.setPrefWidth(140);
                label7.setPrefHeight(25);
                p_pane1.getChildren().add(label7);
                //--
                cb_node_intermediate_outline=new CheckBox("Outline");
                cb_node_intermediate_outline.setStyle(Global.INSTANCE.lable_type2);
                cb_node_intermediate_outline.setLayoutX(160);
                cb_node_intermediate_outline.setLayoutY(275);
                cb_node_intermediate_outline.setPrefWidth(70);
                cb_node_intermediate_outline.setPrefHeight(25);
                cb_node_intermediate_outline.setOnAction(e->{flush_graph();});
                if(!p_pane1.getChildren().contains(cb_node_intermediate_outline)) p_pane1.getChildren().add(cb_node_intermediate_outline);
                //--
                node_color2.setLayoutX(240);
                node_color2.setLayoutY(275);
                node_color2.setPrefWidth(50);
                node_color2.setPrefHeight(20);
                node_color2.setOnAction(e->{
                    graph.nodes().forEach(n->{
                        Vertex v=id_vertex.get(n.getId());
                        if(!v.isIntermediate) return;
                        replace_ui_style_attribute(id_sprite.get(v.id), "fill-color", color_2_str(node_color2.getValue()));
                    });
                });
                if(!p_pane1.getChildren().contains(node_color2)) p_pane1.getChildren().add(node_color2);
                //--
                Label label8=new Label("============Group/Snp===========");
                label8.setStyle(Global.INSTANCE.lable_type1);
                label8.setLayoutX(10);
                label8.setLayoutY(300);
                label8.setPrefWidth(280);
                label8.setPrefHeight(25);
                p_pane1.getChildren().add(label8);
                //--
                Button open_group=new Button("Group file/Map file(phylip)");
                open_group.setStyle(Global.INSTANCE.button_type2);
                open_group.setLayoutX(10);
                open_group.setLayoutY(330);
                open_group.setPrefWidth(205);
                open_group.setPrefHeight(20);
                open_group.setOnAction(e->{
                    is_display_hover=false;
                    FileChooser chooser=new FileChooser();
                    if(pre_file_path!=null) chooser.setInitialDirectory(new File(pre_file_path));
                    File file=chooser.showOpenDialog(null);
                    if(file!=null){
                        pre_file_path=file.getParentFile().getAbsolutePath();
                        clear_graph();
                        read_hap_group_file(file.getAbsolutePath());
                        if(is_group_phylip_format){
                            phy_pre_status=new boolean[phy_pos_snps.size()];

                            ObservableList<SnpItem> data=FXCollections.observableArrayList();
                            int row=0;
                            for(Integer pos:phy_pos_snps.keySet()){
                                data.add(new SnpItem(row++, pos, phy_pos_snps.get(pos)));
                            }
                            jt_snps.getItems().clear();
                            jt_snps.setItems(data);
                            p_pane1.getChildren().add(jt_snps);
                        }else{
                            ObservableList<GroupItem> data1= FXCollections.observableArrayList();
                            List<String> c_list=cache_colors.get(groups.length);
                            if(c_list==null) c_list=cache_colors.get(cache_colors_max_num);
                            for(int i=0;i<groups.length;i++){
                                String str_color=c_list.get(i%c_list.size());
                                group_color.put(groups[i], str_color);
                                GroupItem item=new GroupItem(i, groups[i], str_color, Global.INSTANCE.img_color);
                                data1.add(item);
                            }
                            jt_groups.getItems().clear();
                            jt_groups.setItems(data1);
                            p_pane1.getChildren().add(jt_groups);

                            ObservableList<HapItem> data2= FXCollections.observableArrayList();
                            int row=0;
                            for(String hap:hap_group.keySet()){
                                data2.add(new HapItem(row++, hap, hap_group.get(hap)));
                            }
                            jt_haps.getItems().clear();
                            jt_haps.setItems(data2);
                            p_pane1.getChildren().add(jt_haps);
                        }
                        flush_graph();
                    }
                    is_display_hover=true;
                });
                p_pane1.getChildren().add(open_group);
                //--
                Button clear=new Button("Clear");
                clear.setStyle(Global.INSTANCE.button_type2);
                clear.setLayoutX(220);
                clear.setLayoutY(330);
                clear.setPrefWidth(70);
                clear.setPrefHeight(20);
                clear.setOnAction(e->{
                    clear_graph();
                    flush_graph();
                });
                p_pane1.getChildren().add(clear);
                //--
                jt_groups.setLayoutX(10);
                jt_groups.setLayoutY(360);
                jt_groups.setPrefWidth(280);
                jt_groups.setPrefHeight(150);
                TableColumn<GroupItem, String> col1_1=new TableColumn<>("Group");
                TableColumn<GroupItem, String> col1_2=new TableColumn<>("Color");
                TableColumn<GroupItem, ColorPicker> col1_3=new TableColumn<>("Select");
                col1_1.setStyle(Global.INSTANCE.tableView_type3);
                col1_2.setStyle(Global.INSTANCE.tableView_type3);
                col1_3.setStyle(Global.INSTANCE.tableView_type3);
                col1_1.setCellValueFactory(cell->cell.getValue().getGroup());
                col1_2.setCellValueFactory(cell->cell.getValue().getColor());
                col1_3.setCellValueFactory(cell->cell.getValue().getSelect());
                col1_1.setPrefWidth(140);
                col1_2.setPrefWidth(70);
                col1_3.setPrefWidth(50);
                jt_groups.setEditable(true);
                col1_2.setCellFactory(TextFieldTableCell.forTableColumn());
                col1_2.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<GroupItem, String>>() {
                    @Override
                    public void handle(TableColumn.CellEditEvent<GroupItem, String> event) {
                        String color=event.getNewValue();
                        if(is_color_grb(color)){
                            GroupItem item=event.getRowValue();
                            item.setColor(color);
                            group_color.put(item.getGroup().getValue(), item.getColor().getValue());
                            flush_graph();
                        }
                    }
                });
                jt_groups.getSelectionModel().selectedItemProperty().addListener((obj, oldItem, newItem)->{
                    jt_haps.getSelectionModel().clearSelection();

                    if(newItem==null){
                        graph.nodes().forEach(n -> {
                            replace_ui_style_attribute(n, "shadow-mode", "none");
                        });
                        return;
                    }

                    String group = newItem.getGroup().getValue();
                    if (group == null) return;
                    graph.nodes().forEach(n -> {
                        Vertex v=id_vertex.get(n.getId());

                        boolean flag=false;
                        for(String name:v.names){
                            String group2=hap_group.get(name);
                            if(group2!=null && group2.equals(group)) flag=true;
                        }

                        if (flag) {
                            replace_ui_style_attribute(n, "shadow-mode", "plain");
                            replace_ui_style_attribute(n, "shadow-color", "gray");
                            replace_ui_style_attribute(n, "shadow-offset", "0");
                            replace_ui_style_attribute(n, "shadow-width", "9");
                        } else {
                            replace_ui_style_attribute(n, "shadow-mode", "none");
                        }
                    });
                });
                jt_groups.getColumns().clear();
                jt_groups.getColumns().addAll(col1_1, col1_2, col1_3);
                //--
                jt_haps.setLayoutX(10);
                jt_haps.setLayoutY(515);
                jt_haps.setPrefWidth(280);
                jt_haps.setPrefHeight(height-455-35);
                TableColumn<HapItem, String> col2_1=new TableColumn<>("Hap");
                TableColumn<HapItem, String> col2_2=new TableColumn<>("Group");
                col2_1.setStyle(Global.INSTANCE.tableView_type3);
                col2_2.setStyle(Global.INSTANCE.tableView_type3);
                col2_1.setCellValueFactory(cell->cell.getValue().getHap());
                col2_2.setCellValueFactory(cell->cell.getValue().getGroup());
                col2_1.setPrefWidth(140);
                col2_2.setPrefWidth(120);
                jt_haps.getSelectionModel().selectedItemProperty().addListener((obj, oldItem, newItem)->{
                    jt_groups.getSelectionModel().clearSelection();

                    if(newItem==null){
                        graph.nodes().forEach(n -> {
                            replace_ui_style_attribute(n, "shadow-mode", "none");
                        });
                        return;
                    }

                    int r = newItem.getRow();
                    String name = newItem.getHap().getValue();
                    String id = hap_id.get(name);
                    if (id == null) return;
                    graph.nodes().forEach(n -> {
                        if (n.getId().equals(id)) {
                            replace_ui_style_attribute(n, "shadow-mode", "plain");
                            replace_ui_style_attribute(n, "shadow-color", "gray");
                            replace_ui_style_attribute(n, "shadow-offset", "0");
                            replace_ui_style_attribute(n, "shadow-width", "9");
                        } else {
                            replace_ui_style_attribute(n, "shadow-mode", "none");
                        }
                    });
                });
                jt_haps.getColumns().clear();
                jt_haps.getColumns().addAll(col2_1, col2_2);
                //--
                jt_snps.setLayoutX(10);
                jt_snps.setLayoutY(515);
                jt_snps.setPrefWidth(280);
                jt_snps.setPrefHeight(height-485-35);
                TableColumn<SnpItem, String> col3_1=new TableColumn<>("Pos");
                TableColumn<SnpItem, String> col3_2=new TableColumn<>("Snp");
                TableColumn<SnpItem, CheckBox> col3_3=new TableColumn<>("Select");
                col3_1.setStyle(Global.INSTANCE.tableView_type3);
                col3_2.setStyle(Global.INSTANCE.tableView_type3);
                col3_3.setStyle(Global.INSTANCE.tableView_type3);
                col3_1.setCellValueFactory(cell->cell.getValue().getRealPos());
                col3_2.setCellValueFactory(cell->cell.getValue().getSnp());
                col3_3.setCellValueFactory(cell->cell.getValue().getSelect());
                col3_1.setPrefWidth(140);
                col3_2.setPrefWidth(70);
                col3_3.setPrefWidth(50);
                jt_snps.getSelectionModel().selectedItemProperty().addListener((obj, oldItem, newItem)->{
                    jt_groups.getSelectionModel().clearSelection();
                    jt_haps.getSelectionModel().clearSelection();
                });
                jt_snps.getColumns().clear();
                jt_snps.getColumns().addAll(col3_1, col3_2, col3_3);
            }
            if(!main_pane.getChildren().contains(p_pane1)) main_pane.getChildren().add(p_pane1);
        }

        {
            viewer.enableAutoLayout();
            view.enableMouseOptions();

            view.setPrefWidth(width- right_size);
            view.setPrefHeight(height);

            view.setOnMouseClicked(e->{
                try{
                    jt_groups.getSelectionModel().clearSelection();
                    jt_haps.getSelectionModel().clearSelection();

                    if(e.getButton()== MouseButton.PRIMARY){
                        display_frame.hide();
                        is_current_display_panel=false;
                    }else if(e.getButton()==MouseButton.SECONDARY){
                        if(display_frame.isShowing()){
                            if(is_current_display_panel){
                                display_frame.hide();
                                is_current_display_panel=false;
                            }else{
                                is_current_display_panel=true;
                            }
                        }else{
                            is_current_display_panel=false;
                        }
                    }
                }catch (Exception e1){
                    viewer.disableAutoLayout();
                    viewer.enableAutoLayout();
                }
            });
            view.setOnMouseMoved(e->{
                try{
                    if(is_current_display_panel) return;
                    if(!is_display_hover) return;

                    double x=e.getScreenX();
                    double y=e.getScreenY();

                    Node node=(Node)view.findGraphicElementAt(EnumSet.of(InteractiveElement.NODE), e.getX(), e.getY());
                    if(node!=null){
                        Vertex v= id_vertex.get(node.getId());

                        Pane display_panel=new Pane();

                        int display_width=0, display_height=0;
                        {
                            List<String> contents=new ArrayList<>();

                            int num=3, tmp_width=150, one_height=20, group_height1=0, group_height2=0;

                            display_width=tmp_width;
                            display_height=num*one_height+5;
                            if(groups!=null && id_name_freq2.containsKey(node.getId())){
                                display_width=tmp_width=250;
                                int num1=id_name_freq2.get(node.getId()).size();
                                if(num1>5) num1=5;
                                group_height1=(num1+1)*one_height;
                                int num2=v.names.length;
                                if(num2>5) num2=5;
                                group_height2=(num2+1)*one_height;
                                display_height+=group_height1+group_height2;
                            }else{
                                int num1=v.names.length;
                                if(num1>5) num1=5;
                                display_height+=(num1*one_height);
                            }

                            display_panel.setPrefWidth(display_width);
                            display_panel.setPrefHeight(display_height);

                            int tmp_w=5, tmp_h=0;
                            Label label1=new Label("ID="+v.id);
                            label1.setStyle(Global.INSTANCE.lable_type2);
                            label1.setLayoutX(tmp_w);
                            label1.setLayoutY(tmp_h);
                            label1.setPrefWidth(tmp_width-tmp_w);
                            label1.setPrefHeight(one_height);
                            display_panel.getChildren().add(label1);
                            contents.add("ID="+v.id);

                            Label save=new Label(" Save");
                            save.setStyle(Global.INSTANCE.lable_type2_for_button);
                            save.setLayoutX(tmp_width-30);
                            save.setLayoutY(tmp_h);
                            save.setPrefWidth(30);
                            save.setPrefHeight(one_height);
                            save.setOnMouseClicked(e1->{
                                FileChooser chooser=new FileChooser();
                                if(pre_file_path!=null) chooser.setInitialDirectory(new File(pre_file_path));
                                chooser.setInitialFileName(v.name+".txt");
                                chooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Save Node information", "*.txt"));
                                File file=chooser.showSaveDialog(null);
                                if(file!=null){
                                    Global.INSTANCE.write_file_content(file.getAbsolutePath(), contents);
                                }
                            });
                            display_panel.getChildren().add(save);
                            tmp_h+=one_height;

                            Label label2=new Label("Name="+v.name);
                            label2.setStyle(Global.INSTANCE.lable_type2);
                            label2.setLayoutX(tmp_w);
                            label2.setLayoutY(tmp_h);
                            label2.setPrefWidth(tmp_width-tmp_w);
                            label2.setPrefHeight(one_height);
                            tmp_h+=one_height;
                            display_panel.getChildren().add(label2);
                            contents.add("Name="+v.name);

                            Label label3=new Label("Weight="+((int)v.weight));
                            label3.setStyle(Global.INSTANCE.lable_type2);
                            label3.setLayoutX(tmp_w);
                            label3.setLayoutY(tmp_h);
                            label3.setPrefWidth(tmp_width-tmp_w);
                            label3.setPrefHeight(one_height);
                            tmp_h+=one_height;
                            display_panel.getChildren().add(label3);
                            contents.add("Weight="+((int)v.weight));

                            if(groups!=null && id_name_freq2.containsKey(node.getId())){
                                final Map<String, Integer> freq1=id_name_freq1.get(v.id+"");
                                final Map<String, Double> freq2= id_name_freq2.get(v.id+"");
                                String []keys=freq2.keySet().toArray(new String[0]);
                                Arrays.sort(keys, new Comparator<String>() {
                                    @Override
                                    public int compare(String o1, String o2) {
                                        double f1=freq2.get(o1);
                                        double f2=freq2.get(o2);
                                        if(f1>f2) return -1;
                                        if(f1<f2) return 1;
                                        return 0;
                                    }
                                });
                                TableView jt1=new XTableView2();
                                jt1.setLayoutX(tmp_w);
                                jt1.setLayoutY(tmp_h);
                                jt1.setPrefWidth(tmp_width-tmp_w-tmp_w);
                                jt1.setPrefHeight(group_height1-tmp_w);
                                TableColumn<NodeItem1, String> col1_1=new TableColumn<>("Group");
                                TableColumn<NodeItem1, Integer> col1_2=new TableColumn<>("Num");
                                TableColumn<NodeItem1, Double> col1_3=new TableColumn<>("Proportion");
                                col1_1.setStyle(Global.INSTANCE.tableView_type3);
                                col1_2.setStyle(Global.INSTANCE.tableView_type3);
                                col1_3.setStyle(Global.INSTANCE.tableView_type3);
                                col1_1.setCellValueFactory(cell->cell.getValue().getGroup());
                                col1_2.setCellValueFactory(cell->cell.getValue().getNum());
                                col1_3.setCellValueFactory(cell->cell.getValue().getRate());
                                col1_1.setPrefWidth((tmp_width-2*tmp_w)/2.0-30.0);
                                col1_2.setPrefWidth(40.0);
                                col1_3.setPrefWidth((tmp_width-2*tmp_w)/2.0-30.0);
                                jt1.getColumns().addAll(col1_1, col1_2, col1_3);
                                {
                                    contents.add("#Group\tNum\tProportion");
                                    ObservableList<NodeItem1> data=FXCollections.observableArrayList();
                                    for(int i=0;i<keys.length;i++){
                                        data.add(new NodeItem1(keys[i], freq1.get(keys[i]), freq2.get(keys[i])));
                                        contents.add(keys[i]+"\t"+freq1.get(keys[i])+"\t"+freq2.get(keys[i]));
                                    }
                                    jt1.setItems(data);
                                }
                                tmp_h+=group_height1;
                                display_panel.getChildren().add(jt1);

                                TableView jt2=new XTableView2();
                                jt2.setLayoutX(tmp_w);
                                jt2.setLayoutY(tmp_h);
                                jt2.setPrefWidth(tmp_width-tmp_w-tmp_w);
                                jt2.setPrefHeight(group_height2-tmp_w);
                                TableColumn<NodeItem2, String> col2_1=new TableColumn<>("Group");
                                TableColumn<NodeItem2, String> col2_2=new TableColumn<>("HapName");
                                col2_1.setStyle(Global.INSTANCE.tableView_type3);
                                col2_2.setStyle(Global.INSTANCE.tableView_type3);
                                col2_1.setPrefWidth((tmp_width-2*tmp_w)/2.0-10.0);
                                col2_2.setPrefWidth((tmp_width-2*tmp_w)/2.0-10.0);
                                col2_1.setCellValueFactory(cell->cell.getValue().getGroup());
                                col2_2.setCellValueFactory(cell->cell.getValue().getHap());
                                jt2.getColumns().addAll(col2_1, col2_2);
                                {
                                    contents.add("#Group\tHapName");
                                    ObservableList<NodeItem2> data=FXCollections.observableArrayList();
                                    for(String group:keys){
                                        for(String name:v.names){
                                            String g=hap_group.get(name);
                                            if(g==null || !g.equals(group)) continue;
                                            data.add(new NodeItem2(group, name));
                                            contents.add(group+"\t"+name);
                                        }
                                    }
                                    jt2.setItems(data);
                                }
                                tmp_h+=group_height2;
                                display_panel.getChildren().add(jt2);
                            }
                            else{
                                ListView<String> listView=new ListView<>();
                                listView.setLayoutX(tmp_w);
                                listView.setLayoutY(tmp_h);
                                listView.setPrefWidth(tmp_width-2*tmp_w);
                                listView.setPrefHeight(display_height-tmp_h-5);
                                listView.setCellFactory(cell->{
                                    return new ListCell<String>() {
                                        @Override
                                        protected void updateItem(String item, boolean empty) {
                                            super.updateItem(item, empty);
                                            if (empty || item == null) {
                                                setText(null);
                                            } else {
                                                setText(item);
                                                setStyle(Global.INSTANCE.tableView_type3);
                                            }
                                        }
                                    };
                                });
                                for(String name:v.names){
                                    listView.getItems().add(name);
                                    contents.add(name);
                                }
                                display_panel.getChildren().add(listView);
                            }
                        }

                        {
                            double p_x, p_y;
                            if((view.getWidth()-e.getX())>display_width){
                                p_x=x+10;
                                if((view.getHeight()-e.getY())>display_height) p_y=y+10;
                                else p_y=y-10-display_height;
                            }else{
                                p_x=x-10-display_width;
                                if((view.getHeight()-e.getY())>display_height) p_y=y+10;
                                else p_y=y-10-display_height;
                            }
                            display_frame.setX(p_x);
                            display_frame.setY(p_y);
                        }

                        display_frame.setScene(new Scene(display_panel, display_width, display_height));
                        display_frame.show();
                        return;
                    }

                    Edge edge=null;
                    {
                        double x3=e.getX();
                        double y3=e.getY();

                        Map<String, Point3> hp=new HashMap<>();
                        Collection<GraphicElement> collection=view.allGraphicElementsIn(EnumSet.of(InteractiveElement.NODE), 0, 0, width, height);
                        for(GraphicElement ele :collection){
                            hp.put(((Node)ele).getId(), renderer.getCamera().transformGuToPx(ele.getX(), ele.getY(), 0));
                        }

                        int span=3;
                        Iterator<Edge> iter=graph.edges().iterator();
                        while (iter.hasNext()){
                            Edge tmp_edge=iter.next();
                            Node n1=tmp_edge.getSourceNode();
                            Node n2=tmp_edge.getTargetNode();
                            Point3 p1=hp.get(n1.getId());
                            Point3 p2=hp.get(n2.getId());
                            if(p1!=null && p2!=null){
                                double x1=p1.x;
                                double y1=p1.y;
                                double x2=p2.x;
                                double y2=p2.y;
                                if(Math.abs(x1-x2)<0.5){
                                    if(Math.abs(x3-x1)<span){
                                        if((y3>y1 && y3<y2) || (y3<y1 && y3>y2)){
                                            edge=tmp_edge;
                                            break;
                                        }
                                    }
                                }else{
                                    if((x3>x1 && x3<x2) || (x3>x2 && x3<x1)){
                                        if((y3>y1 && y3<y2) || (y3>y2 && y3<y1)){
                                            double a=(y2-y1)/(x2-x1);
                                            double b=y1-a*x1;
                                            if(Math.abs(a*x3+b-y3)<span){
                                                edge=tmp_edge;
                                                break;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if(edge!=null){
                        List<String> contents=new ArrayList<>();

                        Map<Integer, String> hp=edge_pos_snps.get(edge.getId());

                        Pane display_panel=new Pane();

                        int display_width=0, display_height=0, tmp_width=150, one_height=20, num=hp==null ? 0:hp.size();
                        if(num>5) num=5;
                        display_width=tmp_width;
                        display_height=hp==null ? one_height : ((num+2)*one_height);

                        display_panel.setPrefWidth(display_width);
                        display_panel.setPrefHeight(display_height);

                        {
                            Node n1=edge.getSourceNode();
                            Node n2=edge.getTargetNode();

                            int tmp_w=5, tmp_h=0;

                            Label label2=new Label("MutationNum="+edge_label.get(edge.getId()));
                            label2.setStyle(Global.INSTANCE.lable_type2);
                            label2.setLayoutX(tmp_w);
                            label2.setLayoutY(tmp_h);
                            label2.setPrefWidth(tmp_width-tmp_w);
                            label2.setPrefHeight(one_height);
                            display_panel.getChildren().add(label2);
                            contents.add("MutationNum="+edge_label.get(edge.getId()));

                            Label save=new Label(" Save");
                            save.setStyle(Global.INSTANCE.lable_type2_for_button);
                            save.setLayoutX(tmp_width-30);
                            save.setLayoutY(tmp_h);
                            save.setPrefWidth(30);
                            save.setPrefHeight(one_height);
                            save.setOnMouseClicked(e1->{
                                FileChooser chooser=new FileChooser();
                                if(pre_file_path!=null) chooser.setInitialDirectory(new File(pre_file_path));
                                Vertex v1=id_vertex.get(n1.getId());
                                Vertex v2=id_vertex.get(n2.getId());
                                chooser.setInitialFileName(v1.name+"_"+v1.name+".txt");
                                chooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Save Node information", "*.txt"));
                                File file=chooser.showSaveDialog(null);
                                if(file!=null){
                                    Global.INSTANCE.write_file_content(file.getAbsolutePath(), contents);
                                }
                            });
                            display_panel.getChildren().add(save);
                            tmp_h+=one_height;

                            if(hp!=null && hp.size()>0){
                                display_height+=10;

                                TableView jt=new XTableView2();
                                jt.setLayoutX(tmp_w);
                                jt.setLayoutY(tmp_h);
                                jt.setPrefWidth(tmp_width-2*tmp_w);
                                jt.setPrefHeight(display_height-tmp_h-5);
                                TableColumn<EdgeItem, String> col1=new TableColumn<>("Position");
                                TableColumn<EdgeItem, String> col2=new TableColumn<>(n1.getId());
                                TableColumn<EdgeItem, String> col3=new TableColumn<>(n2.getId());
                                col1.setStyle(Global.INSTANCE.tableView_type3);
                                col2.setStyle(Global.INSTANCE.tableView_type3);
                                col3.setStyle(Global.INSTANCE.tableView_type3);
                                col1.setCellValueFactory(cell->cell.getValue().getRealPos());
                                col2.setCellValueFactory(cell->cell.getValue().getAlt1());
                                col3.setCellValueFactory(cell->cell.getValue().getAlt2());
                                col1.setPrefWidth(tmp_width-2*tmp_w-70.0);
                                col2.setPrefWidth(25.0);
                                col3.setPrefWidth(25.0);
                                jt.getColumns().addAll(col1, col2, col3);
                                {
                                    contents.add("#Position\t"+n1.getId()+"\t"+n2.getId());
                                    ObservableList<EdgeItem> data=FXCollections.observableArrayList();
                                    for(Integer key:hp.keySet()){
                                        EdgeItem edgeItem=new EdgeItem(key, hp.get(key));
                                        data.add(edgeItem);
                                        contents.add(edgeItem.getRealPos().getValue()+"\t"+edgeItem.getAlt1().getValue()+"\t"+edgeItem.getAlt2().getValue());
                                    }
                                    jt.setItems(data);
                                }
                                display_panel.getChildren().add(jt);
                            }
                        }

                        {
                            double p_x, p_y;
                            if((view.getWidth()-e.getX())>display_width){
                                p_x=x+10;
                                if((view.getHeight()-e.getY())>display_height) p_y=y+10;
                                else p_y=y-10-display_height;
                            }else{
                                p_x=x-10-display_width;
                                if((view.getHeight()-e.getY())>display_height) p_y=y+10;
                                else p_y=y-10-display_height;
                            }
                            display_frame.setX(p_x);
                            display_frame.setY(p_y);
                        }

                        display_frame.setScene(new Scene(display_panel, display_width, display_height));
                        display_frame.show();
                        return;
                    }

                    display_frame.hide();
                }catch (Exception e1){
                    viewer.disableAutoLayout();
                    viewer.enableAutoLayout();
                }
            });
            view.setOnMousePressed(e->{
                try{
                    is_in_dragged=false;

                    if(jl_legend!=null && view.getChildren().contains(jl_legend)){
                        double x_min=jl_legend.getLayoutX();
                        double x_max=x_min+jl_legend.getPrefWidth();
                        double y_min=jl_legend.getLayoutY();
                        double y_max=y_min+jl_legend.getPrefHeight();
                        double x=e.getX();
                        double y=e.getY();
                        if(x>=x_min && x<=x_max && y>=y_min && y<=y_max) return;
                    }

                    Node node=(Node)view.findGraphicElementAt(EnumSet.of(InteractiveElement.NODE), e.getX(), e.getY());
                    if(node!=null) return;
                    Edge edge=null;
                    {
                        double x3=e.getX();
                        double y3=e.getY();

                        Map<String, Point3> hp=new HashMap<>();
                        Collection<GraphicElement> collection=view.allGraphicElementsIn(EnumSet.of(InteractiveElement.NODE), 0, 0, width, height);
                        for(GraphicElement ele :collection){
                            hp.put(((Node)ele).getId(), renderer.getCamera().transformGuToPx(ele.getX(), ele.getY(), 0));
                        }

                        int span=3;
                        Iterator<Edge> iter=graph.edges().iterator();
                        while (iter.hasNext()){
                            Edge tmp_edge=iter.next();
                            Node n1=tmp_edge.getSourceNode();
                            Node n2=tmp_edge.getTargetNode();
                            Point3 p1=hp.get(n1.getId());
                            Point3 p2=hp.get(n2.getId());
                            if(p1!=null && p2!=null){
                                double x1=p1.x;
                                double y1=p1.y;
                                double x2=p2.x;
                                double y2=p2.y;
                                if(Math.abs(x1-x2)<0.5){
                                    if(Math.abs(x3-x1)<span){
                                        if((y3>y1 && y3<y2) || (y3<y1 && y3>y2)){
                                            edge=tmp_edge;
                                            break;
                                        }
                                    }
                                }else{
                                    if((x3>x1 && x3<x2) || (x3>x2 && x3<x1)){
                                        if((y3>y1 && y3<y2) || (y3>y2 && y3<y1)){
                                            double a=(y2-y1)/(x2-x1);
                                            double b=y1-a*x1;
                                            if(Math.abs(a*x3+b-y3)<span){
                                                edge=tmp_edge;
                                                break;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if(edge!=null) return;

                    is_in_dragged=true;

                    Camera cam = view.getCamera();
                    Point3 p1=cam.transformPxToGu(e.getX(), e.getY());
                    Point3 p2=cam.getViewCenter();
                    view_x1=p1.x;
                    view_y1=p1.y;
                    view_x2=p2.x;
                    view_y2=p2.y;
                }catch (Exception e1){
                    viewer.disableAutoLayout();
                    viewer.enableAutoLayout();
                }
            });
            view.setOnMouseDragReleased(e->{
                try{
                    is_in_dragged=false;
                }catch (Exception e1){
                    viewer.disableAutoLayout();
                    viewer.enableAutoLayout();
                }
            });
            view.setOnMouseDragged(e->{
                try{
                    if(is_in_dragged){
                        Camera cam = view.getCamera();
                        Point3 p1=cam.transformPxToGu(e.getX(), e.getY());
                        cam.setViewCenter(view_x2+(view_x1-p1.x), view_y2+(view_y1-p1.y), 0);
                    }
                }catch (Exception e1){
                    viewer.disableAutoLayout();
                    viewer.enableAutoLayout();
                }
            });
            view.setOnScroll(e->{
                try{
                    e.consume();
                    double factor = e.getDeltaY()>0.0 ? 0.97 : 1.03;

                    graph.nodes().forEach(n->{
                        String id=n.getId();
                        double pre_size=Global.INSTANCE.id_size.get(id);
                        double size=pre_size/factor;
                        Global.INSTANCE.id_size.put(id, size);
                        replace_ui_style_attribute(n, "size", size+"");
                        Sprite sprite=id_sprite.get(id);
                        replace_ui_style_attribute(sprite, "size", (size-2.0*line_width)+"");
                    });
                    flush_graph();

                    Camera cam = view.getCamera();
                    double zoom = cam.getViewPercent() * factor;
                    Point2 pxCenter  = cam.transformGuToPx(cam.getViewCenter().x, cam.getViewCenter().y, 0);
                    Point3 guClicked = cam.transformPxToGu(e.getX(), e.getY());
                    double newRatioPx2Gu = cam.getMetrics().ratioPx2Gu/factor;
                    double x = guClicked.x + (pxCenter.x - e.getX())/newRatioPx2Gu;
                    double y = guClicked.y - (pxCenter.y - e.getY())/newRatioPx2Gu;
                    cam.setViewCenter(x, y, 0);
                    cam.setViewPercent(zoom);
                }catch (Exception e1){
                    viewer.disableAutoLayout();
                    viewer.enableAutoLayout();
                }
            });

            if(!main_pane.getChildren().contains(view)) main_pane.getChildren().add(view);
        }
    }

    public void read_vcf(){
        String chr=tf_chr.getText();
        if(chr==null || chr.trim().length()==0){
            Alert alert=new Alert(Alert.AlertType.ERROR, "chr field cannot be null!");
            alert.showAndWait();
            return;
        }else{
            chr=chr.trim();
        }

        int min_pos=0;
        String str=tf_min_chr_pos.getText();
        if(str==null || str.trim().length()==0){
            Alert alert=new Alert(Alert.AlertType.ERROR, "chr-min-pos field cannot be null!");
            alert.showAndWait();
            return;
        }else if(!is_integer(str.trim())){
            Alert alert=new Alert(Alert.AlertType.ERROR, "chr-min-pos field should be integer!");
            alert.showAndWait();
            return;
        }else{
            min_pos=Integer.parseInt(str.trim());
        }

        int max_pos=0;
        str=tf_max_chr_pos.getText();
        if(str==null || str.trim().length()==0){
            Alert alert=new Alert(Alert.AlertType.ERROR, "chr-max-pos field cannot be null!");
            alert.showAndWait();
            return;
        }else if(!is_integer(str.trim())){
            Alert alert=new Alert(Alert.AlertType.ERROR, "chr-max-pos field should be integer!");
            alert.showAndWait();
            return;
        }else{
            max_pos=Integer.parseInt(str.trim());
        }

        if(min_pos>=max_pos){
            Alert alert=new Alert(Alert.AlertType.ERROR, "chr-min-pos field should be less than chr-max-pos field!");
            alert.showAndWait();
            return;
        }

        String cmd_path=Global.INSTANCE.get_network_exe_path();
        int id=new Random().nextInt(10000);
        String output=Global.INSTANCE.dll_exe_dir+id;
        try {
            str=chr+":"+min_pos+"-"+max_pos;
            if(Global.INSTANCE.os.contains("mac")){
                String name1=id+Global.INSTANCE.get_file_posfix(this.graph_file);
                String name2=Global.INSTANCE.dll_exe_dir+name1;
                if(new File(name2).exists()) new File(name2).delete();
                Files.createSymbolicLink(Paths.get(name2), Paths.get(this.graph_file));

                String cmd=cmd_path+" mjn -i "+name1+" -chr "+str+" -o "+id;
                System.out.println("cmd="+cmd);

                InputStream is=Runtime.getRuntime().exec(cmd, null, new File(Global.INSTANCE.dll_exe_dir)).getInputStream();
                byte []buff=new byte[1024];
                while (is.read(buff)!=-1);
                is.close();

                new File(name2).delete();
            }else{
                byte []arr1=Global.INSTANCE.str_2_encoding_array(this.graph_file);
                byte []arr2=Global.INSTANCE.str_2_encoding_array(str);
                byte []arr3=Global.INSTANCE.str_2_encoding_array(output);

                NetworkNative.generate_network(arr1, arr2, arr3);
            }
        }catch (Exception e1){e1.printStackTrace();}
        //--
        clear_graph();
        read_graph(output+".gml");
        //--
        try {
            phy_pos_map.clear();

            InputStream is=new FileInputStream(output+".map");
            InputStreamReader isr=new InputStreamReader(is);
            BufferedReader br=new BufferedReader(isr);

            while (true){
                String line=br.readLine();
                if(line==null) break;
                line=line.trim();
                if(line.length()==0) continue;
                String []ss=line.split("\t");
                phy_pos_map.put(Integer.parseInt(ss[0]), Integer.parseInt(ss[1]));
            }

            br.close();
            isr.close();
            is.close();
        }catch (Exception e1){
            e1.printStackTrace();
        }
        //--
        read_hap_group_file(output+".phy");
        //--
        //new File(output+".gml").delete();
        //new File(output+".json").delete();
        //new File(output+".phy").delete();
        //new File(output+".map").delete();
        //--
        phy_pre_status=new boolean[phy_pos_snps.size()];
        ObservableList<SnpItem> data=FXCollections.observableArrayList();
        int row=0;
        for(Integer pos:phy_pos_snps.keySet()){
            data.add(new SnpItem(row++, pos, phy_pos_snps.get(pos)));
        }
        jt_snps.getItems().clear();
        jt_snps.setItems(data);
        p_pane1.getChildren().add(jt_snps);
        flush_graph();
    }

    public void read_graph(String file){
        graph.clear();
        id_vertex.clear();
        Global.INSTANCE.id_size.clear();
        edge_pos_snps.clear();
        hap_id.clear();
        edge_label.clear();
        id_sprite.clear();
        hap_group.clear();
        group_color.clear();
        id_name_freq1.clear();
        id_name_freq2.clear();
        groups=null;
        is_current_display_panel=false;

        log("reading ("+file+")...");

        java.util.List<Vertex> list=new ArrayList<>();
        try {
            InputStream is=null;
            InputStreamReader isr=null;
            BufferedReader br=null;
            try {
                is=new FileInputStream(file);
                isr=new InputStreamReader(is);
                br=new BufferedReader(isr);

                StringBuilder sb=new StringBuilder();
                while (true){
                    String line=br.readLine();
                    if(line==null) break;
                    sb.append(line.trim());
                    sb.append(' ');
                }
                String str_graph=sb.toString();

                java.util.List<String> list1=new ArrayList<>();
                List<String> list2=new ArrayList<>();
                int index=0;
                while (true){
                    int a=str_graph.indexOf("node", index);
                    if(a==-1) break;
                    for(int i=a+4;i<str_graph.length();i++){
                        char c=str_graph.charAt(i);
                        if(c==' ') continue;
                        if(c=='[') list1.add(get_element(str_graph, i));
                        index=i;
                        break;
                    }
                }
                index=0;
                while (true){
                    int a=str_graph.indexOf("edge", index);
                    if(a==-1) break;
                    for(int i=a+4;i<str_graph.length();i++){
                        char c=str_graph.charAt(i);
                        if(c==' ') continue;
                        if(c=='[') list2.add(get_element(str_graph, i));
                        index=i;
                        break;
                    }
                }

                min_node_weight=Double.MAX_VALUE;
                max_node_weight=Double.MIN_VALUE;
                for(String str:list1){
                    Vertex v;
                    if(false){
                        int b=str.indexOf("frequency=");
                        int c=str.indexOf('"', b);
                        String []ss=str.substring(b+10, c).split(" ");
                        double freq=Double.parseDouble(ss[0].trim());
                        String label=ss[1].trim();
                        //--
                        int d=str.indexOf(" id ");
                        int e=str.indexOf(" ", d+4);
                        if(e==-1) e=str.indexOf(']', d+4);
                        String id=str.substring(d+4, e).trim();
                        v=new Vertex(id, label, freq);
                    }else{
                        v=new Vertex(str);
                    }
                    id_vertex.put(v.id, v);
                    if(min_node_weight>v.weight) min_node_weight=v.weight;
                    if(max_node_weight<v.weight) max_node_weight=v.weight;
                    list.add(v);

                    for(String name:v.names) hap_id.put(name, v.id+"");
                }
                is_all_node_equal=min_node_weight==max_node_weight;

                if(true){
                    if(min_node_weight<1.0){
                        for(Vertex v:list) v.weight2+=(1.0-min_node_weight);
                    }

                    min_node_weight=Double.MAX_VALUE;
                    max_node_weight=Double.MIN_VALUE;
                    for(Vertex v:list){
                        if(min_node_weight>v.weight2) min_node_weight=v.weight2;
                        if(max_node_weight<v.weight2) max_node_weight=v.weight2;
                    }

                    for(Vertex v:list) v.weight2/=min_node_weight;

                    min_node_weight=Double.MAX_VALUE;
                    max_node_weight=Double.MIN_VALUE;
                    for(Vertex v:list){
                        if(min_node_weight>v.weight2) min_node_weight=v.weight2;
                        if(max_node_weight<v.weight2) max_node_weight=v.weight2;
                    }

                    int size=list.size();
                    if(size>1000) min_node_size*=0.8;
                    else if(size>500) min_node_size*=0.9;

                    max_node_size=max_node_weight*min_node_size;
                    if(max_node_size>30) max_node_size=30;

                    if(is_all_node_equal){
                        node_size_slope=0.0;
                        node_size_intercept=min_node_size;
                    }else{
                        node_size_slope=(max_node_size-min_node_size)/(max_node_weight-min_node_weight);
                        node_size_intercept=min_node_size-node_size_slope*min_node_weight;
                    }
                }

                for(Vertex v:list){
                    graph.addNode(v.id+"");
                    Sprite sprite=sman.addSprite("sprite_"+v.id);
                    sprite.attachToNode(v.id+"");
                    id_sprite.put(v.id, sprite);
                }
                if(list.size()>500){
                    cb_hover.setSelected(false);
                    is_display_hover=false;
                }

                min_edge_weight=Double.MAX_VALUE;
                max_edge_weight=Double.MIN_VALUE;

                edge_weight.clear();

                int edge_id=1;
                for(String str:list2){
                    String name="edge"+edge_id;
                    String name2="sprite"+edge_id;
                    edge_id++;
                    //--
                    int b=str.indexOf("Changes");
                    int c=str.indexOf('"', b);
                    int d=str.indexOf('"', c+1);
                    String label=str.substring(c+1, d);
                    //--
                    int g=str.indexOf("Change_snps");
                    if(g!=-1){
                        int h=str.indexOf('"', g);
                        int l=str.indexOf('"', h+2);
                        if(h!=-1 && l!=-1){
                            String []ss=str.substring(h, l).trim().replaceAll("\"", "").split("\\|");
                            Map<Integer, String> hp=edge_pos_snps.get(name);
                            if(hp==null) hp=new LinkedHashMap<>();
                            for(String s:ss){
                                String []ts=s.split(":");
                                hp.put(Integer.parseInt(ts[0]), ts[1]);
                            }
                            edge_pos_snps.put(name, hp);
                        }
                    }
                    //--
                    int e=str.indexOf(" source ");
                    int f=str.indexOf(" ", e+8);
                    if(f==-1) f=str.indexOf(']', e+8);
                    String id1=str.substring(e+8, f).trim();
                    e=str.indexOf(" target ");
                    f=str.indexOf(" ", e+8);
                    if(f==-1) f=str.indexOf(']', e+8);
                    String id2=str.substring(e+8, f).trim();
                    //--
                    graph.addEdge(name, id1+"", id2+"");
                    Edge edge=graph.getEdge(name);
                    //--
                    double weight=Double.parseDouble(label);
                    if(weight<min_edge_weight) min_edge_weight=weight;
                    if(weight>max_edge_weight) max_edge_weight=weight;
                    edge_weight.put(name, weight);
                    edge_label.put(name, label);
                }
                is_all_edge_equal=min_edge_weight==max_edge_weight;

                if(true){
                    String []keys=edge_weight.keySet().toArray(new String[0]);

                    if(min_edge_weight<1.0){
                        for(String key:keys){
                            double pre=edge_weight.get(key);
                            edge_weight.put(key, pre+(1.0-min_edge_weight));
                        }
                    }

                    min_edge_weight=Double.MAX_VALUE;
                    max_edge_weight=Double.MIN_VALUE;
                    for(Double v:edge_weight.values()){
                        if(min_edge_weight>v) min_edge_weight=v;
                        if(max_edge_weight<v) max_edge_weight=v;
                    }

                    for(String key:keys){
                        double pre=edge_weight.get(key);
                        edge_weight.put(key, pre/min_edge_weight);
                    }

                    min_edge_weight=Double.MAX_VALUE;
                    max_edge_weight=Double.MIN_VALUE;
                    for(Double v:edge_weight.values()){
                        if(min_edge_weight>v) min_edge_weight=v;
                        if(max_edge_weight<v) max_edge_weight=v;
                    }

                    max_edge_size = max_edge_weight*min_edge_size;
                    if(max_edge_size>5.0) max_edge_size=5.0;

                    if(is_all_edge_equal){
                        edge_size_slope=0.0;
                        edge_size_intercept=min_edge_size;
                    }else{
                        edge_size_slope=(max_edge_size-min_edge_size)/(max_edge_weight-min_edge_weight);
                        edge_size_intercept=min_edge_size-edge_size_slope*min_edge_weight;
                    }
                }

                tf_min_node_size.setText(min_node_size+"");
                tf_max_node_size.setText(max_node_size+"");
                tf_min_edge_size.setText(min_edge_size +"");
                tf_max_edge_size.setText(max_edge_size +"");

                flush_graph();
            }finally {
                if(br!=null) br.close();
                if(isr!=null) isr.close();
                if(is!=null) is.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private String get_element(String str, int from){
        int index=0;
        boolean is_first=true;
        for(int i=from;i<str.length();i++){
            char c=str.charAt(i);
            if(c=='['){
                index++;
                is_first=false;
            }else if(c==']'){
                index--;
            }
            if(!is_first && index==0) return str.substring(from, i+1);
        }
        return null;
    }

    public void read_hap_group_file(String file){
        try {
            InputStream is=null;
            InputStreamReader isr=null;
            BufferedReader br=null;
            OutputStream os=null;
            OutputStreamWriter osw=null;
            BufferedWriter bw=null;
            try {
                is=new FileInputStream(file);
                if(file.endsWith(".gz")) is=new GZIPInputStream(is);
                isr=new InputStreamReader(is, "UTF-8");
                br=new BufferedReader(isr);

                this.hap_group.clear();
                this.group_color.clear();
                this.phy_name_seq.clear();
                this.phy_pos_snps.clear();

                if(is_phylip_format(file)){
                    is_group_phylip_format=true;

                    String []ss=br.readLine().trim().replaceAll("\t", " ").split("\\s+");
                    phy_num=Integer.parseInt(ss[0]);
                    phy_len=Integer.parseInt(ss[1]);

                    char [][]seqs=new char[phy_num][];

                    for(int i=0;i<phy_num;i++){
                        ss=br.readLine().trim().replaceAll("\t", " ").split("\\s+");
                        phy_name_seq.put(ss[0], ss[1].toUpperCase());
                        seqs[i]=ss[1].toUpperCase().toCharArray();
                    }

                    for(int j=0;j<phy_len;j++){
                        Set<Character> hs=new HashSet<>();
                        for(int i=0;i<phy_num;i++){
                            char c=seqs[i][j];
                            if(c=='A' || c=='C' || c=='G' || c=='T') hs.add(c);
                        }
                        if(hs.size()==1) continue;
                        StringBuilder sb=new StringBuilder();
                        for(Character c:hs) sb.append(c);
                        phy_pos_snps.put(j+1, sb.toString());
                    }

                    if(edge_pos_snps.size()==0) {
                        graph.edges().forEach(edge -> {
                            Node n1 = edge.getSourceNode();
                            Node n2 = edge.getTargetNode();
                            Vertex v1 = id_vertex.get(n1.getId());
                            Vertex v2 = id_vertex.get(n2.getId());
                            if (phy_name_seq.containsKey(v1.name) && phy_name_seq.containsKey(v2.name)) {
                                char[] cs1 = phy_name_seq.get(v1.name).toUpperCase().toCharArray();
                                char[] cs2 = phy_name_seq.get(v2.name).toUpperCase().toCharArray();
                                Map<Integer, String> hp = new LinkedHashMap<>();
                                for (int i = 0; i < phy_len; i++) {
                                    char c1 = cs1[i];
                                    char c2 = cs2[i];
                                    if (c1 == '-' || c2 == '-' || c1 == c2) continue;
                                    if (c1 != 'A' && c1 != 'C' && c1 != 'G' && c1 != 'T') continue;
                                    if (c2 != 'A' && c2 != 'C' && c2 != 'G' && c2 != 'T') continue;
                                    hp.put(i + 1, c1 + "->" + c2);
                                }
                                edge_pos_snps.put(edge.getId(), hp);
                            }
                        });
                    }
                }else{
                    is_group_phylip_format=false;
                    Set<String> set=new HashSet<>();
                    while (true){
                        String line=br.readLine();
                        if(line==null) break;
                        line=line.trim();
                        if(line.length()==0) continue;
                        line=line.replaceAll("；", " ");
                        line=line.replaceAll(";", " ");
                        line=line.replaceAll("，", " ");
                        line=line.replaceAll(",", " ");
                        line=line.replaceAll("\t", " ");
                        String []ss=line.split("\\s+");
                        if(ss.length==1) continue;
                        this.hap_group.put(ss[0], ss[1]);
                        set.add(ss[1]);
                    }
                    this.groups=set.toArray(new String[0]);
                    Arrays.sort(this.groups);
                    //--
                    Set<String> all_node_names=new HashSet<>();
                    graph.nodes().forEach(node->{
                        Vertex v=id_vertex.get(node.getId());
                        for(String name:v.names){
                            all_node_names.add(name);
                            if(this.hap_group.containsKey(name)) continue;
                            if(name.endsWith("_1") || name.endsWith("_2")){
                                String sub=name.substring(0, name.length()-2);
                                String group=this.hap_group.get(sub);
                                if(group!=null) this.hap_group.put(name, group);
                            }
                        }
                    });
                    String []keys=this.hap_group.keySet().toArray(new String[0]);
                    for(String key:keys){
                        if(!all_node_names.contains(key)) this.hap_group.remove(key);
                    }
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

    private boolean is_phylip_format(String file){
        try {
            InputStream is=null;
            InputStreamReader isr=null;
            BufferedReader br=null;
            OutputStream os=null;
            OutputStreamWriter osw=null;
            BufferedWriter bw=null;
            try {
                is=new FileInputStream(file);
                if(file.endsWith(".gz")) is=new GZIPInputStream(is);
                isr=new InputStreamReader(is, "UTF-8");
                br=new BufferedReader(isr);

                String line=br.readLine();
                if(line==null) return false;
                String []ss=line.trim().replaceAll("\t", " ").split(" ");
                if(ss.length!=2) return false;
                if(!is_integer(ss[1])) return false;
                int len=Integer.parseInt(ss[1]);

                line=br.readLine();
                if(line==null) return false;
                ss=line.trim().replaceAll("\t", " ").split(" ");
                if(ss.length!=2) return false;
                if(ss[1].length()==len) return true;
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

        return false;
    }

    private boolean is_integer(String str){
        try{
            Integer.parseInt(str);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    public void flush_graph(){
        if(graph.nodes().count()==0) return;

        min_node_size=Double.parseDouble(tf_min_node_size.getText());
        if(min_node_size<3.0){
            min_node_size=3.0;
            tf_min_node_size.setText(min_node_size+"");
        }
        max_node_size=Double.parseDouble(tf_max_node_size.getText());
        if(max_node_size<min_node_size) max_node_size=min_node_size;

        min_edge_size = Double.parseDouble(tf_min_edge_size.getText());
        if (min_edge_size < 0.1){
            tf_min_edge_size.setText(min_edge_size+"");
            min_edge_size = 0.1;
        }
        max_edge_size = Double.parseDouble(tf_max_edge_size.getText());
        if (max_edge_size < min_edge_size) max_edge_size = min_edge_size;

        if(is_all_node_equal){
            node_size_slope=0.0;
            node_size_intercept=min_node_size;
        }else{
            node_size_slope=(max_node_size-min_node_size)/(max_node_weight-min_node_weight);
            node_size_intercept=min_node_size-node_size_slope*min_node_weight;
        }

        if(is_all_edge_equal){
            edge_size_slope=0.0;
            edge_size_intercept=min_edge_size;
        }else{
            edge_size_slope=(max_edge_size-min_edge_size)/(max_edge_weight-min_edge_weight);
            edge_size_intercept=min_edge_size-edge_size_slope*min_edge_weight;
        }

        double value= Double.parseDouble(tf_edge_force.getText().trim());
        if(value<0.01){
            value=0.01;
            tf_edge_force.setText(value+"");
        }else if(value>5.0){
            value=5.0;
            tf_edge_force.setText(value+"");
        }
        final double edge_rate=value;

        value=Double.parseDouble(tf_node_gravity.getText().trim());
        if(value<0.0){
            value=0.0;
            tf_node_gravity.setText(value+"");
        }else if(value>1.0){
            value=1.0;
            tf_node_gravity.setText(value+"");
        }
        BarnesHutLayout.gravity=value;

        value=Double.parseDouble(tf_line_width.getText().trim());
        if(value<0.01){
            value=0.01;
            tf_line_width.setText(value+"");
        }else if(value>9.0){
            value=9.0;
            tf_line_width.setText(value+"");
        }
        line_width=value;

        graph.nodes().forEach(node->{
            Vertex v=id_vertex.get(node.getId());
            double bp2=node_size_slope*v.weight2+node_size_intercept;
            Global.INSTANCE.id_size.put(v.id, bp2);

            replace_ui_style_attribute(node, "shape", "circle");
            replace_ui_style_attribute(node, "size", bp2+"");
            replace_ui_style_attribute(node, "text-alignment", "above");
            replace_ui_style_attribute(node, "text-size", name_size+"");
            replace_ui_style_attribute(node, "text-color", "black");

            double size2=bp2;
            if(v.isIntermediate){
                if(!cb_node_intermediate_outline.isSelected()) size2-=(2.0*line_width);
            }else{
                if(!cb_node_true_outline.isSelected()) size2-=(2.0*line_width);
            }

            Sprite sprite=id_sprite.get(v.id);
            sprite.removeAttribute("ui.pie-values");
            String color=v.isIntermediate ? color_2_str(node_color2.getValue()) : color_2_str(node_color1.getValue());
            replace_ui_style_attribute(sprite, "shape", "circle");
            replace_ui_style_attribute(sprite, "size", size2+"");
            replace_ui_style_attribute(sprite, "fill-color", color);
        });

        graph.edges().forEach(edge->{
            Node n1=edge.getSourceNode();
            Node n2=edge.getTargetNode();
            String id1=n1.getId();
            String id2=n2.getId();

            if(is_all_edge_equal){
                edge.setAttribute("layout.weight", edge_rate);
                edge.setAttribute("layout, force", edge_rate);
                Global.INSTANCE.edge_size.put(id1+"\t"+id2, edge_rate);
                Global.INSTANCE.edge_size.put(id2+"\t"+id1, edge_rate);
            }else{
                double w1=((Global.INSTANCE.id_size.get(id1)-min_node_size)/min_node_size);
                double w2=((Global.INSTANCE.id_size.get(id2)-min_node_size)/min_node_size);
                double w3=edge_weight.get(edge.getId());
                double w4= edge_size_slope*w3+edge_size_intercept;
                double w5=((w4+(w1+w2)/(7.0/edge_rate)))*edge_rate;
                double w6=w4*edge_rate;
                if(Double.isInfinite(w5) || Double.isNaN(w5)) w5=1.0;
                if(Double.isInfinite(w6) || Double.isNaN(w6)) w6=1.0;
                edge.setAttribute("layout.weight", w5);
                edge.setAttribute("layout, force", w6);
                Global.INSTANCE.edge_size.put(id1+"\t"+id2, w6);
                Global.INSTANCE.edge_size.put(id2+"\t"+id1, w6);
            }

            replace_ui_style_attribute(edge, "size", line_width+"");
            replace_ui_style_attribute(edge, "text-alignment", "along");
            replace_ui_style_attribute(edge, "text-size", name_size+"");
            replace_ui_style_attribute(edge, "text-color", "black");
        });

        if(groups==null || groups.length==0){
            if(jl_legend!=null) view.getChildren().removeAll(jl_legend);
            jl_legend=null;
            return;
        }

        int display_width=0, display_height=0, one_height=20, tmp_h=5;
        display_height=groups.length*one_height+10;

        double pre_x=10, pre_y=10;
        if(jl_legend!=null){
            pre_x=jl_legend.getLayoutX();
            pre_y=jl_legend.getLayoutY();
            view.getChildren().remove(jl_legend);
        }
        jl_legend=new Pane();
        jl_legend.setStyle("-fx-background-color: #c0c0c0");

        Font font=new Font("Times New Roman", Font.PLAIN, legend_name_size);
        SVGGraphics2D g2=new SVGGraphics2D(100, 100);
        for(String group:groups){
            Rectangle2D rec=font.getStringBounds(group, g2.getFontRenderContext());
            if(display_width<(rec.getWidth()+40)) display_width=(int)rec.getWidth()+40;

            Label label1=new Label();
            label1.setStyle("-fx-background-color: "+group_color.get(group)+";");
            label1.setLayoutX(10);
            label1.setLayoutY(tmp_h+2);
            label1.setPrefWidth(16);
            label1.setPrefHeight(16);
            jl_legend.getChildren().add(label1);

            Label label2=new Label(group);
            label2.setStyle(Global.INSTANCE.lable_legend+"-fx-font-size: "+legend_name_size+";");
            label2.setLayoutX(30);
            label2.setLayoutY(tmp_h);
            label2.setPrefWidth(80);
            label2.setPrefHeight(one_height);
            jl_legend.getChildren().add(label2);

            tmp_h+=one_height;
        }

        jl_legend.setLayoutX(pre_x);
        jl_legend.setLayoutY(pre_y);
        jl_legend.setPrefWidth(display_width);
        jl_legend.setPrefHeight(display_height);
        jl_legend.setOnMousePressed(e->{
            legend_x=jl_legend.getLayoutX()-e.getX();
            legend_y=jl_legend.getLayoutY()-e.getY();
        });
        jl_legend.setOnMouseReleased(e->{
            jl_legend.setLayoutX(e.getX()+legend_x);
            jl_legend.setLayoutY(e.getY()+legend_y);
        });
        view.getChildren().add(jl_legend);

        graph.nodes().forEach(n->{
            Vertex v=id_vertex.get(n.getId());
            if(v.isIntermediate) return;

            Map<String, Integer> freq1=new HashMap<>();
            Map<String, Double> freq2=new HashMap<>();
            for(String name:v.names){
                String group=hap_group.get(name);
                if(group==null) group="other";

                Integer f1=freq1.get(group);
                if(f1==null) freq1.put(group, 1);
                else freq1.put(group, f1+1);

                Double f2=freq2.get(group);
                if(f2==null) freq2.put(group, 1.0);
                else freq2.put(group, f2+1.0);
            }

            String []keys=freq2.keySet().toArray(new String[0]);
            for(String key:keys){
                freq2.put(key, freq2.get(key)/v.names.length);
            }

            this.id_name_freq1.put(n.getId(), freq1);
            this.id_name_freq2.put(n.getId(), freq2);

            if(freq2.size()==1){
                String color= group_color.get(keys[0]);
                if(color==null) color="#ffffff";
                Sprite sprite=id_sprite.get(v.id);
                sprite.removeAttribute("ui.pie-values");
                replace_ui_style_attribute(sprite, "shape", "circle");
                replace_ui_style_attribute(sprite, "fill-color", color);
            }else{
                Sprite sprite=id_sprite.get(v.id);
                StringBuilder sb=new StringBuilder();
                Double []values=new Double[keys.length];
                for(int i=0;i<keys.length;i++){
                    String color= group_color.get(keys[i]);
                    values[i]=freq2.get(keys[i]);
                    if(color==null) color="#ffffff";
                    if(i<(keys.length-1)) sb.append(color+",");
                    else sb.append(color);
                }
                replace_ui_style_attribute(sprite, "shape", "pie-chart");
                replace_ui_style_attribute(sprite, "fill-color", sb.toString());
                sprite.removeAttribute("ui.pie-values");
                sprite.setAttribute("ui.pie-values", values);
            }
        });
    }

    public void replace_ui_style_attribute(Sprite ele, String key, String value){
        String pre=(String)ele.getAttribute("ui.style");
        if(pre==null) ele.setAttribute("ui.style", key+":"+value+";");
        else{
            if(pre.startsWith(key+":")){
                int index=pre.indexOf(";", key.length()+1);
                String sub=pre.substring(index+1);
                ele.setAttribute("ui.style", key+":"+value+";"+sub);
            }else{
                int index1=pre.indexOf(";"+key+":");
                if(index1!=-1){
                    int index2=pre.indexOf(";", index1+key.length()+2);
                    String sub1=pre.substring(0, index1+1);
                    String sub2=pre.substring(index2+1);
                    ele.setAttribute("ui.style", sub1+key+":"+value+";"+sub2);
                }else{
                    ele.setAttribute("ui.style", pre+key+":"+value+";");
                }
            }
        }
    }

    public void replace_ui_style_attribute(Node ele, String key, String value){
        String pre=(String)ele.getAttribute("ui.style");
        if(pre==null) ele.setAttribute("ui.style", key+":"+value+";");
        else{
            if(pre.startsWith(key+":")){
                int index=pre.indexOf(";", key.length()+1);
                String sub=pre.substring(index+1);
                ele.setAttribute("ui.style", key+":"+value+";"+sub);
            }else{
                int index1=pre.indexOf(";"+key+":");
                if(index1!=-1){
                    int index2=pre.indexOf(";", index1+key.length()+2);
                    String sub1=pre.substring(0, index1+1);
                    String sub2=pre.substring(index2+1);
                    ele.setAttribute("ui.style", sub1+key+":"+value+";"+sub2);
                }else{
                    ele.setAttribute("ui.style", pre+key+":"+value+";");
                }
            }
        }
    }

    public void replace_ui_style_attribute(Edge ele, String key, String value){
        String pre=(String)ele.getAttribute("ui.style");
        if(pre==null) ele.setAttribute("ui.style", key+":"+value+";");
        else{
            if(pre.startsWith(key+":")){
                int index=pre.indexOf(";", key.length()+1);
                String sub=pre.substring(index+1);
                ele.setAttribute("ui.style", key+":"+value+";"+sub);
            }else{
                int index1=pre.indexOf(";"+key+":");
                if(index1!=-1){
                    int index2=pre.indexOf(";", index1+key.length()+2);
                    String sub1=pre.substring(0, index1+1);
                    String sub2=pre.substring(index2+1);
                    ele.setAttribute("ui.style", sub1+key+":"+value+";"+sub2);
                }else{
                    ele.setAttribute("ui.style", pre+key+":"+value+";");
                }
            }
        }
    }

    private void clear_graph(){
        this.groups=null;
        this.hap_group.clear();
        this.group_color.clear();
        this.id_name_freq1.clear();
        this.id_name_freq2.clear();
        this.phy_pos_map.clear();
        p_pane1.getChildren().remove(jt_groups);
        p_pane1.getChildren().remove(jt_haps);
        p_pane1.getChildren().remove(jt_snps);
        if(jl_legend!=null) view.getChildren().remove(jl_legend);
        jl_legend=null;
    }

    private void read_cache_colors(){
        log("reading cache colors...");

        try {
            InputStream is=null;
            InputStreamReader isr=null;
            BufferedReader br=null;
            try {
                is=getClass().getResourceAsStream("/color/group_color.txt");
                isr=new InputStreamReader(is, "UTF-8");
                br=new BufferedReader(isr);

                while (true){
                    String line=br.readLine();
                    if(line==null) break;
                    String []ss=line.trim().split("\\s+");
                    List<String> list=new ArrayList<>();
                    for(String s:ss){
                        if(false){
                            String []ts=s.split(",");
                            int red=Integer.parseInt(ts[0]);
                            int green=Integer.parseInt(ts[1]);
                            int blue=Integer.parseInt(ts[2]);
                            String r=String.format("%x", red);
                            String g=String.format("%x", green);
                            String b=String.format("%x", blue);
                            if(r.length()==1) r="0"+r;
                            if(g.length()==1) g="0"+g;
                            if(b.length()==1) b="0"+b;
                            list.add("#"+r+g+b);
                        }else{
                            list.add(s);
                        }
                    }
                    this.cache_colors.put(ss.length, list);
                    if(this.cache_colors_max_num<ss.length) this.cache_colors_max_num=ss.length;
                }
            }finally {
                if(br!=null) br.close();
                if(isr!=null) isr.close();
                if(is!=null) is.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private boolean is_color_grb(String str){
        if(str.length()!=7) return false;

        char []cs=str.toLowerCase().toCharArray();
        for(int i=1;i<cs.length;i++){
            char c=cs[i];
            if(c>='0' && c<='9') continue;
            if(c>='a' && c<='f') continue;
            return false;
        }

        return true;
    }

    private String color_2_str(Color color){
        String r=String.format("%x", (int)(color.getRed()*255));
        String g=String.format("%x", (int)(color.getGreen()*255));
        String b=String.format("%x", (int)(color.getBlue()*255));
        if(r.length()==1) r="0"+r;
        if(g.length()==1) g="0"+g;
        if(b.length()==1) b="0"+b;
        return "#"+r+g+b;
    }

    private Color str_2_color(String str){
        if(!is_color_grb(str)) return null;

        double r=Integer.parseInt(str.substring(1, 3), 16)/255.0;
        double g=Integer.parseInt(str.substring(3, 5), 16)/255.0;
        double b=Integer.parseInt(str.substring(5, 7), 16)/255.0;

        return new Color(r, g, b, 1.0);
    }

    private java.awt.Color str_2_color2(String str){
        if(!is_color_grb(str)) return null;

        double r=Integer.parseInt(str.substring(1, 3), 16)/255.0;
        double g=Integer.parseInt(str.substring(3, 5), 16)/255.0;
        double b=Integer.parseInt(str.substring(5, 7), 16)/255.0;

        return new java.awt.Color((float)r, (float)g, (float)b, 1.0f);
    }

    private void graph_2_svg(String out_file){
        if(graph.nodes().count()==0){
            SVGGraphics2D g2=new SVGGraphics2D((int)width, (int)height);
            try {
                InputStream is = new ByteArrayInputStream(g2.getSVGDocument().getBytes());
                OutputStream os=new FileOutputStream(out_file);

                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = is.read(buffer)) != -1) {
                    os.write(buffer, 0, bytesRead);
                }

                os.close();
                is.close();
            }catch (Exception e){
                e.printStackTrace();
            }

            return;
        }

        int legend_pos_type=1;
        if(jl_legend!=null){
            double x1=jl_legend.getLayoutX();
            double y1=jl_legend.getLayoutY();
            double w2=view.getPrefWidth();
            double h2=view.getPrefHeight();

            if(x1<(w2/2.0) && y1<(h2/2.0)) legend_pos_type=1;
            else if(x1>(w2/2.0) && y1<(h2/2.0)) legend_pos_type=2;
            else if(x1<(w2/2.0) && y1>(h2/2.0)) legend_pos_type=3;
            else legend_pos_type=4;
        }

        double tmp_min_width=Double.MAX_VALUE;
        double tmp_min_height=Double.MAX_VALUE;
        double tmp_max_width=0.0;
        double tmp_max_height=0.0;
        double max_size=0.0;
        for(Double d:Global.INSTANCE.id_size.values()){
            if(max_size<d) max_size=d;
        }

        Map<String, Point3> hp=new HashMap<>();
        Collection<GraphicElement> collection=view.allGraphicElementsIn(EnumSet.of(InteractiveElement.NODE), 0, 0, width, height);
        for(GraphicElement ele :collection){
            Point3 p=renderer.getCamera().transformGuToPx(ele.getX(), ele.getY(), 0);
            hp.put(((Node)ele).getId(), p);
            if(tmp_max_width<p.x) tmp_max_width=p.x;
            if(tmp_max_height<p.y) tmp_max_height=p.y;
            if(tmp_min_width>p.x) tmp_min_width=p.x;
            if(tmp_min_height>p.y) tmp_min_height=p.y;
        }

        if(jl_legend!=null){
            double w1=jl_legend.getPrefWidth();
            double h1=jl_legend.getPrefHeight();
            for(Point3 p:hp.values()){
                if(legend_pos_type==1 && p.y<(tmp_min_height+10.0+h1) && tmp_min_width>(p.x-10.0-w1)) tmp_min_width=p.x-10.0-w1;
                if(legend_pos_type==2 && p.y<(tmp_min_height+10.0+h1) && tmp_max_width<(p.x+10.0+w1)) tmp_max_width=p.x+10.0+w1;
                if(legend_pos_type==3 && p.y>(tmp_max_height-10.0-h1) && tmp_min_width>(p.x-10.0-w1)) tmp_min_width=p.x-10.0-w1;
                if(legend_pos_type==4 && p.y>(tmp_max_height-10.0-h1) && tmp_max_width<(p.x+10.0+w1)) tmp_max_width=p.x+10.0+w1;
            }
        }

        tmp_min_width-=(max_size+20.0);
        tmp_min_height-=(max_size+20.0);
        tmp_max_width+=(max_size+20.0);
        tmp_max_height+=(max_size+20.0);

        final double mw=tmp_min_width;
        final double mh=tmp_min_height;

        Font font=new Font("Times New Roman", Font.PLAIN, 12);

        SVGGraphics2D g2=new SVGGraphics2D((int)(tmp_max_width-tmp_min_width), (int)(tmp_max_height-tmp_min_height));
        g2.setFont(font);
        g2.setStroke(new BasicStroke((float)line_width));

        graph.edges().forEach(edge->{
            Node n1=edge.getSourceNode();
            Node n2=edge.getTargetNode();
            Point3 p1=hp.get(n1.getId());
            Point3 p2=hp.get(n2.getId());

            Line2D.Double line=new Line2D.Double(p1.x-mw, p1.y-mh, p2.x-mw, p2.y-mh);
            g2.draw(line);

            if(cb_edge.isSelected()){
                double x=(p1.x-mw+p2.x-mw)/2.0;
                double y=(p1.y-mh+p2.y-mh)/2.0;
                String label=(String)get_element_attribute(edge, "ui.label", null);
                if(label!=null){
                    Rectangle2D rec=font.getStringBounds(label, g2.getFontRenderContext());
                    x-=rec.getWidth()/2.0;
                    y+=rec.getHeight()/2.0;
                    g2.drawString(label, (float)(x), (float)(y));
                }
            }
        });

        graph.nodes().forEach(node->{
            Vertex v=id_vertex.get(node.getId());
            Point3 p=hp.get(node.getId());

            double size=Double.parseDouble((String)get_element_attribute(node, "ui.style", "size"));
            drawEllipse(g2, java.awt.Color.BLACK, p.x-mw, p.y-mh, size, size);

            Sprite sprite=id_sprite.get(node.getId());

            double size2=size;
            if(v.isIntermediate){
                if(cb_node_intermediate_outline.isSelected()) size2+=(2.0*line_width);
            }else{
                if(cb_node_true_outline.isSelected()) size2+=(2.0*line_width);
            }

            Object pie=get_element_attribute(sprite, "ui.pie-values", null);
            if(pie==null){
                String c_color=(String)get_element_attribute(sprite, "ui.style", "fill-color");
                if(c_color==null) fillEllipse(g2, java.awt.Color.WHITE, p.x-mw, p.y-mh, size2, size2);
                else fillEllipse(g2, str_2_color2(c_color.trim()), p.x-mw, p.y-mh, size2, size2);
            }else{
                String c_color=(String)get_element_attribute(sprite, "ui.style", "fill-color");
                if(c_color!=null){
                    String []cs=c_color.split(",");
                    Double []rates=(Double [])pie;
                    double start_ang=0;
                    if(cs!=null && rates!=null){
                        for(int i=0;i<cs.length;i++){
                            double ang=i==(cs.length-1) ? (360-start_ang) : (rates[i]*360.0);
                            fillArc(g2, str_2_color2(cs[i]), p.x-mw, p.y-mh, size2, start_ang, ang);
                            start_ang+=ang;
                        }
                    }
                }
            }

            if(cb_node.isSelected()){
                double x=p.x-mw;
                double y=p.y-mh;
                String label=(String)get_element_attribute(sprite, "ui.label", null);
                if(label!=null){
                    Rectangle2D rec=font.getStringBounds(label, g2.getFontRenderContext());
                    x-=rec.getWidth()/2.0;
                    y+=rec.getHeight()/2.0;
                    g2.setColor(java.awt.Color.BLACK);
                    g2.drawString(label, (float)x, (float)y);
                }
            }
        });

        if(jl_legend!=null){
            double x1, y1;

            if(legend_pos_type==1){
                x1=10.0;
                y1=10.0;
            }else if(legend_pos_type==2){
                x1=(tmp_max_width-tmp_min_width)-jl_legend.getPrefWidth()-10.0;
                y1=10.0;
            }else if(legend_pos_type==3){
                x1=10.0;
                y1=(tmp_max_height-tmp_min_height)-jl_legend.getPrefHeight()-10.0;
            }else{
                x1=(tmp_max_width-tmp_min_width)-jl_legend.getPrefWidth()-10.0;
                y1=(tmp_max_height-tmp_min_height)-jl_legend.getPrefHeight()-10.0;
            }

            int tmp_h=5, one_height=20;
            for(String group:groups){
                g2.setColor(str_2_color2(group_color.get(group)));
                g2.fillRect((int)(x1+10), (int)(y1+tmp_h+2), 16, 16);
                g2.setColor(java.awt.Color.BLACK);
                g2.drawString(group, (int)(x1+30), (int)(y1+tmp_h+one_height-5));
                tmp_h+=one_height;
            }
        }

        try {
            InputStream is = new ByteArrayInputStream(g2.getSVGDocument().getBytes());
            OutputStream os=new FileOutputStream(out_file);

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }

            os.close();
            is.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void drawEllipse(SVGGraphics2D g2, java.awt.Color color, double x, double y, double size1, double size2){
        Ellipse2D.Double e=new Ellipse2D.Double(x-size1/2.0, y-size2/2.0, size1, size2);
        g2.setColor(java.awt.Color.WHITE);
        g2.fill(e);
        g2.setColor(color);
        g2.draw(e);
    }

    private void fillEllipse(SVGGraphics2D g2, java.awt.Color color, double x, double y, double size1, double size2){
        Ellipse2D.Double e=new Ellipse2D.Double(x-size1/2.0, y-size2/2.0, size1, size2);
        g2.setColor(color);
        g2.fill(e);
    }

    private void drawArc(SVGGraphics2D g2, java.awt.Color color, double x, double y, double size, double start, double extend){
        Arc2D.Float arc=new Arc2D.Float(Arc2D.PIE);
        arc.setFrame(x-size/2.0, y-size/2.0, size, size);
        arc.setAngleStart(start);
        arc.setAngleExtent(extend);

        g2.setColor(java.awt.Color.WHITE);
        g2.fill(arc);
        g2.setColor(color);
        g2.draw(arc);
    }

    private void fillArc(SVGGraphics2D g2, java.awt.Color color, double x, double y, double size, double start, double extend){
        Arc2D.Float arc=new Arc2D.Float(Arc2D.PIE);
        arc.setFrame(x-size/2.0, y-size/2.0, size, size);
        arc.setAngleStart(start);
        arc.setAngleExtent(extend);

        g2.setColor(color);
        g2.fill(arc);
    }

    private Object get_element_attribute(Object obj, String label1, String label2){
        if(obj instanceof  Node){
            Node ele=(Node)obj;
            if(label2==null) return ele.getAttribute(label1);
            String style=(String)ele.getAttribute(label1);
            if(style!=null){
                if(!label2.endsWith(":")) label2=label2+":";
                int index1=style.indexOf(label2);
                if(index1!=-1){
                    int index2=style.indexOf(";", index1);
                    if(index2!=-1) return style.substring(index1+label2.length(), index2);
                }
            }
        }else if(obj instanceof Edge){
            Edge ele=(Edge)obj;
            if(label2==null) return ele.getAttribute(label1);
            String style=(String)ele.getAttribute(label1);
            if(style!=null){
                if(!label2.endsWith(":")) label2=label2+":";
                int index1=style.indexOf(label2);
                if(index1!=-1){
                    int index2=style.indexOf(";", index1);
                    if(index2!=-1) return style.substring(index1+label2.length(), index2);
                }
            }
        }else if(obj instanceof Sprite){
            Sprite ele=(Sprite) obj;
            if(label2==null) return ele.getAttribute(label1);
            String style=(String)ele.getAttribute(label1);
            if(style!=null){
                if(!label2.endsWith(":")) label2=label2+":";
                int index1=style.indexOf(label2);
                if(index1!=-1){
                    int index2=style.indexOf(";", index1);
                    if(index2!=-1) return style.substring(index1+label2.length(), index2);
                }
            }
        }

        return null;
    }

    //================================================

    private class GroupItem{

        private int row;
        private SimpleStringProperty group;
        private SimpleStringProperty color;
        private ColorPicker color_picker;
        private ObservableValue<ColorPicker> select;

        public GroupItem(int row, String group, String color, Image img){
            this.row=row;
            this.group=new SimpleStringProperty(group);
            this.color=new SimpleStringProperty(color);
            this.color_picker =new ColorPicker(str_2_color(color));
            this.color_picker.setPrefWidth(50);
            this.color_picker.setPrefHeight(20);
            this.color_picker.setOnAction(e->{
                String c_str=color_2_str(color_picker.getValue());
                this.color=new SimpleStringProperty(c_str);
                jt_groups.refresh();
                group_color.put(group, c_str);
                flush_graph();
            });
            this.select=new TableViewValue<>(this.color_picker);
        }

        public SimpleStringProperty getGroup() {
            return group;
        }

        public SimpleStringProperty getColor() {
            return color;
        }

        public ObservableValue<ColorPicker> getSelect() {
            return select;
        }

        public void setColor(String color){
            this.color=new SimpleStringProperty(color);
        }

    }

    private class HapItem{

        private int row;
        private SimpleStringProperty hap;
        private SimpleStringProperty group;

        public HapItem(int row, String hap, String group){
            this.row=row;
            this.hap=new SimpleStringProperty(hap);
            this.group=new SimpleStringProperty(group);
        }

        public SimpleStringProperty getHap(){
            return this.hap;
        }

        public SimpleStringProperty getGroup(){
            return this.group;
        }

        public int getRow(){return row;}

    }

    private class SnpItem{

        private int row;
        private ObservableValue<Integer> pos;
        private SimpleStringProperty realPos;
        private SimpleStringProperty snp;
        private CheckBox cb;
        private ObservableValue<CheckBox> select;

        public SnpItem(int row, int pos, String snp){
            this.row=row;
            this.pos=new TableViewValue<>(pos);
            this.snp=new SimpleStringProperty(snp);
            this.cb=new CheckBox();
            this.cb.setSelected(false);
            phy_pre_status[row]=false;
            this.select=new TableViewValue<>(this.cb);

            Integer real_pos=phy_pos_map.get(pos);
            if(real_pos==null) this.realPos =new SimpleStringProperty(pos+"");
            else this.realPos =new SimpleStringProperty(tf_chr.getText().trim()+":"+real_pos);

            this.cb.setOnAction(e->{
                phy_pre_status[row]=this.cb.isSelected();
                List<Integer> poss=new ArrayList<>();
                ObservableList<SnpItem> data=jt_snps.getItems();
                for(int i=0;i<data.size();i++){
                    if(phy_pre_status[i]==true){
                        poss.add(data.get(i).getPos().getValue());
                    }
                }
                if(poss.size()==0){
                    groups=null;
                    hap_group.clear();
                    p_pane1.getChildren().remove(jt_groups);
                    flush_graph();
                    return;
                }
                hap_group.clear();
                Set<String> set=new HashSet<>();
                for(String name:phy_name_seq.keySet()){
                    StringBuilder sb=new StringBuilder();
                    char []cs=phy_name_seq.get(name).toCharArray();
                    for(Integer a:poss) sb.append(cs[a-1]);
                    set.add(sb.toString());
                    hap_group.put(name, sb.toString());
                }
                if(set.size()>phy_max_group_num){
                    Alert alert=new Alert(Alert.AlertType.WARNING, "Max group number should < "+phy_max_group_num);
                    alert.showAndWait();
                    this.cb.setSelected(false);
                    phy_pre_status[row]=false;
                }
                groups=set.toArray(new String[0]);
                //--
                String []tmps_colors=null;
                if(cache_colors.containsKey(groups.length)) tmps_colors=cache_colors.get(groups.length).toArray(new String[0]);
                else tmps_colors=cache_colors.get(cache_colors_max_num).toArray(new String[0]);
                ObservableList<GroupItem> data1=FXCollections.observableArrayList();
                for (int i = 0; i < groups.length; i++) {
                    String c_str = tmps_colors[i % tmps_colors.length];
                    group_color.put(groups[i], c_str);
                    data1.add(new GroupItem(i, groups[i], c_str, Global.INSTANCE.img_color));
                }
                jt_groups.getItems().clear();
                jt_groups.setItems(data1);
                if(!p_pane1.getChildren().contains(jt_groups)) p_pane1.getChildren().add(jt_groups);
                flush_graph();
            });
        }

        public ObservableValue<Integer> getPos(){return pos;}

        public SimpleStringProperty getRealPos(){return realPos;}

        public SimpleStringProperty getSnp(){return snp;}

        public ObservableValue<CheckBox> getSelect(){return select;}

    }

    private class NodeItem1 {

        private SimpleStringProperty group;
        private ObservableValue<Integer> num;
        private ObservableValue<Double> rate;

        public NodeItem1(String group, int num, double rate){
            this.group=new SimpleStringProperty(group);
            this.num=new TableViewValue<>(num);
            this.rate=new TableViewValue<>(Double.parseDouble(String.format("%.3f", rate)));
        }

        public SimpleStringProperty getGroup(){return group;}

        public ObservableValue<Integer> getNum(){return num;}

        public ObservableValue<Double> getRate(){return rate;}

    }

    private class NodeItem2{

        private SimpleStringProperty group;
        private SimpleStringProperty hap;

        public NodeItem2(String group, String hap){
            this.group=new SimpleStringProperty(group);
            this.hap=new SimpleStringProperty(hap);
        }

        public SimpleStringProperty getGroup(){return group;}

        public SimpleStringProperty getHap(){return hap;}

    }

    private class EdgeItem{

        private ObservableValue<Integer> pos;
        private SimpleStringProperty realPos;
        private SimpleStringProperty alt1;
        private SimpleStringProperty alt2;

        public EdgeItem(Integer pos, String snp){
            this.pos=new TableViewValue<>(pos);
            String alt1=snp.substring(0, 1);
            String alt2=snp.substring(3, 4);
            this.alt1=new SimpleStringProperty(alt1);
            this.alt2=new SimpleStringProperty(alt2);

            Integer real_pos=phy_pos_map.get(pos);
            if(real_pos==null) this.realPos =new SimpleStringProperty(pos+"");
            else this.realPos =new SimpleStringProperty(tf_chr.getText().trim()+":"+real_pos);
        }

        public ObservableValue<Integer> getPos(){return pos;}

        public SimpleStringProperty getRealPos(){return realPos;}

        public SimpleStringProperty getAlt1(){return alt1;}

        public SimpleStringProperty getAlt2(){return alt2;}

    }

    //================================================

    public static void main(String []args){
        System.setProperty("org.graphstream.ui", "javafx");
        get_width_height();
        launch(args);
    }

    private static void get_width_height(){
//        GraphicsEnvironment ge= GraphicsEnvironment.getLocalGraphicsEnvironment();
//        Dimension dim=Toolkit.getDefaultToolkit().getScreenSize();
//        Insets insets=Toolkit.getDefaultToolkit().getScreenInsets(ge.getDefaultScreenDevice().getDefaultConfiguration());
//        width=dim.width-insets.left-insets.right;
//        height=dim.height-insets.top-insets.bottom;
        width= Screen.getPrimary().getVisualBounds().getWidth();
        height=Screen.getPrimary().getVisualBounds().getHeight()-20;
        max_width=width;
        max_height=height;

        log("width="+width+"\theight="+height);
    }

    private static void log(String str){
        String nowTime=new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS") .format(new java.util.Date());
        System.out.println(nowTime+"\t"+str);
    }

    //================================================

    private class Invalid{
        public void jgrdjtlesjl999(){

        }
    }

}
