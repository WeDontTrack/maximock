package org.wedonttrack;

import org.wedonttrack.runner.RunCollection;

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
