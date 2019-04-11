package com.stefanolupo;

import net.named_data.jndn.*;
import net.named_data.jndn.security.KeyChain;
import net.named_data.jndn.util.Blob;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Scanner;


public class Test implements OnInterestCallback, OnData, OnTimeout {
    @Override
    public void onInterest(Name prefix, Interest interest, Face face, long interestFilterId, InterestFilter filter) {
        System.out.println("Received interest..sleeping for 10s");
        System.out.println(interest.toUri());
        try {
            Thread.sleep(10000);
            System.out.println("sending data reply");

            Data data = new Data(interest.getName());

            data.setContent(new Blob("Hello world!"));
            face.putData(data);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onTimeout(Interest interest) {
        System.out.println("Timeout for " + interest.toUri());
    }

    @Override
    public void onData(Interest interest, Data data) {
        System.out.println("Got data for " + interest.toUri() + ": " + data.getName());
    }

    public static void main(String[] args) throws Exception {
        Face face = new Face();
        face.setCommandSigningInfo(new KeyChain(), new KeyChain().getDefaultCertificateName());
        Test test = new Test();
//        face.registerPrefix(new Name("/com/treeA"), test, p -> System.out.println(p));

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String s;
        while ((s = reader.readLine()) != null) {
            if (s.equals("pev")) {
                System.out.println("Processing events");
                face.processEvents();
            }
            else if (s.startsWith("rp")) {
                String name = s.split(" ")[1];
                System.out.println("registering prefix " + name);
                face.registerPrefix(new Name(name), test, p -> System.out.println("failed to reg " + p.toUri()));
            }
            else {
                System.out.println("Expressing interest for " + s);
                face.expressInterest(new Name(s), test, test);
            }
        }
    }
}
