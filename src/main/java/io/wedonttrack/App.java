package io.wedonttrack;

import io.wedonttrack.runner.RunCollection;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
    }

    public static void executeCollection(){
        RunCollection collection = new RunCollection();
        collection.run();
    }
}
