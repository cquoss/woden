/**
 * Copyright 2006 Apache Software Foundation 
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 */
package org.apache.woden.tests;

import java.io.File;
import java.net.MalformedURLException;

public class W3CFileRepository {

    private String w3cBase = null;

    private String localBase = null;

    private boolean localCacheExists = false;

    public W3CFileRepository(String w3cBaseLocation, String localBaseLocation) {
        w3cBase = w3cBaseLocation;
        localBase = localBaseLocation;

        File loc = new File(localBase);
        if (loc.exists() && loc.isDirectory()) {
            localCacheExists = true;
        }
    }

    /**
     * Given a path of a file in the W3C CVS repository, return the path to the
     * local cache of this file. If the file doesn't exist, it is retrieved and
     * stored in the cache.
     * 
     * @param path
     *            path of the file in the W3C CVS repository
     * @return if the file exists locally then a file URL, otherwise, the path
     *         parameter value.
     */
    public String getFilePath(String path) {
        // If we don't have a cache return the server path
        if (!localCacheExists) {
            return path;
        }

        // If the path doesn't start with the base location then return the
        // server path
        if (!path.startsWith(w3cBase)) {
            return path;
        }

        String localPath = localBase + path.substring(w3cBase.length());
        File localFile = new File(localPath);
        if (localFile.exists()) {
            try {
                return localFile.toURI().toURL().toString();
            } catch (MalformedURLException mue) {
                System.err.println("Got MalformedURLException trying to create a URL from " + localPath);
                return path;
            }
        } else {
            return path;
        }
    }
}
