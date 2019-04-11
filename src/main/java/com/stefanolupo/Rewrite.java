package com.stefanolupo;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Rewrite {

    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            throw new IllegalArgumentException("Need full path of conf and outpath");
        }
        String fullPath = args[0];
        String outpath = args[1];
//        fullPath = "/home/stefano/projects/ndn-script/nlsr/topologies/linear-three/nodeB-nlsr.conf";
        String file = readFile(fullPath, Charset.defaultCharset());
//        System.out.println(file);
        Pattern pattern = Pattern.compile("udp4://(.*)");
        Matcher matcher = pattern.matcher(file);
        while (matcher.find()) {
            String hostName = matcher.group(1);
            System.out.println("Found: " + hostName);
            String ip = getIpOfHost(hostName);
            file = file.replace(hostName, ip);
            System.out.println("Replacing with: " + ip);
        }

        FileWriter fileWriter = new FileWriter(outpath);
        PrintWriter printWriter = new PrintWriter(fileWriter);
        printWriter.print(file);
        printWriter.close();
    }

    private static String getIpOfHost(String hostName) {
        try {
            InetAddress address = InetAddress.getByName(hostName);
            return  address.getHostAddress();
        } catch (UnknownHostException e) {
            return hostName;
        }
    }

    static String readFile(String path, Charset encoding)
            throws IOException
    {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }
}
