/*
 * Copyright (c) 2009, 2010, Ken Arnold All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * Neither the name of the myself nor the names of its contributors may be used
 * to endorse or promote products derived from this software without specific
 * prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * @SimplxCopyright
 */

package org.simplx.xstream;

import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.simplx.xstream.MixedStreamReader.TextPart;

import java.util.Iterator;

/**
 * A class that copies mixed streams.
 *
 * @author Ken Arnold
 */
public class MixedStreamCopier {
    /** Creates a new {@link MixedStreamCopier}. */
    public MixedStreamCopier() {
    }

    /**
     * Copies a mixed stream to a destination writer.
     *
     * @param source      The mixed stream source.
     * @param destination The copy destination.
     */
    public void copy(MixedStreamReader source,
            HierarchicalStreamWriter destination) {

        doCopy(source, destination, false);
    }

    private void doCopy(MixedStreamReader source,
            HierarchicalStreamWriter destination, boolean startNode) {

        if (startNode)
            destination.startNode(source.getNodeName());
        int attributeCount = source.getAttributeCount();
        for (int i = 0; i < attributeCount; i++) {
            destination.addAttribute(source.getAttributeName(i),
                    source.getAttribute(i));
        }
        Iterator parts = source.partIterator();
        while (parts.hasNext()) {
            Object part = parts.next();
            if (part instanceof TextPart)
                destination.setValue(((TextPart) part).contents());
            else {
                source.moveDown();
                doCopy(source, destination, true);
                source.moveUp();
            }
        }
        if (startNode)
            destination.endNode();
    }
}