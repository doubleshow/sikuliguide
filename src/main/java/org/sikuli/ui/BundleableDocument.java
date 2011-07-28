package org.sikuli.ui;

import java.util.List;

public interface BundleableDocument extends Bundleable {
   List<Bundleable> getBundleables();
}