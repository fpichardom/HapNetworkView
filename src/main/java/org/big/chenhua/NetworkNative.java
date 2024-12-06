package org.big.chenhua;

import org.Global;

public class NetworkNative {

    static {
        String posfix;

        String os=System.getProperty("os.name").toLowerCase();
        if(os.startsWith("win")) posfix=".dll";
        else if(os.contains("mac")) posfix=".dylib";
        else posfix=".so";

        if(!os.contains("mac")){
            System.load(Global.INSTANCE.dll_exe_dir+"fastHaN"+posfix);
            System.err.println("load:"+Global.INSTANCE.dll_exe_dir+"fastHaN"+posfix);
        }
    }

    public static native void generate_network(String input, String output);

    public static native void generate_network(String input, String chr, String output);

    public static native void generate_network(byte []input, byte []output);

    public static native void generate_network(byte []input, byte []chr, byte []output);

}
