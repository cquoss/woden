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

package org.apache.woden.ant;

import org.apache.woden.wsdl20.extensions.rpc.Argument;
import org.apache.woden.wsdl20.extensions.rpc.Direction;
import org.apache.woden.wsdl20.extensions.rpc.RPCInterfaceOperationExtensions;

/**
 * @author Arthur Ryman (ryman@ca.ibm.com, arthur.ryman@gmail.com)
 *
 */
public class CmRpcWriter extends NamespaceWriter {

    public final static String NS = "http://www.w3.org/2002/ws/desc/wsdl/component-rpc";

    public final static String PREFIX = "cmrpc";

    public static final String RPC_INTERFACE_OPERATION_EXTENSION = PREFIX
            + ":rpcInterfaceOperationExtension";

    public static final String RPC_SIGNATURE = PREFIX + ":rpcSignature";

    /**
     * @param out
     */
    public CmRpcWriter(XMLWriter out) {

        super(out, NS, PREFIX);
    }

    public void rpcInterfaceOperationExtension(
            RPCInterfaceOperationExtensions rpcExtensions) {

        if (rpcExtensions == null)
            return;

        out.beginElement(RPC_INTERFACE_OPERATION_EXTENSION);

        write(RPC_SIGNATURE, rpcExtensions.getRPCSignature());

        out.endElement();
    }

    private void write(String tag, Argument[] rpcSignature) {

        out.beginElement(tag);
        for (int i = 0; i < rpcSignature.length; i++) {

            write(PREFIX + ":argument", rpcSignature[i]);
        }
        out.endElement();
    }

    private void write(String tag, Argument argument) {

        CmBaseWriter cmbase = (CmBaseWriter) out.lookup(CmBaseWriter.NS);

        out.beginElement(tag);

        cmbase.write(PREFIX + ":name", argument.getName());
        write(PREFIX + ":direction", argument.getDirection());

        out.endElement();
    }

    private void write(String tag, Direction direction) {

        out.element(tag, direction.toString());
    }
}
