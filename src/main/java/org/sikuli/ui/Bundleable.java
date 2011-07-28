package org.sikuli.ui;

import java.io.File;
import java.io.IOException;

public interface Bundleable{
   public void writeToBundle(File bundlePath) throws IOException;
   public void readFromBundle(File bundlePath) throws IOException;
}